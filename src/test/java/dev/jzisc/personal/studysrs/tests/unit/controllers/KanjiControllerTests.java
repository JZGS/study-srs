package dev.jzisc.personal.studysrs.tests.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jzisc.personal.studysrs.controller.KanjiController;
import dev.jzisc.personal.studysrs.dto.KanjiDTO;
import dev.jzisc.personal.studysrs.service.KanjiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KanjiController.class)
@DisplayName("Kanji Controller Unit Tests")
public class KanjiControllerTests {

    static final String BASE_URL = "/api/kanjis";

    @Autowired
    ObjectMapper mapper;

    @MockBean
    KanjiService service;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Successfully autowire dependencies")
    void successfullyAutowireDependencies(){
        assertThat(service).isNotNull();
        assertThat(mapper).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully return a KanjiDTO by its id")
    void getKanjiByID(KanjiDTO kanji) throws Exception {
        String url = BASE_URL + "/{id}";
        doReturn(Optional.of(kanji)).when(service).getKanjiById(kanji.getId());

        MvcResult mvcResult = mockMvc.perform(get(url, kanji.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        KanjiDTO response = mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), KanjiDTO.class);

        assertThat(response).isEqualTo(kanji);
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully save a new kanji and get its DTO")
    void saveNewKanji(KanjiDTO kanji) throws Exception {
        String url = BASE_URL;
        String requestBody = mapper.writeValueAsString(kanji);
        doReturn(kanji).when(service).saveNewKanji(kanji);

        MvcResult mvcResult = mockMvc.perform(post(url).contentType(APPLICATION_JSON).content(requestBody))
                                     .andDo(print())
                                     .andExpect(status().isCreated())
                                     .andExpect(header().string("Location", url + "/" + kanji.getId()))
                                     .andReturn();
        KanjiDTO response = mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), KanjiDTO.class);

        assertThat(response).isEqualTo(kanji);
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully return a KanjiDTO by its kanji character")
    void getKanjiByKanjiString(KanjiDTO kanji) throws Exception {
        String url = BASE_URL + "/search";
        doReturn(Optional.of(kanji)).when(service).getKanjiByKanjiString(kanji.getKanji());

        MvcResult mvcResult = mockMvc.perform(get(url).queryParam("kanji", kanji.getKanji()))
                                     .andDo(print())
                                     .andExpect(status().isOk())
                                     .andReturn();

        KanjiDTO response = mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), KanjiDTO.class);

        assertThat(response).isEqualTo(kanji);
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully return a list of kanjiDTOs that share the same meaning")
    void getKanjiListByMeaning(KanjiDTO kanji) throws Exception{
        String url = BASE_URL + "/search";
        doReturn(Arrays.asList(kanji)).when(service).getKanjiListByMeaning(kanji.getMeaning());

        MvcResult mvcResult = mockMvc.perform(get(url).queryParam("meaning", kanji.getMeaning()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        KanjiDTO[] response = mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), KanjiDTO[].class);

        assertThat(response).hasSize(1);
        assertThat(response).contains(kanji);
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully update a kanji")
    void updateKanji(KanjiDTO kanji) throws Exception{
        String url = BASE_URL + "/" + kanji.getId();
        String content = mapper.writeValueAsString(kanji);
        doReturn(kanji).when(service).updateKanji(kanji);

        MvcResult mvcResult = mockMvc.perform(put(url).contentType(APPLICATION_JSON).content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        KanjiDTO response = mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), KanjiDTO.class);

        assertThat(response).isEqualTo(kanji);
    }

    @ParameterizedTest
    @MethodSource("getAllData")
    @DisplayName("Successfully delete a kanji")
    void deleteKanji(KanjiDTO kanji) throws Exception{
        String url = BASE_URL + "/" + kanji.getId();
        doReturn(kanji).when(service).deleteKanjiById(kanji.getId());

        MvcResult mvcResult = mockMvc.perform(delete(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        KanjiDTO response = mapper.readValue(mvcResult.getResponse().getContentAsByteArray(), KanjiDTO.class);

        assertThat(response).isEqualTo(kanji);
    }

    static List<KanjiDTO> getAllData(){
        return Arrays.asList(
                new KanjiDTO().setId((short)1).setKanji("一").setMeaning("One"),
                new KanjiDTO().setId((short)2).setKanji("二").setMeaning("Two"),
                new KanjiDTO().setId((short)3).setKanji("三").setMeaning("Three"),
                new KanjiDTO().setId((short)4).setKanji("人").setMeaning("Person").setConfusions(Arrays.asList((short)5)),
                new KanjiDTO().setId((short)5).setKanji("入").setMeaning("Enter").setConfusions(Arrays.asList((short)4))
        );
    }

}
