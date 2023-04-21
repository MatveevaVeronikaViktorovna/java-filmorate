package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {

    @NonNull
    final long id;
    @NonNull
    String name;
    final String description;
    final LocalDate releaseDate;
    final int duration;
    @NonNull
    final Mpa mpa;
    Set<Genre> genres = new LinkedHashSet<>();

}
