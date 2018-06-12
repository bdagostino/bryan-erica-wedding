package net.ddns.buckeyeflash.controllers.admin;

import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.datatable.DatatableRequest;
import net.ddns.buckeyeflash.models.datatable.DatatableResponse;
import net.ddns.buckeyeflash.models.datatable.Search;
import net.ddns.buckeyeflash.repositories.GuestRepository;
import net.ddns.buckeyeflash.serializers.GuestSerializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GuestAdminControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private GuestRepository guestRepository;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .alwaysDo(print())
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN_READ")
    public void testGuestRoot() throws Exception {
        this.mockMvc.perform(get("/admin/guest")).andExpect(status().isOk())
                .andExpect(model().attributeExists("canAdminEdit"));
    }

    @Test
    @WithMockUser
    public void testGuestRootInvalidRole() throws Exception {
        this.mockMvc.perform(get("/admin/guest")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN_READ")
    public void testGuestRootModelElementsNoParam() throws Exception {
        this.mockMvc.perform(get("/admin/guest"))
                .andExpect(model().attribute("alertStatus", (Object) null))
                .andExpect(model().attribute("canAdminEdit", false));
    }

    @Test
    @WithMockUser(roles = "ADMIN_READ")
    public void testGuestRootModelElementsWithStatusParam() throws Exception {
        this.mockMvc.perform(get("/admin/guest").param("status", "success"))
                .andExpect(model().attribute("alertStatus", "success"))
                .andExpect(model().attribute("canAdminEdit", false));
    }

    @Test
    @WithMockUser(roles = "ADMIN_EDIT")
    public void testGuestRootModelElementsWithStatusParamAndEditRole() throws Exception {
        this.mockMvc.perform(get("/admin/guest").param("status", "success"))
                .andExpect(model().attribute("alertStatus", "success"))
                .andExpect(model().attribute("canAdminEdit", true));
    }

    @Test
    @WithMockUser(roles = "ADMIN_READ")
    public void testGetGuestDataAdminRead() throws Exception {
        testGetGuestData("Hello World", 1);
    }

    @Test
    @WithMockUser(roles = "ADMIN_EDIT")
    public void testGetGuestDataAdminEdit() throws Exception {
        testGetGuestData("Hello", 2);
    }

    @Test
    @WithMockUser
    public void testGetGuestDataInvalidRole() throws Exception {
        this.mockMvc.perform(post("/admin/guest/getGuestData")
                .content(json().build().writeValueAsString(buildTestDatatableRequest("hello", 1)))
                .contentType(MediaType.APPLICATION_JSON).with(csrf())).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void testOpenGuestModalNonAdminRole() throws Exception {
        this.mockMvc.perform(post("/admin/guest/openGuestModal").with(csrf())).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN_EDIT")
    public void testOpenGuestModalAdminEditRoleEmptyGuestId() throws Exception {
        this.mockMvc.perform(post("/admin/guest/openGuestModal").with(csrf()).param("guestId", ""))
                .andExpect(view().name("redirect:/admin/guest?status=error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN_EDIT")
    public void testOpenGuestModalAdminEditRole() throws Exception {
        when(guestRepository.findById(22)).thenReturn(Optional.of(new Guest(22, "Hello", "World")));
        this.mockMvc.perform(post("/admin/guest/openGuestModal").with(csrf()).param("guestId", "22"))
                .andExpect(view().name("fragments/admin/guest_fragments :: guestModalContent"));
    }


    private void testGetGuestData(final String searchCriteria, final int expectedDrawCount) throws Exception {
        final Guest helloGuest = new Guest(1, "Hello", "World");
        final Guest worldGuest = new Guest(2, "Hello", "GoodBye");

        final List<Guest> singleGuestList = Collections.singletonList(helloGuest);
        final List<Guest> testGuestList = Arrays.asList(helloGuest, worldGuest);

        final Page<Guest> singleGuest = new PageImpl<>(singleGuestList);
        final Page<Guest> guests = new PageImpl<>(testGuestList);
        when(guestRepository.findByFirstNameStartingWithAndLastNameStartingWithOrFirstNameStartingWithAndLastNameStartingWith(any(), any(), any(), any(), any()))
                .thenReturn(singleGuest);
        when(guestRepository.findByFirstNameStartingWithOrLastNameStartingWith(any(), any(), any())).thenReturn(guests);

        final List<Guest> expectedResultList;
        if (expectedDrawCount > 1) {
            expectedResultList = testGuestList;
        } else {
            expectedResultList = singleGuestList;
        }
        // @formatter:off
        this.mockMvc.perform(post("/admin/guest/getGuestData")
                .content(json().build().writeValueAsString(buildTestDatatableRequest(searchCriteria, expectedDrawCount)))
                .contentType(MediaType.APPLICATION_JSON).with(csrf())).andDo(print())
                .andExpect(content().json(json().serializers(new GuestSerializer()).build().writeValueAsString(buildTestDatatableResponse(expectedResultList, expectedDrawCount))));
        // @formatter:on
    }

    private DatatableRequest buildTestDatatableRequest(final String searchCriteria, final int draw) {
        final DatatableRequest datatableRequest = new DatatableRequest();
        final Search search = new Search();
        search.setValue(searchCriteria);
        datatableRequest.setDraw(draw);
        datatableRequest.setStart(0);
        datatableRequest.setLength(10);
        datatableRequest.setSearch(search);
        return datatableRequest;
    }

    private DatatableResponse buildTestDatatableResponse(final List<Guest> data, final int draw) {
        final DatatableResponse<Guest> datatableResponse = new DatatableResponse<>();
        datatableResponse.setData(data);
        datatableResponse.setRecordsTotal(data.size());
        datatableResponse.setDraw(draw);
        datatableResponse.setRecordsFiltered(data.size());
        return datatableResponse;
    }
}
