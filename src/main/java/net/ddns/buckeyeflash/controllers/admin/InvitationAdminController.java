package net.ddns.buckeyeflash.controllers.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.Invitation;
import net.ddns.buckeyeflash.models.datatable.DatatableRequest;
import net.ddns.buckeyeflash.models.datatable.DatatableResponse;
import net.ddns.buckeyeflash.repositories.InvitationRepository;
import net.ddns.buckeyeflash.serializers.InvitationSerializer;
import net.ddns.buckeyeflash.utilities.CommonConstants;
import net.ddns.buckeyeflash.utilities.InvitationUtils;
import net.ddns.buckeyeflash.utilities.PageUtils;
import net.ddns.buckeyeflash.validators.InvitationValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "admin/invitation")
@SessionAttributes({"invitation","canAdminEdit"})
public class InvitationAdminController extends BaseAdminController {
    private static final Logger logger = Logger.getLogger(InvitationAdminController.class);
    private static final String INVITATION_FORM_TYPE = "Invitation";
    private static final String INVITATION_ATTRIBUTE_NAME = "invitation";
    private static final String CREATE_INVITATION_MODAL_TITLE = "Create Invitation";
    private static final String UPDATE_INVITATION_MODAL_TITLE = "Update Invitation";
    private static final String INVITATION_MODAL_CONTENT_FRAGMENT = "fragments/admin/invitation_fragments :: invitationModalContent(title='%s')";

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private InvitationValidator invitationValidator;

    @PreAuthorize("hasAnyRole('ADMIN_EDIT','ADMIN_READ')")
    @RequestMapping(method = RequestMethod.GET)
    public String invitation(ModelMap modelMap, SecurityContextHolderAwareRequestWrapper securityContextHolderAwareRequestWrapper) {
        logger.info("Admin Invitation Page Accessed");
        modelMap.put("canAdminEdit", securityContextHolderAwareRequestWrapper.isUserInRole("ADMIN_EDIT"));
        return "pages/admin/invitation";
    }

    @PreAuthorize("hasAnyRole('ADMIN_EDIT','ROLE_ADMIN_READ')")
    @RequestMapping(value = "/getInvitationData", method = RequestMethod.POST)
    public @ResponseBody
    String getInvitationData(@RequestBody DatatableRequest request) throws JsonProcessingException {

        String searchParameter = request.getSearch().getValue().trim();
        String[] splitSearch = StringUtils.split(searchParameter, StringUtils.SPACE, 3);
        Page<Invitation> invitations;
        if (splitSearch.length > 1) {
            invitations = invitationRepository.findByFullName(splitSearch[0], splitSearch[1], PageUtils.getPageRequest(request.getStart(), request.getLength()));
        } else {
            invitations = invitationRepository.findByGuestName(searchParameter, PageUtils.getPageRequest(request.getStart(), request.getLength()));
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

    @PreAuthorize("hasRole('ADMIN_EDIT')")
    @RequestMapping(value = "/saveInvitation", method = RequestMethod.POST)
    public String saveInvitation(@Valid @ModelAttribute Invitation invitation, Errors errors) {
        invitationValidator.validate(invitation, errors);
        if (errors.hasErrors()) {
            return generateInvitationModalContentLocator(invitation.getId());
        }

        Invitation storableInviation;
        if (invitation.getId() != null) {
            //Update Invitation
            storableInviation = invitationRepository.findById(invitation.getId());
            storableInviation.setMaxGuests(invitation.getMaxGuests());

            updateExistingGuests(invitation.getGuestList(),storableInviation.getGuestList());

            processUnsavedGuests(invitation.getGuestList(),storableInviation);

            if (storableInviation.getGuestList().size() != invitation.getGuestList().size()) {
                logger.error("List Sizes Do Not Match...");
                return String.format(CommonConstants.MODAL_ERROR_FRAGMENT_TEMPLATE, INVITATION_FORM_TYPE);
            }
        } else {
            //Create Invitation
            storableInviation = createInvitation(invitation);
        }
        return saveInvitation(storableInviation);
    }

    @PreAuthorize("hasRole('ADMIN_EDIT')")
    @RequestMapping(value = "/openInvitationModal", method = RequestMethod.POST)
    public String openInvitationModal(String invitationId, ModelMap modelMap) {
        modelMap.clear();
        if (StringUtils.isNotBlank(invitationId)) {
            Invitation invitation = invitationRepository.findById(Integer.parseInt(invitationId));
            modelMap.addAttribute(INVITATION_ATTRIBUTE_NAME, invitation);
        } else {
            Invitation invitation = new Invitation();
            invitation.setInvitationCode(InvitationUtils.generateInvitationCode(invitationRepository));
            modelMap.addAttribute(INVITATION_ATTRIBUTE_NAME, invitation);
        }
        return generateInvitationModalContentLocator(invitationId);
    }

    @PreAuthorize("hasRole('ADMIN_EDIT')")
    @RequestMapping(value = "/invitationModal/addGuest", method = RequestMethod.POST)
    public String invitationModalAddGuest(@ModelAttribute Invitation invitation) {
        if (invitation.getMaxGuests() != null && invitation.getGuestList().size() < invitation.getMaxGuests()) {
            invitation.getGuestList().add(new Guest());
        }
        return generateInvitationModalContentLocator(invitation.getId());
    }

    @PreAuthorize("hasRole('ADMIN_EDIT')")
    @RequestMapping(value = "/invitationModal/removeGuest", method = RequestMethod.POST)
    public String invitationModalRemoveGuest(@ModelAttribute Invitation invitation, @RequestParam() Integer removalIndex) {
        int guestSize = invitation.getGuestList().size();
        if (guestSize > 0 && removalIndex != null) {
            invitation.getGuestList().remove(removalIndex.intValue());
        }
        return generateInvitationModalContentLocator(invitation.getId());
    }

    private String generateInvitationModalContentLocator(Integer invitationId) {
        return generateInvitationModalContentLocator(invitationId != null ? invitationId.toString() : null);
    }

    private String generateInvitationModalContentLocator(String invitationId) {
        if (StringUtils.isNotBlank(invitationId)) {
            return String.format(INVITATION_MODAL_CONTENT_FRAGMENT, UPDATE_INVITATION_MODAL_TITLE);
        }
        return String.format(INVITATION_MODAL_CONTENT_FRAGMENT, CREATE_INVITATION_MODAL_TITLE);
    }

    private Invitation createInvitation(Invitation invitation){
        for (Guest guest : invitation.getGuestList()) {
            guest.setInvitedPerson(true);
            guest.setInvitation(invitation);
        }
        return invitation;
    }

    private void updateExistingGuests(List<Guest> pendingGuestList, List<Guest> existingGuestList){
        Iterator<Guest> existingGuestListIterator = existingGuestList.iterator();
        while(existingGuestListIterator.hasNext()){
            Guest existingGuest = existingGuestListIterator.next();
            List<Guest> filteredGuestList = pendingGuestList.stream().filter(pendingGuest -> existingGuest.getId().equals(pendingGuest.getId())).collect(Collectors.toList());
            if (!filteredGuestList.isEmpty()) {
                if (filteredGuestList.size() > 1) {
                    throw new IllegalStateException("Too Many Items Found");
                } else {
                    Guest pendingGuest = filteredGuestList.get(0);
                    existingGuest.setFirstName(pendingGuest.getFirstName());
                    existingGuest.setLastName(pendingGuest.getLastName());
                }
            } else {
                logger.info("Removing Guest");
                existingGuest.setInvitation(null);
                existingGuestListIterator.remove();
            }
        }
    }

    private void processUnsavedGuests(List<Guest> pendingGuestList, Invitation existingInvitation){
        List<Guest> unsavedGuests = pendingGuestList.stream().filter(pendingGuest -> pendingGuest.getId() == null).collect(Collectors.toList());
        if (!unsavedGuests.isEmpty()) {
            unsavedGuests.stream().forEach(pendingGuest -> { pendingGuest.setInvitation(existingInvitation); pendingGuest.setInvitedPerson(true);});
            existingInvitation.getGuestList().addAll(unsavedGuests);
        }
    }

    private String saveInvitation(Invitation invitation){
        try {
            invitationRepository.save(invitation);
            logger.info("Invitation Saved");
            return String.format(CommonConstants.MODAL_SUCCESS_FRAGMENT_TEMPLATE, INVITATION_FORM_TYPE);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return String.format(CommonConstants.MODAL_ERROR_FRAGMENT_TEMPLATE, INVITATION_FORM_TYPE);
        }
    }

}
