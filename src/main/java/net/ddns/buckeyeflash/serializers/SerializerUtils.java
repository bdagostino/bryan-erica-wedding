package net.ddns.buckeyeflash.serializers;

import net.ddns.buckeyeflash.models.Food;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class SerializerUtils {

    public static String convertBoolean(Boolean value) {
        if (BooleanUtils.isTrue(value)) {
            return "Yes";
        } else if (BooleanUtils.isFalse(value)) {
            return "No";
        }
        return "";
    }

    public static String convertFood(Food food) {
        if (food != null) {
            return food.getType();
        }
        return StringUtils.EMPTY;
    }
}
