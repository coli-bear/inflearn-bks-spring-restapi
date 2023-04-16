package my.colibear.study.restapi.events.hateoas;

import my.colibear.study.restapi.events.Event;
import my.colibear.study.restapi.events.EventController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource2 extends EntityModel<Event> {
    public EventResource2(Event event) {
        super(event);
    }

    public EventResource2(Event event, Link... links) {
        super(event);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(event.getId());
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
        add(links);
    }
}
