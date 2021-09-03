package dev.jzisc.personal.studysrs.tests.integration;

import dev.jzisc.personal.studysrs.dto.WordDTO;
import dev.jzisc.personal.studysrs.dto.mapper.WordMapper;
import dev.jzisc.personal.studysrs.model.Word;
import dev.jzisc.personal.studysrs.service.WordService;
import dev.jzisc.personal.studysrs.service.WordServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.jzisc.personal.studysrs.dto.mapper.WordMapper.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(WordServiceImpl.class)
@Sql({"/japaneseDB-drop.sql", "/japaneseDB-vocab-schema.sql", "/japaneseDB-vocab-data-insertion.sql"})
@DisplayName("Word Service Integration tests")
class WordServiceTest {

    @Autowired
    WordService service;

    @Test
    @DisplayName("Successfully autowired service")
    void successfullyAutowiredService(){
        assertThat(service).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully return a word's DTO by its id")
    void getWordById(Word word) {
        WordDTO expected = toWordDTO(word);
        Optional<WordDTO> result = service.getWordById(word.getWord_id());

        assertThat(result).isNotEmpty();
        assertThat(result).hasValue(expected);
    }

    @ParameterizedTest
    @MethodSource("getSameWordData")
    @DisplayName("Successfully return a list of wordDTOs that share the same word")
    void getWordListByWordString(List<Word> words) {
        List<WordDTO> expected = words.stream().map(WordMapper::toWordDTO).collect(Collectors.toList());
        String word = expected.get(0).getWord();
        List<WordDTO> result = service.getWordListByWordString(word);

        assertThat(result).hasSize(expected.size());
        assertThat(result).containsAll(expected);
    }

    @ParameterizedTest
    @MethodSource("getSameReadingData")
    @DisplayName("Successfully return a list of wordDTOs that share the same reading")
    void getWordListByReading(List<Word> words) {
        List<WordDTO> expected = words.stream().map(WordMapper::toWordDTO).collect(Collectors.toList());
        String reading = expected.get(0).getReading();

        List<WordDTO> result = service.getWordListByReading(reading);

        assertThat(result).hasSize(expected.size());
        assertThat(result).containsSubsequence(expected);
    }

    @ParameterizedTest
    @MethodSource("getSameMeaningData")
    @DisplayName("Successfully return a list of wordDTOs that share the same meaning")
    void getWordListByMeaning(List<Word> words) {
        List<WordDTO> expected = words.stream()
                                      .filter(word -> word.getWord_id() != null)
                                      .map(WordMapper::toWordDTO).collect(Collectors.toList());
        String meaning = words.get(0).getMeaning();

        List<WordDTO> result = service.getWordListByMeaning(meaning);

        assertThat(result).hasSize(expected.size());
        assertThat(result).containsSubsequence(expected);
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @Sql(statements = { "DELETE FROM vocabulary" })
    @DisplayName("Successfully save new Word")
    void saveNewWord(Word word) {
        WordDTO expected = toWordDTO(word);

        WordDTO result = service.saveNewWord(expected);

        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully update a new word by its DTO")
    void updateWord(Word word) {
        WordDTO expected = toWordDTO(word).setMeaning("Meaning changed");

        WordDTO result = service.updateWord(expected);
        WordDTO foundWord = service.getWordById(word.getWord_id()).get();

        assertThat(result).isEqualTo(expected);
        assertThat(foundWord).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully delete a word by its id")
    void deleteWordById(Word word) {
        WordDTO expected = toWordDTO(word);

        WordDTO result = service.deleteWordById(word.getWord_id());
        Optional<WordDTO> foundWord = service.getWordById(word.getWord_id());

        assertThat(result).isEqualTo(expected);
        assertThat(foundWord).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully delete a word by its DTO")
    void deleteWord(Word word) {
        WordDTO expected = toWordDTO(word);

        WordDTO result = service.deleteWord(expected);
        Optional<WordDTO> foundWord = service.getWordById(word.getWord_id());

        assertThat(result).isEqualTo(expected);
        assertThat(foundWord).isEmpty();
    }

    static List<Word> getAllData(){
        return asList(
                new Word().setWord_id(1).setWord("一日").setReading("イチニチ").setMeaning("One day"),
                new Word().setWord_id(2).setWord("一日").setReading("ついたち").setMeaning("First day of month"),
                new Word().setWord_id(3).setWord("一月").setReading("イチガツ").setMeaning("January"),
                new Word().setWord_id(4).setWord("一月").setReading("ひとつき").setMeaning("One month"),
                new Word().setWord_id(5).setWord("丸い").setReading("まるい").setMeaning("Round/Circular"),
                new Word().setWord_id(6).setWord("円い").setReading("まるい").setMeaning("Round/Circular"),
                new Word().setWord_id(7).setWord("日").setReading("ひ").setMeaning("Day"),
                new Word().setWord_id(8).setWord("火").setReading("ひ").setMeaning("Fire/Flame/Blaze"),
                new Word().setWord_id(9).setWord("一").setReading("イチ").setMeaning("One"),
                new Word().setWord_id(10).setWord("早い").setReading("はやい").setMeaning("Early/Fast/Quick/Hasty/"),
                new Word().setWord_id(11).setWord("速い").setReading("はやい").setMeaning("Fast/Quick/Hasty")
        );
    }

    static List<List<Word>> getSameWordData(){
        return asList(
                asList(
                        new Word().setWord_id(1).setWord("一日").setReading("イチニチ").setMeaning("One day"),
                        new Word().setWord_id(2).setWord("一日").setReading("ついたち").setMeaning("First day of month")
                ),
                asList(
                        new Word().setWord_id(3).setWord("一月").setReading("イチガツ").setMeaning("January"),
                        new Word().setWord_id(4).setWord("一月").setReading("ひとつき").setMeaning("One month")
                )
        );
    }

    static List<List<Word>> getSameReadingData(){
        return asList(
                asList(
                        new Word().setWord_id(5).setWord("丸い").setReading("まるい").setMeaning("Round/Circular"),
                        new Word().setWord_id(6).setWord("円い").setReading("まるい").setMeaning("Round/Circular")
                ),
                asList(
                        new Word().setWord_id(7).setWord("日").setReading("ひ").setMeaning("Day"),
                        new Word().setWord_id(8).setWord("火").setReading("ひ").setMeaning("Fire/Flame/Blaze")
                )
        );
    }

    static List<List<Word>> getSameMeaningData(){
        return asList(
                asList(
                        new Word().setMeaning("Round"),
                        new Word().setWord_id(5).setWord("丸い").setReading("まるい").setMeaning("Round/Circular"),
                        new Word().setWord_id(6).setWord("円い").setReading("まるい").setMeaning("Round/Circular")
                ),
                asList(
                        new Word().setMeaning("Fast"),
                        new Word().setWord_id(10).setWord("早い").setReading("はやい").setMeaning("Early/Fast/Quick/Hasty/"),
                        new Word().setWord_id(11).setWord("速い").setReading("はやい").setMeaning("Fast/Quick/Hasty")
                )
        );
    }

}