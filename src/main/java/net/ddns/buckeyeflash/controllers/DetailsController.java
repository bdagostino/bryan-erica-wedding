package net.ddns.buckeyeflash.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

@Controller
@RequestMapping(value = "details")
public class DetailsController {

    private final String googleEmbeddedApiKey;

    private final String weddingLocationAddress;

    private final String staybridgeHotelAddress;

    public DetailsController(@Value("${googleEmbeddedApiKey}") final String googleEmbeddedApiKey, @Value("${weddingLocationAddress}") final String weddingLocationAddress, @Value("${staybridgeHotelAddress}") final String staybridgeHotelAddress) {
        this.googleEmbeddedApiKey = googleEmbeddedApiKey;
        this.weddingLocationAddress = weddingLocationAddress;
        this.staybridgeHotelAddress = staybridgeHotelAddress;
    }

    @ModelAttribute(name = "googleEmbeddedApiKey")
    public String googleEmbeddedApiKey() {
        return googleEmbeddedApiKey;
    }

    @GetMapping(value = "accommodations")
    public ModelAndView accommodations() {
        final ModelAndView modelAndView = new ModelAndView("pages/details/accommodations");
        modelAndView.addObject("staybridgeHotelAddress", staybridgeHotelAddress);
        return modelAndView;
    }

    @GetMapping(value = "location")
    public ModelAndView location() {
        final ModelAndView modelAndView = new ModelAndView("pages/details/location");
        modelAndView.addObject("weddingLocationAddress", HtmlUtils.htmlEscape(weddingLocationAddress));
        return modelAndView;
    }
}
