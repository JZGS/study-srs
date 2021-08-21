package dev.jzisc.personal.studysrs.dto.mapper;

import dev.jzisc.personal.studysrs.dto.KanjiDTO;
import dev.jzisc.personal.studysrs.model.Kanji;

import java.util.stream.Collectors;

public class KanjiMapper {

    private KanjiMapper(){}

    public static KanjiDTO toKanjiDTO(Kanji kanji){
        if (kanji == null)
            return new KanjiDTO();
        return new KanjiDTO()
                .setId(kanji.getKanji_id())
                .setKanji(kanji.getKanji())
                .setMeaning(kanji.getMeaning())
                .setConfusions(
                        kanji.getConfusions().stream()
                                .map( k -> k.getKanji_id())
                                .collect(Collectors.toList())
                );
    }

    public static Kanji toKanji(KanjiDTO dto){
        if (dto == null)
            return new Kanji();
        Kanji res = new Kanji()
                .setKanji_id(dto.getId())
                .setKanji(dto.getKanji())
                .setMeaning(dto.getMeaning());
        if (dto.getConfusions() != null){
            res.setConfusions(
                    dto.getConfusions().stream()
                        .map( id -> new Kanji().setKanji_id(id))
                        .collect(Collectors.toList())
            );
        }
        return res;
    }

}
