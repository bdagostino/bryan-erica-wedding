package net.ddns.buckeyeflash.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DetailsControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .alwaysDo(print())
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void testAccommodationsRoot() throws Exception {
        this.mockMvc.perform(get("/details/accommodations")).andExpect(status().isOk()).andExpect(view().name("pages/details/accommodations"))
                .andExpect(model().attributeExists("googleEmbeddedApiKey", "staybridgeHotelAddress"));
    }

    @Test
    public void testLocationRoot() throws Exception {
        this.mockMvc.perform(get("/details/location")).andExpect(status().isOk()).andExpect(view().name("pages/details/location"))
                .andExpect(model().attributeExists("googleEmbeddedApiKey", "weddingLocationAddress"));
    }

}