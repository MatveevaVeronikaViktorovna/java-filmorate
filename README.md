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
```