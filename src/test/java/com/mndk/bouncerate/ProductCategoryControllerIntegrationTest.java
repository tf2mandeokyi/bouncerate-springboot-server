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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
@ContextConfiguration
@SuppressWarnings("UnusedDeclaration")
public class ProductCategoryControllerIntegrationTest {

    private static final String[] CATEGORY_NAMES = new String[] { "식로퓸", "전자기구", "옷/의류" };
    private static boolean setupIsDone = false;

    @Autowired
    private MockMvc mvc;

    @Before
    public void prepareCategories() throws Exception {
        if(setupIsDone) return;
        for (String categoryName : CATEGORY_NAMES) {
            mvc.perform(post("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\": \"" + categoryName + "\"}"))
                    .andExpect(status().isOk());
        }
        setupIsDone = true;
    }

    @Test
    public void test1_getAll() throws Exception {
        ResultActions resultActions = mvc.perform(get("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        for (int i = 0; i < CATEGORY_NAMES.length; i++) {
            resultActions.andExpect(jsonPath("$[" + i + "].name", is(CATEGORY_NAMES[i])));
        }
    }

    @Test
    public void test1_get() throws Exception {
        mvc.perform(get("/api/v1/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("식로퓸")));
    }

    @Test
    public void test1_getError() throws Exception {
        mvc.perform(get("/api/v1/categories/4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test1_count() throws Exception {
        mvc.perform(get("/api/v1/categories/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.value", is(3)));
    }

    @Test
    @Rollback
    public void test2_update() throws Exception {
        mvc.perform(post("/api/v1/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"식료품\"}"))
                .andExpect(status().isOk());

        mvc.perform(get("/api/v1/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("식료품")));
    }

    @Test
    @Rollback
    public void test2_updateError() throws Exception {
        mvc.perform(post("/api/v1/categories/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"占쏙옙\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Rollback
    public void test3_delete() throws Exception {
        mvc.perform(delete("/api/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ResultActions resultActions = mvc.perform(get("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        for (int i = 0; i < CATEGORY_NAMES.length - 1; i++) {
            resultActions.andExpect(jsonPath("$[" + i + "].name", is(CATEGORY_NAMES[i + 1])));
        }
    }

    @Test
    @Rollback
    public void test3_deleteError() throws Exception {
        mvc.perform(delete("/api/v1/categories/4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

}
