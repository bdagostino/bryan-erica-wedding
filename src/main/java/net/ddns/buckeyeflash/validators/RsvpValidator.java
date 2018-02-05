package net.ddns.buckeyeflash.validators;

import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.Invitation;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class RsvpValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Invitation.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Invitation invitation = (Invitation) obj;
        for (int i = 0; i < invitation.getGuestList().size(); i++) {
            Guest guest = invitation.getGuestList().get(i);
            validateAttendance(guest, i, errors);
            validateFood(guest, i, errors);
            validateDietaryConcerns(guest, i, errors);
            validateDietartComments(guest, i, errors);
        }
    }

    private void validateAttendance(Guest guest, int index, Errors errors) {
        if (guest.getAttendance() == null) {
            errors.rejectValue("guestList[" + index + "].attendance", "A", "Please let us know if you will be joining our special day");
        }
    }

    private void validateFood(Guest guest, int index, Errors errors) {
        if (guest.getFood() == null || guest.getFood().getId() == null) {
            errors.rejectValue("guestList[" + index + "].food.id", "B", "Please select your meal");
        }
    }

    private void validateDietaryConcerns(Guest guest, int index, Errors errors) {
        if (guest.getDietaryConcerns() == null) {
            errors.rejectValue("guestList[" + index + "].dietaryConcerns", "C", "Please let us know if you have any dietary concerns");
        }
    }

    private void validateDietartComments(Guest guest, int index, Errors errors) {
        if (BooleanUtils.isTrue(guest.getDietaryConcerns()) && StringUtils.isBlank(guest.getDietaryComments())) {
            errors.rejectValue("guestList[" + index + "].dietaryComments", "D", "Please provide more information regarding your dietary concerns");
        }
    }
}
