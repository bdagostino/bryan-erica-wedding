package net.ddns.buckeyeflash.controllers.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.ddns.buckeyeflash.models.Food;
import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.datatable.DatatableRequest;
import net.ddns.buckeyeflash.models.datatable.DatatableResponse;
import net.ddns.buckeyeflash.repositories.FoodRepository;
import net.ddns.buckeyeflash.repositories.GuestRepository;
import net.ddns.buckeyeflash.serializers.GuestSerializer;
import net.ddns.buckeyeflash.utilities.PageUtils;
import net.ddns.buckeyeflash.validators.admin.GuestSaveValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/admin/guest")
@SessionAttributes("guest")
public class GuestAdminController {
    private static final String REDIRECT = "redirect:";
    private static final String GUEST_ERROR_URL = "/admin/guest?status=error";
    private static final String GUEST_SUCCESS_URL = "/admin/guest?status=success";
    private static final String GUEST_MODAL_TYPE = "Guest";
    private static final String GUEST_ATTRIBUTE_NAME = "guest";

    private static final Logger logger = LogManager.getLogger(GuestAdminController.class);
    private static final String GUEST_MODAL_CONTENT_FRAGMENT = "fragments/admin/guest_fragments :: guestModalContent";

    private final GuestRepository guestRepository;

    private final FoodRepository foodRepository;

    private final GuestSaveValidator guestSaveValidator;

    public GuestAdminController(GuestRepository guestRepository, FoodRepository foodRepository, GuestSaveValidator guestSaveValidator) {
        this.guestRepository = guestRepository;
        this.foodRepository = foodRepository;
        this.guestSaveValidator = guestSaveValidator;
    }

    @PreAuthorize("hasAnyRole('ADMIN_EDIT','ADMIN_READ')")
    @RequestMapping(method = RequestMethod.GET)
    public String guest(@RequestParam(required = false) String status, final ModelMap modelMap, final SecurityContextHolderAwareRequestWrapper securityContextHolderAwareRequestWrapper) {
        modelMap.put("canAdminEdit", securityContextHolderAwareRequestWrapper.isUserInRole("ADMIN_EDIT"));
        modelMap.put("alertStatus", status);
        logger.info("Admin Guest Page Accessed");
        return "pages/admin/guest";
    }

    @PreAuthorize("hasAnyRole('ADMIN_EDIT','ADMIN_READ')")
    @RequestMapping(value = "/getGuestData", method = RequestMethod.POST)
    public @ResponseBody
    String getGuestData(@RequestBody DatatableRequest request) throws JsonProcessingException {
        final String searchParameter = request.getSearch().getValue().trim();
        final String[] splitSearch = StringUtils.split(searchParameter, StringUtils.SPACE, 3);
        final Page<Guest> guests;
        if (splitSearch.length > 1) {
            guests = guestRepository.findByFirstNameStartingWithAndLastNameStartingWithOrFirstNameStartingWithAndLastNameStartingWith(splitSearch[0], splitSearch[1], splitSearch[1], splitSearch[0], PageUtils.getPageRequest(request.getStart(), request.getLength()));
        } else {
            guests = guestRepository.findByFirstNameStartingWithOrLastNameStartingWith(searchParameter, searchParameter, PageUtils.getPageRequest(request.getStart(), request.getLength()));
        }
        final ObjectMapper objectMapper = new ObjectMapper();
        final SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(new GuestSerializer());
        objectMapper.registerModule(simpleModule);
        final DatatableResponse<Guest> datatableResponse = new DatatableResponse<>();
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
        final List<Food> foodList = new ArrayList<>();
        foodRepository.findAll().forEach(foodList::add);
        Guest guest = null;
        if (StringUtils.isNotBlank(guestId)) {
            final Optional<Guest> optionalGuest = guestRepository.findById(Integer.parseInt(guestId));
            if (optionalGuest.isPresent()) {
                guest = optionalGuest.get();
            } else {
                logger.error("Id %s could not be retrieved from the database", guestId);
            }
        }
        if (guest == null) {
            return REDIRECT + GUEST_ERROR_URL;
        }
        modelMap.addAttribute(GUEST_ATTRIBUTE_NAME, guest);
        modelMap.addAttribute("foodList", foodList);
        return GUEST_MODAL_CONTENT_FRAGMENT;
    }

    @PreAuthorize("hasRole('ADMIN_EDIT')")
    @RequestMapping(value = "/saveGuest", method = RequestMethod.POST)
    public String saveGuest(@Valid @ModelAttribute Guest guest, Errors errors) {
        guestSaveValidator.validate(guest, errors);
        if (errors.hasErrors()) {
            return REDIRECT + GUEST_ERROR_URL;
        }
        if (guest.getId() != null) {
            logger.info("Searching for guestId: {}", guest.getId());
            final Optional<Guest> optionalGuest = guestRepository.findById(guest.getId());
            if (optionalGuest.isPresent()) {
                final Guest storedGuest = optionalGuest.get();
                if (guest.getFood() != null && guest.getFood().getId() != null) {
                    final Optional<Food> optionalFood = foodRepository.findById(guest.getFood().getId());
                    if (optionalFood.isPresent()) {
                        storedGuest.setFood(optionalFood.get());
                    } else {
                        logger.error("Food information could not be found while saving guest");
                        return REDIRECT + GUEST_ERROR_URL;
                    }
                } else {
                    storedGuest.setFood(null);
                }
                storedGuest.setDietaryConcerns(guest.getDietaryConcerns());
                storedGuest.setDietaryComments(guest.getDietaryComments());
                storedGuest.setCeremonyAttendance(guest.getCeremonyAttendance());
                storedGuest.setReceptionAttendance(guest.getReceptionAttendance());
                try {
                    guestRepository.save(storedGuest);
                    return REDIRECT + GUEST_SUCCESS_URL;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return REDIRECT + GUEST_ERROR_URL;
                }
            }
        }
        logger.error("Guest information could not be found");
        return REDIRECT + GUEST_ERROR_URL;
    }
}
