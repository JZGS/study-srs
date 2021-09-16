package dev.jzisc.personal.studysrs.service;

import dev.jzisc.personal.studysrs.dto.WordDTO;
import dev.jzisc.personal.studysrs.exception.DuplicatedDataException;
import dev.jzisc.personal.studysrs.model.Word;
import dev.jzisc.personal.studysrs.repository.VocabRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.jzisc.personal.studysrs.dto.mapper.WordMapper.*;

@AllArgsConstructor(onConstructor = @__({@Autowired}))
@Service
public class WordServiceImpl implements WordService{

    private VocabRepository repository;

    @Override
    public Optional<WordDTO> getWordById(Integer id) {
        if (id != null && repository.existsById(id)){
            return Optional.of(toWordDTO(repository.findById(id).get()));
        }
        return Optional.empty();
    }

    @Override
    public List<WordDTO> getWordListByWordString(String wordStr) {
        if (wordStr == null)
            return new ArrayList<>();
        List<Word> words = repository.findByWord(wordStr);
        return words.stream()
                    .map(word -> toWordDTO(word))
                    .collect(Collectors.toList());
    }

    @Override
    public List<WordDTO> getWordListByReading(String reading) {
        if (reading == null)
            return new ArrayList<>();
        List<Word> words = repository.findByReading(reading);
        return words.stream()
                .map(word -> toWordDTO(word))
                .collect(Collectors.toList());
    }

    @Override
    public List<WordDTO> getWordListByMeaning(String meaning) {
        if (meaning == null)
            return new ArrayList<>();
        String regex = "(^|.*/)" + meaning + "(/.*$|$)";
        List<Word> words = repository.findByMeaningRegex(regex);
        return words.stream()
                .map(word -> toWordDTO(word))
                .collect(Collectors.toList());
    }

    @Override
    public WordDTO saveNewWord(WordDTO word) {
        if (word == null
                || word.getWord() == null
                || word.getReading() == null
        )
            return new WordDTO();
        if (repository.existsByWordAndReading(word.getWord(), word.getReading()))
            throw new DuplicatedDataException("Word \"" + word.getWord()
                    + "\" with reading \"" + word.getReading() + "already saved on DB");
        return toWordDTO(repository.save(toWord(word)));
    }

    @Override
    public WordDTO updateWord(WordDTO word) {
        if (word == null || !repository.existsById(word.getId()))
            return new WordDTO();
        return toWordDTO(repository.save(toWord(word)));
    }

    @Override
    public WordDTO deleteWordById(Integer id) {
        if (id == null || !repository.existsById(id))
            return new WordDTO();
        Word toDelete = repository.findById(id).get();
        repository.delete(toDelete);
        return toWordDTO(toDelete);
    }

    @Override
    public WordDTO deleteWord(WordDTO word) {
        Word toDelete = toWord(word);
        if (word == null || !repository.exists(Example.of(toDelete)))
            return new WordDTO();
        repository.delete(toDelete);
        return word;
    }

}
