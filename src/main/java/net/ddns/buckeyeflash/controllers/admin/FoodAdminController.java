package net.ddns.buckeyeflash.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.buckeyeflash.models.Food;
import net.ddns.buckeyeflash.models.datatable.DatatableRequest;
import net.ddns.buckeyeflash.models.datatable.DatatableResponse;
import net.ddns.buckeyeflash.models.modal.AjaxResponse;
import net.ddns.buckeyeflash.repositories.FoodRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
public class FoodAdminController extends BaseAdminController {

    private static final Logger logger = Logger.getLogger(FoodAdminController.class);

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
    @RequestMapping(value = "/admin/food/addFood", method = RequestMethod.POST)
    public ResponseEntity<AjaxResponse> addFood(@Valid @RequestBody Food food, Errors errors) {
        if (errors.hasErrors()) {
            AjaxResponse foodAjaxResponse = new AjaxResponse();
            processErrors(foodAjaxResponse, errors);
            return ResponseEntity.badRequest().body(foodAjaxResponse);
        }
        foodRepository.save(food);
        logger.info("Food Saved");
        return ResponseEntity.ok(null);
    }
}
