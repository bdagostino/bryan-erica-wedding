package net.ddns.buckeyeflash.controllers;

import net.ddns.buckeyeflash.models.Food;
import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.Invitation;
import net.ddns.buckeyeflash.models.RsvpSearch;
import net.ddns.buckeyeflash.repositories.FoodRepository;
import net.ddns.buckeyeflash.repositories.InvitationRepository;
import net.ddns.buckeyeflash.validators.RsvpValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "rsvp")
@SessionAttributes({"invitation", "foodList"})
public class RsvpController {
    private static final Logger logger = Logger.getLogger(RsvpController.class);
    private static final String RSVP_FORM_VIEW = "pages/rsvp/rsvp_form";

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private RsvpValidator rsvpValidator;

    @RequestMapping(method = RequestMethod.GET)
    public String rsvp(ModelMap modelMap) {
        logger.info("RSVP Page Accessed");
        modelMap.addAttribute("rsvpSearch", new RsvpSearch());
        return "pages/rsvp/rsvp_search";
    }

    @RequestMapping(value = "/search")
    public String test(@Valid @ModelAttribute RsvpSearch rsvpSearch, Errors errors, ModelMap modelMap) {
        if (errors.hasErrors()) {
            return "pages/rsvp/rsvp_search";
        }
        modelMap.clear();
        Invitation invitation = invitationRepository.findByInvitationCode(rsvpSearch.getInvitationCode());
        if (invitation == null) {
            modelMap.addAttribute("errorMessage", "No invitation found for code " + rsvpSearch.getInvitationCode() + ".");
            return "pages/rsvp/rsvp_error";
        }
        List<Food> foodList = new ArrayList<>();
        foodRepository.findAll().forEach(foodList::add);
        modelMap.addAttribute("foodList", foodList);
        modelMap.addAttribute("invitation", invitation);
        return RSVP_FORM_VIEW;

    }

    @RequestMapping(value = "/submit", params = "action=addAdditionalGuest")
    public String addAdditionalGuest(@Valid @ModelAttribute Invitation invitation, Errors errors, ModelMap modelMap) {
        invitation.getGuestList().add(new Guest());
        return RSVP_FORM_VIEW;
    }

    @RequestMapping(value = "/submit", params = "action=saveInvitationRsvp")
    public String saveInvitationRsvp(@Valid @ModelAttribute Invitation invitation, Errors errors, ModelMap modelMap) {
        rsvpValidator.validate(invitation, errors);
        return RSVP_FORM_VIEW;
    }

}
