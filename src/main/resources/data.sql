INSERT INTO public.genres(name) VALUES ('Комедия');
INSERT INTO public.genres(name) VALUES ('Драма');
INSERT INTO public.genres(name) VALUES ('Мультфильм');
INSERT INTO public.genres(name) VALUES ('Триллер');
INSERT INTO public.genres(name) VALUES ('Документальный');
INSERT INTO public.genres(name) VALUES ('Боевик');

INSERT INTO public.mpa(name) VALUES ('G');
INSERT INTO public.mpa(name) VALUES ('PG');
INSERT INTO public.mpa(name) VALUES ('PG-13');
INSERT INTO public.mpa(name) VALUES ('R');
INSERT INTO public.mpa(name) VALUES ('NC-17');

INSERT INTO public.films(name, description, release_date, duration, mpa_id) VALUES ('Титаник','Любовь на корабле','1991-09-21',240,1);

INSERT INTO film_genre(film_id, genre_id) VALUES (1, 2);
INSERT INTO film_genre(film_id, genre_id) VALUES (1, 5);

INSERT INTO public.films(name, description, release_date, duration, mpa_id) VALUES ('Убить Билла','Месть как она есть','1995-01-01',180,4);

INSERT INTO film_genre(film_id, genre_id) VALUES (2, 6);