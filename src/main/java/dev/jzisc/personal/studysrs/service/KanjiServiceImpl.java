package dev.jzisc.personal.studysrs.service;

import dev.jzisc.personal.studysrs.dto.KanjiDTO;
import dev.jzisc.personal.studysrs.exception.DuplicatedDataException;
import dev.jzisc.personal.studysrs.model.Kanji;
import dev.jzisc.personal.studysrs.repository.KanjiRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.jzisc.personal.studysrs.dto.mapper.KanjiMapper.toKanji;
import static dev.jzisc.personal.studysrs.dto.mapper.KanjiMapper.toKanjiDTO;

@AllArgsConstructor(onConstructor = @__({@Autowired}))
@Service
public class KanjiServiceImpl implements KanjiService{

    private KanjiRepository repository;

    @Override
    public Optional<KanjiDTO> getKanjiById(Short id) {
        Optional<Kanji> kanji = repository.findById(id);
        if (kanji.isPresent())
            return Optional.of( toKanjiDTO(kanji.get()) );
        return Optional.empty();
    }

    @Override
    public Optional<KanjiDTO> getKanjiByKanjiString(String kanjiStr) {
        Optional<Kanji> kanji = repository.findByKanji(kanjiStr);
        if (kanji.isPresent())
            return Optional.of( toKanjiDTO(kanji.get()) );
        return Optional.empty();
    }

    @Override
    public List<KanjiDTO> getKanjiListByMeaning(String meaning) {
        String regex = "(^|.*/)" + meaning + "(/.*$|$)";
        List<Kanji> kanjis = repository.findByMeaningRegex(regex);
        return kanjis.stream()
                .map( kanji -> toKanjiDTO(kanji) )
                .collect(Collectors.toList());
    }

    @Override
    public KanjiDTO saveNewKanji(KanjiDTO kanji) {
        if (kanji == null)
            return new KanjiDTO();
        if (!repository.existsByKanji(kanji.getKanji())){
            Kanji toSave = toKanji(kanji);
            Kanji saved = repository.save(toSave);
            return toKanjiDTO(saved);
        }
        throw new DuplicatedDataException("Kanji already saved on DB");
    }

    @Override
    public KanjiDTO saveNewKanji(KanjiDTO kanji, String... confusedKanjis) {
        if (kanji == null)
            return new KanjiDTO();
        if (repository.existsByKanji(kanji.getKanji()))
            throw new DuplicatedDataException("Kanji already saved on DB");
        List<Short> confusedIds = Arrays.stream(confusedKanjis)
                                        .filter(kanjiStr -> repository.existsByKanji(kanjiStr))
                                        .map( kanjiStr -> repository.findByKanji(kanjiStr).get().getKanji_id())
                                        .collect(Collectors.toList());
        kanji.setConfusions(confusedIds);
        return saveNewKanji(kanji);
    }

    @Override
    public KanjiDTO saveNewKanji(String kanji, String meaning) {
        return saveNewKanji( new KanjiDTO().setKanji(kanji).setMeaning(meaning) );
    }

    @Override
    public KanjiDTO saveNewKanji(String kanji, String meaning, String... confusedKanjis) {
        return saveNewKanji( new KanjiDTO().setKanji(kanji).setMeaning(meaning), confusedKanjis);
    }

    @Override
    public KanjiDTO saveNewKanji(String kanji, String meaning, Short... confusedIds) {
        List<Short> ids = Arrays.stream(confusedIds)
                            .filter(id -> repository.existsById(id)).collect(Collectors.toList());
        return saveNewKanji(
                new KanjiDTO()
                        .setKanji(kanji)
                        .setMeaning(meaning)
                        .setConfusions(ids)
        );
    }

    @Override
    public KanjiDTO updateKanji(KanjiDTO kanji) {
        if (kanji == null || kanji.getId() == null || !repository.existsById(kanji.getId()))
            return new KanjiDTO();
        Kanji toSave = toKanji(kanji);
        return toKanjiDTO( repository.save(toSave) );
    }

    @Override
    public KanjiDTO deleteKanjiById(Short id) {
        if (id != null && repository.existsById(id)){
            Kanji kanji = repository.findById(id).get();
            repository.delete(kanji);
            return toKanjiDTO(kanji);
        }
        return new KanjiDTO();
    }

    @Override
    public KanjiDTO deleteKanji(KanjiDTO kanji) {
        Kanji toDelete = toKanji(kanji);
        if (kanji == null || !repository.exists(Example.of(toDelete)) )
            return new KanjiDTO();
        repository.delete(toDelete);
        return kanji;
    }

}
