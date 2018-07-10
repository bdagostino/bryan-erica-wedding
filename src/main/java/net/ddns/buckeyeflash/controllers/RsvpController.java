package net.ddns.buckeyeflash.controllers;

import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.Invitation;
import net.ddns.buckeyeflash.models.RsvpSearch;
import net.ddns.buckeyeflash.repositories.FoodRepository;
import net.ddns.buckeyeflash.repositories.InvitationRepository;
import net.ddns.buckeyeflash.validators.RsvpValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/rsvp")
@SessionAttributes({"invitation", "foodList"})
public class RsvpController {
    private static final Logger logger = LogManager.getLogger(RsvpController.class);
    private static final String RSVP_FORM_VIEW = "pages/rsvp/rsvp_form";
    private static final String RSVP_SEARCH_VIEW = "pages/rsvp/rsvp_search";

    private final InvitationRepository invitationRepository;

    private final FoodRepository foodRepository;

    private final RsvpValidator rsvpValidator;

    public RsvpController(final InvitationRepository invitationRepository, final FoodRepository foodRepository, final RsvpValidator rsvpValidator) {
        this.invitationRepository = invitationRepository;
        this.foodRepository = foodRepository;
        this.rsvpValidator = rsvpValidator;
    }

    @GetMapping
    public ModelAndView rsvp(final ModelMap modelMap) {
        logger.info("RSVP Page Accessed");
        modelMap.clear();
        modelMap.addAttribute("rsvpSearch", new RsvpSearch());
        return new ModelAndView(RSVP_SEARCH_VIEW, modelMap);
    }

    @GetMapping(value = "/view")
    public ModelAndView rsvpView(@RequestParam(value = "invitationCode") final String invitationCode, final ModelMap modelMap) {
        modelMap.clear();
        modelMap.addAttribute("foodList", this.foodRepository.findAll());
        modelMap.addAttribute("invitation", this.invitationRepository.findByInvitationCode(invitationCode));
        return new ModelAndView(RSVP_FORM_VIEW, modelMap);
    }

    @PostMapping(value = "/search")
    public ModelAndView rsvpSearch(@Valid @ModelAttribute final RsvpSearch rsvpSearch, final Errors errors, final ModelMap modelMap) {
        if (errors.hasErrors()) {
            modelMap.addAttribute("errorMessage", "No invitation found for code " + rsvpSearch.getInvitationCode() + ".");
            return new ModelAndView(RSVP_SEARCH_VIEW, modelMap);
        }
        if (!invitationRepository.existsByInvitationCode(rsvpSearch.getInvitationCode())) {
            modelMap.addAttribute("errorMessage", "No invitation found for code " + rsvpSearch.getInvitationCode() + ".");
            return new ModelAndView(RSVP_SEARCH_VIEW, modelMap);
        }
        return new ModelAndView(new RedirectView("/rsvp/view?invitationCode=" + rsvpSearch.getInvitationCode()), modelMap);

    }

    @PostMapping(value = "/saveRsvp")
    public ModelAndView saveInvitationRsvp(@Valid @ModelAttribute final Invitation invitation, final Errors errors) {
        rsvpValidator.validate(invitation, errors);
        if (!errors.hasFieldErrors() && this.invitationRepository.saveInvitation(invitation)) {
            return new ModelAndView(new RedirectView("/"));
        }
        return new ModelAndView(RSVP_FORM_VIEW);
    }

    @PostMapping(value = "/addAdditionalGuest")
    public String addAdditionalGuest(@ModelAttribute final Invitation invitation) {
        if (invitation.getMaxGuests() != null && invitation.getGuestList().size() < invitation.getMaxGuests()) {
            invitation.getGuestList().add(new Guest());
        }
        return "fragments/rsvp/rsvp_form_fragment :: rsvpForm";
    }

    @PostMapping(value = "/removeAdditionalGuest")
    public String removeAdditionalGuest(@ModelAttribute final Invitation invitation, @RequestParam() final Integer removalIndex) {
        if (!CollectionUtils.isEmpty(invitation.getGuestList()) && removalIndex != null) {
            invitation.getGuestList().remove(removalIndex.intValue());
        }
        return "fragments/rsvp/rsvp_form_fragment :: rsvpForm";
    }

}
