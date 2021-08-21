package dev.jzisc.personal.studysrs.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter @Setter
@Accessors(chain = true)
public class WordDTO {

    private Integer id;
    private String word, reading, meaning;

}
