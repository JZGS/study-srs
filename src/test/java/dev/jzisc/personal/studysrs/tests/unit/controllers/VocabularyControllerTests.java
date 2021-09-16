package dev.jzisc.personal.studysrs.tests.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jzisc.personal.studysrs.controller.VocabularyController;
import dev.jzisc.personal.studysrs.dto.WordDTO;
import dev.jzisc.personal.studysrs.service.WordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VocabularyController.class)
@DisplayName("Vocabulary Controller Unit Tests")
public class VocabularyControllerTests {

    static final String BASE_URL = "/api/vocab";

    @MockBean
    WordService service;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    void successfullyAutowireDependencies(){
        assertThat(service).isNotNull();
        assertThat(mapper).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully save a new Word")
    void saveNewWord(WordDTO word) throws Exception {
        String url = BASE_URL;
        String requestBody = mapper.writeValueAsString(word);
        doReturn(word).when(service).saveNewWord(word);

        MvcResult mvcResult = mockMvc.perform(post(url).contentType(APPLICATION_JSON).content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", url + "/" + word.getId()))
                .andReturn();
        WordDTO response = mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), WordDTO.class);

        assertThat(response).isEqualTo(word);
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully get a new Word")
    void getWordById(WordDTO word) throws Exception {
        String url = BASE_URL + "/{id}";
        doReturn(Optional.of(word)).when(service).getWordById(word.getId());

        MvcResult mvcResult = mockMvc.perform(get(url, word.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        WordDTO response = mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), WordDTO.class);

        assertThat(response).isEqualTo(word);
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully update a Word")
    void updateWord(WordDTO word) throws Exception{
        String url = BASE_URL + "/{id}";
        String requestBody = mapper.writeValueAsString(word);
        doReturn(word).when(service).updateWord(word);

        MvcResult mvcResult = mockMvc.perform(put(url, word.getId()).contentType(APPLICATION_JSON).content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        WordDTO response = mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), WordDTO.class);

        assertThat(response).isEqualTo(word);
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully delete a Word")
    void deleteWord(WordDTO word) throws Exception{
        String url = BASE_URL + "/{id}";
        doReturn(word).when(service).deleteWordById(word.getId());

        MvcResult mvcResult = mockMvc.perform(delete(url, word.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        WordDTO response = mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), WordDTO.class);

        assertThat(response).isEqualTo(word);
    }

    static List<WordDTO> getAllData(){
        return asList(
                new WordDTO().setId(1).setWord("一日").setReading("イチニチ").setMeaning("One day"),
                new WordDTO().setId(2).setWord("一日").setReading("ついたち").setMeaning("First day of month"),
                new WordDTO().setId(3).setWord("一月").setReading("イチガツ").setMeaning("January"),
                new WordDTO().setId(4).setWord("一月").setReading("ひとつき").setMeaning("One month"),
                new WordDTO().setId(5).setWord("丸い").setReading("まるい").setMeaning("Round/Circular"),
                new WordDTO().setId(6).setWord("円い").setReading("まるい").setMeaning("Round/Circular"),
                new WordDTO().setId(7).setWord("日").setReading("ひ").setMeaning("Day"),
                new WordDTO().setId(8).setWord("火").setReading("ひ").setMeaning("Fire/Flame/Blaze"),
                new WordDTO().setId(9).setWord("一").setReading("イチ").setMeaning("One"),
                new WordDTO().setId(10).setWord("早い").setReading("はやい").setMeaning("Early/Fast/Quick/Hasty/"),
                new WordDTO().setId(11).setWord("速い").setReading("はやい").setMeaning("Fast/Quick/Hasty")
        );
    }

}
