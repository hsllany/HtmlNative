package com.mozz.htmlnative.http;

import java.util.Map;

/**
 * @author Yang Tao, 17/5/30.
 */

public class HttpResponse {

    private Map<String, String> mHeader;
    private byte[] mBodyRaw;
    private int mStatusCode;
    private Exception mError;

    private HttpResponse() {
        mStatusCode = 500;
    }

    private HttpResponse(Map<String, String> header, byte[] body, int statusCode, Exception error) {
        this.mHeader = header;
        this.mBodyRaw = body;
        this.mStatusCode = statusCode;
        this.mError = error;
    }

    private HttpResponse(Map<String, String> header, String body, int statusCode, Exception error) {
        this(null, body.getBytes(), statusCode, error);
    }

    public Map<String, String> getHeader() {
        return mHeader;
    }

    public String getBodyAsString() {
        if (mBodyRaw == null) {
            return null;
        }
        return new String(mBodyRaw);
    }

    public byte[] getBody() {
        return mBodyRaw;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public boolean isSuccess() {
        return mError != null;
    }

    public boolean isOk() {
        return isSuccess() && mStatusCode == 200;
    }

    public static class Builder {
        HttpResponse response = null;

        public Builder() {
            response = new HttpResponse();
        }

        public Builder setBody(byte[] bytes) {
            response.mBodyRaw = bytes;
            return this;
        }

        public Builder setStatusCode(int statusCode) {
            response.mStatusCode = statusCode;
            return this;
        }

        public Builder setHeader(Map<String, String> header) {
            response.mHeader = header;
            return this;
        }

        public Builder setError(Exception exception) {
            response.mError = exception;
            return this;
        }

        public HttpResponse build() {
            return response;
        }
    }
}
