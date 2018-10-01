package com.personalassistant.endpoint;

import android.support.v4.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.personalassistant.BuildConfig;
import com.personalassistant.utils.Constants;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class JsonRequest extends JsonObjectRequest {

    /**
     * User token for authorized requests.
     */

    private int requestStatusCode;

    private String requestUrl;

    public JsonRequest(int method, String requestUrl, JSONObject jsonRequest, Response.Listener<JSONObject> successListener,
                       Response.ErrorListener errorListener, FragmentManager fragmentManager, String accessToken) {
        super(method, requestUrl, jsonRequest, successListener, errorListener);
        this.requestUrl = requestUrl;
    }

    public JsonRequest(int method, String requestUrl, JSONObject jsonRequest, Response.Listener<JSONObject> successListener,
                       Response.ErrorListener errorListener) {
        this(method, requestUrl, jsonRequest, successListener, errorListener, null, null);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        return headers;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            requestStatusCode = response.statusCode;
            if (BuildConfig.DEBUG)
                Timber.d("%s URL: %s. ResponseCode: %d", this.getClass().getSimpleName(), requestUrl, response.statusCode);
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
        return super.parseNetworkResponse(response);
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        if (volleyError.networkResponse != null) {
            // Save request status code
            requestStatusCode = volleyError.networkResponse.statusCode;
            if (BuildConfig.DEBUG)
                Timber.e("%s URL: %s. ERROR: %s", this.getClass().getSimpleName(), requestUrl, new String(volleyError.networkResponse.data));
        } else {
            requestStatusCode = Constants.MissingStatusCode;
        }
        return super.parseNetworkError(volleyError);
    }

    /**
     * Method returns result statusCode of invoked request.
     *
     * @return HTTP status code.
     */
    private int getStatusCode() {
        return requestStatusCode;
    }
}
