package com.mozz.htmlnative.http;

import android.support.annotation.IntDef;

import com.mozz.htmlnative.utils.IOUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import static java.util.Collections.EMPTY_MAP;

/**
 * @author Yang Tao, 17/5/29.
 */

public class HttpRequest {
    public interface Method {
        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({DEPRECATED_GET_OR_POST, GET, POST, PUT, DELETE, HEAD, OPTIONS, TRACE, PATCH})
        @interface MethodDef {
        }
    }

    private final String url;
    private final byte[] body;

    @Method.MethodDef
    private final int method;

    private final Map<String, String> mHeaders;

    public HttpRequest(String url, @Method.MethodDef int method, byte[] bodyRaw, Map<String,
            String> headers) {
        this.url = url;
        this.body = bodyRaw;
        this.mHeaders = headers;
        this.method = method;
    }

    public HttpRequest(String url, @Method.MethodDef int method, byte[] bodyRaw) {
        this(url, method, bodyRaw, EMPTY_MAP);
    }

    public HttpRequest(String url, @Method.MethodDef int method, Map<String, String> postData,
                       Map<String, String> headers) {
        this(url, method, IOUtils.postParamsToString(postData).getBytes(), headers);
    }

    public String getUrl() {
        return url;
    }

    public String getBodyAsString() {
        return new String(body);
    }

    public byte[] getRawBody() {
        return body;
    }

    @Method.MethodDef
    public int getMethod() {
        return method;
    }

    public Map<String, String> getAdditionalHeaders() {
        return mHeaders;
    }
}
