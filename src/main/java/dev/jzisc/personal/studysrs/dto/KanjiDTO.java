package dev.jzisc.personal.studysrs.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@Getter @Setter
@Accessors(chain = true)
public class KanjiDTO {

    private Short id;
    private String kanji, meaning;
    private List<Short> confusions;

}
