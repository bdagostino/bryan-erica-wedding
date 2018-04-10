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

    private static final String GUEST_LIST_FORMAT = "guestList[%d].%s";

    @Override
    public boolean supports(Class<?> clazz) {
        return Invitation.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Invitation invitation = (Invitation) obj;
        for (int i = 0; i < invitation.getGuestList().size(); i++) {
            Guest guest = invitation.getGuestList().get(i);
            validateFirstName(guest, i, errors);
            validateLastName(guest, i, errors);
            validateAttendance(guest, i, errors);
            if (BooleanUtils.isTrue(guest.getReceptionAttendance())) {
                validateFood(guest, i, errors);
                validateDietaryConcerns(guest, i, errors);
                validateDietartComments(guest, i, errors);
            }
        }
    }

    private void validateFirstName(Guest guest, int index, Errors errors) {
        if (StringUtils.isBlank(guest.getFirstName())) {
            errors.rejectValue(String.format(GUEST_LIST_FORMAT, index, "firstName"), "A", "Please provide guests first name");
        }
    }

    private void validateLastName(Guest guest, int index, Errors errors) {
        if (StringUtils.isBlank(guest.getLastName())) {
            errors.rejectValue(String.format(GUEST_LIST_FORMAT, index, "lastName"), "A", "Please provide guests last name");
        }
    }

    private void validateAttendance(Guest guest, int index, Errors errors) {
        if (guest.getCeremonyAttendance() == null) {
            errors.rejectValue(String.format(GUEST_LIST_FORMAT, index, "ceremonyAttendance"), "A", "Please let us know if you will be joining our ceremony");
        }
        if (guest.getReceptionAttendance() == null) {
            errors.rejectValue(String.format(GUEST_LIST_FORMAT, index, "receptionAttendance"), "A", "Please let us know if you will be joining our reception");
        }
    }

    private void validateFood(Guest guest, int index, Errors errors) {
        if (guest.getFood() == null || guest.getFood().getId() == null) {
            errors.rejectValue(String.format(GUEST_LIST_FORMAT, index, "food"), "B", "Please select your meal");
        }
    }

    private void validateDietaryConcerns(Guest guest, int index, Errors errors) {
        if (guest.getDietaryConcerns() == null) {
            errors.rejectValue(String.format(GUEST_LIST_FORMAT, index, "dietaryConcerns"), "C", "Please let us know if you have any dietary restrictions");
        }
    }

    private void validateDietartComments(Guest guest, int index, Errors errors) {
        if (BooleanUtils.isTrue(guest.getDietaryConcerns()) && StringUtils.isBlank(guest.getDietaryComments())) {
            errors.rejectValue(String.format(GUEST_LIST_FORMAT, index, "dietaryComments"), "D", "Please provide more information regarding your dietary restrictions");
        }
        if (BooleanUtils.isFalse(guest.getDietaryConcerns()) && StringUtils.isNotBlank(guest.getDietaryComments())) {
            errors.rejectValue(String.format(GUEST_LIST_FORMAT, index, "dietaryComments"), "D", "Please remove comments if you do not have any dietary restrictions");
        }
    }
}
