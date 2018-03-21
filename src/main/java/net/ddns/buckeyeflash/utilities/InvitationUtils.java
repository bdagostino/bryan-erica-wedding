package net.ddns.buckeyeflash.utilities;

import net.ddns.buckeyeflash.models.Invitation;
import net.ddns.buckeyeflash.repositories.InvitationRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InvitationUtils {
    private static final Logger logger = LogManager.getLogger(InvitationUtils.class);

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
}
