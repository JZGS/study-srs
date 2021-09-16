package dev.jzisc.personal.studysrs.controller;

import dev.jzisc.personal.studysrs.dto.ErrorBody;
import dev.jzisc.personal.studysrs.dto.KanjiDTO;
import dev.jzisc.personal.studysrs.exception.DuplicatedDataException;
import dev.jzisc.personal.studysrs.service.KanjiService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@AllArgsConstructor(onConstructor = @__({@Autowired}))
@RestController
@RequestMapping("/api/kanjis")
public class KanjiController {

    private KanjiService service;

    @PostMapping
    public ResponseEntity saveNewKanji(@RequestBody KanjiDTO kanji,
                                       HttpServletRequest request) throws URISyntaxException {
        ErrorBody error = new ErrorBody()
                .setStatusCode(BAD_REQUEST.value())
                .setMessage("Invalid Request");
        try{
            KanjiDTO result = service.saveNewKanji(kanji);
            if (result.getId() != null) {
                URI uri = new URI(request.getRequestURI() + "/"+result.getId());
                return ResponseEntity.created(uri).body(result);
            }
        }catch (DuplicatedDataException ex){
            error.setMessage(ex.getMessage());
        }

        return ResponseEntity.badRequest().body(error);
    }

    @GetMapping("/{id}")
    public ResponseEntity getKanjiById(@PathVariable("id") Short id){
        Optional<KanjiDTO> result = service.getKanjiById(id);
        if (result.isPresent())
            return ResponseEntity.ok(result.get());
        return ResponseEntity
                    .badRequest()
                    .body(
                        new ErrorBody()
                              .setStatusCode(BAD_REQUEST.value())
                              .setMessage(String.format("The kanji with the id {%d} doesn't exist", id))
                    );
    }

    @PutMapping("/{id}")
    public ResponseEntity updateKanji(@RequestBody KanjiDTO update,
                                      @PathVariable("id") Short id){
        if (update != null)
            update.setId(id);
        KanjiDTO result = service.updateKanji(update);
        if (result.getId() == id)
            return ResponseEntity.ok(result);
        ErrorBody err = new ErrorBody()
                .setStatusCode(BAD_REQUEST.value())
                .setMessage("Invalid update object or Kanji does not exist");
        return ResponseEntity.badRequest().body(err);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteKanji(@PathVariable("id") Short id){
        KanjiDTO deleted = service.deleteKanjiById(id);
        if (deleted.getId() == id)
            return ResponseEntity.ok(deleted);
        ErrorBody err = new ErrorBody()
                .setStatusCode(BAD_REQUEST.value())
                .setMessage("Kanji with id {" + id + "} does not exist");
        return ResponseEntity.badRequest().body(err);
    }

    @GetMapping("/search")
    public ResponseEntity searchKanji(@RequestParam(value = "kanji", required = false) String kanjiStr,
                               @RequestParam(value = "meaning", required = false) String meaning){
        if (kanjiStr != null){
            Optional<KanjiDTO> result = service.getKanjiByKanjiString(kanjiStr);
            if (result.isPresent())
                return ResponseEntity.ok(result.get());
            return ResponseEntity.notFound().build();
        }
        if (meaning != null){
            List<KanjiDTO> result = service.getKanjiListByMeaning(meaning);
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body(new ErrorBody().setMessage("Nothing to search"));
    }

}