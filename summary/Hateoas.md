# HATEOAS 변경점

> https://docs.spring.io/spring-hateoas/docs/current/reference/html/

- ResourceSupport is now RepresentationModel
- Resource is now EntityModel
- Resources is now CollectionModel
- PagedResources is now PagedModel

## RepresentationModel

- Hateoas resource 객체 생성
```java
public class EventResource extends RepresentationModel {
    private Event event;

    public EventResource(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return this.event;
    }
}
```

- Controller

```java
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/events", produces = MediaType.APPLICATION_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final MyEventValidator myEventValidator;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        ...

        Event event = eventMapper.toEvent(eventDto);
        event.update();
        eventRepository.save(event);

        EventResource eventResource = new EventResource(event);
        
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(event.getId());
        eventResource.add(
            linkTo(EventController.class).withRel("query-events"),
            selfLinkBuilder.withSelfRel(),
            selfLinkBuilder.withRel("update-event")
        );
        return ResponseEntity
            .created(selfLinkBuilder.toUri())
            .body(eventResource)
        ;
    }
}

```

- 응답
```json
{
    "event": {
        "id":1,
        "name":"spring",
        "description":"RestAPI Development with Spring",
        "beginEnrollmentDateTime":"2023-04-12T14:00:00",
        "closeEnrollmentDateTime":"2023-04-13T14:00:00",
        "beginEventDateTime":"2023-04-15T14:00:00",
        "endEventDateTime":"2023-04-15T18:00:00",
        "location":"강남역 D2 Start up factory",
        "basePrice":100,
        "maxPrice":200,
        "limitOfEnrollment":100,
        "offline":true,
        "free":false,
        "eventStatus":"DRAFT"
    },
    "_links":{
        "query-events":{
            "href":"http://localhost/api/events"
        },
        "self":{
            "href":"http://localhost/api/events/1"
        },
        "update-event":{
            "href":"http://localhost/api/events/1"
        }
    }
}
```

만약에 event 객체를 상위로 꺼내고 싶다고 하면 `JsonUnwrapped annotation`을 붙인다.

```java
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
```

아래와 같은 결과를 볼 수 있다. 이게 좀더 좋을 수 있겠네요 ^^ 

```json

{
    "id":1,
    "name":"spring",
    "description":"RestAPI Development with Spring",
    "beginEnrollmentDateTime":"2023-04-12T14:00:00",
    "closeEnrollmentDateTime":"2023-04-13T14:00:00",
    "beginEventDateTime":"2023-04-15T14:00:00",
    "endEventDateTime":"2023-04-15T18:00:00",
    "location":"강남역 D2 Start up factory",
    "basePrice":100,
    "maxPrice":200,
    "limitOfEnrollment":100,
    "offline":true,
    "free":false,
    "eventStatus":"DRAFT",
    "_links":{
        "query-events":{
            "href":"http://localhost/api/events"
        },
        "self":{
            "href":"http://localhost/api/events/1"
        },
        "update-event":{
            "href":"http://localhost/api/events/1"
        }
    }
}
```

## EntityModel

이 객체는 content 를 받는데 `EntityModel#getContent` 호출시 `@JsonUnwrapped` 가 이미 존재한다.

```java
public class EntityModel<T> extends RepresentationModel<EntityModel<T>> {
    ...
    
    @Nullable
    @JsonUnwrapped
    @JsonSerialize(using = MapSuppressingUnwrappingSerializer.class)
    public T getContent() {
        return content;
    }
    
    ...
}
```
따라서 아래와 같이 구현하면 위의 `RepresentationModel` 와 동일한 결과를 기대하라 수 있다.

```java
public class EventResource extends EntityModel<Event> {
    public EventResource(Event event) {
        super(event);
    }

    public EventResource2(Event event, Link... links) {
        super(event);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(event.getId());
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
        add(links);
    }
}

```

link 의 목록을 받아서 처리할 수 있는 방법도 제공하면 좋을 듯 하다.