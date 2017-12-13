package net.ddns.buckeyeflash.repositories;

import net.ddns.buckeyeflash.models.Invite;
import org.springframework.data.repository.CrudRepository;

public interface InviteRepository extends CrudRepository<Invite, Long> {
}
