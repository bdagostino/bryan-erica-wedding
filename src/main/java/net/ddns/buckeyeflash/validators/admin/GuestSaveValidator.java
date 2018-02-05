package net.ddns.buckeyeflash.validators.admin;

import net.ddns.buckeyeflash.models.Guest;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class GuestSaveValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Guest.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Guest guest = (Guest) obj;
        validateDietartComments(guest, errors);
    }

    private void validateDietartComments(Guest guest, Errors errors) {
        if (BooleanUtils.isTrue(guest.getDietaryConcerns())) {
            if (StringUtils.isBlank(guest.getDietaryComments())) {
                errors.rejectValue("dietaryComments", "error.dietaryComments","Must provide comments if the guest has a dietary concern");
            }
        }else{
            if (StringUtils.isNotBlank(guest.getDietaryComments())){
                errors.rejectValue("dietaryComments","error.dietaryComments","Remove comments if the guest does not have a dietary concern");
            }
        }
    }
}