package com.contact.management.server.response.domain;

public class Response {

    public static final Response OK = new Response(200, "success");
    public static final Response Error = new Response(400, "Something wrong");
    public static final Response NothingFound = new Response(404, "Nothing found");

    public final int code;
    public final Object data;

    public Response(final int code, final Object data) {
        this.code = code;
        this.data = data;
    }

    public static Response ok(final Object data) {
        return new Response(200, data);
    }
}
