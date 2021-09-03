package dev.jzisc.personal.studysrs.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@Getter @Setter
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
public class KanjiDTO {

    private Short id;
    private String kanji, meaning;
    private List<Short> confusions;

}
