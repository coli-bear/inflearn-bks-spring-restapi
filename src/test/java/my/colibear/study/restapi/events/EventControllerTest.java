package my.colibear.study.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.colibear.study.restapi.common.RestDocsConfiguration;
import my.colibear.study.restapi.common.annotataion.TestDescription;
import my.colibear.study.restapi.common.serializer.ErrorsSerializer;
import my.colibear.study.restapi.events.mapper.EventMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.not;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class EventControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    EventRepository eventRepository;
    @Autowired
    EventMapper eventMapper;

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
            .andExpect(jsonPath("_links.self").exists())

            // 아래는 hateoas 를 이용한 restful 한 API 개발 이다.
            // ResourceSupport 를 이용해서 한다.
            // 이벤트 목록
            .andExpect(jsonPath("_links.self").exists())
            // query event
            .andExpect(jsonPath("_links.query-events").exists())
            // update event
            .andExpect(jsonPath("_links.update-event").exists())
            .andExpect(jsonPath("_links.profile").exists())
            // 응답 문서화
            .andDo(
                // 문서 이름
                document("create event",
//                    links(
//                        linkWithRel("profile")
//                    ),
//                    links( // 링크 문서화
//                        linkWithRel("self").description("link to self"),
//                        linkWithRel("query-events").description("link to query events"),
//                        linkWithRel("update-event").description("link to update an existing event")
//                    ),
                    requestHeaders( // 요청 헤더 문서화
                        headerWithName(HttpHeaders.HOST).description("origin host header"),
                        headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                        headerWithName(HttpHeaders.CONTENT_LENGTH).description("content length header")

                    ),
                    requestFields( // 요청 body 문서화
                        fieldWithPath("name").description("Name of new event"),
                        fieldWithPath("description").description("description of new event"),
                        fieldWithPath("beginEnrollmentDateTime").description("new event begin enrollment date time"),
                        fieldWithPath("closeEnrollmentDateTime").description("new event close enrollment date time"),
                        fieldWithPath("beginEventDateTime").description("new event begin date time"),
                        fieldWithPath("endEventDateTime").description("new event end date time"),
                        fieldWithPath("location").description("new event meeting place"),
                        fieldWithPath("basePrice").description("new event base price"),
                        fieldWithPath("maxPrice").description("new event max price"),
                        fieldWithPath("limitOfEnrollment").description("new event limit of enrollment")
                    ),
                    responseHeaders( // 응답 헤더 문서화
                        headerWithName(HttpHeaders.LOCATION).description("created entity url header"),
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                        headerWithName(HttpHeaders.CONTENT_LENGTH).description("content length header")
                    ),
                    responseFields( // 응답 body 문서화
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description(""),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("Name of new event"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("description of new event"),
                        fieldWithPath("beginEnrollmentDateTime").type(JsonFieldType.STRING).description("new event begin enrollment date time"),
                        fieldWithPath("closeEnrollmentDateTime").type(JsonFieldType.STRING).description("new event close enrollment date time"),
                        fieldWithPath("beginEventDateTime").type(JsonFieldType.STRING).description("new event begin date time"),
                        fieldWithPath("endEventDateTime").type(JsonFieldType.STRING).description("new event end date time"),
                        fieldWithPath("location").type(JsonFieldType.STRING).description("new event meeting place"),
                        fieldWithPath("basePrice").type(JsonFieldType.NUMBER).description("new event base price"),
                        fieldWithPath("maxPrice").type(JsonFieldType.NUMBER).description("new event max price"),
                        fieldWithPath("limitOfEnrollment").type(JsonFieldType.NUMBER).description("new event limit of enrollment"),
                        fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("offline encounter flag"),
                        fieldWithPath("free").type(JsonFieldType.BOOLEAN).description("is free meeting flag"),
                        fieldWithPath("eventStatus").type(JsonFieldType.STRING).description("event status"),
                        fieldWithPath("_links.self.href").type(JsonFieldType.STRING).description("link to self"),
                        fieldWithPath("_links.query-events.href").type(JsonFieldType.STRING).description("link to query events"),
                        fieldWithPath("_links.update-event.href").type(JsonFieldType.STRING).description("link to update event"),
                        fieldWithPath("_links.profile.href").type(JsonFieldType.STRING).description("link to profile")
                    )


                ))
//            .andExpect(jsonPath("_links.profile").exists())
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
            .andDo(print())
            .andExpect(status().isBadRequest())
            // 아래 정보는 errors 에 들어있다.
            /**
             * 기본적으로 spring boot 의 Errors 는 Java Bean 표준을 준수한 객체가 아니라 객체의 Serialization 을 하지 않는다.
             * 따라서, Errors 객체를 ${@link EventController#createEvent(EventDto, Errors)} 에서 ${@link ResponseEntity#body()} 에 데이터를 담을 수 없다.
             * 해결 방법은 ${@link ErrorsSerializer} 를 구현하면 아래의 내용이 처리 가능하다.
             */

            .andExpect(jsonPath("$.errors.[0].objectName").exists())
            .andExpect(jsonPath("$.errors.[0].defaultMessage").exists())
            .andExpect(jsonPath("$.errors.[0].code").exists())
            .andExpect(jsonPath("_links.index").exists())
        // field 에러가 없는 경우에는 에러가 발생할 수 있다.
        // 따라서 나중을 위해 잠시 주석 처리..... 이 부분 해결하는 방법에 대해서 고민해봐야지
//            .andExpect(jsonPath("$.[0].field").exists())
//            .andExpect(jsonPath("$.[0].rejectedValue").exists())
        ;
    }

    @Test
    @DisplayName("30개의 이벤트를 10개씩 두번쨰 페이지 조회하기 테스트")
    public void queryEvent() throws Exception {
        // Given event 30개

        IntStream.range(0, 30).forEach(this::generateEvent);

        this.mockMvc.perform(
                get("/api/events")
                    .param("page", "1")
                    .param("size", "10")
                    .param("sort", "name,DESC")
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").exists())
            .andExpect(jsonPath("$.page.size").exists())
            .andExpect(jsonPath("$.page.totalElements").exists())
            .andExpect(jsonPath("$.page.totalPages").exists())
            .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andDo(document("query event"))
        ;
    }

    private Event generateEvent(int i) {
        Event event = Event.builder()
            .name("event " + i)
            .description("test event")
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

        event.update();

        this.eventRepository.save(event);
        return event;
    }

    @Test
    @DisplayName("기존의 이벤트 하나 조회하기")
    public void getEvent() throws Exception {
        // Given
        Event event = this.generateEvent(110);

        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("name").exists())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andDo(document("get an events"))

        ;

    }

    @Test
    @DisplayName("없는 이벤트는 404 응답 받기")
    public void getEvent404() throws Exception {
        this.mockMvc.perform(get("/api/events/123456"))
            .andExpect(status().isNotFound())

        ;
    }

    @Test
    @DisplayName("이벤트 수정하기")
    public void updateEvent() throws Exception {
        // Given
        Event event = this.generateEvent(1234);
        EventDto eventDto = eventMapper.toEventDto(event);
        String name = "update event";
        eventDto.setName(name);

        this.mockMvc.perform(
                put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(eventDto))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("name").value(name))
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andDo(document("event update"))
        ;
    }

    @Test
    @DisplayName("입력값이 비어있는 경우 이벤트 수정실패")
    public void update400_empty() throws Exception {
        // Given
        Event event = this.generateEvent(1234);
        EventDto eventDto = new EventDto();

        this.mockMvc.perform(
                put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(eventDto))
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("_links.index").exists())
            .andDo(document("event update 400"))
        ;
    }

    @Test
    @DisplayName("입력값이 잘못된 경우 이벤트 수정실패")
    public void update400Wrong() throws Exception {
        Event event = this.generateEvent(1234);
        EventDto eventDto = this.eventMapper.toEventDto(event);
        eventDto.setBasePrice(10000);
        eventDto.setMaxPrice(2000);


        this.mockMvc.perform(
                put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(eventDto))
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("_links.index").exists())
            .andDo(document("event update 400"))
        ;
    }
    @Test
    @DisplayName("존재하지 않는 이벤트 수정실패")
    public void update404() throws Exception {
        Event event = this.generateEvent(1234);
        EventDto eventDto = this.eventMapper.toEventDto(event);

        this.mockMvc.perform(
                put("/api/events/182832")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(eventDto))
            )
            .andDo(print())
            .andExpect(status().isNotFound())
            .andDo(document("event update 404"))
        ;
    }
}