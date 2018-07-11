package net.ddns.buckeyeflash.validators.admin;

import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.Invitation;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import static org.assertj.core.api.Assertions.assertThat;

public class InvitationValidatorTest {

    private InvitationValidator invitationValidator;

    @Before
    public void setup() {
        invitationValidator = new InvitationValidator();
    }

    @Test
    public void testInvitationSupports() {
        assertThat(invitationValidator.supports(Invitation.class)).isTrue();
    }

    @Test
    public void testValidateNullMaxGuestNonEmptyGuestList() {
        final Invitation invitation = new Invitation();
        invitation.setMaxGuests(null);
        invitation.getGuestList().add(new Guest(1, "Hello", "Goodbye"));
        final Errors errors = new BeanPropertyBindingResult(invitation, "invitation");
        invitationValidator.validate(invitation, errors);
        assertThat(errors.getFieldErrors()).isNotNull().hasSize(1);
        final FieldError fieldError = errors.getFieldErrors().get(0);
        assertThat(fieldError.getField()).isEqualTo("maxGuests");
        assertThat(fieldError.getCode()).isEqualTo("INV_GL_LG");
        assertThat(fieldError.getDefaultMessage()).isEqualTo("Too many guests below");
    }

    @Test
    public void testValidateMaxGuestLessThanGuestListSize() {
        final Invitation invitation = new Invitation();
        invitation.setMaxGuests(1);
        invitation.getGuestList().add(new Guest(1, "Hello", "Goodbye"));
        invitation.getGuestList().add(new Guest(2, "Goodbye", "Hello"));
        final Errors errors = new BeanPropertyBindingResult(invitation, "invitation");
        invitationValidator.validate(invitation, errors);
        assertThat(errors.getFieldErrors()).isNotNull().hasSize(1);
        final FieldError fieldError = errors.getFieldErrors().get(0);
        assertThat(fieldError.getField()).isEqualTo("maxGuests");
        assertThat(fieldError.getCode()).isEqualTo("INV_GL_LG");
        assertThat(fieldError.getDefaultMessage()).isEqualTo("Too many guests below");
    }

    @Test
    public void testValidateMaxGuestEmptyGuestList() {
        final Invitation invitation = new Invitation();
        invitation.setMaxGuests(1);
        final Errors errors = new BeanPropertyBindingResult(invitation, "invitation");
        invitationValidator.validate(invitation, errors);
        assertThat(errors.getFieldErrors()).isNotNull().hasSize(1);
        final FieldError fieldError = errors.getFieldErrors().get(0);
        assertThat(fieldError.getField()).isEqualTo("maxGuests");
        assertThat(fieldError.getCode()).isEqualTo("INV_GL_SM");
        assertThat(fieldError.getDefaultMessage()).isEqualTo("At least One Guest is Required below");
    }
}
