CREATE SCHEMA `config_server_console` DEFAULT CHARACTER SET utf8mb4 ;


create table tb_role
(
	id bigint auto_increment,
	name varchar(20) not null,
	created_datetime datetime null,
	last_modify_datetime datetime null,
	constraint tb_role_pk
		primary key (id)
)
comment '角色表';


create table tb_user
(
	id bigint auto_increment,
	username varchar(32) not null,
	password varchar(16) not null,
	role_id bigint not null,
	created_datetime datetime null,
	last_modify_datetime datetime null,
	constraint tb_user_pk
		primary key (id)
)
comment '用户表';

create table tb_user_application
(
	id bigint auto_increment,
	user_id bigint not null,
	application varchar(32) not null,
	constraint tb_user_application_pk
		primary key (id)
)
comment '用户-应用管理关系表';


