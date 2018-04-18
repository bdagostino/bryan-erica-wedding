package net.ddns.buckeyeflash.controllers.admin;

import net.ddns.buckeyeflash.models.Food;
import net.ddns.buckeyeflash.models.datatable.DatatableRequest;
import net.ddns.buckeyeflash.models.datatable.DatatableResponse;
import net.ddns.buckeyeflash.models.datatable.Search;
import net.ddns.buckeyeflash.repositories.FoodRepository;
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

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FoodAdminControllerIntegrationTest {

    private static final String REDIRECT_FOOD_ERROR = "redirect:/admin/food?status=error";
    private static final String REDIRECT_FOOD_SUCCESS = "redirect:/admin/food?status=success";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private FoodRepository foodRepository;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .alwaysDo(print())
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN_READ")
    public void testFoodRoot() throws Exception {
        this.mockMvc.perform(get("/admin/food")).andExpect(status().isOk())
                .andExpect(model().attributeExists("canAdminEdit"));
    }

    @Test
    @WithMockUser
    public void testFoodRootInvalidRole() throws Exception {
        this.mockMvc.perform(get("/admin/food")).andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "ADMIN_READ")
    public void testFoodRootModelElementsNoParam() throws Exception {
        this.mockMvc.perform(get("/admin/food"))
                .andExpect(model().attribute("alertStatus", (Object) null))
                .andExpect(model().attribute("canAdminEdit", false));
    }

    @Test
    @WithMockUser(roles = "ADMIN_READ")
    public void testFoodRootModelElementsWithStatusParam() throws Exception {
        this.mockMvc.perform(get("/admin/food").param("status", "success"))
                .andExpect(model().attribute("alertStatus", "success"))
                .andExpect(model().attribute("canAdminEdit", false));
    }

    @Test
    @WithMockUser(roles = "ADMIN_EDIT")
    public void testFoodRootModelElementsWithStatusParamAndEditRole() throws Exception {
        this.mockMvc.perform(get("/admin/food").param("status", "success"))
                .andExpect(model().attribute("alertStatus", "success"))
                .andExpect(model().attribute("canAdminEdit", true));
    }

    @Test
    @WithMockUser(roles = "ADMIN_READ")
    public void testGetFoodDataAdminRead() throws Exception {
        testGetFoodData();
    }

    @Test
    @WithMockUser(roles = "ADMIN_EDIT")
    public void testGetFoodDataAdminEdit() throws Exception {
        testGetFoodData();
    }

    @Test
    @WithMockUser
    public void testGetFoodDataInvalidRole() throws Exception {
        this.mockMvc.perform(post("/admin/food/getFoodData")
                .content(json().build().writeValueAsString(buildTestDatatableRequest(1)))
                .contentType(MediaType.APPLICATION_JSON).with(csrf())).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void testOpenFoodModalNonAdminRole() throws Exception {
        this.mockMvc.perform(post("/admin/food/openFoodModal").with(csrf())).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN_READ")
    public void testOpenFoodModalAdminReadRole() throws Exception {
        this.mockMvc.perform(post("/admin/food/openFoodModal").with(csrf())).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN_EDIT")
    public void testOpenFoodModalAdminEditRoleAddFood() throws Exception {
        this.mockMvc.perform(post("/admin/food/openFoodModal").with(csrf()))
                .andExpect(view().name("fragments/admin/food_fragments :: foodModalContent(title='Add Food')"));
    }

    @Test
    @WithMockUser(roles = "ADMIN_EDIT")
    public void testOpenFoodModalAdminEditRoleUpdateFood() throws Exception {
        this.mockMvc.perform(post("/admin/food/openFoodModal").param("foodId", "1").with(csrf()))
                .andExpect(view().name("fragments/admin/food_fragments :: foodModalContent(title='Update Food')"));
    }

    @Test
    @WithMockUser(roles = "ADMIN_READ")
    public void testSaveFoodAdminReadRole() throws Exception {
        this.mockMvc.perform(post("/admin/food/saveFood").with(csrf()).sessionAttr("food", new Food())).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN_EDIT")
    public void testSaveFoodAdminEditWithModelErrors() throws Exception {
        this.mockMvc.perform(post("/admin/food/saveFood").with(csrf()).sessionAttr("food", new Food())).andExpect(view().name(REDIRECT_FOOD_ERROR));
    }

    @Test
    @WithMockUser(roles = "ADMIN_EDIT")
    public void testSaveFoodAdminEditNewFood() throws Exception {
        final Food food = new Food();
        food.setType("Chicken");
        food.setDescription("Grilled Chicken");
        this.mockMvc.perform(post("/admin/food/saveFood").with(csrf()).sessionAttr("food", food)).andExpect(view().name(REDIRECT_FOOD_SUCCESS));
        verify(foodRepository).save(food);
    }

    @Test
    @WithMockUser(roles = "ADMIN_EDIT")
    public void testSaveFoodAdminEditExistingFood() throws Exception {
        final Food steak = new Food(2);
        when(foodRepository.findById(2)).thenReturn(Optional.of(steak));
        final Food food = new Food(2);
        food.setType("Shrimp");
        food.setDescription("Grilled Shrimp");
        this.mockMvc.perform(post("/admin/food/saveFood").with(csrf()).sessionAttr("food", food)).andExpect(view().name(REDIRECT_FOOD_SUCCESS));
        verify(foodRepository).save(steak);
    }

    @Test
    @WithMockUser(roles = "ADMIN_EDIT")
    public void testSaveFoodAdminEditSaveRepoError() throws Exception {
        final Food steakFoodType = new Food(2);
        when(foodRepository.findById(2)).thenReturn(Optional.of(steakFoodType));
        when(foodRepository.save(any())).thenThrow(new EntityNotFoundException());
        final Food food = new Food();
        food.setId(2);
        food.setType("Shrimp");
        food.setDescription("Grilled Shrimp");
        this.mockMvc.perform(post("/admin/food/saveFood").with(csrf()).sessionAttr("food", food)).andExpect(view().name(REDIRECT_FOOD_ERROR));
    }

    @Test
    @WithMockUser(roles = "ADMIN_EDIT")
    public void testSaveFoodAdminEditFindNull() throws Exception {
        when(foodRepository.findById(2)).thenReturn(Optional.empty());
        final Food food = new Food();
        food.setId(2);
        food.setType("Shrimp");
        food.setDescription("Grilled Shrimp");
        this.mockMvc.perform(post("/admin/food/saveFood").with(csrf()).sessionAttr("food", food)).andExpect(view().name(REDIRECT_FOOD_ERROR));
    }

    @Test
    @WithMockUser(roles = "ADMIN_READ")
    public void testRemoveFoodAdminReadRole() throws Exception {
        this.mockMvc.perform(post("/admin/food/removeFood").with(csrf()).content("2").contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN_EDIT")
    public void testRemoveFoodAdminEditRole() throws Exception {
        final Food chickenFoodType = new Food(1);
        when(foodRepository.findById(1)).thenReturn(Optional.of(chickenFoodType));
        this.mockMvc.perform(post("/admin/food/removeFood").with(csrf()).content("1").contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string("/admin/food?status=success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN_EDIT")
    public void testRemoveFoodAdminEditRoleNullId() throws Exception {
        this.mockMvc.perform(post("/admin/food/removeFood").with(csrf()).content("").contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string("/admin/food?status=error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN_EDIT")
    public void testRemoveFoodAdminEditRoleRepoError() throws Exception {
        final Food chickenFoodType = new Food(1);
        when(foodRepository.findById(1)).thenReturn(Optional.of(chickenFoodType));
        doThrow(new EntityNotFoundException()).when(foodRepository).delete(any());
        this.mockMvc.perform(post("/admin/food/removeFood").with(csrf()).content("1").contentType(MediaType.TEXT_PLAIN)).andExpect(content().string("/admin/food?status=error"));
    }

    private void testGetFoodData() throws Exception {
        final Food chickenFoodType = new Food(1);
        chickenFoodType.setDescription("Grilled Chicken");
        chickenFoodType.setType("Chicken");
        final Food steakFoodType = new Food(2);
        steakFoodType.setDescription("Ribeye Steak");
        steakFoodType.setType("Steak");


        final List<Food> testFoodList = Arrays.asList(chickenFoodType, steakFoodType);
        final int testDraw = 2;
        final Page<Food> foods = new PageImpl<>(testFoodList);

        when(foodRepository.findByTypeStartingWith(any(), any())).thenReturn(foods);

        // @formatter:off
        this.mockMvc.perform(post("/admin/food/getFoodData")
                .content(json().build().writeValueAsString(buildTestDatatableRequest(testDraw)))
                .contentType(MediaType.APPLICATION_JSON).with(csrf())).andDo(print())
                .andExpect(content().json(json().build().writeValueAsString(buildTestDatatableResponse(testFoodList, testDraw))));
        // @formatter:on
    }

    private DatatableRequest buildTestDatatableRequest(final int draw) {
        final DatatableRequest datatableRequest = new DatatableRequest();
        final Search search = new Search();
        search.setValue("search");
        datatableRequest.setDraw(draw);
        datatableRequest.setStart(0);
        datatableRequest.setLength(10);
        datatableRequest.setSearch(search);
        return datatableRequest;
    }

    private DatatableResponse buildTestDatatableResponse(final List<Food> data, final int draw) {
        final DatatableResponse<Food> datatableResponse = new DatatableResponse<>();
        datatableResponse.setData(data);
        datatableResponse.setRecordsTotal(data.size());
        datatableResponse.setDraw(draw);
        datatableResponse.setRecordsFiltered(data.size());
        return datatableResponse;
    }


}
