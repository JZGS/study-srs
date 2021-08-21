package dev.jzisc.personal.studysrs.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@NoArgsConstructor
@Getter @Setter
@Accessors(chain = true)
@Entity
@Table(name = "vocabulary")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "word_id_gen"
    )
    @SequenceGenerator(name = "word_id_gen",
            sequenceName = "vocabulary_word_id_seq",
            allocationSize = 1
    )
    private Integer word_id;

    private String word;
    private String reading;
    private String meaning;

}
