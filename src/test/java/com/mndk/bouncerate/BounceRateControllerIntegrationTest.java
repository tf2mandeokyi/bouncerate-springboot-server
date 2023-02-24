package com.mndk.bouncerate;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
@ContextConfiguration
@SuppressWarnings("UnusedDeclaration")
public class BounceRateControllerIntegrationTest {

    private static boolean setupIsDone = false;

    @Autowired
    private MockMvc mvc;

    @Before
    public void prepareBounceRate() throws Exception {
        if(setupIsDone) return;
        mvc.perform(post("/api/v1/categories").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"상품 그룹 1\"}"));
        mvc.perform(post("/api/v1/categories").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"상품 그룹 2\"}"));
        mvc.perform(post("/api/v1/setTopBoxes").contentType(MediaType.APPLICATION_JSON)
                .content("{\"location\": \"경기도\"}"));
        mvc.perform(post("/api/v1/setTopBoxes").contentType(MediaType.APPLICATION_JSON)
                .content("{\"location\": \"서울특별시\"}"));
        setupIsDone = true;
    }

    @Test
    public void test1_getNull() throws Exception {
        mvc.perform(get("/api/v1/bounceRates/setTopBox/1/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.value", is(nullValue())));
    }

    @Test
    public void test2_set() throws Exception {
        mvc.perform(post("/api/v1/bounceRates/setTopBox/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("12.3456"))
                .andExpect(status().isOk());

        mvc.perform(get("/api/v1/bounceRates/setTopBox/1/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.value", is(12.3456)));
    }
}
