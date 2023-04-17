package my.colibear.study.restapi.index;

import my.colibear.study.restapi.common.RestDocsConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class IndexControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    public void index() throws Exception{
        // index 에서는 각각의 resource 가 나오길 바란다.
        this.mockMvc.perform(get("/api"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("_links.events").exists());
    }
}
