package com.mozz.htmlnativedemo;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.mozz.htmlnative.http.HNHttpClient;
import com.mozz.htmlnative.http.Http;
import com.mozz.htmlnative.http.HttpRequest;

/**
 * @author Yang Tao, 17/5/30.
 */

public class DemoHttpClient implements HNHttpClient {

    private static RequestQueue sQueue = Volley.newRequestQueue(DemoApplication.instance);

    @Override
    public void send(HttpRequest request, final Http.RequestCallback callback) {
        Request<String> stringRequest = new Request<String>(request.method, request.url, new
                Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("DemoHttpClient", error.getMessage());
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return Response.success(new String(response.data), HttpHeaderParser
                        .parseCacheHeaders(response));
            }

            @Override
            protected void deliverResponse(final String res) {
                callback.onResponse(new Http.Response() {
                    private String header = null;
                    private String response = res;
                    private int statusCode = 200;

                    @Override
                    public void setHeader(String header) {

                    }

                    @Override
                    public String header() {
                        return header;
                    }

                    @Override
                    public void setBody(String body) {

                    }

                    @Override
                    public String body() {
                        return response;
                    }

                    @Override
                    public void setStatusCode(int code) {

                    }

                    @Override
                    public int statusCode() {
                        return statusCode;
                    }
                });
            }
        };

        sQueue.add(stringRequest);
    }
}
