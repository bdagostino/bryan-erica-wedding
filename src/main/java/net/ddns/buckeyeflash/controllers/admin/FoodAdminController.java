package net.ddns.buckeyeflash.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.buckeyeflash.models.CommonConstants;
import net.ddns.buckeyeflash.models.Food;
import net.ddns.buckeyeflash.models.datatable.DatatableRequest;
import net.ddns.buckeyeflash.models.datatable.DatatableResponse;
import net.ddns.buckeyeflash.repositories.FoodRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class FoodAdminController extends BaseAdminController implements CommonConstants {

    private static final Logger logger = Logger.getLogger(FoodAdminController.class);

    private static final String FOOD_ATTRIBUTE_NAME = "food";
    private static final String ADD_FOOD_MODAL_TITLE = "Add Food";
    private static final String UPDATE_FOOD_MODAL_TITLE = "Update Food";
    private static final String FOOD_MODAL_CONTENT_FRAGMENT = "fragments/admin/food_fragments :: foodModalContent(title='%s')";

    @Autowired
    private FoodRepository foodRepository;

    @PreAuthorize("hasAnyRole('ADMIN_EDIT','ADMIN_READ')")
    @RequestMapping(value = "/admin/food", method = RequestMethod.GET)
    public String food(ModelMap modelMap, SecurityContextHolderAwareRequestWrapper securityContextHolderAwareRequestWrapper) {
        modelMap.put("canAdminEdit", securityContextHolderAwareRequestWrapper.isUserInRole("ADMIN_EDIT"));
        logger.info("Admin Food Page Accessed");
        return "pages/admin/food";
    }

    @PreAuthorize("hasAnyRole('ADMIN_EDIT','ADMIN_READ')")
    @RequestMapping(value = "/admin/food/getFoodData", method = RequestMethod.POST)
    public @ResponseBody
    String getFoodData(@RequestBody DatatableRequest request) throws Exception {
        PageRequest pageRequest = new PageRequest((int) Math.floor(request.getStart() / request.getLength()), request.getLength());
        Page<Food> foods = foodRepository.findByTypeStartingWith(request.getSearch().getValue(), pageRequest);
        ObjectMapper objectMapper = new ObjectMapper();
        DatatableResponse datatableResponse = new DatatableResponse();
        datatableResponse.setDraw(request.getDraw());
        datatableResponse.setRecordsFiltered((int) foods.getTotalElements());
        datatableResponse.setRecordsTotal((int) foods.getTotalElements());
        datatableResponse.setData(foods.getContent());
        logger.info("Retrieved Food Data From Database");
        return objectMapper.writeValueAsString(datatableResponse);
    }

    @PreAuthorize("hasRole('ADMIN_EDIT')")
    @RequestMapping(value = "/admin/food/openFoodModal", method = RequestMethod.POST)
    public String openInvitationModal(String foodId, ModelMap modelMap) {
        modelMap.clear();
        if (StringUtils.isNotBlank(foodId)) {
            Food food = foodRepository.findById(Integer.parseInt(foodId));
            modelMap.addAttribute(FOOD_ATTRIBUTE_NAME, food);
        } else {
            modelMap.addAttribute(FOOD_ATTRIBUTE_NAME, new Food());
        }
        return generateFoodModalContentLocator(foodId);
    }

    @PreAuthorize("hasRole('ADMIN_EDIT')")
    @RequestMapping(value = "/admin/food/saveFood", method = RequestMethod.POST)
    public String saveFood(@Valid @ModelAttribute Food food, Errors errors) {
        if (errors.hasErrors()) {
            return generateFoodModalContentLocator(food.getId());
        }
        if (food.getId() != null) {
            Food storedFood = foodRepository.findById(food.getId());
            storedFood.setType(food.getType());
            storedFood.setDescription(food.getDescription());
            try {
                foodRepository.save(storedFood);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return String.format(MODAL_ERROR_FRAGMENT_TEMPLATE, "Food");
            }
        } else {
            //Add Food
            try {
                foodRepository.save(food);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return String.format(MODAL_ERROR_FRAGMENT_TEMPLATE, "Food");
            }
        }
        return String.format(MODAL_SUCCESS_FRAGMENT_TEMPLATE, "Food");
    }

    @PreAuthorize("hasRole('ADMIN_EDIT')")
    @RequestMapping(value = "/admin/food/removeFood", method = RequestMethod.POST)
    public String removeFood(String foodId) {
        if (StringUtils.isNotBlank(foodId)) {
            Food storedFood = foodRepository.findById(Integer.parseInt(foodId));
            try {
                foodRepository.delete(storedFood);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return String.format(MODAL_ERROR_FRAGMENT_TEMPLATE, "Food");
            }
        } else {
            logger.error("Food Id missing cannot delete");
            return String.format(MODAL_ERROR_FRAGMENT_TEMPLATE, "Food");
        }
        return String.format(MODAL_SUCCESS_FRAGMENT_TEMPLATE, "Food");
    }

    private String generateFoodModalContentLocator(Integer foodId) {
        return generateFoodModalContentLocator(foodId != null ? foodId.toString() : null);
    }

    private String generateFoodModalContentLocator(String foodId) {
        if (StringUtils.isNotBlank(foodId)) {
            return String.format(FOOD_MODAL_CONTENT_FRAGMENT, UPDATE_FOOD_MODAL_TITLE);
        }
        return String.format(FOOD_MODAL_CONTENT_FRAGMENT, ADD_FOOD_MODAL_TITLE);
    }
}
