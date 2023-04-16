package my.colibear.study.restapi.events.hateoas;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import my.colibear.study.restapi.events.Event;
import org.springframework.hateoas.RepresentationModel;

public class EventResource extends RepresentationModel {
    @JsonUnwrapped
    private Event event;

    public EventResource(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return this.event;
    }
}
