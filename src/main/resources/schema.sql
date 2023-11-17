drop table if exists USERS;
drop table if exists ROL;
drop table if exists USER_ROL_REL;

create table USERS
(
    id    bigint(20) not null auto_increment,
    nombre varchar(50) not null,
    password varchar(100) not null,
    enabled varchar(1) not null default 'Y',
    primary key (id)
);

create table ROL
(
    id    bigint(20) not null auto_increment,
    nombre varchar(50) not null,
    enabled varchar(1) not null default 'Y',
    primary key (id)
);

create table USER_ROL_REL
(
    id bigint(20) not null auto_increment,
    user_id bigint(20) references USERS(id) on delete cascade,
    rol_id bigint(20) references ROL(id) on delete cascade,
    primary key (id)
);