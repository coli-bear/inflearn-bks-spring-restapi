package my.colibear.study.restapi.index;

import my.colibear.study.restapi.BaseControllerTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
public class IndexControllerTest extends BaseControllerTest {
    @Test
    public void index() throws Exception{
        // index 에서는 각각의 resource 가 나오길 바란다.
        this.mockMvc.perform(get("/api"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("_links.events").exists());
    }
}
