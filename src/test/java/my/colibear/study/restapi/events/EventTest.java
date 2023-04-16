package my.colibear.study.restapi.events;

//import junitparams.JUnitParamsRunner;
//import junitparams.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
//import org.junit.runner.RunWith;

import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

// junit 4
//@RunWith(JUnitParamsRunner.class)
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


    // junit4
//    @Test
//    @Parameters({
//        "0, 0, true",
//        "100, 0, false",
//        "0, 100, false"
//    })
    // type safe
//    @Parameters(method = "paramsForTestFree")

    @ParameterizedTest
    @CsvSource({
        "0, 0, true",
        "100, 0, false",
        "0, 100, false"
    })
    public void testFree(int basePrice, int maxPrice, boolean isFree) throws Exception {
        // given
        Event event = Event.builder()
            .basePrice(basePrice)
            .maxPrice(maxPrice)
            .build();

        // when
        event.update();
        // then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    // junit4
//    @Test
//    @Parameters({
//        "군자역 탐탐, true"
//    })
    @ParameterizedTest
    @MethodSource("isOffline")
    public void testOffline(String location, boolean isOffline) {
        // given
        Event event = Event.builder()
            .location(location)
            .build();

        // when
        event.update();

        // then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }

    /**
     * junit 5에서 사용하는 방법이다.
     * 자세한건 ${@link MethodSource} 에서 확인할 수 있다.
     */
    private static Stream<Arguments> isOffline() {
        return Stream.of(
            Arguments.of("강남역", true),
            Arguments.of(null, false),
            Arguments.of("", false)
        );
    }

    // junit4
//    private Object[] paramsForTestFree() {
//        return new Object[] {
//            new Object[] {0,0,true},
//            new Object[] {100,0,false},
//            new Object[] {0,100,false},
//            new Object[] {100,100,false}
//        };
//    }
}