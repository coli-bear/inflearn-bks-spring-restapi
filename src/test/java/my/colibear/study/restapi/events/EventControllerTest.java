package my.colibear.study.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ExtendWith(SpringExtension.class)
class EventControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    EventRepository eventRepository;

    @Test
    public void test() throws Exception {

        // given
        Event event = Event.builder()
            .name("spring")
            .description("RestAPI Development with Spring")
            .beginEnrollmentDateTime(LocalDateTime.of(2023, 4, 12, 14, 00))
            .closeEnrollmentDateTime(LocalDateTime.of(2023, 4, 13, 14, 00))
            .beginEventDateTime(LocalDateTime.of(2023, 4, 15, 14, 0))
            .endEventDateTime(LocalDateTime.of(2023, 4, 15, 18, 0))
            .basePrice(100)
            .maxPrice(200)
            .limitOfEnrollment(100)
            .location("강남역 D2 Start up factory")
            .id(1L)
            .build();
        // when
        when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(
                post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(event))
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("$.id").value(1L))
        ;
        // then

    }

}