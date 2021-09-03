package dev.jzisc.personal.studysrs.tests.unit.repositories;

import dev.jzisc.personal.studysrs.model.Kanji;
import dev.jzisc.personal.studysrs.repository.KanjiRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@Sql({"/japaneseDB-drop.sql", "/japaneseDB-kanji-schema.sql"})
@DisplayName("Kanji Repository Unit Tests")
public class KanjiRepositoryTests {

    @Autowired
    private KanjiRepository repository;

    @Test
    @DisplayName("Kanji repository autowired successfully")
    void successfullyAutowiredRepo(){
        assertThat(repository, notNullValue());
    }

    @Test
    @DisplayName("Save new Kanji without any kanji confusions")
    void successfullySaveNewKanji(){
        Kanji savedKanji = repository.saveAndFlush(
                                new Kanji()
                                    .setKanji("一")
                                    .setMeaning("One")
                            );
        assertThat(savedKanji.getKanji_id(), notNullValue());
        assertTrue(repository.existsById(savedKanji.getKanji_id()));
    }

    @Test
    @DisplayName("Error while saving a duplicated kanji")
    void failedSavingDuplicatedKanji(){
        repository.saveAndFlush(
                new Kanji()
                .setKanji("一")
                .setMeaning("One")
        );

        assertThrows(
            DataIntegrityViolationException.class,
            () -> {
                repository.saveAndFlush(
                        new Kanji()
                                .setKanji("一")
                                .setMeaning("One")
                );
            }
        );
    }

    @Test
    @DisplayName("Save new Kanji with confusions")
    void successfullySaveKanjiAndConfusions(){
        Kanji sacrifice = repository.saveAndFlush(
                                new Kanji()
                                    .setKanji("牲")
                                    .setMeaning("Sacrifice")
                          );
        Kanji gender = new Kanji().setKanji("性").setMeaning("Personality/Gender");
        gender.addConfusion(sacrifice);
        repository.saveAndFlush(gender);

        Kanji savedSacrifice = repository.findById((short) 1).get();
        Kanji savedGender = repository.findById((short) 2).get();

        assertThat(savedGender.getConfusions(), hasSize(1));
        assertThat(savedSacrifice.getConfusions(), hasSize(1));

        assertThat(savedGender.getConfusions(), containsInAnyOrder(savedSacrifice));
        assertThat(savedSacrifice.getConfusions(), containsInAnyOrder(savedGender));
    }

    @Test
    @DisplayName("Update kanji information")
    void successfullyUpdateKanji(){
        Kanji kanji = repository.saveAndFlush(
                            new Kanji()
                                .setKanji("牲")
                                .setMeaning("Sacrifice")
                      );
        kanji.setMeaning("Example");
        Kanji updated = repository.saveAndFlush(kanji);
        assertThat(updated.getMeaning(), equalTo(kanji.getMeaning()));
    }

    @Test
    @DisplayName("Delete kanji")
    void successfullyDeleteKanji(){
        Kanji kanji = repository.saveAndFlush(
                            new Kanji()
                                .setKanji("牲")
                                .setMeaning("Sacrifice")
                      );

        assertTrue(repository.existsById((short) 1));

        repository.delete(kanji);
        assertFalse(repository.existsById((short) 1));
    }

    @ParameterizedTest
    @MethodSource("confusedKanjis")
    @Sql({"/japaneseDB-kanji-data-insertion.sql"})
    @DisplayName("Find kanji object by its kanji character")
    void successfullyFindKanjiByKanjiCharacter(List<String> kanjis){
        String firstKanji = kanjis.get(0);
        String secondKanji = kanjis.get(1);

        Optional<Kanji> optional = repository.findByKanji(firstKanji);
        assertTrue(optional.isPresent());

        Kanji kanji = optional.get();
        assertThat(kanji, hasProperty("kanji", equalTo(firstKanji)));
        assertThat(kanji.getConfusions(), hasSize(1));
        assertThat(kanji.getConfusions().get(0), hasProperty("kanji", equalTo(secondKanji)));
    }

    @ParameterizedTest
    @MethodSource("sameMeaningKanjis")
    @Sql({"/japaneseDB-kanji-data-insertion.sql"})
    @DisplayName("Find kanji object by a meaning regex")
    void successfullyFindKanjiListByMeaning(List<String> kanjisAndMeaning){
        String kanji1 = kanjisAndMeaning.get(0);
        String kanji2 = kanjisAndMeaning.get(1);
        String meaning = kanjisAndMeaning.get(2);

        String riverRegex = "(^|.*/)" + meaning + "(/.*$|$)";
        Kanji river1 = repository.findByKanji(kanji1).get();
        Kanji river2 = repository.findByKanji(kanji2).get();

        List<Kanji> riverKanjis = repository.findByMeaningRegex(riverRegex);

        assertThat(riverKanjis, hasSize(2));
        assertThat(riverKanjis, containsInAnyOrder(river1, river2));
    }

    @Test
    @Sql({"/japaneseDB-kanji-data-insertion.sql"})
    @DisplayName("Assert that find by meaning doesn't return partial-word meaning")
    void successfullyFindFullWordMeanings(){
        Kanji cutOff = repository.findByKanji("絶").get();
        List<Kanji> cutKanjis = repository.findByMeaningRegex("(^|.*/)Cut(/.*$|$)");

        assertThat(cutKanjis, hasSize(1));
        assertThat(cutKanjis, not(containsInAnyOrder(cutOff)));
    }

    @ParameterizedTest
    @MethodSource("allKanjisAdded")
    @Sql({"/japaneseDB-kanji-data-insertion.sql"})
    @DisplayName("Successfully confirm a kanji existence by its character")
    void successfullyExistsKanjiByKanjiString(String kanji){
        assertTrue(repository.existsByKanji(kanji));
    }

    static List<List<String>> sameMeaningKanjis(){
        return Arrays.asList(
                Arrays.asList( "川", "河", "River" ),
                Arrays.asList( "自", "己", "Self" ),
                Arrays.asList( "本", "元", "Origin" )
        );
    }

    static List<List<String>> confusedKanjis(){
        return Arrays.asList(
                Arrays.asList( "牲", "性" ),
                Arrays.asList( "員", "買" ),
                Arrays.asList( "順", "訓" )
        );
    }

    static List<String> allKanjisAdded(){
        return Arrays.asList("牲", "性", "員", "買", "順", "訓", "川", "河", "自", "己", "本", "元", "切", "絶");
    }

}
