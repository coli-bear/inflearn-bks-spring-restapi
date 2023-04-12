package my.colibear.study.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.colibear.study.restapi.events.mapper.EventMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.BDDAssertions.then;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class EventControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void test() throws Exception {

        // tdd 는 테스트케이스를 모두 만들고 진행한다.
        // tdd 는 최소 3가지 테스트 케이스를 이용해서 테스트를 진행한다.

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
            .id(1000L)
            .free(false)
            .build();
        // then
        then(event.getEventStatus()).isEqualTo(EventStatus.DRAFT);

        mockMvc.perform(
                post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(event))
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("id").value(not(1000)))
            .andExpect(jsonPath("free").value(not(true)))
            .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT))
            .andExpect(header().exists("Location"))
            .andExpect(header().string("Content-Type", "application/json"))
        ;

    }

}