package net.ddns.buckeyeflash.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RsvpSearch {

    @NotNull
    @Size(min = 4, max = 4)
    private String invitationCode;

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }
}
