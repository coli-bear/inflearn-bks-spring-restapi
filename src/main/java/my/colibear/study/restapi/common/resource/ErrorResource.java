package my.colibear.study.restapi.common.resource;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import my.colibear.study.restapi.index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorResource extends EntityModel<Errors> {
    // JsonUnwrapped 를 넣어주지 않으면 데이터가 안보인다.. 왤까?
    @JsonUnwrapped
    private final Errors errors;
    public ErrorResource(Errors errors) {
        this.errors = errors;
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}
