package net.ddns.buckeyeflash.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.Invitation;
import net.ddns.buckeyeflash.models.datatable.DatatableRequest;
import net.ddns.buckeyeflash.models.datatable.DatatableResponse;
import net.ddns.buckeyeflash.models.modal.AjaxResponse;
import net.ddns.buckeyeflash.repositories.InvitationRepository;
import net.ddns.buckeyeflash.serializers.InvitationSerializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
public class InvitationAdminController extends BaseAdminController {
    private static final Logger logger = Logger.getLogger(InvitationAdminController.class);

    @Autowired
    private InvitationRepository invitationRepository;

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
    public ResponseEntity<AjaxResponse> createInvitation(@Valid @RequestBody Invitation invitation, Errors errors) {
        if (errors.hasErrors()) {
            AjaxResponse guestAjaxResponse = new AjaxResponse();
            processErrors(guestAjaxResponse, errors);
            return ResponseEntity.badRequest().body(guestAjaxResponse);
        }
        for(Guest guest : invitation.getGuestList()){
            guest.setInvitedPerson(true);
            guest.setInvitation(invitation);
        }
        invitationRepository.save(invitation);
        logger.info("Invitation Saved");
        return ResponseEntity.ok(null);
    }

}
