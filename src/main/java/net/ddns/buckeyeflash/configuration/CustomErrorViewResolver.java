package net.ddns.buckeyeflash.configuration;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Configuration
public class CustomErrorViewResolver implements ErrorViewResolver {

    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {

        if (status.is4xxClientError()) {
            return new ModelAndView("pages/error/custom-404-error", new ModelMap("imageIndex", RandomUtils.nextInt(0, 4)));
        }
        return null;
    }
}
