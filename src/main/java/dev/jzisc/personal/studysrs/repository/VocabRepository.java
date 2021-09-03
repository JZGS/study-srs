package dev.jzisc.personal.studysrs.repository;

import dev.jzisc.personal.studysrs.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VocabRepository extends JpaRepository<Word, Integer> {

    List<Word> findByWord(String word);
    List<Word> findByReading(String reading);

    @Query(nativeQuery = true, value = "SELECT * FROM vocabulary WHERE meaning ~* ?1")
    List<Word> findByMeaningRegex(String regex);

    boolean existsByWordAndReading(String word, String reading);

}
