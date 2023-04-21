package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @NonNull
    final long id;
    final String email;
    final String login;
    String name;
    final LocalDate birthday;

}
