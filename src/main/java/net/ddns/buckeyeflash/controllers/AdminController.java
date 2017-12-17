package net.ddns.buckeyeflash.controllers;

import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.Invite;
import net.ddns.buckeyeflash.repositories.InviteRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Random;

@Controller
public class AdminController {

    private static final Logger logger = Logger.getLogger(AdminController.class);

    @Autowired
    private InviteRepository inviteRepository;

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String home() {
        logger.info("Admin Page Accessed");
        return "pages/adminxx";
    }

    @RequestMapping(value = "/addData", method = RequestMethod.GET)
    public @ResponseBody
    String addData() {

        addInviteLoop("Elizabeth", "Swan", "Jack", "Sparrow");
        addInviteLoop("Dean", "Martin", "Grace", "Martin");
        addInviteLoop("Dan", "DAgostino", "Debbie", "DAgostino");
        addInviteLoop("Karen", "Morgan", "Justin", "Morgan");
        addInviteLoop("Heather", "Crown", "Some", "Man");
        addInviteLoop("Amanda", "Panda", "Sam", "Something");
        addInviteLoop("Y1", "Y1", "Z2", "Z2");
        addInviteLoop("A1", "B2", "B12", "B2");
        addInviteLoop("C1", "C1", "D1", "D2");
        addInviteLoop("E1", "E1", "F1", "F2");
        addInviteLoop("G1", "G1", "H1", "H2");
        addInviteLoop("I1", "I1", "J1", "J2");
        addInviteLoop("K1", "K1", "L1", "L2");
        addInviteLoop("Dan", "D", "Debbie", "D");

        return "Invites Loaded";
    }

    private void addInviteLoop(String g1Fn, String g1Ln, String g2Fn, String g2Ln) {

        Random random = new Random();
        Guest guest1 = new Guest();
        guest1.setFirstName(g1Fn);
        guest1.setLastName(g1Ln);
        guest1.setInvitedPerson(random.nextBoolean());

        Guest guest2 = new Guest();
        guest2.setFirstName(g2Fn);
        guest2.setLastName(g2Ln);
        guest2.setInvitedPerson(random.nextBoolean());


        Invite invite = new Invite();
        guest1.setInvite(invite);
        guest2.setInvite(invite);

        invite.setMaxAdditionalGuests(3);
        invite.getGuestList().add(guest1);
        invite.getGuestList().add(guest2);
        inviteRepository.save(invite);
    }


}
