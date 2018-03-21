package net.ddns.buckeyeflash.repositories;

import net.ddns.buckeyeflash.models.Invitation;

public interface CustomInvitationRepository {

    boolean saveInvitation(Invitation invitation);
}
