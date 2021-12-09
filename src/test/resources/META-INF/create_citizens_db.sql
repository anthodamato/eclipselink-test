create table citizen (id integer primary key, first_name varchar(100), last_name varchar(100), version int)

create table address (id integer primary key, name varchar(100), postcode varchar(100), tt boolean)

create sequence seq_gen_sequence start with 1 increment by 1
