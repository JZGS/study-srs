package dev.jzisc.personal.studysrs.entities;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
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

    @ManyToMany( fetch = FetchType.LAZY, mappedBy = "priorKanjisConfused" )
    private List<Kanji> createdLaterKanjisConfused;

    public boolean addConfusion(Kanji confusion){
        if(!confusion.containsConfusion(this)) {
            if (priorKanjisConfused == null)
                priorKanjisConfused = new ArrayList<>();
            if (!priorKanjisConfused.contains(confusion))
                priorKanjisConfused.add(confusion);
            confusion.addConfusion(this);
            return true;
        } else{
            if (createdLaterKanjisConfused == null)
                createdLaterKanjisConfused = new ArrayList<>();
            if (!createdLaterKanjisConfused.contains(confusion)) {
                createdLaterKanjisConfused.add(confusion);
                return true;
            }
        }
        return false;
    }

    public List<Kanji> getConfusions(){
        if (priorKanjisConfused == null) {
            if (createdLaterKanjisConfused == null)
                return new ArrayList<>();
            else
                return new ArrayList<>(createdLaterKanjisConfused);
        }else {
            List<Kanji> res = new ArrayList<>(priorKanjisConfused);
            if (createdLaterKanjisConfused != null)
                res.addAll(createdLaterKanjisConfused);
            return res;
        }
    }

    public boolean containsConfusion(Kanji confusion){
        boolean contained = false;
        if (priorKanjisConfused != null)
            contained = priorKanjisConfused.contains(confusion);
        if(!contained && createdLaterKanjisConfused != null)
            contained = createdLaterKanjisConfused.contains(confusion);
        return contained;
    }

    public List<Kanji> getPriorKanjisConfused(){
        if (priorKanjisConfused == null)
            priorKanjisConfused = new ArrayList<>();
        return priorKanjisConfused;
    }

    public List<Kanji> getCreatedLaterKanjisConfused(){
        if (createdLaterKanjisConfused == null)
            createdLaterKanjisConfused = new ArrayList<>();
        return createdLaterKanjisConfused;
    }

}
