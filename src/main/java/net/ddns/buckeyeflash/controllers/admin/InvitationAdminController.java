package net.ddns.buckeyeflash.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.Invitation;
import net.ddns.buckeyeflash.models.datatable.DatatableRequest;
import net.ddns.buckeyeflash.models.datatable.DatatableResponse;
import net.ddns.buckeyeflash.repositories.InvitationRepository;
import net.ddns.buckeyeflash.serializers.InvitationSerializer;
import net.ddns.buckeyeflash.validators.InvitationValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class InvitationAdminController extends BaseAdminController {
    private static final Logger logger = Logger.getLogger(InvitationAdminController.class);

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private InvitationValidator invitationValidator;

    @RequestMapping(value = "/admin/invitation", method = RequestMethod.GET)
    public String invitation() {
        logger.info("Admin Invitation Page Accessed");
        return "pages/admin/invitation";
    }

    @RequestMapping(value = "/admin/invitation/getInvitationData", method = RequestMethod.POST)
    public @ResponseBody
    String getInvitationData(@RequestBody DatatableRequest request) throws Exception {

        PageRequest pageRequest = new PageRequest((int) Math.floor(request.getStart() / request.getLength()), request.getLength());
        String searchParameter = request.getSearch().getValue().trim();
        String[] splitSearch = StringUtils.split(searchParameter, StringUtils.SPACE, 3);
        Page<Invitation> invitations;
        if (splitSearch.length > 1) {
            invitations = invitationRepository.findByFullName(splitSearch[0], splitSearch[1], pageRequest);
        } else {
            invitations = invitationRepository.findByGuestName(searchParameter, pageRequest);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(new InvitationSerializer());
        objectMapper.registerModule(simpleModule);
        DatatableResponse datatableResponse = new DatatableResponse();
        datatableResponse.setDraw(request.getDraw());
        datatableResponse.setRecordsFiltered((int) invitations.getTotalElements());
        datatableResponse.setRecordsTotal((int) invitations.getTotalElements());
        datatableResponse.setData(invitations.getContent());
        logger.info("Retrieved Invitation Data From Database");
        return objectMapper.writeValueAsString(datatableResponse);
    }

    @RequestMapping(value = "/admin/invitation/createInvitation", method = RequestMethod.POST)
    public String createInvitation(@Valid @ModelAttribute Invitation invitation, Errors errors) {
        invitationValidator.validate(invitation, errors);
        if (errors.hasErrors()) {
            return "fragments/admin/invitation_fragments :: invitationModalContent";
        }
        for (Guest guest : invitation.getGuestList()) {
            guest.setInvitedPerson(true);
            guest.setInvitation(invitation);
        }
        try {
            invitationRepository.save(invitation);
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return "fragments/admin/invitation_fragments :: invitationModalError";
        }
        logger.info("Invitation Saved");
        return "fragments/admin/invitation_fragments :: invitationModalSuccess";
    }

    @RequestMapping(value = "/admin/invitation/invitationModal", method = RequestMethod.POST)
    public String invitationModal(ModelMap modelMap) {
        modelMap.clear();
        modelMap.addAttribute("invitation", new Invitation());
        return "fragments/admin/invitation_fragments :: invitationModalContent";
    }

    @RequestMapping(value = "/admin/invitation/invitationModal/addGuest", method = RequestMethod.POST)
    public String invitationModalAddGuest(@ModelAttribute Invitation invitation) {
        if (invitation.getMaxGuests() != null) {
            if (invitation.getGuestList().size() < invitation.getMaxGuests()) {
                invitation.getGuestList().add(new Guest());
            }
        }
        return "fragments/admin/invitation_fragments :: invitationModalContent";
    }

    @RequestMapping(value = "/admin/invitation/invitationModal/removeGuest", method = RequestMethod.POST)
    public String invitationModalRemoveGuest(@ModelAttribute Invitation invitation) {
        int guestSize = invitation.getGuestList().size();
        if (guestSize > 0) {
            invitation.getGuestList().remove(guestSize - 1);
        }
        return "fragments/admin/invitation_fragments :: invitationModalContent";
    }

}
