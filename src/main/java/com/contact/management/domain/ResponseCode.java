package com.contact.management.domain;

public enum ResponseCode {
    Ok(200, "Success"),
    Error(400, "Something wrong"),
    Unauthorized(401, "Unauthorized"),
    PermissionDenied(403, "Permission denied"),
    NothingFound(404, "Nothing found");

    public final int code;
    public final String message;

    ResponseCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }
}
