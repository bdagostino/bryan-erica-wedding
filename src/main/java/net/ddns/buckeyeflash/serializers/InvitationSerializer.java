package net.ddns.buckeyeflash.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.ddns.buckeyeflash.models.Guest;
import net.ddns.buckeyeflash.models.Invitation;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class InvitationSerializer extends StdSerializer<Invitation> {

    public InvitationSerializer() {
        super(Invitation.class);
    }

    @Override
    public void serialize(Invitation invitation, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", invitation.getId());
        jsonGenerator.writeStringField("invitedGuests", filterInvitedGuests(invitation.getGuestList()));
        jsonGenerator.writeStringField("additionalGuests", filterNonInvitedGuests(invitation.getGuestList()));
        jsonGenerator.writeNumberField("maxAdditionalGuests", invitation.getMaxGuests());
        jsonGenerator.writeStringField("invitationCode",invitation.getInvitationCode());
        jsonGenerator.writeEndObject();
    }

    private String filterInvitedGuests(List<Guest> guests) {
        List<Guest> filtered = guests.stream().filter(guest -> guest.getInvitedPerson()).collect(Collectors.toList());
        return prettyPrintGuests(filtered);
    }

    private String filterNonInvitedGuests(List<Guest> guests) {
        List<Guest> filtered = guests.stream().filter(guest -> !guest.getInvitedPerson()).collect(Collectors.toList());
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
