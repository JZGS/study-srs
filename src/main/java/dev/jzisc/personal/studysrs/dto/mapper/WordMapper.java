package dev.jzisc.personal.studysrs.dto.mapper;

import dev.jzisc.personal.studysrs.dto.WordDTO;
import dev.jzisc.personal.studysrs.model.Word;

public class WordMapper {

    private WordMapper() {}

    public static WordDTO toWordDTO(Word word){
        if (word == null)
            return new WordDTO();
        return new WordDTO()
                .setId(word.getWord_id())
                .setWord(word.getWord())
                .setReading(word.getReading())
                .setMeaning(word.getMeaning());
    }

    public static  Word toWord(WordDTO dto){
        if (dto == null)
            return new Word();
        return new Word()
                .setWord_id(dto.getId())
                .setWord(dto.getWord())
                .setReading(dto.getReading())
                .setMeaning(dto.getMeaning());
    }

}
