# MapStruct

Maven Repository 에서 `ModelMapper`를 찾다가보니 대부분 취약점이 있어서 대체제로 찾은 의존성

## 사용법

gradle 기준으로 작성한다.

```groovy
dependencies {
    ... 
    
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    compileOnly 'org.mapstruct:mapstruct:1.5.3.Final'
    annotationProcessor 'org.mapstruct:mapstruct-processor:1.5.3.Final'
    
    ...
}
```

근데 여기서 주의해야 할 점이 하나 있다.
의존성은 무조건 lombok 아래 두어야 한다. 그 이유는 아래에서 설명 남기겠다.

- 
```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    Event toEvent(EventDto eventDto);

}
```

위 형식으로 event Mapper 을 생성하자. 

아래는 샘플로 사용할 event 객체와 event dto 객체이다.
```java
@Entity
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) if null online
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;
}

@Data
@Builder
public class EventDto {
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) if null online
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
}

```

이 3가지만 구현하면 mapStruct 를 사용할 준비는 끝났다.

intellij 설정에서 어노테이션 프로세서 활성화 해주고 빌드를 해보자

그러면 `build.generated.sources.annotationProcessor.java` 아래 `EventMapper` 인터페이스 경로에 `EventMapperImpl` 객체가 생성되어 있는것을 확인할 수 있다.

해당 내용을 보면 아래와 같이 구현되어 있다.

```java
@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-04-16T12:31:21+0900",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.6.1.jar, environment: Java 17.0.4.1 (Amazon.com Inc.)"
)
@Component
public class EventMapperImpl implements EventMapper {

    @Override
    public Event toEvent(EventDto eventDto) {
        if ( eventDto == null ) {
            return null;
        }

        Event.EventBuilder event = Event.builder();

        event.name( eventDto.getName() );
        event.description( eventDto.getDescription() );
        event.beginEnrollmentDateTime( eventDto.getBeginEnrollmentDateTime() );
        event.closeEnrollmentDateTime( eventDto.getCloseEnrollmentDateTime() );
        event.beginEventDateTime( eventDto.getBeginEventDateTime() );
        event.endEventDateTime( eventDto.getEndEventDateTime() );
        event.location( eventDto.getLocation() );
        event.basePrice( eventDto.getBasePrice() );
        event.maxPrice( eventDto.getMaxPrice() );
        event.limitOfEnrollment( eventDto.getLimitOfEnrollment() );

        return event.build();
    }
}
```

빈 등록도 되어 있으므로 이제 사용하고 싶은 곳에서 의존성을 주입해서 바로 사용하면 된다.

```java
@RestController
public class EventController{
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    public EventController(EventMapper eventMapper, EventRepository eventRepository) {
        this.eventMapper = eventMapper;
        this.eventRepository = eventRepository;
    }
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody EventDto eventDto) {
    // Event DTO 를 Event Entity 객체로 변환 해준다.    
    Event event = eventMapper.toEvent(eventDto);
    eventRepository.save(event);

    URI createdUri = linkTo(EventController.class).slash(event.getId()).toUri();
    return ResponseEntity
        .created(createdUri)
        .body(event);
    }
}
```

## 주의사항

위에서 언급한 lombok 아래 두어야 하는 이유에대해 설명하겠다.

일단 의존성 주입을 lombok 위에 두었을때의 결과부터 먼저 보자.

```java
@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-04-16T12:47:07+0900",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.6.1.jar, environment: Java 17.0.4.1 (Amazon.com Inc.)"
)
@Component
public class EventMapperImpl implements EventMapper {

    @Override
    public Event toEvent(EventDto eventDto) {
        if ( eventDto == null ) {
            return null;
        }

        Event event = new Event();

        return event;
    }
}

```

내용을 보면 event 객체만 생성하고 다음으로는 어떠한 내용도 추가되지 않는다는것을 알 수 있다.

이유는 MapStruct 는 Lombok의 getter, setter, builder 을 이용해서 생성이 되기 때문에 Lombok 보다 먼저 선언되는 경우에는 위 같은 결과가 나올 수 있다.
 
아래는 mapStruct 의 사용법을 정리해놓은 블로그다 자세한건 저기서 참고하자.

> https://medium.com/naver-cloud-platform/%EA%B8%B0%EC%88%A0-%EC%BB%A8%ED%85%90%EC%B8%A0-%EB%AC%B8%EC%9E%90-%EC%95%8C%EB%A6%BC-%EB%B0%9C%EC%86%A1-%EC%84%9C%EB%B9%84%EC%8A%A4-sens%EC%9D%98-mapstruct-%EC%A0%81%EC%9A%A9%EA%B8%B0-8fd2bc2bc33b