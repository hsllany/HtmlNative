package com.mozz.htmlnative.script.lua;

import com.mozz.htmlnative.http.HNHttpClient;
import com.mozz.htmlnative.http.HttpRequest;
import com.mozz.htmlnative.http.HttpResponse;
import com.mozz.htmlnative.http.RequestCallback;

/**
 * @author Yang Tao, 17/5/29.
 */

public class EmptyHttpClient implements HNHttpClient {

    public static EmptyHttpClient instance = new EmptyHttpClient();

    private EmptyHttpClient() {
    }

    ;

    @Override
    public void send(HttpRequest request, RequestCallback callback) {
        HttpResponse.Builder builder = new HttpResponse.Builder();
        builder.setBody("helloworld".getBytes());
        builder.setStatusCode(200);
        callback.onResponse(builder.build());
    }
}
