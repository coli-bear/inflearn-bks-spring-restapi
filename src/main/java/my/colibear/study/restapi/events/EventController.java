package my.colibear.study.restapi.events;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.colibear.study.restapi.common.resource.ErrorResource;
import my.colibear.study.restapi.events.hateoas.EventResource2;
import my.colibear.study.restapi.events.mapper.EventMapper;
import my.colibear.study.restapi.events.validator.MyEventValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.Stream;

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
                .body(new ErrorResource(errors));
//                .body(errors);
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
        EventResource2 eventResource = new EventResource2(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(linkTo(EventController.class).withRel("update-event"));

        String profileHref = "/docs/index.html#resources-events-create";
        eventResource.add(Link.of(profileHref).withRel("profile"));


        return ResponseEntity
            .created(selfLinkBuilder.toUri())
            .body(eventResource)
            ;
    }

    @GetMapping
    public ResponseEntity queryEvents(@PageableDefault Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> findEvents = this.eventRepository.findAll(pageable);

        PagedModel<EntityModel<Event>> model = assembler.toModel(findEvents, entity -> new EventResource2(entity));

        String profileHref = "/docs/index.html#resources-events-list";
        model.add(Link.of(profileHref).withRel("profile"));

        return ResponseEntity
            .ok(model);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Long id) {
        Optional<Event> byId = eventRepository.findById(id);

        if (byId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }


        EventResource2 body = new EventResource2(byId.get());
        String profileHref = "/docs/index.html#resources-get-an-event";
        body.add(Link.of(profileHref).withRel("profile"));
        return ResponseEntity.ok(body);

    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Long id, @RequestBody @Valid EventDto eventDto, Errors errors) {
        Optional<Event> byId = this.eventRepository.findById(id);
        if (byId.isEmpty()) {
            return ResponseEntity.notFound()
                .build();
        }

        myEventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest()
                .body(new ErrorResource(errors));
        }

        Event event = byId.get();


        eventMapper.map(eventDto, event);
        eventRepository.save(event);


        EventResource2 eventResource = new EventResource2(event);
        String profileHref = "/docs/index.html#resources-update-event";
        eventResource.add(Link.of(profileHref).withRel("profile"));

        return ResponseEntity.ok().body(eventResource);
    }
}
