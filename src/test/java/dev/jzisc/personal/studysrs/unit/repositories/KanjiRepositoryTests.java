package dev.jzisc.personal.studysrs.unit.repositories;

import dev.jzisc.personal.studysrs.entities.Kanji;
import dev.jzisc.personal.studysrs.repositories.KanjiRepository;
import org.hamcrest.core.IsEqual;
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
                new Kanji(null, "一", "One", null, null)
        );
        assertThat(savedKanji.getKanji_id(), notNullValue());
        assertTrue(repository.existsById(savedKanji.getKanji_id()));
//        assertNotNull(kanji.getKanji_id());
//        assertThat("一", equalToIgnoringCase(savedKanji.getKanji()));
//        assertEquals("一", savedKanji.getKanji());
//        assertThat("One", equalToIgnoringCase(savedKanji.getMeaning()));
//        assertEquals("One", savedKanji.getMeaning());
    }

    @Test
    @DisplayName("Error while saving a duplicated kanji")
    public void failedSavingDuplicatedKanji(){
        repository.saveAndFlush(
                new Kanji(null, "一", "One", null, null)
        );

        assertThrows(
            DataIntegrityViolationException.class,
            () -> {
                repository.saveAndFlush(
                        new Kanji(null, "一", "One", null, null)
                );
            }
        );
    }

    @Test
    @DisplayName("Save new Kanji with confusions")
    public void successfullySaveKanjiAndConfusions(){
        Kanji sacrifice = repository.saveAndFlush(
                new Kanji(null, "牲", "Sacrifice", null, null)
        );
        Kanji gender = new Kanji(null, "性", "Personality/Gender", null, null);
        gender.addConfusion(sacrifice);
        repository.saveAndFlush(gender);

        Kanji savedSacrifice = repository.findById((short) 1).get();
        Kanji savedGender = repository.findById((short) 2).get();

        assertThat(savedGender.getConfusions(), hasSize(1));
        assertThat(savedSacrifice.getConfusions(), hasSize(1));

//        assertEquals(1, savedGender.getConfusions().size());
//        assertEquals(1, savedSacrifice.getConfusions().size());
        assertThat(savedGender.getConfusions(), containsInAnyOrder(savedSacrifice));
        assertThat(savedSacrifice.getConfusions(), containsInAnyOrder(savedGender));
//        assertTrue(
//        savedGender.getConfusions().contains(savedSacrifice)
//                    && savedSacrifice.getConfusions().contains(savedGender)
//        );
    }

    @Test
    @DisplayName("Error while saving a duplicate confusion")
    public void failedSavingDuplicatedConfusion(){
        Kanji sacrifice = repository.saveAndFlush(
                new Kanji(null, "牲", "Sacrifice", null, null)
        );

        Kanji gender = new Kanji(null, "性", "Personality/Gender", null, null);
        gender.addConfusion(sacrifice);
        repository.saveAndFlush(gender);

        sacrifice.getPriorKanjisConfused().add(gender);

        assertThrows(
            DataIntegrityViolationException.class,
            () ->
                repository.saveAndFlush(sacrifice)
        );
    }

    @Test
    @DisplayName("Update kanji information")
    public void successfullyUpdateKanji(){
        Kanji kanji = repository.saveAndFlush(
                new Kanji(null, "牲", "Sacrifice", null, null)
        );
        kanji.setMeaning("Example");
        Kanji updated = repository.saveAndFlush(kanji);
        assertThat(updated.getMeaning(), equalTo(kanji.getMeaning()));
//        assertEquals("Example", repository.findById((short) 1).get().getMeaning());
    }

    @Test
    @DisplayName("Delete kanji")
    public void successfullyDeleteKanji(){
        Kanji kanji = repository.saveAndFlush(
                new Kanji(null, "牲", "Sacrifice", null, null)
        );

        assertTrue(repository.existsById((short) 1));

        repository.delete(kanji);
        assertFalse(repository.existsById((short) 1));
    }

}
