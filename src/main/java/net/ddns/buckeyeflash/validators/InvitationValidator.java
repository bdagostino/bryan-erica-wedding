package net.ddns.buckeyeflash.validators;


import net.ddns.buckeyeflash.models.Invitation;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class InvitationValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Invitation.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Invitation invitation = (Invitation) obj;
        if (invitation.getMaxGuests() > 0 && invitation.getGuestList().size() < 1) {
            errors.rejectValue("maxGuests", "invitation.guestList.min", "At least One Guest is Required below");
        }

        if (invitation.getMaxGuests() != null) {
            if (invitation.getGuestList().size() > invitation.getMaxGuests()) {
                errors.rejectValue("maxGuests", "invitation.guestList.max", "Too many guests below");
            }
        } else {
            if (!invitation.getGuestList().isEmpty()) {
                errors.rejectValue("maxGuests", "invitation.guestList.max", "Too many guests below");
            }
        }
    }
}
