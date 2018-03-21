package net.ddns.buckeyeflash.controllers.admin;

import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.Invitation;
import net.ddns.buckeyeflash.repositories.InvitationRepository;
import net.ddns.buckeyeflash.utilities.InvitationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Random;

@Controller
public class AdminController {

    private static final Logger logger = LogManager.getLogger(AdminController.class);

    private final InvitationRepository invitationRepository;

    public AdminController(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN_EDIT','ADMIN_READ')")
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String home() {
        logger.info("Admin Page Accessed");
        return "pages/adminxx";
    }

    @PreAuthorize("hasAnyRole('ADMIN_EDIT')")
    @RequestMapping(value = "/addData", method = RequestMethod.GET)
    public @ResponseBody
    String addData() {

        addInvitationLoop("Elizabeth", "Swan", "Jack", "Sparrow");
        addInvitationLoop("Dean", "Martin", "Grace", "Martin");
        addInvitationLoop("Dan", "DAgostino", "Debbie", "DAgostino");
        addInvitationLoop("Karen", "Morgan", "Justin", "Morgan");
        addInvitationLoop("Heather", "Crown", "Some", "Man");
        addInvitationLoop("Amanda", "Panda", "Sam", "Something");
        addInvitationLoop("Y1", "Y1", "Z2", "Z2");
        addInvitationLoop("A1", "B2", "B12", "B2");
        addInvitationLoop("C1", "C1", "D1", "D2");
        addInvitationLoop("E1", "E1", "F1", "F2");
        addInvitationLoop("G1", "G1", "H1", "H2");
        addInvitationLoop("I1", "I1", "J1", "J2");
        addInvitationLoop("K1", "K1", "L1", "L2");
        addInvitationLoop("Dan", "D", "Debbie", "D");

        return "Invitation Loaded";
    }

    private void addInvitationLoop(String g1Fn, String g1Ln, String g2Fn, String g2Ln) {

        Random random = new Random();
        Guest guest1 = new Guest();
        guest1.setFirstName(g1Fn);
        guest1.setLastName(g1Ln);
        guest1.setInvitedPerson(random.nextBoolean());

        Guest guest2 = new Guest();
        guest2.setFirstName(g2Fn);
        guest2.setLastName(g2Ln);
        guest2.setInvitedPerson(random.nextBoolean());


        Invitation invitation = new Invitation();
        guest1.setInvitation(invitation);
        guest2.setInvitation(invitation);

        invitation.setMaxGuests(3);
        invitation.getGuestList().add(guest1);
        invitation.getGuestList().add(guest2);
        invitation.setInvitationCode(InvitationUtils.generateInvitationCode(invitationRepository));
        invitationRepository.save(invitation);
    }


}
