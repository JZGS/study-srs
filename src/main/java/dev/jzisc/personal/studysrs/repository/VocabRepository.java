package dev.jzisc.personal.studysrs.repository;

import dev.jzisc.personal.studysrs.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VocabRepository extends JpaRepository<Word, Integer> {
}
