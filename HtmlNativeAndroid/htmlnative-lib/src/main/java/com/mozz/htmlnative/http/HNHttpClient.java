package com.mozz.htmlnative.http;

/**
 * @author Yang Tao, 17/5/29.
 */

public interface HNHttpClient {
    void send(HttpRequest request, RequestCallback callback);
}
