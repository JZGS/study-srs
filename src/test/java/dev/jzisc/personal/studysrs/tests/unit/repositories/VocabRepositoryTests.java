package dev.jzisc.personal.studysrs.tests.unit.repositories;

import dev.jzisc.personal.studysrs.model.Word;
import dev.jzisc.personal.studysrs.repository.VocabRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Sql({"/japaneseDB-drop.sql", "/japaneseDB-vocab-schema.sql"})
@DisplayName("Vocabulary Repository Unit Tests")
public class VocabRepositoryTests {

    @Autowired
    private VocabRepository repository;

    @Test
    @DisplayName("Vocabulary repository autowired successfully")
    void successfullyAutowiredRepo(){
        assertThat(repository, notNullValue());
    }

    @Test
    @DisplayName("Save new word successfully")
    void successfullySaveNewWord(){
        Word word = repository.saveAndFlush(
                new Word()
                .setWord("中学校")
                .setReading("チュウガッコウ")
                .setMeaning("Junior High School")
        );

        assertThat(word.getWord_id(), notNullValue());
        assertTrue(repository.existsById(word.getWord_id()));
    }

    @Test
    @DisplayName("Update word's information")
    void successfullyUpdateWord(){
        Word saved = repository.saveAndFlush(
                new Word()
                    .setWord("中学校")
                    .setReading("チュウガッコウ")
                    .setMeaning("Junior High School")
        );

        Word word = new Word()
                .setWord_id(saved.getWord_id())
                .setWord("中学校")
                .setReading("チュウガッコウ")
                .setMeaning("Junior High School");

        Word updated = repository.saveAndFlush(word);

        assertThat(updated.getMeaning(), equalTo(word.getMeaning()));

    }

    @Test
    @DisplayName("Delete word")
    void successfullyDeleteWord(){
        Word word = repository.saveAndFlush(
                        new Word()
                            .setWord("中学校")
                            .setReading("チュウガッコウ")
                            .setMeaning("Junior High School")
                    );

        assertTrue(repository.existsById(1));

        repository.delete(word);
        assertFalse(repository.existsById(1));
    }

    @ParameterizedTest
    @MethodSource("wordsWithSameKanjisDifferentReadings")
    @Sql({"/japaneseDB-vocab-data-insertion.sql"})
    @DisplayName("Find a list of words with the same characters")
    void successfullyFindWordsByWordString(String wordStr){
        List<Word> words = repository.findByWord(wordStr);
        assertThat(words, hasSize(2));
        words.stream().forEach(
                word -> assertThat(word, hasProperty("word", equalTo(wordStr)))
        );
    }

    @ParameterizedTest
    @MethodSource("wordsWithSameReadingDifferentKanjis")
    @Sql({"/japaneseDB-vocab-data-insertion.sql"})
    @DisplayName("Find a list of words with the same reading")
    void successfullyFindWordsByReading(String readStr){
        List<Word> words = repository.findByReading(readStr);
        assertThat(words, hasSize(2));
        words.stream().forEach(
                word -> assertThat(word, hasProperty("reading", equalTo(readStr)))
        );
    }

    @Test
    @Sql({"/japaneseDB-vocab-data-insertion.sql"})
    @DisplayName("Find a list of words with the same meaning")
    void successfullyFindWordsByMeaning(){
        String regexPrefix = "(^|.*/)";
        String regexSuffix = "(/.*$|$)";
        String day = regexPrefix + "Day" + regexSuffix;
        String one = regexPrefix + "One" + regexSuffix;
        String round = regexPrefix + "Round" + regexSuffix;

        List<Word> words = repository.findByMeaningRegex(day);
        assertThat(words, hasSize(1));
        assertThat(words.get(0), hasProperty("word", equalTo("日")));

        words = repository.findByMeaningRegex(one);
        assertThat(words, hasSize(1));
        assertThat(words.get(0), hasProperty("word", equalTo("一")));

        words = repository.findByMeaningRegex(round);
        assertThat(words, hasSize(2));
        words.stream().forEach(
                word -> assertThat(
                        word,
                        hasProperty(
                                "word",
                                anyOf(
                                        equalTo("丸い"),
                                        equalTo("円い")
                                )
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("allWordsAdded")
    @Sql({"/japaneseDB-vocab-data-insertion.sql"})
    @DisplayName("Successfully confirm that a word exists by word and reading")
    void successfullyExistsWordByWordStringAndReading(List<String> words){
        assertTrue(repository.existsByWordAndReading(words.get(0), words.get(1)));
    }

    static List<String> wordsWithSameKanjisDifferentReadings(){
        return Arrays.asList("一日", "一月");
    }

    static List<String> wordsWithSameReadingDifferentKanjis(){
        return Arrays.asList("まるい", "ひ");
    }

    static List<List<String>> allWordsAdded(){
        return Arrays.asList(
                Arrays.asList("一日", "イチニチ"),
                Arrays.asList("一日", "ついたち"),
                Arrays.asList("一月", "イチガツ"),
                Arrays.asList("一月", "ひとつき"),
                Arrays.asList("丸い", "まるい"),
                Arrays.asList("円い", "まるい"),
                Arrays.asList("日", "ひ"),
                Arrays.asList("火", "ひ"),
                Arrays.asList("一", "イチ")
        );
    }

}
