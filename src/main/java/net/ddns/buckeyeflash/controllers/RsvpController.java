package net.ddns.buckeyeflash.controllers;

import net.ddns.buckeyeflash.models.Food;
import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.Invitation;
import net.ddns.buckeyeflash.models.RsvpSearch;
import net.ddns.buckeyeflash.repositories.FoodRepository;
import net.ddns.buckeyeflash.repositories.InvitationRepository;
import net.ddns.buckeyeflash.utilities.InvitationUtils;
import net.ddns.buckeyeflash.validators.RsvpValidator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

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
            modelMap.addAttribute("errorMessage", "No invitation found for code " + rsvpSearch.getInvitationCode() + ".");
            return "pages/rsvp/rsvp_search";
        }
        modelMap.clear();
        Invitation invitation = invitationRepository.findByInvitationCode(rsvpSearch.getInvitationCode());
        if (invitation == null) {
            modelMap.addAttribute("errorMessage", "No invitation found for code " + rsvpSearch.getInvitationCode() + ".");
            return "pages/rsvp/rsvp_search";
        }
        List<Food> foodList = new ArrayList<>();
        foodRepository.findAll().forEach(foodList::add);
        modelMap.addAttribute("foodList", foodList);
        modelMap.addAttribute("invitation", invitation);
        return RSVP_FORM_VIEW;

    }

    @RequestMapping(value = "/saveRsvp")
    public String saveInvitationRsvp(@Valid @ModelAttribute Invitation invitation, Errors errors, ModelMap modelMap) {
        rsvpValidator.validate(invitation, errors);
        if(errors.hasFieldErrors()){
            return RSVP_FORM_VIEW;
        }
        boolean isSaved = InvitationUtils.saveInvitation(invitationRepository,foodRepository,invitation);
        if(isSaved){
            return "redirect:/";
        }
        return RSVP_FORM_VIEW;
    }

    @RequestMapping(value = "/addAdditionalGuest", method = RequestMethod.POST)
    public String addAdditionalGuest(@ModelAttribute Invitation invitation){
        if (invitation.getMaxGuests() != null && invitation.getGuestList().size() < invitation.getMaxGuests()) {
            invitation.getGuestList().add(new Guest());
        }
        return "fragments/rsvp/rsvp_form_fragment :: rsvpForm";
    }

    @RequestMapping(value = "/removeAdditionalGuest", method = RequestMethod.POST)
    public String removeAdditionalGuest(@ModelAttribute Invitation invitation, @RequestParam() Integer removalIndex){
        if(!CollectionUtils.isEmpty(invitation.getGuestList()) && removalIndex != null){
            invitation.getGuestList().remove(removalIndex.intValue());
        }
        return "fragments/rsvp/rsvp_form_fragment :: rsvpForm";
    }

}
