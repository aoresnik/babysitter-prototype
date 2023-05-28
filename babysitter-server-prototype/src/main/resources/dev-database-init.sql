
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

create table SCRIPT_SOURCE_SSH_DIR(
     ID int not null auto_increment,
     SCRIPT_SOURCE_ID int not null,
     DIR_NAME varchar(255) not null,
     SSH_CONFIG blob,
     primary key (ID),
     foreign key (SCRIPT_SOURCE_ID) references SCRIPT_SOURCE(ID)
);

create table SCRIPT_EXECUTION(
    ID int not null auto_increment,
    SCRIPT_SOURCE_ID int not null,
    SCRIPT_ID varchar(255) not null,

    scriptRun int not null,
    scriptCompleted int not null,
    exitCode int null,
    errorText varchar(1024) null,
    startTime timestamp null,
    endTime timestamp null,

    primary key (ID),
    foreign key (SCRIPT_SOURCE_ID) references SCRIPT_SOURCE(ID)
);

insert into SCRIPT_SOURCE (NAME) values ('Local dir on server');
insert into SCRIPT_SOURCE (NAME) values ('test-target-vm via SSH');

-- TODO: add support for ${server_root} in dir name
insert into SCRIPT_SOURCE_SERVER_DIR (SCRIPT_SOURCE_ID, DIR_NAME) values (select ID from SCRIPT_SOURCE where NAME='Local dir on server', '../../../../babysitter/scripts');

insert into SCRIPT_SOURCE_SSH_DIR (SCRIPT_SOURCE_ID, DIR_NAME) values (select ID from SCRIPT_SOURCE where NAME='test-target-vm via SSH', 'scripts');
