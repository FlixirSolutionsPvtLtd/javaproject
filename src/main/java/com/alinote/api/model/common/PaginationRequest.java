package com.alinote.api.model.common;

import lombok.*;
import org.springframework.data.domain.*;

import static com.alinote.api.utility.CheckUtil.*;

@Setter
@Getter
@NoArgsConstructor
public class PaginationRequest {
    private int currentPageNo;
    private int currentPageSize;
    private long totalRecords;
    private double totalPages;
    private int currentPageElements;

    public PaginationRequest(int currentPageNo, int currentPageSize, long totalRecords, double totalPages) {
        this.currentPageNo = currentPageNo;
        this.totalRecords = totalRecords;
        this.currentPageSize = currentPageSize;
        this.totalPages = totalPages;
    }

    public PaginationRequest(int currentPageNo, int currentPageSize, Page<?> page) {
        this.currentPageNo = currentPageNo <= 0 ? 1 : currentPageNo;
        this.currentPageSize = currentPageSize;
        this.totalRecords = hasValue(page) ? page.getTotalElements() : 0;
        this.totalPages = hasValue(page) ? page.getTotalPages() : 0;
        this.currentPageElements = hasValue(page) ? page.getContent().size() : 0;
    }
}
