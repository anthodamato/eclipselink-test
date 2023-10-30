create sequence SEQ_GEN_SEQUENCE_STOVE start with 1 increment by 1

create table stove (id integer primary key, model varchar(100), numberOfBurners int, induction boolean)
