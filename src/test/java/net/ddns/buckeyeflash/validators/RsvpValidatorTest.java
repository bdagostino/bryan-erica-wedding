package net.ddns.buckeyeflash.validators;

import net.ddns.buckeyeflash.models.Food;
import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.Invitation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import static org.assertj.core.api.Assertions.assertThat;

public class RsvpValidatorTest {

    private RsvpValidator rsvpValidator;

    @Before
    public void setup() {
        rsvpValidator = new RsvpValidator();
    }

    @Test
    public void testInvitationSupports() {
        assertThat(rsvpValidator.supports(Invitation.class)).isTrue();
    }

    @Test
    public void testStringSupports() {
        assertThat(rsvpValidator.supports(String.class)).isFalse();
    }

    @Test
    public void testValidateEmptyFirstName() {
        final Invitation invitation = new Invitation();
        final Guest guest = new Guest(1, "", "GoodBye");
        guest.setReceptionAttendance(false);
        guest.setCeremonyAttendance(false);
        invitation.getGuestList().add(guest);
        final Errors errors = new BeanPropertyBindingResult(invitation, "invitation");
        rsvpValidator.validate(invitation, errors);
        assertThat(errors.getFieldErrors()).isNotEmpty().hasSize(1);
        final FieldError fieldError = errors.getFieldErrors().get(0);
        assertThat(fieldError.getField()).isEqualTo("guestList[0].firstName");
        assertThat(fieldError.getCode()).isEqualTo("RSVP-FN");
        assertThat(fieldError.getDefaultMessage()).isEqualTo("Please provide guests first name");
    }

    @Test
    public void testValidateEmptyLastName() {
        final Invitation invitation = new Invitation();
        final Guest guest = new Guest(1, "Hello", "");
        guest.setReceptionAttendance(false);
        guest.setCeremonyAttendance(false);
        invitation.getGuestList().add(guest);
        final Errors errors = new BeanPropertyBindingResult(invitation, "invitation");
        rsvpValidator.validate(invitation, errors);
        assertThat(errors.getFieldErrors()).isNotEmpty().hasSize(1);
        final FieldError fieldError = errors.getFieldErrors().get(0);
        assertThat(fieldError.getField()).isEqualTo("guestList[0].lastName");
        assertThat(fieldError.getCode()).isEqualTo("RSVP-LN");
        assertThat(fieldError.getDefaultMessage()).isEqualTo("Please provide guests last name");
    }

    @Test
    public void testValidateNullCeremonyAttendance() {
        final Invitation invitation = new Invitation();
        final Guest guest = new Guest(1, "Hello", "Goodbye");
        guest.setReceptionAttendance(false);
        guest.setCeremonyAttendance(null);
        invitation.getGuestList().add(guest);
        final Errors errors = new BeanPropertyBindingResult(invitation, "invitation");
        rsvpValidator.validate(invitation, errors);
        assertThat(errors.getFieldErrors()).isNotEmpty().hasSize(1);
        final FieldError fieldError = errors.getFieldErrors().get(0);
        assertThat(fieldError.getField()).isEqualTo("guestList[0].ceremonyAttendance");
        assertThat(fieldError.getCode()).isEqualTo("RSVP-CA");
        assertThat(fieldError.getDefaultMessage()).isEqualTo("Please let us know if you will be joining our ceremony");
    }

    @Test
    public void testValidateNullReceptionAttendance() {
        final Invitation invitation = new Invitation();
        final Guest guest = new Guest(1, "Hello", "Goodbye");
        guest.setReceptionAttendance(null);
        guest.setCeremonyAttendance(false);
        invitation.getGuestList().add(guest);
        final Errors errors = new BeanPropertyBindingResult(invitation, "invitation");
        rsvpValidator.validate(invitation, errors);
        assertThat(errors.getFieldErrors()).isNotEmpty().hasSize(1);
        final FieldError fieldError = errors.getFieldErrors().get(0);
        assertThat(fieldError.getField()).isEqualTo("guestList[0].receptionAttendance");
        assertThat(fieldError.getCode()).isEqualTo("RSVP-RA");
        assertThat(fieldError.getDefaultMessage()).isEqualTo("Please let us know if you will be joining our reception");
    }

    @Test
    public void testValidateNullFood() {
        final Invitation invitation = new Invitation();
        final Guest guest = new Guest(1, "Hello", "Goodbye");
        guest.setReceptionAttendance(true);
        guest.setCeremonyAttendance(false);
        guest.setDietaryConcerns(false);
        invitation.getGuestList().add(guest);
        final Errors errors = new BeanPropertyBindingResult(invitation, "invitation");
        rsvpValidator.validate(invitation, errors);
        assertThat(errors.getFieldErrors()).isNotEmpty().hasSize(1);
        final FieldError fieldError = errors.getFieldErrors().get(0);
        assertThat(fieldError.getField()).isEqualTo("guestList[0].food");
        assertThat(fieldError.getCode()).isEqualTo("RSVP-FOOD");
        assertThat(fieldError.getDefaultMessage()).isEqualTo("Please select your meal");
    }

    @Test
    public void testValidateNullFoodId() {
        final Invitation invitation = new Invitation();
        final Guest guest = new Guest(1, "Hello", "Goodbye");
        guest.setReceptionAttendance(true);
        guest.setCeremonyAttendance(false);
        guest.setDietaryConcerns(false);
        guest.setFood(new Food());
        invitation.getGuestList().add(guest);
        final Errors errors = new BeanPropertyBindingResult(invitation, "invitation");
        rsvpValidator.validate(invitation, errors);
        assertThat(errors.getFieldErrors()).isNotEmpty().hasSize(1);
        final FieldError fieldError = errors.getFieldErrors().get(0);
        assertThat(fieldError.getField()).isEqualTo("guestList[0].food");
        assertThat(fieldError.getCode()).isEqualTo("RSVP-FOOD");
        assertThat(fieldError.getDefaultMessage()).isEqualTo("Please select your meal");
    }

    @Test
    public void testValidateDietaryConcerns() {
        final Invitation invitation = new Invitation();
        final Guest guest = new Guest(1, "Hello", "Goodbye");
        guest.setReceptionAttendance(true);
        guest.setCeremonyAttendance(false);
        guest.setDietaryConcerns(null);
        guest.setFood(new Food(1));
        invitation.getGuestList().add(guest);
        final Errors errors = new BeanPropertyBindingResult(invitation, "invitation");
        rsvpValidator.validate(invitation, errors);
        assertThat(errors.getFieldErrors()).isNotEmpty().hasSize(1);
        final FieldError fieldError = errors.getFieldErrors().get(0);
        assertThat(fieldError.getField()).isEqualTo("guestList[0].dietaryConcerns");
        assertThat(fieldError.getCode()).isEqualTo("RSVP-DCON");
        assertThat(fieldError.getDefaultMessage()).isEqualTo("Please let us know if you have any dietary restrictions");
    }

    @Test
    public void testValidateBlankDietaryCommentsWithDietaryConcerns() {
        final Invitation invitation = new Invitation();
        final Guest guest = new Guest(1, "Hello", "Goodbye");
        guest.setReceptionAttendance(true);
        guest.setCeremonyAttendance(false);
        guest.setDietaryConcerns(true);
        guest.setDietaryComments("");
        guest.setFood(new Food(1));
        invitation.getGuestList().add(guest);
        final Errors errors = new BeanPropertyBindingResult(invitation, "invitation");
        rsvpValidator.validate(invitation, errors);
        assertThat(errors.getFieldErrors()).isNotEmpty().hasSize(1);
        final FieldError fieldError = errors.getFieldErrors().get(0);
        assertThat(fieldError.getField()).isEqualTo("guestList[0].dietaryComments");
        assertThat(fieldError.getCode()).isEqualTo("RSVP-DCOM-ADD");
        assertThat(fieldError.getDefaultMessage()).isEqualTo("Please provide more information regarding your dietary restrictions");
    }

    @Test
    public void testValidateHavingDietaryCommentsWithoutDietaryConcerns() {
        final Invitation invitation = new Invitation();
        final Guest guest = new Guest(1, "Hello", "Goodbye");
        guest.setReceptionAttendance(true);
        guest.setCeremonyAttendance(false);
        guest.setDietaryConcerns(false);
        guest.setDietaryComments("Peanut Allergy");
        guest.setFood(new Food(1));
        invitation.getGuestList().add(guest);
        final Errors errors = new BeanPropertyBindingResult(invitation, "invitation");
        rsvpValidator.validate(invitation, errors);
        assertThat(errors.getFieldErrors()).isNotEmpty().hasSize(1);
        final FieldError fieldError = errors.getFieldErrors().get(0);
        assertThat(fieldError.getField()).isEqualTo("guestList[0].dietaryComments");
        assertThat(fieldError.getCode()).isEqualTo("RSVP-DCOM-REM");
        assertThat(fieldError.getDefaultMessage()).isEqualTo("Please remove comments if you do not have any dietary restrictions");
    }

    @Test
    public void testValidateNoErrors(){
        final Invitation invitation = new Invitation();
        final Guest guest = new Guest(1, "Hello", "Goodbye");
        guest.setReceptionAttendance(true);
        guest.setCeremonyAttendance(true);
        guest.setDietaryConcerns(true);
        guest.setDietaryComments("Peanut Allergy");
        guest.setFood(new Food(1));
        invitation.getGuestList().add(guest);
        final Errors errors = new BeanPropertyBindingResult(invitation, "invitation");
        rsvpValidator.validate(invitation, errors);
        assertThat(errors.getFieldErrors()).isNotNull().isEmpty();
    }
}