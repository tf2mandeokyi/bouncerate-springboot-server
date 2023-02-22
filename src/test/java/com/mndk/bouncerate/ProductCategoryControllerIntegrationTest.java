package com.mndk.bouncerate;

import com.mndk.bouncerate.db.ProductCategoryDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@SuppressWarnings("UnusedDeclaration")
public class ProductCategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    ProductCategoryDAO categoryDAO;

    @Test
    public void givenInsert_whenGet_thenStatus200() throws Exception {
        categoryDAO.addOne("식료품");
        categoryDAO.addOne("전자기기");
        categoryDAO.addOne("옷/의류");

        mvc.perform(get("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name", is("식료품")))
                .andExpect(jsonPath("$[1].name", is("전자기기")))
                .andExpect(jsonPath("$[2].name", is("옷/의류")));
    }

}
