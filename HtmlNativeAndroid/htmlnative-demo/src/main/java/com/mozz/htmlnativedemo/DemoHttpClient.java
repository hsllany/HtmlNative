package com.mozz.htmlnativedemo;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mozz.htmlnative.http.HNHttpClient;
import com.mozz.htmlnative.http.HttpRequest;
import com.mozz.htmlnative.http.HttpResponse;
import com.mozz.htmlnative.http.RequestCallback;

/**
 * @author Yang Tao, 17/5/30.
 */

public class DemoHttpClient implements HNHttpClient {

    private static RequestQueue sQueue = Volley.newRequestQueue(DemoApplication.instance);

    @Override
    public void send(final HttpRequest request, final RequestCallback callback) {

        Request<String> stringRequest = new StringRequest(request.getMethod(), request.getUrl(),
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                HttpResponse.Builder builder = new HttpResponse.Builder();
                builder.setStatusCode(200);
                builder.setBody(response.getBytes());
                if (callback != null) {
                    callback.onResponse(builder.build());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                HttpResponse.Builder builder = new HttpResponse.Builder();
                builder.setError(error);
                if (callback != null) {
                    callback.onResponse(builder.build());
                }
            }
        });
        sQueue.add(stringRequest);
    }
}
