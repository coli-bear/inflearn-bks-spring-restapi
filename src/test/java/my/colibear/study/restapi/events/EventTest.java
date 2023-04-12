package my.colibear.study.restapi.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {
    @Test
    public void builder() {
        Event event = Event.builder()
            .name("Inflearn Spring REST API")
            .description("REST API development with Spring")
            .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        String name = "Event";
        String description = "Spring";

        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
        assertThat(event.getEventStatus()).isEqualTo(EventStatus.DRAFT);


    }
}