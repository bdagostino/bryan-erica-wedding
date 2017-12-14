package net.ddns.buckeyeflash.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ddns.buckeyeflash.models.Food;
import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.datatable.DatatableRequest;
import net.ddns.buckeyeflash.models.datatable.DatatableResponse;
import net.ddns.buckeyeflash.models.modal.AjaxError;
import net.ddns.buckeyeflash.models.modal.AjaxResponse;
import net.ddns.buckeyeflash.repositories.FoodRepository;
import net.ddns.buckeyeflash.repositories.GuestRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
public class AdminController {

    private static final Logger logger = Logger.getLogger(AdminController.class);

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private GuestRepository guestRepository;

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String home() {
        logger.info("Admin Page Accessed");
        return "pages/adminxx";
    }

    @RequestMapping(value = "/admin/food", method = RequestMethod.GET)
    public String food() {
        logger.info("Admin Food Page Accessed");
        return "pages/admin/food";
    }

    @RequestMapping(value = "/admin/guest", method = RequestMethod.GET)
    public String guest() {
        logger.info("Admin Guest Page Accessed");
        return "pages/admin/guest";
    }

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

    @RequestMapping(value = "/admin/guest/getGuestData", method = RequestMethod.POST)
    public @ResponseBody
    String getGuestData(@RequestBody DatatableRequest request) throws Exception {
        PageRequest pageRequest = new PageRequest((int) Math.floor(request.getStart() / request.getLength()), request.getLength());
        String searchParameter = request.getSearch().getValue();
        Page<Guest> guests = guestRepository.findByFirstNameStartingWithOrLastNameStartingWith(searchParameter, searchParameter, pageRequest);
        ObjectMapper objectMapper = new ObjectMapper();
        DatatableResponse datatableResponse = new DatatableResponse();
        datatableResponse.setDraw(request.getDraw());
        datatableResponse.setRecordsFiltered((int) guests.getTotalElements());
        datatableResponse.setRecordsTotal((int) guests.getTotalElements());
        datatableResponse.setData(guests.getContent());
        logger.info("Retrieved Guest Data From Database");
        return objectMapper.writeValueAsString(datatableResponse);
    }

    @RequestMapping(value = "/admin/guest/addGuest", method = RequestMethod.POST)
    public ResponseEntity<AjaxResponse> addGuest(@Valid @RequestBody Guest guest, Errors errors) {
        if (errors.hasErrors()) {
            AjaxResponse guestAjaxResponse = new AjaxResponse();
            processErrors(guestAjaxResponse, errors);
            return ResponseEntity.badRequest().body(guestAjaxResponse);
        }
        guestRepository.save(guest);
        logger.info("Guest Saved");
        return ResponseEntity.ok(null);
    }

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

    private void processErrors(AjaxResponse ajaxResponse, Errors errors) {
        for (FieldError fieldError : errors.getFieldErrors()) {
            ajaxResponse.getFieldErrorList().add(new AjaxError(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        if (errors.hasGlobalErrors()) {
            ajaxResponse.setGlobalError(true);
        }
    }
}
