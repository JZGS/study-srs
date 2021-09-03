package dev.jzisc.personal.studysrs.tests.unit.services;

import dev.jzisc.personal.studysrs.dto.KanjiDTO;
import dev.jzisc.personal.studysrs.model.Kanji;
import dev.jzisc.personal.studysrs.repository.KanjiRepository;
import dev.jzisc.personal.studysrs.service.KanjiService;
import dev.jzisc.personal.studysrs.service.KanjiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.Example;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.jzisc.personal.studysrs.dto.mapper.KanjiMapper.toKanjiDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Kanji Service Unit Tests")
public class KanjiServiceTests {

    private KanjiRepository repository;

    private KanjiService service;

    @BeforeEach
    void setUp(){
        repository = mock(KanjiRepository.class);
        service = new KanjiServiceImpl(repository);
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully returns a kanjiDto by its id")
    void getKanjiById(Kanji kanji){
        KanjiDTO expectedDTO = toKanjiDTO(kanji);
        when(repository.findById(kanji.getKanji_id())).thenReturn(Optional.of(kanji));

        Optional<KanjiDTO> result = service.getKanjiById(kanji.getKanji_id());

        assertThat(result).isNotEmpty().hasValue(expectedDTO);
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully returns a kanjiDTO by its Kanji character")
    void successfullyGetAKanjiByItsKanjiCharacter(Kanji kanji){
        KanjiDTO expectedDTO = toKanjiDTO(kanji);
        when(repository.findByKanji(kanji.getKanji())).thenReturn(Optional.of(kanji));

        Optional<KanjiDTO> result = service.getKanjiByKanjiString(kanji.getKanji());

        assertThat(result).isNotEmpty().hasValue(expectedDTO);
    }

    @ParameterizedTest
    @MethodSource("getSameMeaningKanjis")
    @DisplayName("Successfully returns a list of kanjiDTOs with the same meaning")
    void successfullyGetKanjiListWithTheSameMeaning(List<Kanji> kanjis){
        List<KanjiDTO> expectedDTOs = kanjis.stream().map( kanji -> toKanjiDTO(kanji) ).collect(Collectors.toList());
        String meaning = kanjis.get(1).getMeaning();
        String regexMeaning = "(^|.*/)" + meaning + "(/.*$|$)";
        when(repository.findByMeaningRegex(regexMeaning)).thenReturn(kanjis);

        List<KanjiDTO> result = service.getKanjiListByMeaning(meaning);

        assertThat(result).hasSize(2).containsSubsequence(expectedDTOs);
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully save a new Kanji with its DTO definition")
    void successfullySaveANewKanji(Kanji kanji){
        when(repository.existsByKanji(any(String.class))).thenReturn(false);
        when(repository.save(kanji)).thenReturn(kanji);

        KanjiDTO expected = toKanjiDTO(kanji);
        KanjiDTO result = service.saveNewKanji(expected);

        assertThat(result).hasFieldOrPropertyWithValue("id", kanji.getKanji_id());
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getConfusedKanjis")
    @DisplayName("Successfully save a new kanji with its DTO and a list of confused kanji characters")
    void successfullySaveNewKanjiWithConfusions(List<Kanji> kanjis){
        Kanji toSave = kanjis.get(0);
        List<Kanji> confusedKanjis = kanjis.subList(1, 1);
        String[] confusedKanjisStr = confusedKanjis.stream().map(k -> k.getKanji()).toArray(String[]::new);

        confusedKanjis.forEach(kanji -> {
            when(repository.existsByKanji(kanji.getKanji())).thenReturn(true);
            when(repository.findByKanji(kanji.getKanji())).thenReturn(Optional.of(kanji));
        });
        when(repository.existsByKanji(toSave.getKanji())).thenReturn(false);
        when(repository.save(toSave)).thenReturn(toSave);

        KanjiDTO result = service.saveNewKanji(toKanjiDTO(toSave), confusedKanjisStr);

        assertThat(result).isEqualTo(toKanjiDTO(toSave));
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully save a new kanji with its kanji character and meaning")
    void successfullySaveNewKanjiWithKanjiStringAndMeaning(Kanji kanji){
        KanjiDTO expected = toKanjiDTO(kanji);
        Kanji toSave = new Kanji().setKanji(kanji.getKanji()).setMeaning(kanji.getMeaning());

        when(repository.existsByKanji(any(String.class))).thenReturn(false);
        when(repository.save(toSave)).thenReturn(kanji);

        KanjiDTO result = service.saveNewKanji(kanji.getKanji(), kanji.getMeaning());

        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getConfusedKanjis")
    @DisplayName("Successfully save a new kanji with its kanji character, meaning and confusions characters")
    void successfullySaveNewKanjiWithKanjiStringAndMeaningAndConfusions(List<Kanji> kanjis){
        Kanji savedResult = kanjis.get(0);
        Kanji toSave = new Kanji().setKanji(savedResult.getKanji()).setMeaning(savedResult.getMeaning());
        List<Kanji> confusedKanjis = kanjis.subList(1, 1);
        String[] confusedKanjisStr = new String[confusedKanjis.size()];
        confusedKanjis.toArray(confusedKanjisStr);

        confusedKanjis.forEach(kanji -> {
            when(repository.existsByKanji(kanji.getKanji())).thenReturn(true);
            when(repository.findByKanji(kanji.getKanji())).thenReturn(Optional.of(kanji));
        });
        when(repository.existsByKanji(toSave.getKanji())).thenReturn(false);
        when(repository.save(toSave)).thenReturn(savedResult);

        KanjiDTO result = service.saveNewKanji(toSave.getKanji(), toSave.getMeaning(), confusedKanjisStr);
        assertThat(result).isEqualTo(toKanjiDTO(savedResult));
    }

    @ParameterizedTest
    @MethodSource("getConfusedKanjis")
    @DisplayName("Successfully save a new kanji with its kanji character, meaning and confusions characters")
    void successfullySaveNewKanjiWithKanjiStringAndMeaningAndConfusionsIds(List<Kanji> kanjis){
        Kanji savedResult = kanjis.get(0);
        Kanji toSave = new Kanji().setKanji(savedResult.getKanji()).setMeaning(savedResult.getMeaning());
        Short[] confusedKanjis = kanjis.subList(1, 1).stream().map(k -> k.getKanji_id()).toArray(Short[]::new);

        for (short conf : confusedKanjis)
            when(repository.existsById(conf)).thenReturn(true);
        when(repository.existsById(null)).thenReturn(false);
        when(repository.save(toSave)).thenReturn(savedResult);

        KanjiDTO result = service.saveNewKanji(toSave.getKanji(), toSave.getMeaning(), confusedKanjis);
        assertThat(result).isEqualTo(toKanjiDTO(savedResult));
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully update a new kanji")
    void successfullyUpdateAnExistingKanji(Kanji kanji){
        Kanji updated = new Kanji()
                .setKanji_id(kanji.getKanji_id())
                .setKanji(kanji.getKanji())
                .setMeaning(kanji.getMeaning())
                .setConfusions(kanji.getConfusions());
        updated.setMeaning("Meaning example");
        when(repository.existsById(updated.getKanji_id())).thenReturn(true);
        when(repository.save(updated)).thenReturn(updated);

        KanjiDTO toUpdate = toKanjiDTO(updated);

        KanjiDTO result = service.updateKanji(toUpdate);

        assertThat(result).isEqualTo(toKanjiDTO(updated));
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully delete a kanji by its id")
    void successfullyDeleteAnExistingKanjiById(Kanji kanji){
        short id = kanji.getKanji_id();
        doReturn(true).when(repository).existsById(id);
        doReturn(Optional.of(kanji)).when(repository).findById(id);
        doAnswer(
                invocation -> {
                    doReturn(false).when(repository).existsById(id);
                    doReturn(Optional.empty()).when(repository).findById(id);
                    return null;
                }
        ).when(repository).delete(kanji);

        KanjiDTO expected = toKanjiDTO(kanji);
        KanjiDTO result = service.deleteKanjiById(id);

        assertThat(result).isEqualTo(expected);
        assertThat(service.getKanjiById(id)).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully delete a kanji")
    void successfullyDeleteAnExistingKanji(Kanji kanji){
        doReturn(true).when(repository).exists(Example.of(kanji));
        doAnswer(
                invocation -> {
                    doReturn(false).when(repository).exists(Example.of(kanji));
                    doReturn(Optional.empty()).when(repository).findById(kanji.getKanji_id());
                    return null;
                }
        ).when(repository).delete(kanji);

        KanjiDTO toDelete = toKanjiDTO(kanji);
        KanjiDTO result = service.deleteKanji(toDelete);

        assertThat(result).isEqualTo(toDelete);
        assertThat(service.getKanjiById(kanji.getKanji_id())).isEmpty();
    }

    static List<Kanji> getAllData(){
        Kanji k1 = new Kanji().setKanji_id((short) 1).setKanji("牲").setMeaning( "Sacrifice");
        Kanji k2 = new Kanji().setKanji_id((short) 2).setKanji("性").setMeaning( "Personality/Gender");
        Kanji k3 = new Kanji().setKanji_id((short) 3).setKanji("員").setMeaning( "Member");
        Kanji k4 = new Kanji().setKanji_id((short) 4).setKanji("買").setMeaning( "Buy");
        Kanji k5 = new Kanji().setKanji_id((short) 5).setKanji("順").setMeaning( "Sequence/Obey");
        Kanji k6 = new Kanji().setKanji_id((short) 6).setKanji("訓").setMeaning( "Instruction");
        Kanji k7 = new Kanji().setKanji_id((short) 7).setKanji("川").setMeaning( "River");
        Kanji k8 = new Kanji().setKanji_id((short) 8).setKanji("河").setMeaning( "River");
        Kanji k9 = new Kanji().setKanji_id((short) 9).setKanji("自").setMeaning( "Self");
        Kanji k10 = new Kanji().setKanji_id((short) 10).setKanji("己").setMeaning( "Self");
        Kanji k11 = new Kanji().setKanji_id((short) 11).setKanji("本").setMeaning( "Book/Origin");
        Kanji k12 = new Kanji().setKanji_id((short) 12).setKanji("元").setMeaning( "Origin");
        Kanji k13 = new Kanji().setKanji_id((short) 13).setKanji("切").setMeaning( "Cut");
        Kanji k14 = new Kanji().setKanji_id((short) 14).setKanji("絶").setMeaning( "Cut off");

        return Arrays.asList(k1,k2,k3,k4,k5,k6,k7,k8,k9,k10,k11,k12,k13,k14);
    }

    static List<List<Kanji>> getSameMeaningKanjis(){
        Kanji k7 = new Kanji().setKanji_id((short) 7).setKanji("川").setMeaning( "River");
        Kanji k8 = new Kanji().setKanji_id((short) 8).setKanji("河").setMeaning( "River");
        Kanji k9 = new Kanji().setKanji_id((short) 9).setKanji("自").setMeaning( "Self");
        Kanji k10 = new Kanji().setKanji_id((short) 10).setKanji("己").setMeaning( "Self");
        Kanji k11 = new Kanji().setKanji_id((short) 11).setKanji("本").setMeaning( "Book/Origin");
        Kanji k12 = new Kanji().setKanji_id((short) 12).setKanji("元").setMeaning( "Origin");

        return Arrays.asList(
                Arrays.asList(k7,k8),
                Arrays.asList(k9,k10),
                Arrays.asList(k11,k12)
        );

    }

    static List<List<Kanji>> getConfusedKanjis(){
        Kanji k1 = new Kanji().setKanji_id((short) 1).setKanji("牲").setMeaning( "Sacrifice");
        Kanji k2 = new Kanji().setKanji_id((short) 2).setKanji("性").setMeaning( "Personality/Gender");
        Kanji k3 = new Kanji().setKanji_id((short) 3).setKanji("員").setMeaning( "Member");
        Kanji k4 = new Kanji().setKanji_id((short) 4).setKanji("買").setMeaning( "Buy");
        Kanji k5 = new Kanji().setKanji_id((short) 5).setKanji("順").setMeaning( "Sequence/Obey");
        Kanji k6 = new Kanji().setKanji_id((short) 6).setKanji("訓").setMeaning( "Instruction");

        return Arrays.asList(
                Arrays.asList(k2, k1),
                Arrays.asList(k4, k3),
                Arrays.asList(k6, k5)
        );
    }

}
