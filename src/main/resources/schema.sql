DROP TABLE if exists films_genres;
DROP TABLE if exists likes;
DROP TABLE if exists friends;
DROP TABLE if exists users;
DROP TABLE if exists films;
DROP TABLE if exists genres;
DROP TABLE if exists mpa_ratings;


CREATE  table If not exists users(
id integer generated by default as identity primary key,
email varchar(100) not null unique,
login varchar(100) not null unique,
name varchar(100),
birthday date);


CREATE TABLE if not exists genres(
id integer generated by default as identity primary key,
name varchar(100) not null unique);

CREATE TABLE if not exists mpa_ratings(
id integer generated by default as identity primary key,
name varchar(100) not null unique);

CREATE TABLE If not exists films(
id integer generated by default as identity primary key,
name varchar(100) not null,
description varchar(200),
releaseDate date,
duration integer,
mpa_id integer not null references mpa_ratings(id));

CREATE TABLE if not exists likes(
film_id integer not null references films(id) on delete cascade,
user_id integer not null references users(id));

CREATE TABLE if not exists friends(
user_id integer not null references users(id) on delete cascade,
friend_id integer not null references users(id));

CREATE TABLE if not exists films_genres(
film_id integer not null references films(id) on delete cascade,
genre_id integer not null references genres(id));
