INSERT INTO public.films(name, description, release_date, duration, mpa_id) VALUES ('Титаник','Любовь на корабле','1991-09-21',240,1);

INSERT INTO film_genre(film_id, genre_id) VALUES (1, 2);
INSERT INTO film_genre(film_id, genre_id) VALUES (1, 5);

INSERT INTO public.films(name, description, release_date, duration, mpa_id) VALUES ('Убить Билла','Месть как она есть','1995-01-01',180,4);

INSERT INTO film_genre(film_id, genre_id) VALUES (2, 6);

INSERT INTO public.users(email,login,name,birthday) VALUES ('v.v_ivanova@mail.ru','veronaIV','Veronika Matveeva','1989-01-19');
INSERT INTO public.users(email,login,name,birthday) VALUES ('sever_pomnit@mail.ru','sevyndel','Sever Matveev','2021-11-26');
INSERT INTO public.users(email,login,name,birthday) VALUES ('matway@mail.ru','arturM','Artur Matveev','1985-01-02');

INSERT INTO friendship(request_from, request_to, IS_CONFIRMED) VALUES (1, 2,false);
INSERT INTO friendship(request_from, request_to, IS_CONFIRMED) VALUES (1, 3, true);