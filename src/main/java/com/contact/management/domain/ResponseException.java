package com.contact.management.domain;

public final class ResponseException extends RuntimeException {

    public static final ResponseException Error = new ResponseException(ResponseCode.Error);
    public static final ResponseException PermissionDenied = new ResponseException(ResponseCode.PermissionDenied);
    public static final ResponseException NothingFound = new ResponseException(ResponseCode.NothingFound);

    public final int code;
    public final Object data;

    private ResponseException(final ResponseCode code) {
        this(code, code.message);
    }

    private ResponseException(final ResponseCode code, final Object data) {
        super(data instanceof String message ? message : code.message, null, false, false);
        this.code = code.code;
        this.data = data;
    }

    public static ResponseException wrongParam(final String paramName) {
        return new ResponseException(ResponseCode.Error, "Parameter " + paramName + " contains wrong value");
    }

    public static ResponseException error(final ResponseCode code, final Object data) {
        return new ResponseException(code, data);
    }
}
