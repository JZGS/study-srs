package dev.jzisc.personal.studysrs.service;

import dev.jzisc.personal.studysrs.dto.WordDTO;

import java.util.List;
import java.util.Optional;

public interface WordService {

    Optional<WordDTO> getWordById(Integer id);
    List<WordDTO> getWordListByWordString(String word);
    List<WordDTO> getWordListByReading(String reading);
    List<WordDTO> getWordListByMeaning(String meaning);

    WordDTO saveNewWord(WordDTO word);

    WordDTO updateWord(WordDTO word);

    WordDTO deleteWordById(Integer id);
    WordDTO deleteWord(WordDTO word);

}
