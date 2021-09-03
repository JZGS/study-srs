package dev.jzisc.personal.studysrs.tests.unit.services;

import dev.jzisc.personal.studysrs.dto.WordDTO;
import dev.jzisc.personal.studysrs.dto.mapper.WordMapper;
import dev.jzisc.personal.studysrs.model.Word;
import dev.jzisc.personal.studysrs.repository.VocabRepository;
import dev.jzisc.personal.studysrs.service.WordService;
import dev.jzisc.personal.studysrs.service.WordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.Example;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.jzisc.personal.studysrs.dto.mapper.WordMapper.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Word Service Unit Tests")
class WordServiceTests {

    VocabRepository repository;

    WordService service;

    @BeforeEach
    void setUp(){
        repository = mock(VocabRepository.class);
        service = new WordServiceImpl(repository);
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully return a WordDTO by its id")
    void getWordById(Word word) {
        doReturn(true).when(repository).existsById(word.getWord_id());
        doReturn(Optional.of(word)).when(repository).findById(word.getWord_id());

        WordDTO expected = toWordDTO(word);

        Optional<WordDTO> result = service.getWordById(word.getWord_id());
        assertThat(result).isNotEmpty();
        assertThat(result).hasValue(expected);
    }

    @ParameterizedTest
    @MethodSource("getSameWordData")
    @DisplayName("Successfully return a list of WordDTO that shares same word string")
    void getWordListByWordString(List<Word> words) {
        String wordStr = words.get(0).getWord();
        doReturn(words).when(repository).findByWord(wordStr);

        List<WordDTO> expected = words.stream().map(WordMapper::toWordDTO).collect(Collectors.toList());

        List<WordDTO> result = service.getWordListByWordString(wordStr);

        assertThat(result).hasSize(words.size());
        assertThat(result).containsSubsequence(expected);
    }

    @ParameterizedTest
    @MethodSource("getSameReadingData")
    @DisplayName("Successfully return a list of WordDTO that shares same reading")
    void getWordListByReading(List<Word> words) {
        String reading  = words.get(0).getReading();
        doReturn(words).when(repository).findByReading(reading);

        List<WordDTO> expected = words.stream().map(WordMapper::toWordDTO).collect(Collectors.toList());

        List<WordDTO> result = service.getWordListByReading(reading);

        assertThat(result).hasSize(words.size());
        assertThat(result).containsSubsequence(expected);
    }

    @ParameterizedTest
    @MethodSource("getSameMeaningData")
    @DisplayName("Successfully return a List of WordDTO that shares same meaning")
    void getWordListByMeaning(List<Word> preWords) {
        String meaning = preWords.get(0).getMeaning();
        List<Word> words = preWords.subList(1, preWords.size() - 1);

        doReturn(words).when(repository).findByMeaningRegex("(^|.*/)" + meaning + "(/.*$|$)");

        List<WordDTO> expected = words.stream().map(WordMapper::toWordDTO).collect(Collectors.toList());

        List<WordDTO> result = service.getWordListByMeaning(meaning);

        assertThat(result).hasSize(words.size());
        assertThat(result).containsSubsequence(expected);
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully return a WordDTO after save a new word")
    void saveNewWord(Word word) {
        Word toSave = new Word()
                            .setWord(word.getWord())
                            .setReading(word.getReading())
                            .setMeaning(word.getMeaning());
        doReturn(false).when(repository).existsByWordAndReading(word.getWord(), word.getReading());
        doReturn(word).when(repository).save(toSave);

        WordDTO result = service.saveNewWord(toWordDTO(toSave));

        assertThat(result).hasFieldOrPropertyWithValue("id", word.getWord_id());
        assertThat(result).isEqualTo(toWordDTO(word));
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully return a WordDTO after updating an existing word")
    void updateWord(Word word) {
        Word updated = new Word()
                            .setWord_id(word.getWord_id())
                            .setWord(word.getWord())
                            .setReading(word.getReading())
                            .setMeaning(word.getMeaning());
        updated.setMeaning("Meaning example");
        doReturn(true).when(repository).existsById(word.getWord_id());
        doReturn(updated).when(repository).save(updated);

        WordDTO toUpdate = toWordDTO(updated);

        WordDTO result = service.updateWord(toUpdate);

        assertThat(result).isEqualTo(toWordDTO(updated));
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully delete an existing Word by its id and return its DTO")
    void deleteWordById(Word word) {
        int id = word.getWord_id();

        doReturn(true).when(repository).existsById(id);
        doReturn(Optional.of(word)).when(repository).findById(id);
        doAnswer(
                invocation -> {
                    doReturn(false).when(repository).existsById(id);
                    doReturn(Optional.empty()).when(repository).findById(id);
                    return null;
                }
        ).when(repository).delete(word);

        WordDTO expected = toWordDTO(word);

        WordDTO result = service.deleteWordById(id);

        assertThat(result).isEqualTo(expected);
        assertThat(service.getWordById(id)).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully delete an existing word and return its DTO")
    void deleteWord(Word word) {
        doReturn(true).when(repository).exists(Example.of(word));
        doAnswer(
                invocation -> {
                    doReturn(false).when(repository).exists(Example.of(word));
                    doReturn(Optional.empty()).when(repository).findById(word.getWord_id());
                    return null;
                }
        ).when(repository).delete(word);

        WordDTO toDelete = toWordDTO(word);
        WordDTO result = service.deleteWord(toDelete);

        assertThat(result).isEqualTo(toDelete);
        assertThat(service.getWordById(word.getWord_id())).isEmpty();
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