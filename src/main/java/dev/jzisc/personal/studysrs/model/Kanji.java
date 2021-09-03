package dev.jzisc.personal.studysrs.model;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor
@Getter @Setter
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
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
    @Getter(PRIVATE) @Setter(PRIVATE)
    private List<Kanji> priorKanjisConfused = new ArrayList<>();

    @ManyToMany( fetch = FetchType.LAZY, mappedBy = "priorKanjisConfused" )
    @Getter(PRIVATE) @Setter(PRIVATE)
    private List<Kanji> createdLaterKanjisConfused = new ArrayList<>();

    public boolean addConfusion(Kanji confusion){
        if (confusion != null
                && confusion.kanji_id != this.kanji_id
                && !this.containsConfusion(confusion))
        {
            if ( confusion.kanji_id != null && ( this.kanji_id == null || confusion.kanji_id < this.kanji_id )) {
                if (this.priorKanjisConfused == null)
                    this.priorKanjisConfused = new ArrayList<>();
                this.priorKanjisConfused.add(confusion);
            } else {
                if (this.createdLaterKanjisConfused == null)
                    this.createdLaterKanjisConfused = new ArrayList<>();
                this.createdLaterKanjisConfused.add(confusion);
            }
            confusion.addConfusion(this);
            return true;
        }
        return false;
    }

    public List<Kanji> getConfusions(){
        if (priorKanjisConfused == null) {
            if (createdLaterKanjisConfused == null)
                return new ArrayList<>();
            else
                return new ArrayList<>(createdLaterKanjisConfused);
        } else {
            List<Kanji> res = new ArrayList<>(priorKanjisConfused);
            if (createdLaterKanjisConfused != null)
                res.addAll(createdLaterKanjisConfused);
            return res;
        }
    }

    public Kanji setConfusions(List<Kanji> confusions){
        if (confusions != null)
            confusions.stream().forEach( confusion -> this.addConfusion(confusion) );
        else {
            this.priorKanjisConfused = null;
            this.createdLaterKanjisConfused = null;
        }
        return this;
    }

    public boolean containsConfusion(Kanji confusion){
        boolean contained = false;
        if (priorKanjisConfused != null)
            contained = priorKanjisConfused.contains(confusion);
        if(!contained && createdLaterKanjisConfused != null)
            contained = createdLaterKanjisConfused.contains(confusion);
        return contained;
    }

}
