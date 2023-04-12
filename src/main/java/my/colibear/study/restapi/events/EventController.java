package my.colibear.study.restapi.events;

import my.colibear.study.restapi.events.mapper.EventMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaType.APPLICATION_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventController(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody EventDto eventDto) {

        Event event = eventMapper.toEvent(eventDto);
        eventRepository.save(event);

        URI createdUri = linkTo(EventController.class).slash(event.getId()).toUri();
        return ResponseEntity
            .created(createdUri)
            .body(event);
    }
}
