create table TEST(
    ID int not null auto_increment,
    NAME varchar(255) not null,
    primary key (ID)
);

create table SCRIPT_SOURCE(
     ID int not null auto_increment,
     NAME varchar(255) not null,
     primary key (ID)
);

create table SCRIPT_SOURCE_SERVER_DIR(
     ID int not null auto_increment,
     SCRIPT_SOURCE_ID int not null,
     DIR_NAME varchar(255) not null,
     primary key (ID),
     foreign key (SCRIPT_SOURCE_ID) references SCRIPT_SOURCE(ID)
);

insert into SCRIPT_SOURCE (NAME) values ('Local dir on server');

insert into SCRIPT_SOURCE_SERVER_DIR (SCRIPT_SOURCE_ID, DIR_NAME) values (select ID from SCRIPT_SOURCE where NAME='Local dir on server', '/home/user/scripts');

