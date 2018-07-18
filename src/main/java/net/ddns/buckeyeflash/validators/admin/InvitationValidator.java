package net.ddns.buckeyeflash.validators.admin;


import net.ddns.buckeyeflash.models.Invitation;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class InvitationValidator implements Validator {

    private static final String MAX_GUESTS_FIELD = "maxGuests";

    @Override
    public boolean supports(final Class<?> clazz) {
        return Invitation.class.equals(clazz);
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        final Invitation invitation = (Invitation) obj;
        if(invitation.getMaxGuests() != null){
            if (invitation.getMaxGuests() > 0 && invitation.getGuestList().isEmpty()) {
                errors.rejectValue(MAX_GUESTS_FIELD, "INV_GL_SM", "At least One Guest is Required below");
            }
            if (invitation.getGuestList().size() > invitation.getMaxGuests()) {
                errors.rejectValue(MAX_GUESTS_FIELD, "INV_GL_LG", "Too many guests below");
            }
        }else{
            if (!invitation.getGuestList().isEmpty()) {
                errors.rejectValue(MAX_GUESTS_FIELD, "INV_GL_LG", "Too many guests below");
            }
        }
    }
}