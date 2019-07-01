package edu.illinois.cs.cs125.mp6;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.illinois.cs.cs125.mp6.lib.RecognizePhoto;

/**
 * Main screen for our image recognition app.
 */
public final class MainActivity extends AppCompatActivity {
    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "MP6:Main";

    /** Constant to perform a read file request. */
    private static final int READ_REQUEST_CODE = 42;

    /** Constant to request an image capture. */
    private static final int IMAGE_CAPTURE_REQUEST_CODE = 1;

    /** Constant to request permission to write to the external storage device. */
    private static final int REQUEST_WRITE_STORAGE = 112;

    /** Request queue for our network requests. */
    private static RequestQueue requestQueue;

    /** Whether we can write to public storage. */
    private boolean canWriteToPublicStorage = false;

    /**
     * Run when our activity comes into view.
     *
     * @param savedInstanceState state that was saved by the activity last time it was paused
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        requestQueue = Volley.newRequestQueue(this);

        super.onCreate(savedInstanceState);

        // Load the main layout for our activity
        setContentView(R.layout.activity_main);

        /*
         * Set up handlers for each button in our UI. These run when the buttons are clicked.
         */
        final ImageButton openFile = findViewById(R.id.openFile);
        openFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Open file button clicked");
                startOpenFile();
            }
        });
        final ImageButton takePhoto = findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Take photo button clicked");
                startTakePhoto();
            }
        });
        final ImageButton downloadFile = findViewById(R.id.downloadFile);
        downloadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Download file button click");
                startDownloadFile();
            }
        });
        final ImageButton rotateLeft = findViewById(R.id.rotateLeft);
        rotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Rotate left button clicked");
                rotateLeft();
            }
        });
        final ImageButton processImage = findViewById(R.id.processImage);
        processImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "Process image button clicked");
                startProcessImage();
            }
        });

        // There are a few button that we disable into an image has been loaded
        enableOrDisableButtons(false);

        // We also want to make sure that our progress bar isn't spinning, and style it a bit
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(getResources()
                        .getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);

        /*
         * Here we check for permission to write to external storage and request it if necessary.
         * Normally you would not want to do this on ever start, but we want to be persistent
         * since it makes development a lot easier.
         */
        canWriteToPublicStorage = (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        Log.d(TAG, "Do we have permission to write to external storage: "
                + canWriteToPublicStorage);
        if (!canWriteToPublicStorage) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
    }

    /**
     * Called when an intent that we requested has finished.
     *
     * In our case, we either asked the file browser to open a file, or the camera to take a
     * photo. We respond appropriately below.
     *
     * @param requestCode the code that we used to make the request
     * @param resultCode a code indicating what happened: success or failure
     * @param resultData any data returned by the activity
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent resultData) {

        // If something went wrong we simply log a warning and return
        if (resultCode != Activity.RESULT_OK) {
            Log.w(TAG, "onActivityResult with code " + requestCode + " failed");
            if (requestCode == IMAGE_CAPTURE_REQUEST_CODE) {
                photoRequestActive = false;
            }
            return;
        }

        // Otherwise we get a link to the photo either from the file browser or the camera,
        Uri currentPhotoURI;
        if (requestCode == READ_REQUEST_CODE) {
            currentPhotoURI = resultData.getData();
        } else if (requestCode == IMAGE_CAPTURE_REQUEST_CODE) {
            currentPhotoURI = Uri.fromFile(currentPhotoFile);
            photoRequestActive = false;
            if (canWriteToPublicStorage) {
                addPhotoToGallery(currentPhotoURI);
            }
        } else {
            Log.w(TAG, "Unhandled activityResult with code " + requestCode);
            return;
        }

        // Now load the photo into the view
        Log.d(TAG, "Photo selection produced URI " + currentPhotoURI);
        loadPhoto(currentPhotoURI);
    }


    /**
     * Start an open file dialog to look for image files.
     */
    private void startOpenFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    /** Current file that we are using for our image request. */
    private boolean photoRequestActive = false;

    /** Whether a current photo request is being processed. */
    private File currentPhotoFile = null;

    /** Take a photo using the camera. */
    private void startTakePhoto() {
        if (photoRequestActive) {
            Log.w(TAG, "Overlapping photo requests");
            return;
        }

        // Set up an intent to launch the camera app and have it take a photo for us
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        currentPhotoFile = getSaveFilename();
        if (takePictureIntent.resolveActivity(getPackageManager()) == null
                || currentPhotoFile == null) {
            // Alert the user if there was a problem taking the photo
            Toast.makeText(getApplicationContext(), "Problem taking photo",
                    Toast.LENGTH_LONG).show();
            Log.w(TAG, "Problem taking photo");
            return;
        }

        // Configure and launch the intent
        Uri photoURI = FileProvider.getUriForFile(this,
                "edu.illinois.cs.cs125.mp6.fileprovider", currentPhotoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        photoRequestActive = true;
        startActivityForResult(takePictureIntent, IMAGE_CAPTURE_REQUEST_CODE);
    }

    /** URL storing the file to download. */
    private String downloadFileURL;

    /** Initiate the file download process. */
    private void startDownloadFile() {

        // Build a dialog that we will use to ask for the URL to the photo

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Download File");
        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int unused) {

                // If the user clicks OK, try and download the file
                downloadFileURL = input.getText().toString().trim();
                Log.d(TAG, "Got download URL " + downloadFileURL);
                new Tasks.DownloadFileTask(MainActivity.this, requestQueue)
                        .execute(downloadFileURL);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int unused) {
                dialog.cancel();
            }
        });

        // Display the dialog
        builder.show();
    }

    /** Degrees for a 90 degree left rotation. */
    private static final int ROTATE_LEFT = -90;

    /**
     * Rotate the image to the left.
     *
     * Mainly to deal with idiocy caused by the emulated camera.
     */
    private void rotateLeft() {
        if (currentBitmap == null) {
            Toast.makeText(getApplicationContext(), "No image selected",
                    Toast.LENGTH_LONG).show();
            Log.w(TAG, "No image selected");
            return;
        }

        Log.d(TAG, "Starting rotation");

        Matrix matrix = new Matrix();
        matrix.postRotate(ROTATE_LEFT);
        updateCurrentBitmap(Bitmap.createBitmap(currentBitmap,
                0, 0, currentBitmap.getWidth(), currentBitmap.getHeight(), matrix, true), false);
    }

    /** Initiate the image recognition process. */
    private void startProcessImage() {
        if (currentBitmap == null) {
            Toast.makeText(getApplicationContext(), "No image selected",
                    Toast.LENGTH_LONG).show();
            Log.w(TAG, "No image selected");
            return;
        }

        /*
         * Launch our background task which actually makes the request. It will call
         * finishProcessImage below with the JSON string when it finishes.
         */
        new Tasks.ProcessImageTask(MainActivity.this, requestQueue)
                .execute(currentBitmap);
    }

    /**
     * Process the result from making the API call.
     *
     * @param jsonResult the result of the API call as a string
     * */
    protected void finishProcessImage(final String jsonResult) {
        /*
         * Pretty-print the JSON into the bottom text-view to help with debugging.
         */
        TextView textView = findViewById(R.id.jsonResult);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(jsonResult);
        String prettyJsonString = gson.toJson(jsonElement);
        textView.setText(prettyJsonString);

        /*
         * Create a string describing the image type, width and height.
         */
        int width = RecognizePhoto.getWidth(jsonResult);
        int height = RecognizePhoto.getHeight(jsonResult);
        String format = RecognizePhoto.getFormat(jsonResult);
        format = format.toUpperCase();
        String description = String.format(Locale.US, "%s (%d x %d)", format, width, height);

        /*
         * Update the UI to display the string.
         */
        TextView photoInfo = findViewById(R.id.photoInfo);
        photoInfo.setText(description);

        /*
         * Add code here to show the caption, show or hide the dog and cat icons,
         * and deal with Rick.
         */
    }

    /** Current bitmap we are working with. */
    private Bitmap currentBitmap;

    /**
     * Process a photo.
     *
     * Resizes an image and loads it into the UI.
     *
     * @param currentPhotoURI URI of the image to process
     */
    private void loadPhoto(final Uri currentPhotoURI) {
        enableOrDisableButtons(false);
        final ImageButton rotateLeft = findViewById(R.id.rotateLeft);
        rotateLeft.setClickable(false);
        rotateLeft.setEnabled(false);

        if (currentPhotoURI == null) {
            Toast.makeText(getApplicationContext(), "No image selected",
                    Toast.LENGTH_LONG).show();
            Log.w(TAG, "No image selected");
            return;
        }
        String uriScheme = currentPhotoURI.getScheme();

        byte[] imageData;
        try {
            switch (uriScheme) {
                case "file":
                    imageData = FileUtils.readFileToByteArray(new File(currentPhotoURI.getPath()));
                    break;
                case "content":
                    InputStream inputStream = getContentResolver().openInputStream(currentPhotoURI);
                    assert inputStream != null;
                    imageData = IOUtils.toByteArray(inputStream);
                    inputStream.close();
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Unknown scheme " + uriScheme,
                            Toast.LENGTH_LONG).show();
                    return;
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error processing file",
                    Toast.LENGTH_LONG).show();
            Log.w(TAG, "Error processing file: " + e);
            return;
        }

        /*
         * Resize the image appropriately for the display.
         */
        final ImageView photoView = findViewById(R.id.photoView);
        int targetWidth = photoView.getWidth();
        int targetHeight = photoView.getHeight();

        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(imageData, 0, imageData.length, decodeOptions);

        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;
        int scaleFactor = Math.min(actualWidth / targetWidth, actualHeight / targetHeight);

        BitmapFactory.Options modifyOptions = new BitmapFactory.Options();
        modifyOptions.inJustDecodeBounds = false;
        modifyOptions.inSampleSize = scaleFactor;
        modifyOptions.inPurgeable = true;

        // Actually draw the image
        updateCurrentBitmap(BitmapFactory.decodeByteArray(imageData,
                0, imageData.length, modifyOptions), true);
    }

    /*
     * Helper functions follow.
     */

    /**
     * Update the currently displayed image.
     *
     * @param setCurrentBitmap the new bitmap to display
     * @param resetInfo whether to reset the image information
     */
    void updateCurrentBitmap(final Bitmap setCurrentBitmap, final boolean resetInfo) {
        currentBitmap = setCurrentBitmap;
        ImageView photoView = findViewById(R.id.photoView);
        photoView.setImageBitmap(currentBitmap);
        enableOrDisableButtons(true);

        if (resetInfo) {
            TextView textView = findViewById(R.id.jsonResult);
            textView.setText("");
            TextView photoCaption = findViewById(R.id.photoCaption);
            photoCaption.setVisibility(View.INVISIBLE);
            ImageView isADog = findViewById(R.id.isADog);
            isADog.setVisibility(View.INVISIBLE);
            ImageView isACat = findViewById(R.id.isACat);
            isACat.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Helper function to swap button states.
     *
     * We disable the buttons when we don't have a valid image to process.
     *
     * @param enableOrDisable whether to enable or disable the buttons
     */
    private void enableOrDisableButtons(final boolean enableOrDisable) {
        final ImageButton rotateLeft = findViewById(R.id.rotateLeft);
        rotateLeft.setClickable(enableOrDisable);
        rotateLeft.setEnabled(enableOrDisable);
        final ImageButton processImage = findViewById(R.id.processImage);
        processImage.setClickable(enableOrDisable);
        processImage.setEnabled(enableOrDisable);
    }

    /**
     * Add a photo to the gallery so that we can use it later.
     *
     * @param toAdd URI of the file to add
     */
    void addPhotoToGallery(final Uri toAdd) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(toAdd);
        this.sendBroadcast(mediaScanIntent);
        Log.d(TAG, "Added photo to gallery: " + toAdd);
    }

    /**
     * Get a new file location for saving.
     *
     * @return the path to the new file or null of the create failed
     */
    File getSaveFilename() {
        String imageFileName = "MP6_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        File storageDir;
        if (canWriteToPublicStorage) {
            storageDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        } else {
            storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        try {
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            Log.w(TAG, "Problem saving file: " + e);
            return null;
        }
    }
}
