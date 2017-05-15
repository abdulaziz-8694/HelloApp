package com.example.abdul.helloapp.Utils;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;


public class GsonRequest<T> extends Request<T> {
    private Class<T> mClass;
    private Response.Listener<T> mListener;
    private Gson mGson;
    public GsonRequest(int method, String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mGson = new Gson();
        mClass = clazz;
        mListener = listener;
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        try{
            String data = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return new Response.success(mGson.fromJson(data, mClass),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return new Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return new Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }
}