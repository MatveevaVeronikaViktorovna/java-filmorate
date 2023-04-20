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

SELECT f.film_id,
              f.name,
              f.description,
              f.release_date,
              f.duration,
              f.mpa_id
FROM film AS f
FULL OUTER JOIN like AS l ON f.film_id = l.film_id
GROUP BY f.film_id
ORDER BY COUNT(l.user_id) DESC
LIMIT 10; -- Получение топ-10 наиболее популярных фильмов

SELECT * 
FROM user; -- Получение всех пользователей

SELECT * 
FROM user
WHERE user_id = 1; -- Получение пользователя с id = 1

SELECT *
FROM user
WHERE user_id IN  (SELECT friend_id
                  FROM friendship
                  WHERE user_id = 1
                  AND is_confirmed = true
                  UNION
                  SELECT user_id
                  FROM friendship
                  WHERE friend_id = 1); -- Получение списка друзей пользователя с id = 1

```