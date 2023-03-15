create
    database if not exists friends_mate;

use
    friends_mate;

select team.id as id,team.name as name,team.description as description,team.maxNum as maxNum,
       team.userId as userId ,team.status as status,team.createTime as createTime,
       team.expireTime as expireTime,team.updateTime as updateTime,
       concat(user.id,user.username,user.userAccount,user.avatarUrl,user.gender,user.phone,
           user.email,user.tags,user.userStatus,user.createTime,user.updateTime,user.userRole,user.planetCode) as createUser,
    count(team.id) as hasJoinNum         from team left join user on team.userId = user.id left join user_team on team.id=user_team.teamId
                                         where 1=1
                                            and team.name like ('%','测试','%')
                    and team.status = 0                           group by team.id;


select team.id as id,team.name as name,team.description as description,team.maxNum as maxNum, team.userId as
    userId ,team.status as status,team.createTime as createTime, team.expireTime as expireTime,team.updateTime
        as updateTime, concat(user.id,user.username,user.userAccount,user.avatarUrl,user.gender,user.phone,
            user.email,user.tags,user.userStatus,user.createTime,user.updateTime,user.userRole,user.planetCode)
            as createUser, count(team.id) as hasJoinNum from team left join user on team.userId = user.id
                left join user_team on team.id=user_team.teamId where 1=1 and team.description like concat('%','测试','%') or
            team.name like concat('%','测试','%') and team.status =2 group by team.id;
-- 用户表
create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    username     varchar(256) not null comment '用户昵称',
    userAccount  varchar(256) null comment '账号',
    avatarUrl    varchar(1024) null comment '用户头像',
    gender       tinyint null comment '性别',
    userPassword varchar(512)       not null comment '密码',
    phone        varchar(128) null comment '电话',
    email        varchar(512) null comment '邮箱',
    userStatus   int      default 0 not null comment '状态 0 - 正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0 not null comment '是否删除',
    userRole     int      default 0 not null comment '用户角色 0 - 普通用户 1 - 管理员',
    planetCode   varchar(512) null comment '星球编号',
    tags         varchar(1024) null comment '标签 json 列表'
) comment '用户';

-- 队伍表
create table team
(
    id          bigint auto_increment comment 'id' primary key,
    name        varchar(256)       not null comment '队伍名称',
    description varchar(1024) null comment '描述',
    maxNum      int      default 1 not null comment '最大人数',
    expireTime  datetime null comment '过期时间',
    userId      bigint comment '用户id（队长 id）',
    status      int      default 0 not null comment '0 - 公开，1 - 私有，2 - 加密',
    password    varchar(512) null comment '密码',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete    tinyint  default 0 not null comment '是否删除'
) comment '队伍';

-- 用户队伍关系
create table user_team
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint comment '用户id',
    teamId     bigint comment '队伍id',
    joinTime   datetime null comment '加入时间',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete   tinyint  default 0 not null comment '是否删除'
) comment '用户队伍关系';


-- 标签表（可以不创建，因为标签字段已经放到了用户表中）
create table tag
(
    id         bigint auto_increment comment 'id'
        primary key,
    tagName    varchar(256) null comment '标签名称',
    userId     bigint null comment '用户 id',
    parentId   bigint null comment '父标签 id',
    isParent   tinyint null comment '0 - 不是, 1 - 父标签',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete   tinyint  default 0 not null comment '是否删除',
    constraint uniIdx_tagName
        unique (tagName)
) comment '标签';

create index idx_userId
    on tag (userId);

-- 队伍表
create table team
(
    id          bigint auto_increment comment 'id' primary key,
    name        varchar(256)       not null comment '队伍名称',
    description varchar(1024) null comment '描述',
    maxNum      int      default 1 not null comment '最大人数',
    expireTime  datetime null comment '过期时间',
    userId      bigint comment '用户id（队长 id）',
    status      int      default 0 not null comment '0 - 公开，1 - 私有，2 - 加密',
    password    varchar(512) null comment '密码',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete    tinyint  default 0 not null comment '是否删除'
) comment '队伍';

-- 用户队伍关系
create table user_team
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint comment '用户id',
    teamId     bigint comment '队伍id',
    joinTime   datetime null comment '加入时间',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete   tinyint  default 0 not null comment '是否删除'
) comment '用户队伍关系';


insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('廖鹏', '钱靖琪', 'www.roxann-marks.net', '1', '15747621697', 'malissa.schmitt@yahoo.com', '0', 0, '段弘文', '0', '76', 'Q');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('程立辉', '顾擎苍', 'www.dannie-heidenreich.info', '1', '17822795834', 'maria.boyle@gmail.com', '0', 0, '毛明轩', '0', '374', 'cg');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('钱明轩', '谭鸿煊', 'www.delbert-cartwright.io', '0', '15907975837', 'keven.kiehn@yahoo.com', '0', 0, '万子涵', '0', '35', 'Icon');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('冯擎宇', '方鹤轩', 'www.wayne-skiles.net', '1', '15581254927', 'gordon.murphy@yahoo.com', '0', 0, '谭烨华', '0', '354571393', 'bc');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('尹博涛', '万苑博', 'www.sasha-hamill.info', '1', '17706788940', 'candance.hand@hotmail.com', '0', 0, '张荣轩', '0', '9011643', 'SAS');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('陆明辉', '黎耀杰', 'www.shamika-hermann.net', '1', '14592863046', 'svetlana.paucek@gmail.com', '0', 0, '李鹏', '0', '373', 'F#');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('许擎宇', '赵烨伟', 'www.leo-kertzmann.org', '1', '17753821678', 'mckinley.smith@hotmail.com', '0', 0, '谢晟睿', '0', '40920964', 'OCaml');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('石鑫磊', '吴建辉', 'www.chantell-lehner.net', '0', '13846423508', 'deb.sporer@gmail.com', '0', 0, '莫展鹏', '0', '553090', 'MQL5');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('姜金鑫', '孙煜祺', 'www.tierra-bode.io', '1', '17146876214', 'nichelle.ortiz@hotmail.com', '0', 0, '许建辉', '0', '724', 'JavaScript');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('孙绍齐', '秦瑞霖', 'www.stephine-hirthe.com', '1', '17594206421', 'lindy.abshire@gmail.com', '0', 0, '郭弘文', '0', '591', 'bc');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('许明杰', '宋明辉', 'www.alise-yundt.org', '0', '15977084341', 'tracey.cruickshank@gmail.com', '0', 0, '戴绍齐', '0', '4374047', 'Objective-C');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('江金鑫', '钱志泽', 'www.ardelia-kuvalis.com', '1', '15974257598', 'carlton.erdman@gmail.com', '0', 0, '熊远航', '0', '24565', 'Visual Basic');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('邓哲瀚', '姜思源', 'www.dalila-rempel.info', '0', '17189973677', 'johnny.murazik@yahoo.com', '0', 0, '贾荣轩', '0', '2368127', 'Dart');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('胡果', '吕烨磊', 'www.tomas-huel.name', '1', '13818530445', 'elly.hills@gmail.com', '0', 0, '丁楷瑞', '0', '6', 'CLIPS');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('薛烨霖', '杨思淼', 'www.rea-brekke.co', '1', '15865362557', 'jasmin.batz@yahoo.com', '0', 0, '吴黎昕', '0', '728', 'Forth');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('魏浩轩', '侯鸿煊', 'www.marty-zulauf.name', '0', '18161911390', 'suzie.hammes@gmail.com', '0', 0, '韦旭尧', '0', '5758470', 'SAS');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('杨哲瀚', '姜雨泽', 'www.cherrie-reilly.name', '1', '14702360543', 'estell.kiehn@hotmail.com', '0', 0, '郑立辉', '0', '47', 'Dart');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('田哲瀚', '钱绍辉', 'www.noma-marquardt.co', '1', '15749987306', 'lady.daugherty@hotmail.com', '0', 0, '姚钰轩', '0', '613', 'ML');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('龙浩然', '邓明杰', 'www.jamie-bradtke.info', '1', '15841265963', 'craig.carter@hotmail.com', '0', 0, '秦昊天', '0', '15849448', 'LabVIEW');
insert into `user` (`userAccount`, `username`, `avatarUrl`, `gender`, `phone`, `email`, `userStatus`, `isDelete`, `userPassword`, `userRole`, `planetCode`, `tags`) values ('陈凯瑞', '严鑫磊', 'www.jan-schowalter.com', '0', '15661876168', 'rudolph.jakubowski@hotmail.com', '0', 0, '林黎昕', '0', '1092966243', 'Crystal');