
create table COMMAND_SOURCE(
     ID int not null auto_increment,
     NAME varchar(255) not null,
     primary key (ID)
);

create table COMMAND_SOURCE_SERVER_DIR(
     ID int not null auto_increment,
     COMMAND_SOURCE_ID int not null,
     DIR_NAME varchar(255) not null,
     primary key (ID),
     foreign key (COMMAND_SOURCE_ID) references COMMAND_SOURCE(ID)
);

create table COMMAND_SOURCE_SSH_DIR(
     ID int not null auto_increment,
     COMMAND_SOURCE_ID int not null,
     DIR_NAME varchar(255) not null,
     SSH_CONFIG blob,
     primary key (ID),
     foreign key (COMMAND_SOURCE_ID) references COMMAND_SOURCE(ID)
);

create table COMMAND(
    ID int not null auto_increment,
    COMMAND_SOURCE_ID int not null,
    NAME varchar(255) not null,
    SCRIPT varchar(255) not null,
    primary key (ID),
    foreign key (COMMAND_SOURCE_ID) references COMMAND_SOURCE(ID)
);

create table COMMAND_EXECUTION(
    ID int not null auto_increment,
    COMMAND_SOURCE_ID int not null,
    COMMAND_ID varchar(255) not null,

    commandRun int not null,
    commandCompleted int not null,
    exitCode int null,
    errorText varchar(1024) null,
    startTime timestamp null,
    endTime timestamp null,

    primary key (ID),
    foreign key (COMMAND_SOURCE_ID) references COMMAND_SOURCE(ID)
);

insert into COMMAND_SOURCE (NAME) values ('Local dir on server');
insert into COMMAND_SOURCE (NAME) values ('test-target-vm via SSH');

-- TODO: add support for ${server_root} in dir name
insert into COMMAND_SOURCE_SERVER_DIR (COMMAND_SOURCE_ID, DIR_NAME) values (select ID from COMMAND_SOURCE where NAME='Local dir on server', '../../../../babysitter/scripts');

insert into COMMAND_SOURCE_SSH_DIR (COMMAND_SOURCE_ID, DIR_NAME) values (select ID from COMMAND_SOURCE where NAME='test-target-vm via SSH', 'scripts');
