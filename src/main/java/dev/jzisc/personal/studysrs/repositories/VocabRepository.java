package dev.jzisc.personal.studysrs.repositories;

import dev.jzisc.personal.studysrs.entities.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VocabRepository extends JpaRepository<Word, Integer> {
}
