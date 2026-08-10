package com.rafay.match_service.controller.SwipReq;

import com.rafay.match_service.testConfig.ContainerInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class)
public class SwipeEventControllerTestIT extends ContainerInfo {
    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql(scripts = "/sqlScripts/BothUserSwipedScript.sql")
    void alreadySwipedCase(CapturedOutput output) throws Exception {
        String requestBody = """
                {
                    "swipedId": "userId456",
                    "swipeDirection": "right"
                }
                """;

        String swiperId = "userId123";
        mockMvc.perform(post("/swipe/events").contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", swiperId)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Swipe event processed successfully"));

        System.out.println(output.getAll());

        assertThat(output)
                .contains("Duplicate swipe ignored — swiper: userId123, swiped: userId456");
    }

    @Test
    void NewlySwipedCase(CapturedOutput output) throws Exception {
        String requestBody = """
                {
                    "swipedId": "userId56",
                    "swipeDirection": "right"
                }
                """;

        String swiperId = "userId123";
        mockMvc.perform(post("/swipe/events").contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", swiperId)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Swipe event processed successfully"));

        System.out.println(output.getAll());

        assertThat(output)
                .contains("No match yet — waiting for other person to swipe");
    }

}
