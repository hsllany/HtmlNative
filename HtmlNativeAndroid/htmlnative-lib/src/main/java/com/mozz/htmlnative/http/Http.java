package com.mozz.htmlnative.http;

/**
 * @author Yang Tao, 17/5/29.
 */

public interface Http {

    interface Response {
        void setHeader(String header);

        String header();

        void setBody(String body);

        String body();

        void setStatusCode(int code);

        int statusCode();
    }

    interface Request {
        String getUrl();

        String getBody();

        int getMethod();
    }

    /**
     * Same as {@link com.android.volley.Request}
     */
    interface Method {
        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    interface RequestCallback {
        void onResponse(Response response);
    }
}

