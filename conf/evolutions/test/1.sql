	# Mailinglist SCHEMA
#--!Ups

CREATE TABLE mailinglist (--id integer NOT NULL DEFAULT nextval('task_id_seq'),
email varchar(255) NOT NULL PRIMARY KEY);


CREATE TABLE USER (email varchar(255) NOT NULL PRIMARY KEY, name varchar(255) NOT NULL, password varchar(255) NOT NULL);


CREATE TABLE mailinglist_member (mailinglist_email varchar(255) NOT NULL, user_email varchar(255) NOT NULL,
                                 FOREIGN KEY(mailinglist_email) REFERENCES mailinglist(email) ON
                                 DELETE CASCADE,
                                 FOREIGN KEY(user_email) REFERENCES USER(email) ON
                                 DELETE CASCADE);


INSERT INTO mailinglist (email)
VALUES ('kuhnen@list.com.br');
INSERT INTO mailinglist (email)
VALUES ('mat@list.com.br');
INSERT INTO USER (email,
                  name,
                  password)
VALUES ('andre@terra.com.br',
        'andre',
        'secret');
INSERT INTO USER (email,
                  name,
                  password)
VALUES ('kuhnen@terra.com.br',
        'kuhnen',
        'secret');
INSERT INTO USER (email,
                  name,
                  password)
VALUES ('mattanja@terra.com.br',
        'matt',
        'secret');
INSERT INTO USER (email,
                  name,
                  password)
VALUES ('test@test.com.br',
        'test',
        'secret') ;

INSERT INTO mailinglist_member (mailinglist_email, user_email) VALUES ('kuhnen@list.com.br', 'test@test.com.br');

INSERT INTO mailinglist_member (mailinglist_email, user_email) VALUES ('kuhnen@list.com.br', 'kuhnen@terra.com.br');


#--!Downs

DROP TABLE IF EXISTS mailinglist_member;


DROP TABLE IF EXISTS USER;


DROP TABLE IF EXISTS mailinglist;

