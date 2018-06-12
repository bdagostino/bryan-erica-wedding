package net.ddns.buckeyeflash.controllers.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.buckeyeflash.models.Food;
import net.ddns.buckeyeflash.models.datatable.DatatableRequest;
import net.ddns.buckeyeflash.models.datatable.DatatableResponse;
import net.ddns.buckeyeflash.repositories.FoodRepository;
import net.ddns.buckeyeflash.utilities.PageUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping(value = "/admin/food")
@SessionAttributes("food")
public class FoodAdminController {

    private static final Logger logger = LogManager.getLogger(FoodAdminController.class);

    private static final String ADD_FOOD_MODAL_TITLE = "Add Food";
    private static final String UPDATE_FOOD_MODAL_TITLE = "Update Food";
    private static final String FOOD_MODAL_CONTENT_FRAGMENT = "fragments/admin/food_fragments :: foodModalContent(title='%s')";
    private static final String FOOD_ERROR_URL = "/admin/food?status=error";
    private static final String FOOD_SUCCESS_URL = "/admin/food?status=success";
    private static final String REDIRECT = "redirect:";

    private final FoodRepository foodRepository;

    public FoodAdminController(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN_EDIT','ADMIN_READ')")
    @RequestMapping(method = RequestMethod.GET)
    public String food(@RequestParam(required = false) String status, ModelMap modelMap, SecurityContextHolderAwareRequestWrapper securityContextHolderAwareRequestWrapper) {
        modelMap.put("canAdminEdit", securityContextHolderAwareRequestWrapper.isUserInRole("ADMIN_EDIT"));
        modelMap.put("alertStatus", status);
        logger.info("Admin Food Page Accessed");
        return "pages/admin/food";
    }

    @PreAuthorize("hasAnyRole('ADMIN_EDIT','ADMIN_READ')")
    @RequestMapping(value = "/getFoodData", method = RequestMethod.POST)
    public @ResponseBody
    String getFoodData(@RequestBody DatatableRequest request) throws JsonProcessingException {
        final Page<Food> foods = foodRepository.findByTypeStartingWith(request.getSearch().getValue(), PageUtils.getPageRequest(request.getStart(), request.getLength()));
        final ObjectMapper objectMapper = new ObjectMapper();
        final DatatableResponse<Food> datatableResponse = new DatatableResponse<>();
        datatableResponse.setDraw(request.getDraw());
        datatableResponse.setRecordsFiltered((int) foods.getTotalElements());
        datatableResponse.setRecordsTotal((int) foods.getTotalElements());
        datatableResponse.setData(foods.getContent());
        logger.info("Retrieved Food Data From Database");
        return objectMapper.writeValueAsString(datatableResponse);
    }

    @PreAuthorize("hasRole('ADMIN_EDIT')")
    @RequestMapping(value = "/openFoodModal", method = RequestMethod.POST)
    public String openFoodModal(String foodId, ModelMap modelMap) {
        modelMap.clear();
        Food food = new Food();
        if (StringUtils.isNotBlank(foodId)) {
            Optional<Food> optionalFood = foodRepository.findById(Integer.parseInt(foodId));
            if (optionalFood.isPresent()) {
                food = optionalFood.get();
            } else {
                logger.error("Id %s could not be retrieved from the database", foodId);
                return REDIRECT + FOOD_ERROR_URL;
            }
        }
        modelMap.addAttribute("food", food);
        return generateFoodModalContentLocator(foodId);
    }

    @PreAuthorize("hasRole('ADMIN_EDIT')")
    @RequestMapping(value = "/saveFood", method = RequestMethod.POST)
    public String saveFood(@Valid @ModelAttribute Food food, Errors errors) {
        if (errors.hasErrors()) {
            return REDIRECT + FOOD_ERROR_URL;
        }
        Food tempFood = food;
        if (food.getId() != null) {
            final Optional<Food> optionalFood = foodRepository.findById(food.getId());
            if (optionalFood.isPresent()) {
                tempFood = optionalFood.get();
                tempFood.setType(food.getType());
                tempFood.setDescription(food.getDescription());
            } else {
                return REDIRECT + FOOD_ERROR_URL;
            }
        }
        try {
            foodRepository.save(tempFood);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return REDIRECT + FOOD_ERROR_URL;
        }
        return REDIRECT + FOOD_SUCCESS_URL;
    }

    @PreAuthorize("hasRole('ADMIN_EDIT')")
    @RequestMapping(value = "/removeFood", method = RequestMethod.POST, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody
    String removeFood(@RequestBody @Nullable String foodId) {
        if (StringUtils.isNotBlank(foodId)) {
            final Optional<Food> optionalFood = foodRepository.findById(Integer.parseInt(foodId));
            if (optionalFood.isPresent()) {
                final Food storedFood = optionalFood.get();
                try {
                    foodRepository.delete(storedFood);
                    return FOOD_SUCCESS_URL;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        logger.error(String.format("Food with id %s cannot be deleted", foodId));
        return FOOD_ERROR_URL;
    }

    private String generateFoodModalContentLocator(String foodId) {
        if (StringUtils.isNotBlank(foodId)) {
            return String.format(FOOD_MODAL_CONTENT_FRAGMENT, UPDATE_FOOD_MODAL_TITLE);
        }
        return String.format(FOOD_MODAL_CONTENT_FRAGMENT, ADD_FOOD_MODAL_TITLE);
    }
}
