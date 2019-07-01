package edu.illinois.cs.cs125.mp6;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Background tasks for use by our image recognition app.
 */
class Tasks {
    /** Default logging tag for messages from app tasks. */
    private static final String TAG = "MP6:Tasks";

    /** Default quality level for bitmap compression. */
    private static final int DEFAULT_COMPRESSION_QUALITY_LEVEL = 100;

    /**
     * Save a bitmap to external storage for later use.
     */
    static class DownloadFileTask extends AsyncTask<String, Integer, Integer> {

        /** Reference to the calling activity so that we can return results. */
        private WeakReference<MainActivity> activityReference;

        /** Request queue to use for our API call. */
        private RequestQueue requestQueue;

        /**
         * Create a new talk to upload data and return the API results.
         *
         * We pass in a reference to the app so that this task can be static.
         * Otherwise we get warnings about leaking the context.
         *
         * @param context calling activity context
         * @param setRequestQueue Volley request queue to use for the API request
         */
        DownloadFileTask(final MainActivity context, final RequestQueue setRequestQueue) {
            activityReference = new WeakReference<>(context);
            requestQueue = setRequestQueue;
        }

        /**
         * Before we start draw the waiting indicator.
         */
        @Override
        protected void onPreExecute() {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            ProgressBar progressBar = activity.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * Download a file, save it to the storage device if possible, and update the image.
         *
         * @param downloadURL the URL to download
         * @return return value is ignored but required to extend AsyncTask
         */
        @Override
        protected Integer doInBackground(final String... downloadURL) {
            final MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return 0;
            }
            final ImageView photoView = activity.findViewById(R.id.photoView);
            int targetWidth = photoView.getWidth();
            int targetHeight = photoView.getHeight();

            ImageRequest imageRequest = new ImageRequest(downloadURL[0],
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(final Bitmap response) {
                            /*
                             * If the download succeeded, try to draw the image on the screen.
                             */
                            activity.updateCurrentBitmap(response, true);
                            try {
                                /*
                                 * And also try to save the image.
                                 */
                                File outputFile = activity.getSaveFilename();
                                if (outputFile == null) {
                                    throw new Exception("null output file");
                                }
                                OutputStream outputStream = new FileOutputStream(outputFile);
                                response.compress(Bitmap.CompressFormat.JPEG,
                                        DEFAULT_COMPRESSION_QUALITY_LEVEL, outputStream);
                                outputStream.flush();
                                outputStream.close();
                                activity.addPhotoToGallery(Uri.fromFile(outputFile));
                            } catch (Exception e) {
                                Log.w(TAG, "Problem saving image: " + e);
                            }

                            // Clear the progress bar
                            ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }, targetWidth, targetHeight,
                    ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(final VolleyError e) {
                            // If the download failed alert the user and clear the progress bar
                            Toast.makeText(activity.getApplicationContext(),
                                    "Image download failed",
                                    Toast.LENGTH_LONG).show();
                            Log.w(TAG, "Image download failed: " + e);
                            ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
            requestQueue.add(imageRequest);
            return 0;
        }
    }

    /**
     * Compress an image in the background, then send it to Cognitive Services for identification.
     */
    static class ProcessImageTask extends AsyncTask<Bitmap, Integer, Integer> {

        /** Url for the MS cognitive services API. */
        private static final String MS_CV_API_URL =
                "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/analyze";

        /** Default visual features to request. You may need to change this value. */
        private static final String MS_CV_API_DEFAULT_VISUAL_FEATURES =
                "Categories,Description,Faces,ImageType,Color,Adult";

        /** Default visual features to request. */
        private static final String MS_CV_API_DEFAULT_LANGUAGE = "en";

        /** Default visual features to request. You may need to change this value. */
        private static final String MS_CV_API_DEFAULT_DETAILS = "Landmarks";

        /** Subscription key. */
        private static final String SUBSCRIPTION_KEY = BuildConfig.API_KEY;

        /** Reference to the calling activity so that we can return results. */
        private WeakReference<MainActivity> activityReference;

        /** Request queue to use for our API call. */
        private RequestQueue requestQueue;

        /**
         * Create a new talk to upload data and return the API results.
         *
         * We pass in a reference to the app so that this task can be static.
         * Otherwise we get warnings about leaking the context.
         *
         * @param context calling activity context
         * @param setRequestQueue Volley request queue to use for the API request
         */
        ProcessImageTask(final MainActivity context, final RequestQueue setRequestQueue) {
            activityReference = new WeakReference<>(context);
            requestQueue = setRequestQueue;
        }

        /**
         * Before we start draw the waiting indicator.
         */
        @Override
        protected void onPreExecute() {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            ProgressBar progressBar = activity.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * Convert an image to a byte array, upload to the Microsoft Cognitive Services API,
         * and return a result.
         *
         * @param currentBitmap the bitmap to process
         * @return unused unused result
         */
        protected Integer doInBackground(final Bitmap... currentBitmap) {
            /*
             * Convert the image from a Bitmap to a byte array for upload.
             */
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            currentBitmap[0].compress(Bitmap.CompressFormat.PNG,
                    DEFAULT_COMPRESSION_QUALITY_LEVEL, stream);

            // Prepare our API request
            String requestURL = Uri.parse(MS_CV_API_URL)
                    .buildUpon()
                    .appendQueryParameter("visualFeatures", MS_CV_API_DEFAULT_VISUAL_FEATURES)
                    .appendQueryParameter("details", MS_CV_API_DEFAULT_DETAILS)
                    .appendQueryParameter("language", MS_CV_API_DEFAULT_LANGUAGE)
                    .build()
                    .toString();
            Log.d(TAG, "Using URL: " + requestURL);

            /*
             * Make the API request.
             */
            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST, requestURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(final String response) {
                            // On success, clear the progress bar and call finishProcessImage
                            Log.d(TAG, "Response: " + response);
                            MainActivity activity = activityReference.get();
                            if (activity == null || activity.isFinishing()) {
                                return;
                            }
                            ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.INVISIBLE);
                            activity.finishProcessImage(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    // On failure just clear the progress bar
                    Log.w(TAG, "Error: " + error.toString());
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null &&
                            networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        Log.w(TAG, "Unauthorized request. "
                                + "Make sure you added your API_KEY to app/secrets.properties");
                    }
                    MainActivity activity = activityReference.get();
                    if (activity == null || activity.isFinishing()) {
                        return;
                    }
                    ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    // Set up headers properly
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/octet-stream");
                    headers.put("Ocp-Apim-Subscription-Key", SUBSCRIPTION_KEY);
                    return headers;
                }
                @Override
                public String getBodyContentType() {
                    // Set the body content type properly for a binary upload
                    return "application/octet-stream";
                }
                @Override
                public byte[] getBody() {
                    return stream.toByteArray();
                }
            };
            requestQueue.add(stringRequest);

            /* doInBackground can't return void, otherwise we would. */
            return 0;
        }
    }
}
