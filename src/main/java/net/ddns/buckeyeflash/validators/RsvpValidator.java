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
        final Invitation invitation = (Invitation) obj;
        for (int i = 0; i < invitation.getGuestList().size(); i++) {
            final Guest guest = invitation.getGuestList().get(i);
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

    private void validateFirstName(final Guest guest, final int index, final Errors errors) {
        if (StringUtils.isBlank(guest.getFirstName())) {
            rejectValue("firstName",index,"RSVP-FN","Please provide guests first name",errors);
        }
    }

    private void validateLastName(final Guest guest, final int index, final Errors errors) {
        if (StringUtils.isBlank(guest.getLastName())) {
            rejectValue("lastName",index,"RSVP-LN","Please provide guests last name",errors);
        }
    }

    private void validateAttendance(final Guest guest, final int index, final Errors errors) {
        if (guest.getCeremonyAttendance() == null) {
            rejectValue("ceremonyAttendance",index,"RSVP-CA","Please let us know if you will be joining our ceremony",errors);
        }
        if (guest.getReceptionAttendance() == null) {
            rejectValue("receptionAttendance",index,"RSVP-RA","Please let us know if you will be joining our reception",errors);
        }
    }

    private void validateFood(final Guest guest, final int index, final Errors errors) {
        if (guest.getFood() == null || guest.getFood().getId() == null) {
            rejectValue("food",index,"RSVP-FOOD","Please select your meal",errors);
        }
    }

    private void validateDietaryConcerns(final Guest guest, final int index, final Errors errors) {
        if (guest.getDietaryConcerns() == null) {
            rejectValue("dietaryConcerns",index,"RSVP-DCON","Please let us know if you have any dietary restrictions",errors);
        }
    }

    private void validateDietartComments(final Guest guest, final int index, final Errors errors) {
        if (BooleanUtils.isTrue(guest.getDietaryConcerns()) && StringUtils.isBlank(guest.getDietaryComments())) {
            rejectValue("dietaryComments",index,"RSVP-DCOM-ADD","Please provide more information regarding your dietary restrictions",errors);
        }
        if (BooleanUtils.isFalse(guest.getDietaryConcerns()) && StringUtils.isNotBlank(guest.getDietaryComments())) {
            rejectValue("dietaryComments",index,"RSVP-DCOM-REM","Please remove comments if you do not have any dietary restrictions",errors);
        }
    }

    private void rejectValue(final String field, final int index, final String errorCode, final String defaultMessage,final Errors errors){
        errors.rejectValue("guestList["+index+"]."+field, errorCode, defaultMessage);
    }
}
