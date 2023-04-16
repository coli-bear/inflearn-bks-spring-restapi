package my.colibear.study.restapi.events;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.colibear.study.restapi.events.hateoas.EventResource;
import my.colibear.study.restapi.events.hateoas.EventResource2;
import my.colibear.study.restapi.events.mapper.EventMapper;
import my.colibear.study.restapi.events.validator.MyEventValidator;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/events", produces = MediaType.APPLICATION_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final MyEventValidator myEventValidator;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        // case 1:
        myEventValidator.validate(eventDto, errors);

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest()
                .body(errors);
        }


        Event event = eventMapper.toEvent(eventDto);
        event.update();
        eventRepository.save(event);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(event.getId());
//        EventResource eventResource = new EventResource(event);
//        EventResource2 eventResource = new EventResource2(event);

//        eventResource.add(
//            linkTo(EventController.class).withRel("query-events"),
//            selfLinkBuilder.withSelfRel(),
//            selfLinkBuilder.withRel("update-event")
//        );
        EventResource2 eventResource = new EventResource2(event,
            linkTo(EventController.class).withRel("query-events"),
            selfLinkBuilder.withRel("update-event")
        );

        return ResponseEntity
            .created(selfLinkBuilder.toUri())
            .body(eventResource)
        ;
    }
}
