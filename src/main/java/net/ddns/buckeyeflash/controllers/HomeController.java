package net.ddns.buckeyeflash.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;


@Controller
public class HomeController {

    private static final Logger logger = LogManager.getLogger(HomeController.class);

    private static final LocalDate WEDDING_DAY = LocalDate.of(2019, 9, 20);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(ModelMap modelMap) {
        final long dateDifference = DAYS.between(LocalDate.now(), WEDDING_DAY);
        modelMap.addAttribute("remainingDays", dateDifference > 0 ? dateDifference : 0);
        logger.info("Home Page Accessed");
        return "pages/home";
    }
}
