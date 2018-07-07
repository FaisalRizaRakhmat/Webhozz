package wartaonline.chat.chatapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Asus on 8/31/2017.
 */

    public class VolleySingleton {
        private static VolleySingleton mInstance;
        private RequestQueue mRequestQueue;
        private ImageLoader mImageLoader;
        private static Context mCtx;

        /**
         * Private constructor, only initialization from getInstance.
         *
         * @param context parent context
         */
        private VolleySingleton(Context context) {
            mCtx = context;
            VolleyLog.DEBUG = false;
            mRequestQueue = getRequestQueue();

            mImageLoader = new ImageLoader(mRequestQueue,
                    new ImageLoader.ImageCache() {
                        private final LruCache<String, Bitmap> cache = new LruBitmapCache(mCtx);

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

        /**
         * Singleton construct design pattern.
         *
         * @param context parent context
         * @return single instance of VolleySingleton
         */
        public static synchronized VolleySingleton getInstance(Context context) {
            if (mInstance == null) {
                mInstance = new VolleySingleton(context);
            }
            return mInstance;
        }

        /**
         * Get current request queue.
         *
         * @return RequestQueue
         */
        public RequestQueue getRequestQueue() {
            if (mRequestQueue == null) {
                // getApplicationContext() is key, it keeps you from leaking the
                // Activity or BroadcastReceiver if someone passes one in.
                mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());

            }
            return mRequestQueue;
        }

        /**
         * Add new request depend on type like string, json object, json array request.
         *
         * @param req new request
         * @param <T> request type
         */
        public <T> void addToRequestQueue(Request<T> req) {

            int socketTimeout = 30000;//30 seconds
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

            req.setRetryPolicy(policy);
            getRequestQueue().add(req);
        }

        /**
         * Get image loader.
         *
         * @return ImageLoader
         */
        public ImageLoader getImageLoader() {
            return mImageLoader;
        }
    }

