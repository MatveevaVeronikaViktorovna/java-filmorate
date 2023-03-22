package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {

    long id;
    final String name;
    final String description;
    final LocalDate releaseDate;
    final int duration;
    final Set<Long> likes = new HashSet<>();
}
