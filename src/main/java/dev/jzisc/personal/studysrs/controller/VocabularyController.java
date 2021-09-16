package dev.jzisc.personal.studysrs.controller;

import dev.jzisc.personal.studysrs.dto.ErrorBody;
import dev.jzisc.personal.studysrs.dto.WordDTO;
import dev.jzisc.personal.studysrs.exception.DuplicatedDataException;
import dev.jzisc.personal.studysrs.service.WordService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@AllArgsConstructor(onConstructor = @__({@Autowired}))
@RestController
@RequestMapping("/api/vocab")
public class VocabularyController {

    private WordService service;

    @PostMapping
    public ResponseEntity createNewWord(@RequestBody WordDTO word,
                                        HttpServletRequest request) throws URISyntaxException {
        ErrorBody error = new ErrorBody()
                .setStatusCode(BAD_REQUEST.value())
                .setMessage("Invalid Request");
        try{
            WordDTO result = service.saveNewWord(word);
            if (result.getId() != null){
                URI uri = new URI(request.getRequestURI() + "/" + result.getId());
                return ResponseEntity.created(uri).body(result);
            }
        }catch (DuplicatedDataException ex){
            error.setMessage(ex.getMessage());
        }
        return ResponseEntity.badRequest().body(error);
    }

    @GetMapping("/{id}")
    public ResponseEntity getWordById(@PathVariable("id") Integer id){
        Optional<WordDTO> result = service.getWordById(id);
        if (result.isPresent())
            return ResponseEntity.ok(result.get());
        return ResponseEntity
                .badRequest()
                .body(
                        new ErrorBody()
                                .setStatusCode(BAD_REQUEST.value())
                                .setMessage(String.format("The word with the id {%d} doesn't exist", id))
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity updateWord(@RequestBody WordDTO word,
                                     @PathVariable("id") Integer id){
        if (word != null)
            word.setId(id);
        WordDTO result = service.updateWord(word);
        if (result.getId() == id)
            return ResponseEntity.ok(result);
        ErrorBody err = new ErrorBody()
                .setStatusCode(BAD_REQUEST.value())
                .setMessage("Invalid update object or Word does not exist");
        return ResponseEntity.badRequest().body(err);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteWordById(@PathVariable("id") Integer id){
        WordDTO deleted = service.deleteWordById(id);
        if (deleted.getId() == id)
            return ResponseEntity.ok(deleted);
        ErrorBody err = new ErrorBody()
                .setStatusCode(BAD_REQUEST.value())
                .setMessage("Word with id {" + id + "} does not exist");
        return ResponseEntity.badRequest().body(err);
    }

}