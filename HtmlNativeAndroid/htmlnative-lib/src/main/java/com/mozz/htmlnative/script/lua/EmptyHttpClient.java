package com.mozz.htmlnative.script.lua;

import com.mozz.htmlnative.http.HNHttpClient;
import com.mozz.htmlnative.http.Http;
import com.mozz.htmlnative.http.HttpRequest;

/**
 * @author Yang Tao, 17/5/29.
 */

public class EmptyHttpClient implements HNHttpClient {

    public static EmptyHttpClient instance = new EmptyHttpClient();

    private EmptyHttpClient() {
    }

    ;

    @Override
    public void send(HttpRequest request, Http.RequestCallback callback) {
        LHttp.LResponse res = new LHttp.LResponse();
        res.setHeader("fake-header");
        res.setBody("fake response body from " + request.url);
        res.setStatusCode(200);
        callback.onResponse(res);
    }
}
