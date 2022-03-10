package com.alinote.api.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.util.Collection;


@AllArgsConstructor
@Getter
@Setter
public class Pagination {

    private long totalElements;
    private int size;
    private int pageSize;
    private int pageNumber;

    public Pagination(Collection<?> content, long totalElements, Pageable pageable) {
        this.totalElements = totalElements;
        this.pageSize = pageable.getPageSize();
        this.pageNumber = pageable.getPageNumber();
        this.size = content.size();
    }

    public Pagination(Pageable pageable) {
        this.pageNumber = pageable.getPageNumber();
        this.pageSize = pageable.getPageSize();
    }


}
