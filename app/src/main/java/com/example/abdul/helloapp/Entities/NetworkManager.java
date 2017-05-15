package com.example.abdul.helloapp.Entities;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Singleton class to handle request queues and caches using
 * Volley library.
 */
public class NetworkManager {
    private static NetworkManager mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Context mContext;

    private NetworkManager(Context context){
        mContext = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = getImageLoader();
    }

    public static synchronized NetworkManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new NetworkManager(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(mRequestQueue == null){
            //Getting application context instead of activity or other components context
            //to prevent leaks.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }

    public ImageLoader getImageLoader(){
        if(mImageLoader == null){
            mImageLoader = new ImageLoader(mRequestQueue,
                    new ImageLoader.ImageCache() {
                        private final LruCache<String, Bitmap>
                                cache = new LruCache<>(20);
                        @Override
                        public Bitmap getBitmap(String url) {
                            return cache.get(url);
                        }

                        @Override
                        public void putBitmap(String url, Bitmap bitmap) {
                            cache.put(url, bitmap);
                        }
                    });
        }
        return mImageLoader;
    }


}
