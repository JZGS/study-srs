package dev.jzisc.personal.studysrs.repositories;

import dev.jzisc.personal.studysrs.entities.Kanji;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KanjiRepository extends JpaRepository<Kanji, Short> {
}
