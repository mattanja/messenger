# Mailinglist schema

# --- !Ups

create table mailinglist (
    email varchar(255) not null primary key
);

create table user (
  email                     varchar(255) not null primary key,
  name                      varchar(255) not null,
  password                  varchar(255) not null
);

create table mailinglist_member (
  mailinglist_email         varchar(255) not null,
  user_email                varchar(255) not null,
  primary key(mailinglist_email, user_email),
  foreign key(mailinglist_email) references mailinglist(email) on delete cascade,
  foreign key(user_email)   references user(email) on delete cascade
);


# --- !Downs

drop table if exists mailinglist_member;
drop table if exists user;
drop table if exists mailinglist;
