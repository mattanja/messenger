# Mailinglist schema
 
# --- !Ups

--CREATE SEQUENCE mailinglist_id_seq;
CREATE TABLE mailinglist (
    --id integer NOT NULL DEFAULT nextval('task_id_seq'),
    name varchar(255)
);
 
# --- !Downs
 
DROP TABLE mailinglist;
--DROP SEQUENCE task_id_seq;