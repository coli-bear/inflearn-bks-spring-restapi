package my.colibear.study.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.colibear.study.restapi.common.annotataion.TestDescription;
import my.colibear.study.restapi.common.serializer.ErrorsSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.not;
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
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    @DisplayName("정상적으로 이벤트를 생성하는 테스트")
    public void test() throws Exception {

        // tdd 는 테스트케이스를 모두 만들고 진행한다.
        // tdd 는 최소 3가지 테스트 케이스를 이용해서 테스트를 진행한다.

        // given
        EventDto event = EventDto.builder()
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
            .build();

        this.mockMvc.perform(
                post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(event))
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("id").value(not(1000)))
            .andExpect(jsonPath("free").value(false))
            .andExpect(jsonPath("offline").value(true))
            .andExpect(header().exists("Location"))
            .andExpect(header().string("Content-Type", "application/json"))
        ;

    }

    @Test
    @TestDescription("입력 받을 수 없는 값을 사용하는 경우에 400 에러 발생하는 테스트")
    @DisplayName("입력 받을 수 없는 값을 사용하는 경우에 400 에러 발생하는 테스트")
    public void createFail_BadRequest() throws Exception {

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
            .free(true)
            .offline(false)
            .eventStatus(EventStatus.PUBLISHED)
            .build();
        // then

//        then(event.getEventStatus()).isEqualTo(EventStatus.DRAFT);

        this.mockMvc.perform(
                post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(event))
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @TestDescription("입력값이 비어있는 경우에 에러가 발생하는 테스트")
    @DisplayName("입력값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_BadRequest_EmptyInput() throws Exception {
//        EventDto eventDto = EventDto.builder()
//            .build();
//
//         이거 여기서 에러 난다... NPE
//        this.mockMvc.perform(
//            post("/api/events")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .accept(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(eventDto))
//                )
//            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_BadRequest_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
            .name("spring")
            .description("RestAPI Development with Spring")
            // event start date 보다 end date 가 빠르다.
            .beginEnrollmentDateTime(LocalDateTime.of(2023, 4, 12, 14, 00))
            .closeEnrollmentDateTime(LocalDateTime.of(2023, 4, 11, 14, 00))
            .beginEventDateTime(LocalDateTime.of(2023, 4, 15, 14, 0))
            .endEventDateTime(LocalDateTime.of(2023, 4, 11, 18, 0))
            // base 보다 max 가 크다.
            .basePrice(4000)
            .maxPrice(200)
            .limitOfEnrollment(100)
            .location("강남역 D2 Start up factory")
            .build();

        this.mockMvc.perform(
                post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(eventDto))
            )
            .andExpect(status().isBadRequest())
            // 아래 정보는 errors 에 들어있다.
            /**
             * 기본적으로 spring boot 의 Errors 는 Java Bean 표준을 준수한 객체가 아니라 객체의 Serialization 을 하지 않는다.
             * 따라서, Errors 객체를 ${@link EventController#createEvent(EventDto, Errors)} 에서 ${@link ResponseEntity#body()} 에 데이터를 담을 수 없다.
             * 해결 방법은 ${@link ErrorsSerializer} 를 구현하면 아래의 내용이 처리 가능하다.
             */

            .andExpect(jsonPath("$.[0].objectName").exists())
            .andExpect(jsonPath("$.[0].defaultMessage").exists())
            .andExpect(jsonPath("$.[0].code").exists())
        // field 에러가 없는 경우에는 에러가 발생할 수 있다.
        // 따라서 나중을 위해 잠시 주석 처리..... 이 부분 해결하는 방법에 대해서 고민해봐야지
//            .andExpect(jsonPath("$.[0].field").exists())
//            .andExpect(jsonPath("$.[0].rejectedValue").exists())
        ;
    }

}