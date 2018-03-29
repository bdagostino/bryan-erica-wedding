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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "admin/invitation")
@SessionAttributes({"invitation", "canAdminEdit"})
public class InvitationAdminController {
    private static final Logger logger = LogManager.getLogger(InvitationAdminController.class);
    private static final String INVITATION_FORM_TYPE = "Invitation";
    private static final String INVITATION_ATTRIBUTE_NAME = "invitation";
    private static final String CREATE_INVITATION_MODAL_TITLE = "Create Invitation";
    private static final String UPDATE_INVITATION_MODAL_TITLE = "Update Invitation";
    private static final String INVITATION_MODAL_CONTENT_FRAGMENT = "fragments/admin/invitation_fragments :: invitationModalContent(title='%s')";

    private final InvitationRepository invitationRepository;

    private final InvitationValidator invitationValidator;

    public InvitationAdminController(InvitationRepository invitationRepository, InvitationValidator invitationValidator) {
        this.invitationRepository = invitationRepository;
        this.invitationValidator = invitationValidator;
    }

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
        DatatableResponse<Invitation> datatableResponse = new DatatableResponse<>();
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

        boolean isSaved = this.invitationRepository.saveInvitation(invitation);
        if (isSaved) {
            return String.format(CommonConstants.MODAL_SUCCESS_FRAGMENT_TEMPLATE, INVITATION_FORM_TYPE);
        } else {
            return String.format(CommonConstants.MODAL_ERROR_FRAGMENT_TEMPLATE, INVITATION_FORM_TYPE);
        }
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
}