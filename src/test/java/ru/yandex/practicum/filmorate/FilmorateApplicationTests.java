package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Sql(value = {"classpath:test_data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"classpath:drop_test_data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {

    private final MpaDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;


    @Test
    public void testFindMpaById() {
        Optional<Mpa> mpaOptional = mpaStorage.findById(1);
        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G")
                );
    }

    @Test
    public void testFindAllMpa() {
        List<Mpa> allMpa = mpaStorage.findAll();
        assertThat(allMpa).size().isEqualTo(5);
    }

    @Test
    public void testFindGenreById() {
        Optional<Genre> genreOptional = genreStorage.findById(1);
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }

    @Test
    public void testFindAllGenres() {
        List<Genre> allGenres = genreStorage.findAll();
        assertThat(allGenres)
                .size().isEqualTo(6);
    }

    @Test
    public void testFindGenresByFilmId() {
        List<Genre> genres = genreStorage.findByFilmId(1L);
        assertThat(genres)
                .size().isEqualTo(2);
    }

    @Test
    public void testFindFilmById() {
        Optional<Film> filmOptional = filmStorage.findById(1L);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "Титаник")
                );
    }

    @Test
    public void testFindAllFilms() {
        List<Film> films = filmStorage.findAll();
        assertThat(films)
                .size().isEqualTo(2);
    }

    @Test
    public void testCreateFilm() {
        Film film = filmStorage.create(new Film(3, "Крестный отец", "На что ты пойдешь ради семьи?",
                LocalDate.of(1995, 12, 27), 320, mpaStorage.findById(1).get()));
        assertThat(film)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "Крестный отец")
                .hasFieldOrPropertyWithValue("description", "На что ты пойдешь ради семьи?")
                .hasFieldOrPropertyWithValue("duration", 320);
    }

    @Test
    public void testUpdateFilm() {
        Film film = filmStorage.findById(1L).get();
        film.setDescription("Все умрут, а я останусь");
        Film updatedFilm = filmStorage.update(film);
        assertThat(updatedFilm)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "Титаник")
                .hasFieldOrPropertyWithValue("description", "Все умрут, а я останусь");
    }

    @Test
    public void testAddLike() {
        Film film = filmStorage.findById(1L).get();
        User user = userStorage.findById(1L).get();
        Film filmWithOneLike = filmStorage.addLike(film.getId(), user.getId());
        Set<Long> likes = filmWithOneLike.getLikes();
        assertThat(likes)
                .size().isEqualTo(1);
    }

    @Test
    public void testDeleteLike() {
        Film film = filmStorage.findById(1L).get();
        User user = userStorage.findById(1L).get();
        Film filmWithOneLike = filmStorage.addLike(film.getId(), user.getId());
        Film filmWithoutLikes = filmStorage.deleteLike(filmWithOneLike.getId(), user.getId());
        Set<Long> likes = filmWithoutLikes.getLikes();
        assertThat(likes)
                .size().isEqualTo(0);
    }

    @Test
    public void testFindMostPopularFilms() {
        Film film = filmStorage.findById(1L).get();
        User user = userStorage.findById(1L).get();
        Film filmWithOneLike = filmStorage.addLike(film.getId(), user.getId());
        List<Film> popularFilms = filmStorage.findMostPopularFilms(3);
        assertThat(popularFilms)
                .size().isEqualTo(2);
        assertThat(popularFilms.get(0))
                .hasFieldOrPropertyWithValue("name", "Титаник");
    }

    @Test
    public void testFindUserById() {
        Optional<User> userOptional = userStorage.findById(1L);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "veronaIV")
                );
    }

    @Test
    public void testFindAllUsers() {
        List<User> users = userStorage.findAll();
        assertThat(users)
                .size().isEqualTo(3);
    }

    @Test
    public void testCreateUser() {
        User user = userStorage.create(new User(4, "cat@mail.ru", "kitty", "Cat Felix",
                LocalDate.of(2020, 12, 12)));
        assertThat(user)
                .isNotNull()
                .hasFieldOrPropertyWithValue("email", "cat@mail.ru")
                .hasFieldOrPropertyWithValue("login", "kitty")
                .hasFieldOrPropertyWithValue("name", "Cat Felix");
    }

    @Test
    public void testUpdateUser() {
        User user = userStorage.findById(1L).get();
        user.setLogin("VeronaM");
        User updatedUser = userStorage.update(user);
        assertThat(updatedUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "Veronika Matveeva")
                .hasFieldOrPropertyWithValue("login", "VeronaM");
    }


    @Test
    public void testAddFriend() {
        User user = userStorage.findById(1L).get();
        User friend = userStorage.findById(2L).get();
        User userWithOneFriend = userStorage.addFriend(user.getId(), friend.getId());
        Set<Long> friends = userWithOneFriend.getFriends();
        assertThat(friends)
                .size().isEqualTo(1);
    }

    @Test
    public void testDeleteFriend() {
        User user = userStorage.findById(1L).get();
        User friend = userStorage.findById(2L).get();
        User userWithOneFriend = userStorage.addFriend(user.getId(), friend.getId());
        User userWithoutFriends = userStorage.deleteFriend(userWithOneFriend.getId(), friend.getId());
        Set<Long> friends = userWithoutFriends.getFriends();
        assertThat(friends)
                .size().isEqualTo(0);
    }

    @Test
    public void testGetFriends() {
        User user = userStorage.findById(1L).get();
        User friend = userStorage.findById(2L).get();
        User userWithOneFriend = userStorage.addFriend(user.getId(), friend.getId());
        List<User> userFriends = userStorage.getFriends(userWithOneFriend.getId());
        assertThat(userFriends)
                .size().isEqualTo(1);
        assertThat(userFriends.get(0))
                .hasFieldOrPropertyWithValue("name", "Sever Matveev");
    }

    @Test
    public void testGetCommonFriends() {
        User user = userStorage.findById(1L).get();
        User friend = userStorage.findById(2L).get();
        User commonFriend = userStorage.findById(3L).get();
        User userAddCommonFriend = userStorage.addFriend(user.getId(), commonFriend.getId());
        User friendAddCommonFriend = userStorage.addFriend(friend.getId(), commonFriend.getId());
        List<User> commonFriends = userStorage.getCommonFriends(userAddCommonFriend.getId(), friendAddCommonFriend.getId());
        assertThat(commonFriends)
                .size().isEqualTo(1);
        assertThat(commonFriends.get(0))
                .hasFieldOrPropertyWithValue("name", "Artur Matveev");
    }

}