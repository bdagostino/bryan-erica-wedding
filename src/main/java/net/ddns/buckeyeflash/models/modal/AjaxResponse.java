package net.ddns.buckeyeflash.models.modal;

import java.util.ArrayList;
import java.util.List;

public class AjaxResponse {

    private List<AjaxError> fieldErrorList;
    private boolean globalError;

    public List<AjaxError> getFieldErrorList() {
        if (this.fieldErrorList == null) {
            this.fieldErrorList = new ArrayList<>();
        }
        return this.fieldErrorList;
    }

    public void setFieldErrorList(List<AjaxError> fieldErrorList) {
        this.fieldErrorList = fieldErrorList;
    }

    public boolean isGlobalError() {
        return globalError;
    }

    public void setGlobalError(boolean globalError) {
        this.globalError = globalError;
    }

}
