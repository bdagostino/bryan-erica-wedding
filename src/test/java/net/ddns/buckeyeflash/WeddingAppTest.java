package net.ddns.buckeyeflash;


import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class WeddingAppTest {

    @Test
    public void testRandomAlphanumeric() {
        for (int i = 0; i < 25; i++) {
            System.out.println(RandomStringUtils.randomAlphanumeric(4).toUpperCase());
        }
    }


}
