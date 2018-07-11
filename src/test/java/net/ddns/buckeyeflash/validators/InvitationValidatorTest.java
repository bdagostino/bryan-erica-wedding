package net.ddns.buckeyeflash.validators;

import net.ddns.buckeyeflash.models.Invitation;
import org.junit.Before;
import org.junit.Test;

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
}
