package net.ddns.buckeyeflash.utilities;

import net.ddns.buckeyeflash.models.Invitation;
import net.ddns.buckeyeflash.repositories.InvitationRepository;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL)
public class InvitationUtilsTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Autowired
    private InvitationRepository invitationRepository;

    @Test
    public void testGenerateInvitationCode() {
        final String generatedCode = InvitationUtils.generateInvitationCode(invitationRepository);
        final String uppercaseGeneratedCode = generatedCode.toUpperCase();
        softly.assertThat(generatedCode).isNotBlank().hasSize(4);
        softly.assertThat(generatedCode).isEqualTo(uppercaseGeneratedCode);
    }

    @Test
    public void testGenerateInvitationCodeUnderLoad() {
        final Set<String> generatedCodes = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            final String generatedCode = InvitationUtils.generateInvitationCode(invitationRepository);
            final String uppercaseGeneratedCode = generatedCode.toUpperCase();
            final boolean isCodeUnique = generatedCodes.add(generatedCode);
            final Invitation invitation = new Invitation();
            invitation.setInvitationCode(generatedCode);
            invitation.setMaxGuests(1);
            final boolean isInvitationSaved = invitationRepository.saveInvitation(invitation);
            softly.assertThat(isInvitationSaved).as("Invitation Saved").isTrue();
            softly.assertThat(isCodeUnique).as("Invitation Code").isTrue();
            softly.assertThat(generatedCode).isNotBlank().hasSize(4);
            softly.assertThat(generatedCode).isEqualTo(uppercaseGeneratedCode);
        }
    }
}
