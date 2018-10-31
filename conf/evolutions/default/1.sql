# --- First database schema

# --- !Ups

create table company (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  constraint pk_company primary key (id)
);

create table computer (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  introduced                    datetime(6),
  discontinued                  datetime(6),
  company_id                    bigint,
  constraint pk_computer primary key (id)
);

create table employee (
  id                            bigint auto_increment not null,
  company_id                    bigint not null,
  name                          varchar(255),
  constraint pk_employee primary key (id)
);

create table uniform (
  id                            bigint auto_increment not null,
  employee_id                   bigint not null,
  name                          varchar(255),
  constraint pk_uniform primary key (id)
);

alter table computer add constraint fk_computer_company_id foreign key (company_id) references company (id) on delete restrict on update restrict;
create index ix_computer_company_id on computer (company_id);

alter table employee add constraint fk_employee_company_id foreign key (company_id) references company (id) on delete restrict on update restrict;
create index ix_employee_company_id on employee (company_id);

alter table uniform add constraint fk_uniform_employee_id foreign key (employee_id) references employee (id) on delete restrict on update restrict;
create index ix_uniform_employee_id on uniform (employee_id);

alter table company add column sys_period_start datetime(6) default now(6);
alter table company add column sys_period_end datetime(6);
create table company_history(
  id                            bigint,
  name                          varchar(255),
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view company_with_history as select * from company union all select * from company_history;

alter table computer add column sys_period_start datetime(6) default now(6);
alter table computer add column sys_period_end datetime(6);
create table computer_history(
  id                            bigint,
  name                          varchar(255),
  introduced                    datetime(6),
  discontinued                  datetime(6),
  company_id                    bigint,
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view computer_with_history as select * from computer union all select * from computer_history;


alter table employee add column sys_period_start datetime(6) default now(6);
alter table employee add column sys_period_end datetime(6);
create table employee_history(
  id                            bigint,
  company_id                    bigint,
  name                          varchar(255),
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view employee_with_history as select * from employee union all select * from employee_history;


alter table uniform add column sys_period_start datetime(6) default now(6);
alter table uniform add column sys_period_end datetime(6);
create table uniform_history(
  id                            bigint,
  employee_id                   bigint,
  name                          varchar(255),
  sys_period_start              datetime(6),
  sys_period_end                datetime(6)
);
create view uniform_with_history as select * from uniform union all select * from uniform_history;

# --- !Downs

alter table computer drop foreign key fk_computer_company_id;
drop index ix_computer_company_id on computer;

alter table employee drop foreign key fk_employee_company_id;
drop index ix_employee_company_id on employee;

alter table uniform drop foreign key fk_uniform_employee_id;
drop index ix_uniform_employee_id on uniform;

drop trigger company_history_upd;
drop trigger company_history_del;
drop view company_with_history;
alter table company drop column sys_period_start;
alter table company drop column sys_period_end;
drop table company_history;

drop table if exists company;

drop trigger computer_history_upd;
drop trigger computer_history_del;
drop view computer_with_history;
alter table computer drop column sys_period_start;
alter table computer drop column sys_period_end;
drop table computer_history;

drop table if exists computer;

drop trigger employee_history_upd;
drop trigger employee_history_del;
drop view employee_with_history;
alter table employee drop column sys_period_start;
alter table employee drop column sys_period_end;
drop table employee_history;

drop table if exists employee;

drop trigger uniform_history_upd;
drop trigger uniform_history_del;
drop view uniform_with_history;
alter table uniform drop column sys_period_start;
alter table uniform drop column sys_period_end;
drop table uniform_history;

drop table if exists uniform;


