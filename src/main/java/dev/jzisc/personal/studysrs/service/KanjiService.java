package dev.jzisc.personal.studysrs.service;

import dev.jzisc.personal.studysrs.dto.KanjiDTO;

import java.util.List;
import java.util.Optional;

public interface KanjiService {

    Optional<KanjiDTO> getKanjiById(Short id);
    Optional<KanjiDTO> getKanjiByKanjiString(String kanji);
    List<KanjiDTO> getKanjiListByMeaning(String meaning);

    KanjiDTO saveNewKanji(KanjiDTO kanji);
    KanjiDTO saveNewKanji(KanjiDTO kanji, String... confusedKanjis);
    KanjiDTO saveNewKanji(String kanji, String meaning);
    KanjiDTO saveNewKanji(String kanji, String meaning, String... confusedKanjis);
    KanjiDTO saveNewKanji(String kanji, String meaning, Short... confusedIds);

    KanjiDTO updateKanji(KanjiDTO kanji);

    KanjiDTO deleteKanjiById(Short id);
    KanjiDTO deleteKanji(KanjiDTO kanji);

}
