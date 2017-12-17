package net.ddns.buckeyeflash.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.ddns.buckeyeflash.models.Invite;
import net.ddns.buckeyeflash.models.datatable.DatatableRequest;
import net.ddns.buckeyeflash.models.datatable.DatatableResponse;
import net.ddns.buckeyeflash.repositories.InviteRepository;
import net.ddns.buckeyeflash.serializers.InviteSerializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class InviteController extends BaseAdminController {
    private static final Logger logger = Logger.getLogger(InviteController.class);

    @Autowired
    private InviteRepository inviteRepository;

    @RequestMapping(value = "/admin/invite", method = RequestMethod.GET)
    public String invite() {
        logger.info("Admin Invite Page Accessed");
        return "pages/admin/invite";
    }

    @RequestMapping(value = "/admin/invite/getInviteData", method = RequestMethod.POST)
    public @ResponseBody
    String getInviteData(@RequestBody DatatableRequest request) throws Exception {

        PageRequest pageRequest = new PageRequest((int) Math.floor(request.getStart() / request.getLength()), request.getLength());
        String searchParameter = request.getSearch().getValue().trim();
        String[] splitSearch = StringUtils.split(searchParameter, StringUtils.SPACE, 3);
        Page<Invite> invites;
        if (splitSearch.length > 1) {
            invites = inviteRepository.findByFullName(splitSearch[0], splitSearch[1], pageRequest);
        } else {
            invites = inviteRepository.findByGuestName(searchParameter, pageRequest);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(new InviteSerializer());
        objectMapper.registerModule(simpleModule);
        DatatableResponse datatableResponse = new DatatableResponse();
        datatableResponse.setDraw(request.getDraw());
        datatableResponse.setRecordsFiltered((int) invites.getTotalElements());
        datatableResponse.setRecordsTotal((int) invites.getTotalElements());
        datatableResponse.setData(invites.getContent());
        logger.info("Retrieved Invite Data From Database");
        String json = objectMapper.writeValueAsString(datatableResponse);
        return json;
    }

}
