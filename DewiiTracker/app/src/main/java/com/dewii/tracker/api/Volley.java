package com.dewii.tracker.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NoCache;

import org.json.JSONObject;


public class Volley {
    private static final String TAG = Volley.class.getCanonicalName();

    public interface OnResponseListener {
        void onSuccess(JSONObject response);

        void onError(VolleyError error);
    }

    public static final int CACHE_SIZE_MAX = 20 * 1024 * 1024; // 20 MB
    private static final int RETRY_TIMEOUT = 0; // 0 secs
    private static final int RETRY_NO_MAX = -1;
    private static final float RETRY_BACKOFF = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;

    public static RetryPolicy retryPolicyChildClasses = new RetryPolicy() {

        private int currentRetryCount = 0;

        @Override
        public int getCurrentTimeout() {
            Log.i(TAG, "retryPolicyChildClasses.getCurrentTimeout: " + RETRY_TIMEOUT);
            return RETRY_TIMEOUT;
        }

        @Override
        public int getCurrentRetryCount() {
            Log.i(TAG, "retryPolicyChildClasses.getCurrentRetryCount: " + ++currentRetryCount);
            return currentRetryCount;
        }

        @Override
        public void retry(VolleyError error) throws VolleyError {
            Log.i(TAG, "retryPolicyChildClasses.retry: " + error);
        }
    };

    public static RetryPolicy retryPolicy = new DefaultRetryPolicy(RETRY_TIMEOUT, RETRY_NO_MAX, RETRY_BACKOFF);

    private static Volley volley;
    private RequestQueue requestQueue;

    private Volley(Context context) {
        requestQueue = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()));
        requestQueue.start();
    }

    public static synchronized Volley getInstance(Context context) {
        if (volley == null) {
            volley = new Volley(context.getApplicationContext());
        }
        return volley;
    }

    public void cancelRequest(Object tag) {
        requestQueue.cancelAll(tag);
    }

    public void executePostJsonRequest(Context context, String url, JSONObject params, OnResponseListener onResponseListener) {
        Log.i(TAG, "executePostJsonRequest: \n" + url + ":" + params.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    Log.i(TAG, response.toString());
                    if (onResponseListener != null)
                        onResponseListener.onSuccess(response);
                },
                error -> {
                    Log.e(TAG, String.valueOf(error));
                    if (onResponseListener != null)
                        onResponseListener.onError(error);
                })
        {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background_login
                    final long cacheExpired = 5 * 24 * 60 * 60 * 1000; // in 5*24 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;

                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire; // Represents refresh interval of cache
                    cacheEntry.ttl = ttl; // Represents total duration(life-span) of cache before expiry

                    String headerValue;

                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }

                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }

                    cacheEntry.responseHeaders = response.headers;

                    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                    return Response.success(new JSONObject(jsonString), cacheEntry);

                } catch (Exception e) {
                    return Response.error(new ParseError(e));
                }
            }
        };

        request.setShouldCache(false);
        request.setRetryPolicy(retryPolicy);
        request.setTag(context);
        requestQueue.add(request);
    }
}
