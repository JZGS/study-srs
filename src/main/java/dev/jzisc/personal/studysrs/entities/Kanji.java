package dev.jzisc.personal.studysrs.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity
@Table(name = "kanjis")
public class Kanji {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "kanjis_id_gen"
    )
    @SequenceGenerator(name = "kanjis_id_gen",
            sequenceName = "kanjis_kanji_id_seq",
            allocationSize = 1
    )
    private Short kanji_id;
    private String kanji;
    private String meaning;

    //Kanjis Confusions relation

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "kanjis_confusions",
            joinColumns = @JoinColumn(name = "kanji_id"),
            inverseJoinColumns = @JoinColumn(name = "confusion_id")
    )
    private List<Kanji> priorKanjisConfused;

    @ManyToMany(
            fetch = FetchType.LAZY, mappedBy = "priorKanjisConfused"
    )
    private List<Kanji> createdLaterKanjisConfused;

}
