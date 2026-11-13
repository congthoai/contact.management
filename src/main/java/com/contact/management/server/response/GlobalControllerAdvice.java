package com.contact.management.server.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.contact.management.server.response.domain.Response;

@ControllerAdvice
public class GlobalControllerAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(final MethodParameter returnType,
            final Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    @Nullable
    public Object beforeBodyWrite(@Nullable final Object body, final MethodParameter returnType,
            final MediaType selectedContentType,
            final Class<? extends HttpMessageConverter<?>> selectedConverterType,
            final ServerHttpRequest request, final ServerHttpResponse response) {
        if (ResponseEntity.class.isAssignableFrom(returnType.getParameterType())) {
            return body;
        }

        if (returnType.getParameterType() == void.class || returnType.getParameterType() == Void.class) {
            return Response.ok(body);
        }

        if (body == null) {
            return Response.NothingFound;
        }

        if (body instanceof Response) {
            return body;
        }

        if (body instanceof Boolean result) {
            return result ? Response.OK : Response.Error;
        }

        return Response.ok(body);
    }
}
