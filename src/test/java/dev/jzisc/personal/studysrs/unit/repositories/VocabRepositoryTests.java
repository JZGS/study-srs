package dev.jzisc.personal.studysrs.unit.repositories;

import dev.jzisc.personal.studysrs.entities.Word;
import dev.jzisc.personal.studysrs.repositories.VocabRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Sql({"/japaneseDB-drop.sql", "/japaneseDB-vocab-schema.sql"})
public class VocabRepositoryTests {

    @Autowired
    private VocabRepository repository;

    @Test
    @DisplayName("Vocabulary repository autowired successfully")
    public void successfullyAutowiredRepo(){
        assertThat(repository, notNullValue());
    }

    @Test
    @DisplayName("Save new word successfully")
    public void successfullySaveNewWord(){
        Word word = repository.saveAndFlush(
                new Word(null, "中学校", "チュウガッコウ", "Junior High School")
        );

        assertThat(word.getWord_id(), notNullValue());
        assertTrue(repository.existsById(word.getWord_id()));
    }

    @Test
    @DisplayName("Update word's information")
    public void successfullyUpdateWord(){
        repository.saveAndFlush(
                new Word(null, "中学校", "チュウガッコウ", "Junior High School")
        );

        Word word = new Word(1, "中学校", "チュウガッコウ", "Example");

        Word updated = repository.saveAndFlush(word);

        assertThat(updated.getMeaning(), equalTo(word.getMeaning()));

    }

    @Test
    @DisplayName("Delete word")
    public void successfullyDeleteWord(){
        Word word = repository.saveAndFlush(
                new Word(null, "中学校", "チュウガッコウ", "Junior High School")
        );

        assertTrue(repository.existsById(1));

        repository.delete(word);
        assertFalse(repository.existsById(1));
    }

}
