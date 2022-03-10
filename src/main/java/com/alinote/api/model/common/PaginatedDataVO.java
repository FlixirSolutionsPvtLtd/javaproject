package com.alinote.api.model.common;

import lombok.*;

import java.io.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedDataVO<T> implements Serializable {
    private static final long serialVersionUID = -7746914573846863535L;

    private T data;
    private PaginationRequest paginationDetails;
}
