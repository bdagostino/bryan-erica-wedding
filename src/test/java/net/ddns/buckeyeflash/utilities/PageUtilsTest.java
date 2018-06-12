package net.ddns.buckeyeflash.utilities;

import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

public class PageUtilsTest {

    @Test
    public void testGetPageRequest(){
        int requestElement = 15;
        int requestedPageLength = 10;
        PageRequest pageRequest = PageUtils.getPageRequest(requestElement,requestedPageLength);
        assertThat(pageRequest).isNotNull();
        assertThat(pageRequest.getPageNumber()).isEqualTo(1);
        assertThat(pageRequest.getPageSize()).isEqualTo(10);
    }
}
