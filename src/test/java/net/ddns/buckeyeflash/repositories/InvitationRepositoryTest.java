package net.ddns.buckeyeflash.repositories;

import net.ddns.buckeyeflash.models.Invitation;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL)
public class InvitationRepositoryTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testFindById() {
        final Invitation invitation = new Invitation();
        invitation.setMaxGuests(1);
        invitation.setInvitationCode("ABCD");
        int id = (int) entityManager.persistAndGetId(invitation);
        Invitation savedInvitation;
        savedInvitation = invitationRepository.findById(id);
        softly.assertThat(savedInvitation).isEqualToComparingFieldByField(invitation);
        savedInvitation = invitationRepository.findById(id + 2);
        softly.assertThat(savedInvitation).isNull();
    }

    @Test
    public void testFindByInvitationCode() {
        final String invitationCode = "AABB";
        final Invitation invitation = new Invitation();
        invitation.setMaxGuests(1);
        invitation.setInvitationCode(invitationCode);
        entityManager.persist(invitation);
        Invitation savedInvitation;
        savedInvitation = invitationRepository.findByInvitationCode(invitationCode);
        softly.assertThat(savedInvitation).isEqualToComparingFieldByField(invitation);
        savedInvitation = invitationRepository.findByInvitationCode("CCCC");
        softly.assertThat(savedInvitation).isNull();
    }
}
