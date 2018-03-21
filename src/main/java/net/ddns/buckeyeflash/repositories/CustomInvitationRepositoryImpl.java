package net.ddns.buckeyeflash.repositories;

import net.ddns.buckeyeflash.models.Food;
import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.Invitation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomInvitationRepositoryImpl implements CustomInvitationRepository {
    private static final Logger logger = LogManager.getLogger(CustomInvitationRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public boolean saveInvitation(Invitation invitation) {
        if (invitation.getId() == null) {
            return this.createInvitation(invitation);
        } else {
            return this.updateInvitation(invitation);
        }
    }

    private boolean createInvitation(Invitation invitation) {
        if (invitation.getId() == null) {
            for (Guest guest : invitation.getGuestList()) {
                guest.setInvitedPerson(true);
                guest.setInvitation(invitation);
            }
            em.persist(invitation);
            return true;
        }
        return false;
    }

    private boolean updateInvitation(Invitation invitation) {
        if (invitation.getId() != null) {
            Invitation existingInviation = this.em.find(Invitation.class, invitation.getId());
            existingInviation.setMaxGuests(invitation.getMaxGuests());

            processExistingGuests(existingInviation.getGuestList(), invitation.getGuestList());

            processUnsavedGuests(invitation.getGuestList(), existingInviation);

            if (existingInviation.getGuestList().size() != invitation.getGuestList().size()) {
                logger.error("List Sizes Do Not Match...");
                return false;
            }
            try {
                em.merge(existingInviation);
                logger.info("Invitation Saved");
                return true;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return false;
            }
        }
        return false;
    }

    private void processExistingGuests(List<Guest> existingGuestList, List<Guest> pendingGuestList) {
        Iterator<Guest> existingGuestListIterator = existingGuestList.iterator();
        while (existingGuestListIterator.hasNext()) {
            Guest existingGuest = existingGuestListIterator.next();
            List<Guest> filteredGuestList = pendingGuestList.stream().filter(pendingGuest -> existingGuest.getId().equals(pendingGuest.getId())).collect(Collectors.toList());
            if (!filteredGuestList.isEmpty()) {
                if (filteredGuestList.size() > 1) {
                    throw new IllegalStateException("Too Many Items Found");
                } else {
                    Guest pendingGuest = filteredGuestList.get(0);
                    existingGuest.setFirstName(pendingGuest.getFirstName());
                    existingGuest.setLastName(pendingGuest.getLastName());
                    existingGuest.setAttendance(pendingGuest.getAttendance());
                    existingGuest.setDietaryConcerns(pendingGuest.getDietaryConcerns());
                    existingGuest.setDietaryComments(pendingGuest.getDietaryComments());
                    if (pendingGuest.getFood() != null) {
                        existingGuest.setFood(this.em.find(Food.class, pendingGuest.getFood().getId()));
                    }
                }
            } else {
                logger.info("Removing Guest");
                existingGuest.setInvitation(null);
                existingGuestListIterator.remove();
            }
        }
    }

    private void processUnsavedGuests(List<Guest> pendingGuestList, Invitation existingInvitation) {
        List<Guest> unsavedGuests = pendingGuestList.stream().filter(pendingGuest -> pendingGuest.getId() == null).collect(Collectors.toList());
        if (!unsavedGuests.isEmpty()) {
            unsavedGuests.stream().forEach(pendingGuest -> {
                pendingGuest.setInvitation(existingInvitation);
                pendingGuest.setInvitedPerson(true);
            });
            existingInvitation.getGuestList().addAll(unsavedGuests);
        }
    }

}
