package net.ddns.buckeyeflash.controllers.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.ddns.buckeyeflash.models.CommonConstants;
import net.ddns.buckeyeflash.models.Food;
import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.datatable.DatatableRequest;
import net.ddns.buckeyeflash.models.datatable.DatatableResponse;
import net.ddns.buckeyeflash.models.modal.AjaxResponse;
import net.ddns.buckeyeflash.repositories.FoodRepository;
import net.ddns.buckeyeflash.repositories.GuestRepository;
import net.ddns.buckeyeflash.serializers.GuestSerializer;
import net.ddns.buckeyeflash.utilities.PageUtils;
import net.ddns.buckeyeflash.validators.admin.GuestSaveValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/guest")
@SessionAttributes({"guest","canAdminEdit"})
public class GuestAdminController extends BaseAdminController implements CommonConstants {
    private static final String GUEST_MODAL_TYPE = "Guest";
    private static final String GUEST_ATTRIBUTE_NAME = "guest";

    private static final Logger logger = Logger.getLogger(GuestAdminController.class);
    private static final String GUEST_MODAL_CONTENT_FRAGMENT = "fragments/admin/guest_fragments :: guestModalContent";

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private GuestSaveValidator guestSaveValidator;

    @PreAuthorize("hasAnyRole('ADMIN_EDIT','ADMIN_READ')")
    @RequestMapping(method = RequestMethod.GET)
    public String guest(ModelMap modelMap, SecurityContextHolderAwareRequestWrapper securityContextHolderAwareRequestWrapper) {
        modelMap.put("canAdminEdit", securityContextHolderAwareRequestWrapper.isUserInRole("ADMIN_EDIT"));
        logger.info("Admin Guest Page Accessed");
        return "pages/admin/guest";
    }

    @PreAuthorize("hasAnyRole('ADMIN_EDIT','ADMIN_READ')")
    @RequestMapping(value = "/getGuestData", method = RequestMethod.POST)
    public @ResponseBody
    String getGuestData(@RequestBody DatatableRequest request) throws JsonProcessingException {
        String searchParameter = request.getSearch().getValue().trim();
        String[] splitSearch = StringUtils.split(searchParameter, StringUtils.SPACE, 3);
        Page<Guest> guests;
        if (splitSearch.length > 1) {
            guests = guestRepository.findByFirstNameStartingWithAndLastNameStartingWithOrFirstNameStartingWithAndLastNameStartingWith(splitSearch[0], splitSearch[1], splitSearch[1], splitSearch[0], PageUtils.getPageRequest(request.getStart(), request.getLength()));
        } else {
            guests = guestRepository.findByFirstNameStartingWithOrLastNameStartingWith(searchParameter, searchParameter, PageUtils.getPageRequest(request.getStart(), request.getLength()));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(new GuestSerializer());
        objectMapper.registerModule(simpleModule);
        DatatableResponse datatableResponse = new DatatableResponse();
        datatableResponse.setDraw(request.getDraw());
        datatableResponse.setRecordsFiltered((int) guests.getTotalElements());
        datatableResponse.setRecordsTotal((int) guests.getTotalElements());
        datatableResponse.setData(guests.getContent());
        logger.info("Retrieved Guest Data From Database");
        return objectMapper.writeValueAsString(datatableResponse);
    }

    @PreAuthorize("hasRole('ADMIN_EDIT')")
    @RequestMapping(value = "/openGuestModal", method = RequestMethod.POST)
    public String openGuestnModal(String guestId, ModelMap modelMap) {
        modelMap.clear();
        List<Food> foodList = new ArrayList<>();
        foodRepository.findAll().forEach(foodList::add);
        if (StringUtils.isNotBlank(guestId)) {
            Guest guest = guestRepository.findById(Integer.parseInt(guestId));
            modelMap.addAttribute(GUEST_ATTRIBUTE_NAME, guest);

        } else {
            modelMap.addAttribute(GUEST_ATTRIBUTE_NAME, new Guest());
        }
        modelMap.addAttribute("foodList", foodList);
        return GUEST_MODAL_CONTENT_FRAGMENT;
    }

    @PreAuthorize("hasRole('ADMIN_EDIT')")
    @RequestMapping(value = "/saveGuest", method = RequestMethod.POST)
    public String saveGuest(@Valid @ModelAttribute Guest guest, Errors errors) {
        guestSaveValidator.validate(guest,errors);
        if (errors.hasErrors()) {
            return GUEST_MODAL_CONTENT_FRAGMENT;
        }
        if (guest.getId() != null) {
            Guest storedGuest = guestRepository.findById(guest.getId());
            if (guest.getFood() != null) {
                Food storedFood = foodRepository.findById(guest.getFood().getId());
                storedGuest.setFood(storedFood);
            } else {
                storedGuest.setFood(null);
            }
            storedGuest.setDietaryConcerns(guest.getDietaryConcerns());
            storedGuest.setDietaryComments(guest.getDietaryComments());
            storedGuest.setAttendance(guest.getAttendance());
            try {
                guestRepository.save(storedGuest);
            } catch (Exception e) {
                return String.format(MODAL_ERROR_FRAGMENT_TEMPLATE, GUEST_MODAL_TYPE);
            }
        } else {
            logger.error("Guest Id Missing");
            return String.format(MODAL_ERROR_FRAGMENT_TEMPLATE, GUEST_MODAL_TYPE);
        }
        return String.format(MODAL_SUCCESS_FRAGMENT_TEMPLATE, GUEST_MODAL_TYPE);
    }

    @PreAuthorize("hasRole('ADMIN_EDIT')")
    @RequestMapping(value = "/addGuest", method = RequestMethod.POST)
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
}
