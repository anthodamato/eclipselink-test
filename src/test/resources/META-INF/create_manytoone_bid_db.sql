create sequence SEQ_GEN_SEQUENCE start with 1 increment by 1

create table employee (id integer primary key, name varchar(100), salary decimal(19,2), department_id int)

create table department (id integer primary key, name varchar(100))
