# java-filmorate
Сервис, облегчающий поиск подходящего пользователю фильма.

## Диаграмма базы данных:
![alt text](https://github.com/MatveevaVeronikaViktorovna/java-filmorate/blob/add-database/src/main/resources/ERD/filmorate_ERD.png?raw=true)

### Примеры запросов для основных операций приложения:
```sql
SELECT * 
FROM film; -- Получение всех фильмов

SELECT * 
FROM film 
WHERE film_id = 1; -- Получение фильма с id = 1

SELECT *
FROM film
WHERE film_id IN (SELECT film_id
                  FROM like
                  GROUP BY film_id
                  ORDER BY COUNT(user_id) DESC
                  LIMIT 10); -- Получение топ-10 наиболее популярных фильмов

SELECT * 
FROM user; -- Получение всех пользователей

SELECT * 
FROM user
WHERE user_id = 1; -- Получение пользователя с id = 1
```