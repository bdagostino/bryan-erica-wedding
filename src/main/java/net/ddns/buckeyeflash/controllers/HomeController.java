package net.ddns.buckeyeflash.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

@Controller
@RequestMapping(value = "/")
public class HomeController {

    private static final Logger logger = LogManager.getLogger(HomeController.class);
    private static final LocalDate WEDDING_DAY = LocalDate.of(2019, 9, 20);

    @GetMapping
    public ModelAndView home() {
        logger.info("Home Page Accessed");
        final ModelAndView modelAndView = new ModelAndView("pages/home");
        final long dateDifference = DAYS.between(LocalDate.now(), WEDDING_DAY);
        modelAndView.addObject("remainingDays", dateDifference > 0 ? dateDifference : 0);
        return modelAndView;
    }
}
