package com.mozz.htmlnative.http;

/**
 * @author Yang Tao, 17/5/30.
 */

public class HttpResponse implements Http.Response {

    public HttpResponse(String header, String body, int statusCode) {
        this.mHeader = header;
        this.mBody = body;
        this.mStatusCode = statusCode;
    }

    public HttpResponse(String body, int statusCode) {
        this(null, body, statusCode);
    }

    private String mHeader;
    private String mBody;
    private int mStatusCode;

    @Override
    public void setHeader(String header) {
        mHeader = header;
    }

    @Override
    public String header() {
        return mHeader;
    }

    @Override
    public void setBody(String body) {
        mBody = body;
    }

    @Override
    public String body() {
        return mBody;
    }

    @Override
    public void setStatusCode(int code) {
        mStatusCode = code;
    }

    @Override
    public int statusCode() {
        return mStatusCode;
    }
}
