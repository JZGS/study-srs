package dev.jzisc.personal.studysrs.unit.repositories;

import dev.jzisc.personal.studysrs.model.Kanji;
import dev.jzisc.personal.studysrs.repository.KanjiRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
@Sql({"/japaneseDB-drop.sql", "/japaneseDB-kanji-schema.sql"})
public class KanjiRepositoryTests {

    @Autowired
    private KanjiRepository repository;

    @Test
    @DisplayName("Kanji repository autowired successfully")
    public void successfullyAutowiredRepo(){
        assertThat(repository, notNullValue());
    }

    @Test
    @DisplayName("Save new Kanji without any kanji confusions")
    public void successfullySaveNewKanji(){
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
    public void failedSavingDuplicatedKanji(){
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
    public void successfullySaveKanjiAndConfusions(){
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
    public void successfullyUpdateKanji(){
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
    public void successfullyDeleteKanji(){
        Kanji kanji = repository.saveAndFlush(
                            new Kanji()
                                .setKanji("牲")
                                .setMeaning("Sacrifice")
                      );

        assertTrue(repository.existsById((short) 1));

        repository.delete(kanji);
        assertFalse(repository.existsById((short) 1));
    }

}
