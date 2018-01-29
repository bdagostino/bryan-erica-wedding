package net.ddns.buckeyeflash.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.ddns.buckeyeflash.models.CommonConstants;
import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.Invitation;
import net.ddns.buckeyeflash.models.datatable.DatatableRequest;
import net.ddns.buckeyeflash.models.datatable.DatatableResponse;
import net.ddns.buckeyeflash.repositories.InvitationRepository;
import net.ddns.buckeyeflash.serializers.InvitationSerializer;
import net.ddns.buckeyeflash.utilities.InvitationUtils;
import net.ddns.buckeyeflash.validators.InvitationValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
@SessionAttributes("invitation")
public class InvitationAdminController extends BaseAdminController implements CommonConstants {
    private static final Logger logger = Logger.getLogger(InvitationAdminController.class);
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

    @PreAuthorize("hasRole('ADMIN_EDIT')")
    @RequestMapping(value = "/saveInvitation", method = RequestMethod.POST)
    public String saveInvitation(@Valid @ModelAttribute Invitation invitation, Errors errors) throws Exception {
        invitationValidator.validate(invitation, errors);
        if (errors.hasErrors()) {
            return generateInvitationModalContentLocator(invitation.getId());
        }

        if (invitation.getId() != null) {
            //Update Invitation
            Invitation storedInvitation = invitationRepository.findById(invitation.getId());
            storedInvitation.setMaxGuests(invitation.getMaxGuests());
            List<Guest> pendingGuestList = invitation.getGuestList();
            Iterator<Guest> listIterator = storedInvitation.getGuestList().iterator();
            while (listIterator.hasNext()) {
                Guest storedGuest = listIterator.next();
                List<Guest> filteredGuestList = pendingGuestList.stream().filter(pendingGuest -> storedGuest.getId().equals(pendingGuest.getId())).collect(Collectors.toList());
                if (filteredGuestList.size() > 0) {
                    if (filteredGuestList.size() > 1) {
                        throw new Exception("Too Many Items Found");
                    } else {
                        Guest pendingGuest = filteredGuestList.get(0);
                        storedGuest.setFirstName(pendingGuest.getFirstName());
                        storedGuest.setLastName(pendingGuest.getLastName());
                    }
                } else {
                    logger.info("Removing Guest");
                    storedGuest.setInvitation(null);
                    listIterator.remove();
                }
            }
            List<Guest> unsavedGuests = pendingGuestList.stream().filter(pendingGuest -> pendingGuest.getId() == null).collect(Collectors.toList());
            if (!unsavedGuests.isEmpty()) {
                unsavedGuests.stream().forEach(pendingGuest -> {
                    pendingGuest.setInvitation(storedInvitation);
                    pendingGuest.setInvitedPerson(true);
                });
                storedInvitation.getGuestList().addAll(unsavedGuests);
            }
            if (storedInvitation.getGuestList().size() != pendingGuestList.size()) {
                logger.error("List Sizes Do Not Match...");
                return String.format(MODAL_ERROR_FRAGMENT_TEMPLATE, "Invitation");
            }
            try {
                invitationRepository.save(storedInvitation);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return String.format(MODAL_ERROR_FRAGMENT_TEMPLATE, "Invitation");
            }

        } else {
            //Create Invitation
            for (Guest guest : invitation.getGuestList()) {
                guest.setInvitedPerson(true);
                guest.setInvitation(invitation);
            }
            try {
                invitationRepository.save(invitation);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return String.format(MODAL_ERROR_FRAGMENT_TEMPLATE, "Invitation");
            }
        }

        logger.info("Invitation Saved");
        return String.format(MODAL_SUCCESS_FRAGMENT_TEMPLATE, "Invitation");
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
        if (invitation.getMaxGuests() != null) {
            if (invitation.getGuestList().size() < invitation.getMaxGuests()) {
                invitation.getGuestList().add(new Guest());
            }
        }
        return generateInvitationModalContentLocator(invitation.getId());
    }

    @PreAuthorize("hasRole('ADMIN_EDIT')")
    @RequestMapping(value = "/invitationModal/removeGuest", method = RequestMethod.POST)
    public String invitationModalRemoveGuest(@ModelAttribute Invitation invitation) {
        int guestSize = invitation.getGuestList().size();
        if (guestSize > 0 && invitation.getRemovalIndex() != null) {
            invitation.getGuestList().remove(invitation.getRemovalIndex().intValue());
        }
        invitation.setRemovalIndex(null);
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

}
