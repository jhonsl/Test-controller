package com.sofka.TestControladores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofka.TestControladores.model.Widget;
import com.sofka.TestControladores.service.WidgetService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WidgetRestControllerTest {

    @MockBean
    private WidgetService service;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /widgets success")
    void testGetWidgetsSuccess() throws Exception {
        Widget widget1 = new Widget(1l, "Widget Name", "Description", 1);
        Widget widget2 = new Widget(2l, "Widget 2 Name", "Description 2", 4);
        doReturn(Lists.newArrayList(widget1, widget2)).when(service).findAll();

        mockMvc.perform(get("/rest/widgets"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.LOCATION, "/rest/widgets"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Widget Name")))
                .andExpect(jsonPath("$[0].description", is("Description")))
                .andExpect(jsonPath("$[0].version", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Widget 2 Name")))
                .andExpect(jsonPath("$[1].description", is("Description 2")))
                .andExpect(jsonPath("$[1].version", is(4)));
    }

    @Test
    @DisplayName("GET /rest/widget/1")
    void testGetWidgetById() throws Exception {
        Widget widget = new Widget(1l, "Widget Example", "Description Example", 1);

        doReturn(Optional.of(widget)).when(service).findById(1L);

        mockMvc.perform(get("/rest/widget/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(header().string(HttpHeaders.LOCATION, "/rest/widget/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Widget Example")))
                .andExpect(jsonPath("$.description", is("Description Example")))
                .andExpect(jsonPath("$.version", is(1))
                );
    }

    @Test
    @DisplayName("GET /rest/widget/1 - Not Found")
    void testGetWidgetByIdNotFound() throws Exception {
        doReturn(Optional.empty()).when(service).findById(1l);

        mockMvc.perform(get("/rest/widget/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /rest/widget")
    void testCreateWidget() throws Exception {
        Widget widgetToPost = new Widget("New Widget", "This is my widget");
        Widget widgetToReturn = new Widget(1L, "New Widget", "This is my widget", 1);
        doReturn(widgetToReturn).when(service).save(any());

        mockMvc.perform(post("/rest/widget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(widgetToPost)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.LOCATION, "/rest/widget/1"))
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("New Widget")))
                .andExpect(jsonPath("$.description", is("This is my widget")))
                .andExpect(jsonPath("$.version", is(1)));
    }

    @Test
    @DisplayName("PUT /rest/widget/1")
    void testUpdateWidget() throws Exception {
        Widget widgetToPut = new Widget("New Widget", "This is my widget");
        Widget widgetToFind = new Widget(1L, "New Widget", "This is my widget", 2);
        Widget updatedWidget = new Widget(1L, "Updated Widget", "This is my Updated widget", 3);

        doReturn(Optional.of(widgetToFind)).when(service).findById(1L);
        doReturn(updatedWidget).when(service).save(any());

        mockMvc.perform(put("/rest/widget/{id}", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.IF_MATCH, 2)
                        .content(asJsonString(widgetToPut)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.LOCATION, "/rest/widget/1"))
                .andExpect(header().string(HttpHeaders.ETAG, "\"3\""))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Widget")))
                .andExpect(jsonPath("$.description", is("This is my Updated widget")))
                .andExpect(jsonPath("$.version", is(3)));
    }

    @Test
    @DisplayName("PUT /rest/widget/1")
    void testUpdateWidgetNotFound() throws Exception {
        Widget widgetToPut = new Widget("New Widget", "This is my widget");
        Widget widgetToFind = new Widget(1L, "New Widget", "This is my widget", 2);
        Widget updatedWidget = new Widget(1L, "Updated Widget", "This is my Updated widget", 3);

        doReturn(Optional.of(widgetToFind)).when(service).findById(1L);
        doReturn(widgetToFind).when(service).save(any());

        mockMvc.perform(put("/rest/widget/{id}", 2l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.IF_MATCH, 2)
                        .content(asJsonString(widgetToPut)))
                .andExpect(status().isNotFound());
    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
