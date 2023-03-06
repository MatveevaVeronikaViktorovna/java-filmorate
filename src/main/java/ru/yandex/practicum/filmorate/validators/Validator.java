package ru.yandex.practicum.filmorate.validators;

public interface Validator<T> {

    boolean isValid(T value);
}
