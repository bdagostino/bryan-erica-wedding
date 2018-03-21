package net.ddns.buckeyeflash.controllers.admin;

import net.ddns.buckeyeflash.models.modal.AjaxError;
import net.ddns.buckeyeflash.models.modal.AjaxResponse;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

public abstract class BaseAdminController {

    void processErrors(AjaxResponse ajaxResponse, Errors errors) {
        for (FieldError fieldError : errors.getFieldErrors()) {
            ajaxResponse.getFieldErrorList().add(new AjaxError(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        if (errors.hasGlobalErrors()) {
            ajaxResponse.setGlobalError(true);
        }
    }
}
