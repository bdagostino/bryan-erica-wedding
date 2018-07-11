package net.ddns.buckeyeflash.validators.admin;

import net.ddns.buckeyeflash.models.Guest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import static org.assertj.core.api.Assertions.assertThat;

public class GuestSaveValidatorTest {

    private GuestSaveValidator guestSaveValidator;

    @Before
    public void setup() {
        guestSaveValidator = new GuestSaveValidator();
    }

    @Test
    public void testGuestSupports() {
        assertThat(guestSaveValidator.supports(Guest.class)).isTrue();
    }

    @Test
    public void testValidateEmptyDietaryCommentsWithDietaryConcern() {
        final Guest guest = new Guest();
        guest.setDietaryComments("");
        guest.setDietaryConcerns(true);
        final Errors errors = new BeanPropertyBindingResult(guest, "guest");
        guestSaveValidator.validate(guest, errors);
        assertThat(errors.getFieldErrors()).isNotNull().hasSize(1);
        final FieldError fieldError = errors.getFieldErrors().get(0);
        assertThat(fieldError.getField()).isEqualTo("dietaryComments");
        assertThat(fieldError.getCode()).isEqualTo("GUEST-DECOM-ADD");
        assertThat(fieldError.getDefaultMessage()).isEqualTo("Must provide comments if the guest has a dietary concern");
    }

    @Test
    public void testValidateDietaryCommentsWithNoDietaryConcern() {
        final Guest guest = new Guest();
        guest.setDietaryComments("Peanut Allergy");
        guest.setDietaryConcerns(false);
        final Errors errors = new BeanPropertyBindingResult(guest, "guest");
        guestSaveValidator.validate(guest, errors);
        assertThat(errors.getFieldErrors()).isNotNull().hasSize(1);
        final FieldError fieldError = errors.getFieldErrors().get(0);
        assertThat(fieldError.getField()).isEqualTo("dietaryComments");
        assertThat(fieldError.getCode()).isEqualTo("GUEST-DECOM-REM");
        assertThat(fieldError.getDefaultMessage()).isEqualTo("Remove comments if the guest does not have a dietary concern");
    }
}
