package net.ddns.buckeyeflash.serializers;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.ddns.buckeyeflash.models.Food;
import net.ddns.buckeyeflash.models.Guest;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

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
        jsonGenerator.writeStringField("attendance", convertBoolean(guest.getAttendance()));
        jsonGenerator.writeStringField("food", convertFood(guest.getFood()));
        jsonGenerator.writeStringField("dietaryConcerns", convertBoolean(guest.getDietaryConcerns()));
        jsonGenerator.writeStringField("dietaryComments", guest.getDietaryComments());
        jsonGenerator.writeEndObject();
    }

    private String convertBoolean(Boolean value) {
        if (BooleanUtils.isTrue(value)) {
            return "Yes";
        } else if (BooleanUtils.isFalse(value)) {
            return "No";
        }
        return "";
    }

    private String convertFood(Food food) {
        if (food != null) {
            return food.getType();
        }
        return StringUtils.EMPTY;
    }
}
