package com.mozz.htmlnative.http;

import java.util.Map;

import static com.mozz.htmlnative.utils.IOUtils.postParamsToString;

/**
 * @author Yang Tao, 17/5/29.
 */

public class HttpRequest {
    public HttpRequest(String url, String body, int method) {
        this.url = url;
        this.body = body;
        this.method = method;
    }

    public HttpRequest(String url, Map<String, String> postParams, int method) {
        this(url, postParamsToString(postParams), method);
    }

    public HttpRequest(String url, int method) {
        this(url, (String) null, method);
    }


    public String url;
    public String body;
    public int method;
}
