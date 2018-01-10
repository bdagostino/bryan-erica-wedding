package net.ddns.buckeyeflash.serializers;

import net.ddns.buckeyeflash.models.Food;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializerUtilsTest {

    @Test
    public void testConvertBooleanTrue(){
        String result = SerializerUtils.convertBoolean(Boolean.TRUE);
        assertThat(result).isNotEmpty().isEqualTo("Yes");
    }

    @Test
    public void testConvertBooleanFalse(){
        String result = SerializerUtils.convertBoolean(Boolean.FALSE);
        assertThat(result).isNotEmpty().isEqualTo("No");
    }

    @Test
    public void testConvertBooleanNull(){
        String result = SerializerUtils.convertBoolean(null);
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    public void testConvertFood(){
        Food food = new Food();
        food.setId(1);
        food.setType("Chicken");
        food.setDescription("Fried Chicken");
        String foodType = SerializerUtils.convertFood(food);
        assertThat(foodType).isNotEmpty().isEqualTo("Chicken");
    }

    @Test
    public void testConvertFoodNull(){
        String foodType = SerializerUtils.convertFood(null);
        assertThat(foodType).isNotNull().isEmpty();
    }
}
