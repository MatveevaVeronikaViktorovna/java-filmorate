DROP TABLE IF EXISTS genres, mpa, films, users, film_genre, likes, friendship;

CREATE TABLE IF NOT EXISTS genres (
    genre_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar NOT NULL
); 

CREATE TABLE IF NOT EXISTS mpa (
    mpa_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar NOT NULL
); 

CREATE TABLE IF NOT EXISTS films (
    film_id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar NOT NULL,
    description varchar(200),
    release_date date NOT NULL,
    duration int CHECK (duration > 0),
    mpa_id int NOT NULL REFERENCES mpa(mpa_id)
); 

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, 
    email varchar NOT NULL,
    login varchar NOT NULL,
    name varchar NOT NULL,
    birthday date
); 

CREATE TABLE IF NOT EXISTS film_genre (
	film_id BIGINT NOT NULL REFERENCES films(film_id),
    genre_id int NOT NULL REFERENCES genres(genre_id),
    PRIMARY KEY (film_id, genre_id)
); 

CREATE TABLE IF NOT EXISTS likes (
    film_id BIGINT NOT NULL REFERENCES films (film_id),
    user_id BIGINT NOT NULL REFERENCES users (user_id),
    PRIMARY KEY (film_id, user_id)
); 

CREATE TABLE IF NOT EXISTS friendship (
    request_from BIGINT NOT NULL REFERENCES users (user_id),
    request_to BIGINT NOT NULL REFERENCES users (user_id),
    is_confirmed boolean NOT NULL,
    PRIMARY KEY (request_from, request_to)
); 




