package net.ddns.buckeyeflash.utilities;

import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.Invitation;
import net.ddns.buckeyeflash.repositories.FoodRepository;
import net.ddns.buckeyeflash.repositories.InvitationRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class InvitationUtils {
    private static final Logger logger = Logger.getLogger(InvitationUtils.class);

    private InvitationUtils() {
    }

    public static synchronized String generateInvitationCode(InvitationRepository invitationRepository) {

        String invitationCode;
        Invitation invitation;
        do {
            invitationCode = RandomStringUtils.randomAlphanumeric(4).toUpperCase();
            invitation = invitationRepository.findByInvitationCode(invitationCode);
        } while (invitation != null);

        return invitationCode;
    }

    public static boolean saveInvitation(InvitationRepository invitationRepository, FoodRepository foodRepository, Invitation invitation) {
        Invitation storableInvitation;
        if (invitation.getId() != null) {
            //Update Invitation
            storableInvitation = invitationRepository.findById(invitation.getId());
            storableInvitation.setMaxGuests(invitation.getMaxGuests());

            updateExistingGuests(foodRepository, invitation.getGuestList(), storableInvitation.getGuestList());

            processUnsavedGuests(invitation.getGuestList(), storableInvitation);

            if (storableInvitation.getGuestList().size() != invitation.getGuestList().size()) {
                logger.error("List Sizes Do Not Match...");
            }
        } else {
            //Create Invitation
            storableInvitation = createInvitation(invitation);
        }
        try {
            invitationRepository.save(storableInvitation);
            logger.info("Invitation Saved");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    private static Invitation createInvitation(Invitation invitation) {
        for (Guest guest : invitation.getGuestList()) {
            guest.setInvitedPerson(true);
            guest.setInvitation(invitation);
        }
        return invitation;
    }

    private static void updateExistingGuests(FoodRepository foodRepository, List<Guest> pendingGuestList, List<Guest> existingGuestList) {
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
                    if(pendingGuest.getFood() != null) {
                        existingGuest.setFood(foodRepository.findById(pendingGuest.getFood().getId()));
                    }
                }
            } else {
                logger.info("Removing Guest");
                existingGuest.setInvitation(null);
                existingGuestListIterator.remove();
            }
        }
    }

    private static void processUnsavedGuests(List<Guest> pendingGuestList, Invitation existingInvitation) {
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
