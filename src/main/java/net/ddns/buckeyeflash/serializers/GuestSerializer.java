package net.ddns.buckeyeflash.serializers;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.ddns.buckeyeflash.models.Guest;

import java.io.IOException;

public class GuestSerializer extends StdSerializer<Guest> {

    public GuestSerializer() {
        super(Guest.class);
    }

    @Override
    public void serialize(Guest guest, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", guest.getId());
        jsonGenerator.writeStringField("firstName", guest.getFirstName());
        jsonGenerator.writeStringField("lastName", guest.getLastName());
        jsonGenerator.writeStringField("attendance", SerializerUtils.convertBoolean(guest.getAttendance()));
        jsonGenerator.writeStringField("food", SerializerUtils.convertFood(guest.getFood()));
        jsonGenerator.writeStringField("dietaryConcerns", SerializerUtils.convertBoolean(guest.getDietaryConcerns()));
        jsonGenerator.writeStringField("dietaryComments", guest.getDietaryComments());
        jsonGenerator.writeEndObject();
    }
}
