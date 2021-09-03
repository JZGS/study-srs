package dev.jzisc.personal.studysrs.dto;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter @Setter
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
public class WordDTO {

    private Integer id;
    private String word, reading, meaning;

}
