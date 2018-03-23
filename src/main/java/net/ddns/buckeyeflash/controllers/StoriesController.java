package net.ddns.buckeyeflash.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "stories")
public class StoriesController {

    private static final Logger logger = LogManager.getLogger(StoriesController.class);

    @RequestMapping(value = "/our-story", method = RequestMethod.GET)
    public String ourStory() {
        logger.debug("Our Story Page Accessed");
        return "pages/stories/our-story";
    }

    @RequestMapping(value = "/bridesmaids", method = RequestMethod.GET)
    public String bridesmaids() {
        logger.debug("Bridesmaids Page Accessed");
        return "pages/stories/bridesmaids";
    }

    @RequestMapping(value = "/groomsmen", method = RequestMethod.GET)
    public String groomsmen() {
        logger.debug("Groomsmen Page Accessed");
        return "pages/stories/groomsmen";
    }
}
