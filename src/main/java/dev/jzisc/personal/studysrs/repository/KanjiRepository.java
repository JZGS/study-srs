package dev.jzisc.personal.studysrs.repository;

import dev.jzisc.personal.studysrs.model.Kanji;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface KanjiRepository extends JpaRepository<Kanji, Short> {

    @Query(nativeQuery = true, value = "SELECT * FROM kanjis WHERE meaning ~* ?1")
    List<Kanji> findByMeaningRegex(String meaning);

    Optional<Kanji> findByKanji(String kanji);

    boolean existsByKanji(String kanji);

}
