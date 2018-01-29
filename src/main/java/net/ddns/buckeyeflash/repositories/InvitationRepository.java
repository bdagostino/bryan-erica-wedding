package net.ddns.buckeyeflash.repositories;

import net.ddns.buckeyeflash.models.Invitation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface InvitationRepository extends PagingAndSortingRepository<Invitation, Long> {

    @Query(value = "select distinct i from Invitation i join i.guestList l where l.firstName like :guestName% or l.lastName like :guestName%")
    Page<Invitation> findByGuestName(@Param("guestName") String guestName, Pageable pageable);

    @Query(value = "select distinct i from Invitation i join i.guestList l where (l.firstName like :firstName% and l.lastName like :lastName%) or (l.firstName like :lastName% and l.lastName like :firstName%)")
    Page<Invitation> findByFullName(@Param("firstName") String firstName, @Param("lastName") String lastName, Pageable pageable);

    Invitation findById(Integer id);

    Invitation findByInvitationCode(String invitationCode);

}
