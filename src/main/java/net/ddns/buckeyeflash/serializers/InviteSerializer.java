package net.ddns.buckeyeflash.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.Invite;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class InviteSerializer extends StdSerializer<Invite> {

    public InviteSerializer() {
        super(Invite.class);
    }

    @Override
    public void serialize(Invite invite, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", invite.getId());
        jsonGenerator.writeStringField("invitedGuests", filterInvitedGuests(invite.getGuestList()));
        jsonGenerator.writeStringField("additionalGuests", filterNonInvitedGuests(invite.getGuestList()));
        jsonGenerator.writeNumberField("maxAdditionalGuests", invite.getMaxAdditionalGuests());
        jsonGenerator.writeEndObject();
    }

    private String filterInvitedGuests(List<Guest> guests) {
        List<Guest> filtered = guests.stream().filter(guest -> guest.getInvitedPerson()).collect(Collectors.toList());
        return prettyPrintGuests(filtered);
    }

    private String filterNonInvitedGuests(List<Guest> guests) {
        List<Guest> filtered = guests.stream().filter(guest -> guest.getInvitedPerson() == false).collect(Collectors.toList());
        return prettyPrintGuests(filtered);
    }

    private String prettyPrintGuests(List<Guest> guests) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Guest> guestIterator = guests.iterator();
        while (guestIterator.hasNext()) {
            Guest guest = guestIterator.next();
            stringBuilder.append(guest.getFirstName());
            stringBuilder.append(StringUtils.SPACE);
            stringBuilder.append(guest.getLastName());
            if (guestIterator.hasNext()) {
                stringBuilder.append(", ");
            } else {
                break;
            }
        }
        return stringBuilder.toString();
    }


}
