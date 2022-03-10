package com.alinote.api.model.common;

import lombok.*;
import org.springframework.http.*;

@Setter
@Getter
public class BaseWrapper {

    private Object response;

    private ResponseMessage responseMessage;

    private PaginationRequest pagination;

    public BaseWrapper() {
        this.responseMessage = new ResponseMessage(HttpStatus.OK.toString(), "OK");
    }

    public BaseWrapper(String message) {
        this.responseMessage = new ResponseMessage(HttpStatus.OK.toString(), message);
    }

    public BaseWrapper(Object response, ResponseMessage responseMessage) {
        this.response = response;
        this.responseMessage = responseMessage;
    }

    public BaseWrapper(Object response, PaginationRequest paginationRequest) {
        this.response = response;
        this.pagination = paginationRequest;
        this.responseMessage = new ResponseMessage(HttpStatus.OK.toString(), "OK");
    }


    public BaseWrapper(Object response) {
        this.response = response;
        this.responseMessage = new ResponseMessage(HttpStatus.OK.toString(), "OK");
    }
}
