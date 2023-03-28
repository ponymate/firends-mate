package com.ponymate.firendsmate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ponymate.firendsmate.common.ErrorCode;
import com.ponymate.firendsmate.contant.UserConstant;
import com.ponymate.firendsmate.exception.BusinessException;
import com.ponymate.firendsmate.exception.ThrowUtils;
import com.ponymate.firendsmate.job.ScheduledRedis;
import com.ponymate.firendsmate.mapper.UserMapper;
import com.ponymate.firendsmate.model.domain.User;
import com.ponymate.firendsmate.model.dto.User.UserUpdateRequest;
import com.ponymate.firendsmate.service.UserService;
import com.ponymate.firendsmate.utils.AlgorithmUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.ponymate.firendsmate.contant.UserConstant.USER_LOGIN_STATE;


/**
 * 用户服务实现类
 *
 * @author ponymate
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    ScheduledRedis scheduledRedis;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "ponymate";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        // 账户不能重复
        synchronized (userAccount.intern()) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 星球编号不能重复
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("planetCode", planetCode);
            count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setPlanetCode(planetCode);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                return -1;
            }
            return user.getId();
        }
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }
        // 3. 用户脱敏
        User safetyUser = getSafetyUser(user);
        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 用户注销
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) userObj;
    }

    /**
     * 更新用户数据
     * @param userUpdateRequest
     * @param loginUser
     * @return
     */
    @Override
    public int updateUser(UserUpdateRequest userUpdateRequest, User loginUser) {
        long userId = userUpdateRequest.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 如果是管理员，允许更新任意用户
        // 如果不是管理员，只允许更新当前（自己的）信息
        if (!isAdmin(loginUser) && userId != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        //todo 修改session中的数据

        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest,user);
        return userMapper.updateById(user);
    }


    /**
     * 根据tags查询用户
     * @param tags
     * @return
     */
    @Override
    public List<User> searchUserByTags(List<String> tags){
        List<User> users = (List<User>) redisTemplate.opsForValue().get("recommend");
        if (users == null) {
            // 异步执行的代码块
            Runnable runnable = new Runnable() {
                public void run() {
                    // 这里是异步执行的业务逻辑
                    System.out.println("redis go");
                    scheduledRedis.cacheRecommendUsers();
                }
            };
            // 创建新的线程并执行异步代码
            new Thread(runnable).start();
            users = this.list();
        }
        return users.stream().filter(user -> {
            String userTags = user.getTags();
            Gson gson = new Gson();
            Set<String> userTagSet = gson.fromJson(userTags, new TypeToken<Set<String>>() {
            }.getType());
            userTagSet = Optional.ofNullable(userTagSet).orElse(new HashSet<>());
            for (String tag : tags) {
                if (!userTagSet.contains(tag))
                    return false;
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public Page<User> recommendUsers(Page<User> userPage, HttpServletRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        //如果有缓存数据，则从缓存中取出
        List<User> redisUserList = (List<User>) redisTemplate.opsForValue().get("recommend");
        if (redisUserList != null) {
            // 将 list 转换为 page
            int size = Integer.parseInt(String.valueOf(userPage.getSize()));
            int pages = Integer.parseInt(String.valueOf(userPage.getCurrent()));
            List<User> userList = redisUserList.subList(size * pages, size * pages+size).stream().map(this::getSafetyUser).collect(Collectors.toList());
            userPage.setRecords(userList);
            userPage.setTotal(redisUserList.size());

            return userPage;
        }
        // 异步执行的代码块
        Runnable runnable = new Runnable () {
            public void run() {
                // 这里是异步执行的业务逻辑
                scheduledRedis.cacheRecommendUsers();
            }
        };
        // 创建新的线程并执行异步代码
        new Thread(runnable).start();
        return this.page(userPage, wrapper);
    }

    /**
     * 使用缓存和优先队列进行匹配
     * @param loginUser
     * @return
     */
    @Override
    public List<User> matchUsersByCache(User loginUser) {
        //获取所有用户
        List<User> userList = (List<User>) redisTemplate.opsForValue().get("recommend");
        if (userList == null) {
            // 异步执行的代码块
            Runnable runnable = new Runnable () {
                public void run() {
                    // 这里是异步执行的业务逻辑
                    System.out.println("redis go");
                    scheduledRedis.cacheRecommendUsers();
                }
            };
            // 创建新的线程并执行异步代码
            new Thread(runnable).start();
            return matchUsers(loginUser);
        }
        else {//获取自己的标签
            List<String> myTags = this.getUserTags(loginUser);
            // 创建 PriorityQueue 并传入比较器
            PriorityQueue<Pair<User, Long>> pq = new PriorityQueue<>(new Comparator<Pair<User, Long>>() {
                @Override
                public int compare(Pair<User, Long> p1, Pair<User, Long> p2) {
                    return p1.getValue().compareTo(p2.getValue());
                }
            });
            // 依次计算所有用户和当前用户的相似度
            for (User user : userList) {
                // 无标签或者为当前用户自己
                if (StringUtils.isBlank(user.getTags()) || Objects.equals(user.getId(), loginUser.getId())) {
                    continue;
                }
                List<String> userTagList = this.getUserTags(user);
                // 计算分数
                long distance = AlgorithmUtils.minDistance(myTags, userTagList);
                pq.add(new MutablePair<>(user, distance));
            }

            // 按照相似度排序后的 userId 列表
            return pq.stream().map(pair -> this.getSafetyUser(pair.getKey())).limit(10).collect(Collectors.toList());
        }
    }

    /**
     * 匹配相似的用户
     * @param loginUser
     * @return
     */
    @Override
    public List<User> matchUsers(User loginUser) {
        //获取自己的标签
        List<String> myTags = this.getUserTags(loginUser);
        //获取所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);

        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (User user : userList) {
            if (StringUtils.isBlank(user.getTags()) || Objects.equals(user.getId(), loginUser.getId())) {
                continue;
            }
            List<String> userTagList = this.getUserTags(user);
            // 计算分数
            long distance = AlgorithmUtils.minDistance(myTags, userTagList);
            list.add(new MutablePair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(10)
                .collect(Collectors.toList());

        // 按照相似度排序后的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(this::getSafetyUser)
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }

    public List<String> getUserTags(User user){
        String tags = user.getTags();
        ThrowUtils.throwIf(tags==null,new BusinessException(ErrorCode.SYSTEM_ERROR,"无法为您推荐"));
        Gson gson = new Gson();
        return gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    /**
     * 是否为管理员
     *
     * @param loginUser
     * @return
     */
    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser)  {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    @Override
    @Deprecated
    public List<User> searchUserByTagsSQL(List<String> tags){
        ThrowUtils.throwIf(CollectionUtils.isEmpty(tags),new BusinessException(ErrorCode.PARAMS_ERROR));

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        for (String tag :tags) {
            wrapper.like(StringUtils.isNotBlank(tag),User::getTags,tag);
        }

        List<User> users = userMapper.selectList(wrapper);
        return users.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }
}




