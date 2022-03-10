package com.alinote.api.exception;

import com.alinote.api.constants.*;
import com.alinote.api.model.common.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.env.*;
import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.*;

import javax.servlet.http.*;
import java.util.*;

@Slf4j
@ControllerAdvice
public class AlinoteExceptionHandler {

    @Autowired
    private Environment env;

    @ExceptionHandler(value = {
            MissingServletRequestParameterException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public BaseWrapper stringException(HttpServletRequest request, HttpServletResponse response,
                                       Exception ex) {

        log.error("Stack Trace: ", ex);

        BaseWrapper apiResponse = new BaseWrapper(
                null,
                new ResponseMessage(
                        "400",
                        ex.getMessage()
                )
        );
        return apiResponse;
    }

    @ExceptionHandler(value = {
            ServicesException.class,
            MissingRequestHeaderException.class,
            MethodArgumentTypeMismatchException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMessageNotReadableException.class,
            UsernameNotFoundException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public BaseWrapper validationException(HttpServletRequest request, HttpServletResponse response,
                                           Exception ex) {

        String finalMessage = env.getProperty(ex.getMessage());

        Object requestAttributesObject = request.getAttribute("requestAttributes");
        if (null != requestAttributesObject && !((ArrayList<String>) requestAttributesObject).isEmpty()) {
            List<String> requestAttributes = (ArrayList<String>) requestAttributesObject;
//            Collection<String> attributeValues = requestAttributes.values();
            finalMessage = String.format(finalMessage, requestAttributes.toArray());
            log.info("finalMessage = {}", finalMessage);
        }

        log.error("PTA Validation Error:" + ex.getMessage());
        log.error("Stack Trace: ", ex);

        BaseWrapper apiResponse = new BaseWrapper(
                null,
                new ResponseMessage(
                        ex.getMessage(),
                        env.getProperty(ex.getMessage())
                )
        );
        return apiResponse;
    }

    @ExceptionHandler(value = {
            DuplicateRecordException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public BaseWrapper duplicateEntryException(
            HttpServletRequest request,
            HttpServletResponse response,
            DuplicateRecordException ex) {

        log.error("PTA Validation Error:" + ex.getMessage());
        log.error("Stack Trace: ", ex);

        BaseWrapper apiResponse = new BaseWrapper(
                null,
                new ResponseMessage(
                        ex.getMessage(),
                        env.getProperty(ex.getMessage())));

        return apiResponse;
    }


    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public BaseWrapper genericException(HttpServletRequest request, HttpServletResponse response,
                                        Exception ex) {

        if (null != request.getAttribute("javax.servlet.error.status_code")
                && request.getAttribute("javax.servlet.error.status_code").equals(HttpStatus.INTERNAL_SERVER_ERROR.value())) {
            log.error("URI in Exception::{}", request.getAttribute("javax.servlet.error.request_uri"));
        }
        log.error("Generic Error:" + ex.getMessage());
        log.error("Stack Trace: ", ex);


        BaseWrapper apiResponse = new BaseWrapper(null, new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.name(), GlobalConstants.GENERIC_ERROR_MSSG));
        return apiResponse;
    }
}
