package net.ddns.buckeyeflash.controllers.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AdminController {

    private static final Logger logger = LogManager.getLogger(AdminController.class);

    @PreAuthorize("hasAnyRole('ADMIN_EDIT','ADMIN_READ')")
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String home() {
        logger.info("Admin Page Accessed");
        return "pages/adminxx";
    }
}
