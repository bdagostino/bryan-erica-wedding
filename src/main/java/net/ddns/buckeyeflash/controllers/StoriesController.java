package net.ddns.buckeyeflash.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "stories")
public class StoriesController {

    private static final Logger logger = LogManager.getLogger(StoriesController.class);

    @GetMapping(value = "/our-story")
    public ModelAndView ourStory() {
        logger.debug("Our Story Page Accessed");
        return new ModelAndView("pages/stories/our-story");
    }

    @GetMapping(value = "/bridesmaids")
    public ModelAndView bridesmaids() {
        logger.debug("Bridesmaids Page Accessed");
        return new ModelAndView("pages/stories/bridesmaids");
    }

    @GetMapping(value = "/groomsmen")
    public ModelAndView groomsmen() {
        logger.debug("Groomsmen Page Accessed");
        return new ModelAndView("pages/stories/groomsmen");
    }
}
