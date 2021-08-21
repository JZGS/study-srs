package dev.jzisc.personal.studysrs.repository;

import dev.jzisc.personal.studysrs.model.Kanji;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KanjiRepository extends JpaRepository<Kanji, Short> {
}
