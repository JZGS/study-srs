package dev.jzisc.personal.studysrs.tests.integration;

import dev.jzisc.personal.studysrs.dto.KanjiDTO;
import dev.jzisc.personal.studysrs.model.Kanji;
import dev.jzisc.personal.studysrs.service.KanjiService;
import dev.jzisc.personal.studysrs.service.KanjiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.jzisc.personal.studysrs.dto.mapper.KanjiMapper.toKanjiDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;

@DataJpaTest
@Import(KanjiServiceImpl.class)
@Sql({"/japaneseDB-drop.sql", "/japaneseDB-kanji-schema.sql", "/japaneseDB-kanji-data-insertion.sql"})
@DisplayName("Kanji Service Integration Tests")
public class KanjiServiceTests {

    @Autowired
    KanjiService service;

    @Test
    @DisplayName("Successfully autowired service")
    void autowiredService(){
        assertThat(service).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully returns a kanjiDto by its id")
    void getKanjiById(Kanji kanji){
        Optional<KanjiDTO> result = service.getKanjiById(kanji.getKanji_id());

        assertThat(result).isNotEmpty();
        assertThat(result).hasValue(toKanjiDTO(kanji));
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully returns a kanjiDTO by its Kanji character")
    void successfullyGetAKanjiByItsKanjiCharacter(Kanji kanji){
        KanjiDTO expectedDTO = toKanjiDTO(kanji);

        Optional<KanjiDTO> result = service.getKanjiByKanjiString(kanji.getKanji());

        assertThat(result).isNotEmpty();
        assertThat(result).hasValue(expectedDTO);
    }

    @ParameterizedTest
    @MethodSource("getSameMeaningKanjis")
    @DisplayName("Successfully returns a list of kanjiDTOs with the same meaning")
    void successfullyGetKanjiListWithTheSameMeaning(List<Kanji> kanjis){
        List<KanjiDTO> expectedDTOs = kanjis.stream().map( kanji -> toKanjiDTO(kanji) ).collect(Collectors.toList());
        String meaning = kanjis.get(1).getMeaning();

        List<KanjiDTO> result = service.getKanjiListByMeaning(meaning);

        assertThat(result).hasSize(expectedDTOs.size()).containsSubsequence(expectedDTOs);
    }

    @ParameterizedTest
    @MethodSource("getNoConfusionsData")
    @Sql(statements = {"DELETE FROM kanjis"})
    @DisplayName("Successfully save a new Kanji with its DTO definition")
    void successfullySaveANewKanji(Kanji kanji){
        KanjiDTO expected = toKanjiDTO(kanji);
        KanjiDTO result = service.saveNewKanji(expected);

        assertThat(result.getId()).isNotNull();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @Sql(statements = {"DELETE FROM kanjis"})
    @DisplayName("Successfully save a new kanji with its DTO and a list of confused kanji characters")
    void successfullySaveNewKanjiWithConfusions(){
        List<Kanji> kanjis = getReadyToBeSavedData();
        kanjis.stream().forEach(
                kanji -> {
                    String[] confusedStr = new String[0];
                    Kanji confusedKanji = null;
                    if (!kanji.getConfusions().isEmpty()) {
                        confusedKanji = kanjis.get(kanji.getKanji_id() - 2);
                        confusedStr = new String[]{ confusedKanji.getKanji()};
                    }
                    KanjiDTO result = service.saveNewKanji(toKanjiDTO(kanji), confusedStr);

                    assertThat(result).isEqualTo(toKanjiDTO(kanji));
                    if (confusedKanji != null){
                        assertThat(result.getConfusions()).contains(confusedKanji.getKanji_id());
                    }
                }
        );
    }

    @ParameterizedTest
    @MethodSource("getNoConfusionsData")
    @Sql(statements = {"DELETE FROM kanjis"})
    @DisplayName("Successfully save a new kanji with its kanji character and meaning")
    void successfullySaveNewKanjiWithKanjiStringAndMeaning(Kanji kanji){
        KanjiDTO expected = toKanjiDTO(kanji);

        KanjiDTO result = service.saveNewKanji(kanji.getKanji(), kanji.getMeaning());

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @Sql(statements = {"DELETE FROM kanjis", "ALTER SEQUENCE kanjis_kanji_id_seq RESTART WITH 1"})
    @DisplayName("Successfully save a new kanji with its kanji character, meaning and confusions characters")
    void successfullySaveNewKanjiWithKanjiStringAndMeaningAndConfusions(){
        List<Kanji> kanjis = getReadyToBeSavedData();
        kanjis.stream().forEach(
                kanji -> {
                    String[] confusedStr = new String[0];
                    Kanji confusedKanji = null;
                    if (!kanji.getConfusions().isEmpty()) {
                        confusedKanji = kanjis.get(kanji.getKanji_id() - 2);
                        confusedStr = new String[]{ confusedKanji.getKanji()};
                    }
                    KanjiDTO result = service.saveNewKanji(kanji.getKanji(), kanji.getMeaning(), confusedStr);

                    assertThat(result).isEqualTo(toKanjiDTO(kanji));
                    if (confusedKanji != null){
                        assertThat(result.getConfusions()).contains(confusedKanji.getKanji_id());
                    }
                }
        );
    }

    @Test
    @Sql(statements = {"DELETE FROM kanjis"})
    @DisplayName("Successfully save a new kanji with its kanji character, meaning and confusions characters")
    void successfullySaveNewKanjiWithKanjiStringAndMeaningAndConfusionsIds(){
        List<Kanji> kanjis = getReadyToBeSavedData();
        kanjis.stream().forEach(
                kanji -> {
                    Short[] confusedStr = new Short[0];
                    Kanji confusedKanji = null;
                    if (!kanji.getConfusions().isEmpty()) {
                        confusedKanji = kanjis.get(kanji.getKanji_id() - 2);
                        confusedStr = new Short[]{ confusedKanji.getKanji_id()};
                    }
                    KanjiDTO result = service.saveNewKanji(kanji.getKanji(), kanji.getMeaning(), confusedStr);

                    assertThat(result).isEqualTo(toKanjiDTO(kanji));
                    if (confusedKanji != null){
                        assertThat(result.getConfusions()).contains(confusedKanji.getKanji_id());
                    }
                }
        );
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

        KanjiDTO toUpdate = toKanjiDTO(kanji).setMeaning("Meaning example");

        KanjiDTO result = service.updateKanji(toUpdate);

        assertThat(result).isEqualTo(toKanjiDTO(updated));
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully delete a kanji by its id")
    void successfullyDeleteAnExistingKanjiById(Kanji kanji){
        KanjiDTO result = service.deleteKanjiById(kanji.getKanji_id());

        assertThat(result).isEqualTo(toKanjiDTO(kanji));
        assertThat(service.getKanjiById(kanji.getKanji_id())).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully delete a kanji")
    void successfullyDeleteAnExistingKanji(Kanji kanji){
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

        k2.addConfusion(k1);  k4.addConfusion(k3);  k6.addConfusion(k5);

        return Arrays.asList(k1,k2,k3,k4,k5,k6,k7,k8,k9,k10,k11,k12,k13,k14);
    }

    static List<Kanji> getNoConfusionsData(){
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

    static List<Kanji> getReadyToBeSavedData(){
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

        k2.addConfusion(k1);  k4.addConfusion(k3);  k6.addConfusion(k5);
        k1.setConfusions(null);  k3.setConfusions(null);  k5.setConfusions(null);

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

}
