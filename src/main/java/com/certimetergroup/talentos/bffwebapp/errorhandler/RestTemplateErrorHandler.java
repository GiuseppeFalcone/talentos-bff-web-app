package com.certimetergroup.talentos.bffwebapp.errorhandler;

import com.certimetergroup.talentos.commons.enumeration.ResponseEnum;
import com.certimetergroup.talentos.commons.exception.FailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

@Component
public class RestTemplateErrorHandler implements ResponseErrorHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is5xxServerError() || response.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        String error = "Error in External Server Endpoint, Status Code: ".concat(response.getStatusText().toString());
        logger.error(error);
        if (response.getStatusCode().is5xxServerError()) {
            throw new FailureException(ResponseEnum.EXTERNAL_SERVER_ERROR);
        }
        if (response.getStatusCode().is4xxClientError()) {
            HttpStatusCode responseStatusCode = response.getStatusCode();
            ResponseEnum responseEnum = ResponseEnum.INTERNAL_SERVER_ERROR;
            if (ResponseEnum.REMOTE_BAD_REQUEST.httpStatus.isSameCodeAs(responseStatusCode)) {
                responseEnum = ResponseEnum.REMOTE_BAD_REQUEST;
            } else if (ResponseEnum.REMOTE_UNAUTHORIZED.httpStatus.isSameCodeAs(responseStatusCode)) {
                responseEnum = ResponseEnum.REMOTE_UNAUTHORIZED;
            } else if (ResponseEnum.REMOTE_FORBIDDEN.httpStatus.isSameCodeAs(responseStatusCode)) {
                responseEnum = ResponseEnum.REMOTE_FORBIDDEN;
            } else if (ResponseEnum.REMOTE_NOT_FOUND.httpStatus.isSameCodeAs(responseStatusCode)) {
                responseEnum = ResponseEnum.REMOTE_NOT_FOUND;
            } else if (ResponseEnum.REMOTE_SERVER_ERROR.httpStatus.isSameCodeAs(responseStatusCode)) {
                responseEnum = ResponseEnum.REMOTE_SERVER_ERROR;
            }

            if (response.getStatusText().isBlank())
                throw new FailureException(responseEnum);
            throw new FailureException(responseEnum, response.getStatusText());
        }
    }
}
