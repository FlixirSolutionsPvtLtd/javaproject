package com.alinote.api.utility;

import org.springframework.data.domain.*;

public class PaginationUtils {


    public static Pageable getPageRequest(int pageNo, int size) {
        pageNo = pageNo <= 0 ? 0 : (pageNo - 1);
        size = size <= 0 ? 50 : size;

        return PageRequest.of(pageNo, size);
    }
}
