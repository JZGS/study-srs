package dev.jzisc.personal.studysrs.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
