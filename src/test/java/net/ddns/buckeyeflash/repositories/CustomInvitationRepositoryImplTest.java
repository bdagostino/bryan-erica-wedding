package net.ddns.buckeyeflash.repositories;

import net.ddns.buckeyeflash.models.Food;
import net.ddns.buckeyeflash.models.Guest;
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

import java.util.Arrays;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL)
public class CustomInvitationRepositoryImplTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Test
    public void testFindByInvitationCode_EmptyGuestList() {
        final String invitationCode = "AAA";
        Invitation invitation = createInvitation(1, invitationCode, null);
        this.entityManager.persist(invitation);
        Invitation retrievedInvitation = this.invitationRepository.findByInvitationCode(invitationCode);
        softly.assertThat(retrievedInvitation).isEqualToComparingFieldByFieldRecursively(invitation);
    }

    @Test
    public void testCreateInvitation_EmptyGuestList() {
        final String invitationCode = "BBB";
        Invitation invitation = createInvitation(3, invitationCode, null);
        boolean creationStatus = this.invitationRepository.saveInvitation(invitation);
        softly.assertThat(creationStatus).isTrue();
        Invitation retrievedInvitation = this.invitationRepository.findByInvitationCode(invitationCode);
        softly.assertThat(retrievedInvitation).isEqualToComparingFieldByFieldRecursively(invitation);
    }

    @Test
    public void testCreateInvitation_OneGuestList() {
        final String invitationCode = "ABC";
        Guest guest = new Guest();
        guest.setFirstName("John");
        guest.setLastName("Doe");
        Invitation invitation = createInvitation(3, invitationCode, guest);
        boolean creationStatus = this.invitationRepository.saveInvitation(invitation);
        softly.assertThat(creationStatus).isTrue();
        Invitation retrievedInvitation = this.invitationRepository.findByInvitationCode(invitationCode);
        softly.assertThat(retrievedInvitation).isEqualToComparingFieldByFieldRecursively(invitation);
    }

    @Test
    public void testUpdateInvitation_AddGuest() {
        final String invitationCode = "BEFA";
        Invitation invitation = createInvitation(1, invitationCode, null);
        this.entityManager.persistAndFlush(invitation);
        this.entityManager.detach(invitation);

        final Invitation savedInvitation = this.invitationRepository.findById(invitation.getId());
        softly.assertThat(savedInvitation.getGuestList()).hasSize(0);

        invitation.getGuestList().add(createGuest("John", "Doe"));
        boolean updateStatus = this.invitationRepository.saveInvitation(invitation);
        softly.assertThat(updateStatus).isTrue();

        Invitation updatedInvitation = this.invitationRepository.findById(invitation.getId());
        softly.assertThat(updatedInvitation.getGuestList()).hasSize(1);
        Guest updatedGuest = updatedInvitation.getGuestList().get(0);
        softly.assertThat(updatedGuest.getId()).isNotNull();
        softly.assertThat(updatedGuest.getInvitation().getId()).isEqualTo(invitation.getId());
        softly.assertThat(updatedGuest.getFirstName()).isEqualTo("John");
        softly.assertThat(updatedGuest.getLastName()).isEqualTo("Doe");
    }

    @Test
    public void testUpdateInvitation_RemoveGuest() {
        final String invitationCode = "ARTD";
        Invitation invitation = createInvitation(3, invitationCode, createGuest("John", "Doe"));
        for (Guest guest : invitation.getGuestList()) {
            guest.setInvitation(invitation);
        }
        this.entityManager.persistAndFlush(invitation);
        this.entityManager.detach(invitation);

        final Invitation savedInvitation = this.invitationRepository.findById(invitation.getId());
        softly.assertThat(savedInvitation.getGuestList()).hasSize(1);

        invitation.getGuestList().remove(0);
        boolean updateStatus = this.invitationRepository.saveInvitation(invitation);
        softly.assertThat(updateStatus).isTrue();

        final Invitation updatedInvitation = this.invitationRepository.findById(invitation.getId());
        softly.assertThat(updatedInvitation.getGuestList()).hasSize(0);
    }

    @Test
    public void testUpdateInvitation_ModifyGuest() {
        final String invitationCode = "ARTD";
        Invitation invitation = createInvitation(3, invitationCode, createGuest("Hello", "World"));
        for (Guest guest : invitation.getGuestList()) {
            guest.setInvitation(invitation);
        }
        this.entityManager.persistAndFlush(invitation);
        this.entityManager.detach(invitation);

        final Invitation savedInvitation = this.invitationRepository.findById(invitation.getId());
        softly.assertThat(savedInvitation.getGuestList()).hasSize(1);
        softly.assertThat(savedInvitation.getGuestList().get(0).getFirstName()).isEqualTo("Hello");
        softly.assertThat(savedInvitation.getGuestList().get(0).getLastName()).isEqualTo("World");

        invitation.getGuestList().get(0).setFirstName("Ohio");
        invitation.getGuestList().get(0).setLastName("State");
        invitation.getGuestList().get(0).setInvitedPerson(false);
        invitation.getGuestList().get(0).setDietaryConcerns(true);
        invitation.getGuestList().get(0).setDietaryComments("Peanut Allergy");
        invitation.getGuestList().get(0).setCeremonyAttendance(false);
        invitation.getGuestList().get(0).setReceptionAttendance(true);

        boolean updateStatus = this.invitationRepository.saveInvitation(invitation);
        softly.assertThat(updateStatus).isTrue();

        final Invitation updatedInvitation = this.invitationRepository.findById(invitation.getId());
        softly.assertThat(updatedInvitation.getGuestList()).hasSize(1);
        softly.assertThat(updatedInvitation.getGuestList().get(0).getFirstName()).isEqualTo("Ohio");
        softly.assertThat(updatedInvitation.getGuestList().get(0).getLastName()).isEqualTo("State");
        softly.assertThat(updatedInvitation.getGuestList().get(0).getInvitedPerson()).isFalse();
        softly.assertThat(updatedInvitation.getGuestList().get(0).getDietaryConcerns()).isTrue();
        softly.assertThat(updatedInvitation.getGuestList().get(0).getDietaryComments()).isEqualTo("Peanut Allergy");
        softly.assertThat(updatedInvitation.getGuestList().get(0).getCeremonyAttendance()).isFalse();
        softly.assertThat(updatedInvitation.getGuestList().get(0).getReceptionAttendance()).isTrue();
    }

    @Test
    public void testUpdateInvitation_ModifyMaxGuestCount() {
        final String invitationCode = "PDUI";
        final int originalMaxGuestCount = 3;
        Invitation invitation = createInvitation(originalMaxGuestCount, invitationCode, null);
        this.entityManager.persistAndFlush(invitation);
        this.entityManager.detach(invitation);

        final Invitation savedInvitation = this.invitationRepository.findById(invitation.getId());
        softly.assertThat(savedInvitation.getMaxGuests()).isEqualTo(originalMaxGuestCount);

        final int modifiedMaxGuestCount = 5;
        invitation.setMaxGuests(modifiedMaxGuestCount);
        boolean updateStatus = this.invitationRepository.saveInvitation(invitation);
        softly.assertThat(updateStatus).isTrue();

        final Invitation updatedInvitation = this.invitationRepository.findById(invitation.getId());
        softly.assertThat(updatedInvitation.getMaxGuests()).isEqualTo(modifiedMaxGuestCount);
    }

    @Test
    public void testUpdateInvitation_ModifyGuestFoodChoice() {
        Food chicken = new Food();
        chicken.setType("Chicken");
        chicken.setDescription("Chicken Description");
        Food steak = new Food();
        steak.setType("Steak");
        steak.setDescription("Steak Description");

        final int chickenOptionId = (int) this.entityManager.persistAndGetId(chicken);
        final int steakOptionId = (int) this.entityManager.persistAndGetId(steak);

        final String invitationCode = "UGDH";
        Invitation invitation = createInvitation(3, invitationCode, createGuest("John", "Doe"));
        for (Guest guest : invitation.getGuestList()) {
            guest.setInvitation(invitation);
            guest.setFood(this.foodRepository.findById(chickenOptionId));
        }
        this.entityManager.persistAndFlush(invitation);
        this.entityManager.detach(invitation);

        final Invitation savedInvitation = this.invitationRepository.findById(invitation.getId());
        softly.assertThat(savedInvitation.getGuestList()).hasSize(1);
        final Food savedGuestFoodOption = savedInvitation.getGuestList().get(0).getFood();
        softly.assertThat(savedGuestFoodOption).isNotNull();
        softly.assertThat(savedGuestFoodOption.getType()).isEqualTo("Chicken");

        invitation.getGuestList().get(0).setFood(this.foodRepository.findById(steakOptionId));
        boolean updateStatus = this.invitationRepository.saveInvitation(invitation);
        softly.assertThat(updateStatus).isTrue();

        final Invitation updatedInvitation = this.invitationRepository.findById(invitation.getId());
        softly.assertThat(savedInvitation.getGuestList()).hasSize(1);
        softly.assertThat(updatedInvitation.getGuestList().get(0).getFood().getType()).isEqualTo("Steak");
    }

    private Invitation createInvitation(final int maxGuestCount, final String invitationCode, final Guest... guests) {
        Invitation invitation = new Invitation();
        invitation.setMaxGuests(maxGuestCount);
        invitation.setInvitationCode(invitationCode);
        if (guests != null) {
            invitation.getGuestList().addAll(Arrays.asList(guests));
        }
        return invitation;
    }

    private Guest createGuest(final String firstName, final String lastName) {
        Guest guest = new Guest();
        guest.setFirstName(firstName);
        guest.setLastName(lastName);
        return guest;
    }

}
