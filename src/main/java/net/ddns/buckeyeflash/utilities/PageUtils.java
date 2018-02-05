package net.ddns.buckeyeflash.utilities;

import org.springframework.data.domain.PageRequest;

public class PageUtils {

    private PageUtils() {
    }

    public static PageRequest getPageRequest(int requestIndex, int requestedPageLength){
        return new PageRequest((requestIndex/requestedPageLength),requestedPageLength);
    }

}
