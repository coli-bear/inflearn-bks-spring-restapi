package my.colibear.study.restapi.events.validator;

import my.colibear.study.restapi.events.EventDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

// Case 1:
@Component
public class MyEventValidator {

    public void validate(EventDto eventDto, Errors errors) {
        if (eventDto.getMaxPrice() < eventDto.getBasePrice() &&
            eventDto.getMaxPrice() > 0
        ) {
            Object[] args = {eventDto.getMaxPrice(), eventDto.getBasePrice()};
//            errors.reject("BadRequest", args, "BasePrice or MaxPrice is wrong");
            errors.rejectValue("basePrice", "wrongValue", "basePrice was wrong");
            errors.rejectValue("maxPrice", "wrongValue", "maxPrice was wrong");
        }

        LocalDateTime endEventTime = eventDto.getEndEventDateTime();
        if (endEventTime == null ||
            endEventTime.isBefore(eventDto.getBeginEventDateTime()) ||
            endEventTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
            endEventTime.isBefore(eventDto.getBeginEnrollmentDateTime())
        ) {
            errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime was wrong");
        }

        // TODO beginEventDateTime


        // TODO closeEnrollmentDateTime
    }
}
