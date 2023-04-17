package my.colibear.study.restapi.events.mapper;

import my.colibear.study.restapi.events.Event;
import my.colibear.study.restapi.events.EventDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    Event toEvent(EventDto eventDto);

    EventDto toEventDto(Event event);

    void map(EventDto eventDto, @MappingTarget Event event);
}
