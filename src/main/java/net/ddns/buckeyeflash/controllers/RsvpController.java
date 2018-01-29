package net.ddns.buckeyeflash.controllers;

import net.ddns.buckeyeflash.models.Invitation;
import net.ddns.buckeyeflash.models.RsvpSearch;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "rsvp")
public class RsvpController {
    private static final Logger logger = Logger.getLogger(RsvpController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String rsvp(ModelMap modelMap) {
        logger.info("RSVP Page Accessed");
        modelMap.addAttribute("rsvpSearch",new RsvpSearch());
        return "pages/rsvp/rsvp_search";
    }

    @RequestMapping(value = "/search")
    public String test(@Valid @ModelAttribute RsvpSearch rsvpSearch, Errors errors,ModelMap modelMap){
        if (errors.hasErrors()) {
            return "pages/rsvp/rsvp_search";
        }
        modelMap.clear();
        modelMap.addAttribute("invitation",new Invitation());
        return "pages/rsvp/rsvp_form";

    }

}
