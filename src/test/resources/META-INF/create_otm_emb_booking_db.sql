create sequence SEQ_GEN_SEQUENCE_HC start with 1 increment by 1

create table hotelbookingdetail (dateof date primary key, room_number int not null, price float not null)

create table hotelcustomer (id integer primary key, name varchar(100))

create table hotelbookingdetail_hotelcustomer (dateof date, room_number int, customers_id integer)
