package com.example.b07demosummer2024;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// Handles uploading artifact images to Supabase Storage.
public class SupabaseImageUploader {

    // Returns either the uploaded URL or an error message.
    public interface UploadCallback {
        void onSuccess(String publicUrl);
        void onError(String message);
    }

    // Maximum allowed image size is 10 MB.
    private static final int MAX_IMAGE_BYTES = 10 * 1024 * 1024; // 10MB

    private final Context appContext;
    private final OkHttpClient client = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final String supabaseUrl;
    private final String supabaseAnonKey;
    private final String bucketName;

    // Load Supabase settings from the application's resources.
    public SupabaseImageUploader(Context context) {
        appContext = context.getApplicationContext();

        supabaseUrl = appContext.getString(R.string.supabase_url).trim();
        supabaseAnonKey = appContext.getString(R.string.supabase_anon_key).trim();
        bucketName = appContext.getString(R.string.supabase_image_bucket).trim();
    }

    // Validate and upload the selected image.
    public void uploadImage(Uri imageUri, String lotNumber, UploadCallback callback) {

        // Make sure the Supabase project URL, anon key, and bucket name were provided.
        if (isBlank(supabaseUrl) || isBlank(supabaseAnonKey) || isBlank(bucketName)) {
            callback.onError("Image uploader not configured with URL, anon key, and bucket name");
            return;
        }

        // Get the selected file's MIME type, such as image/jpeg or image/png.
        String mimeType = appContext.getContentResolver().getType(imageUri);

        if (isBlank(mimeType)) {
            mimeType = "";
        }

        // Only allow image files.
        if (!mimeType.startsWith("image/")) {
            callback.onError("Please select an image file.");
            return;
        }

        // Get the actual extension to append to the URL, such as jpg or png.
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);

        if (isBlank(extension)) {
            callback.onError("Unsupported image type.");
            return;
        }

        // Read image Uri into bytes for the upload request body.
        byte[] imageBytes;

        try {
            imageBytes = readBytes(imageUri);
        } catch (IOException e) {
            callback.onError(e.getMessage() == null ? "Could not read selected image." : e.getMessage());
            return;
        }

        // Image file path, like artifacts/LOT123/1719000000000.jpg.
        String filePath = buildFilePath(lotNumber, extension);

        // Private Storage API URL used for uploading the file.
        HttpUrl uploadUrl = buildStorageUrl("storage/v1/object", filePath);

        if (uploadUrl == null) {
            callback.onError("Supabase URL is invalid.");
            return;
        }

        // Create the HTTP upload request.
        RequestBody requestBody = RequestBody.create(imageBytes, MediaType.parse(mimeType));

        Request request = new Request.Builder()
                .url(uploadUrl)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer " + supabaseAnonKey)
                .post(requestBody)
                .build();

        // Send the upload request asynchronously.
        client.newCall(request).enqueue(new Callback() {

            // Handle network failures.
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                postError(callback, "Image upload failed: " + e.getMessage());
            }

            // Handle the response returned by Supabase.
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {

                    if (response.isSuccessful()) {

                        // Public URL that can be stored in Firebase and loaded with Glide.
                        HttpUrl publicUrl = buildStorageUrl("storage/v1/object/public", filePath);

                        if (publicUrl == null) {
                            postError(callback, "Could not build image URL.");
                        } else {
                            postSuccess(callback, publicUrl.toString());
                        }

                    } else {
                        postError(callback, response.code() + ".");
                    }

                } finally {
                    response.close();
                }
            }
        });
    }

    // Read the selected image into a byte array.
    private byte[] readBytes(Uri imageUri) throws IOException {

        ContentResolver resolver = appContext.getContentResolver();

        try (InputStream inputStream = resolver.openInputStream(imageUri);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            if (inputStream == null) {
                throw new IOException("No image stream.");
            }

            byte[] buffer = new byte[8192];
            int bytesRead;

            // Read the image until all bytes have been copied.
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);

                // Stop if the image becomes larger than the allowed size.
                if (outputStream.size() > MAX_IMAGE_BYTES) {
                    throw new IOException("Image is larger than 10 MB.");
                }
            }

            return outputStream.toByteArray();
        }
    }

    // Creates a safe and unique file path for each uploaded image.
    private String buildFilePath(String lotNumber, String extension) {

        // Replace characters that should not be used in the path.
        String safeLotNumber = lotNumber.replaceAll("[^A-Za-z0-9_-]", "_");

        return "artifacts/" + safeLotNumber + "/" + System.currentTimeMillis() + "." + extension;
    }

    // Builds either the upload URL or public image URL.
    private HttpUrl buildStorageUrl(String storagePath, String filePath) {

        HttpUrl baseUrl = HttpUrl.parse(supabaseUrl);

        if (baseUrl == null) {
            return null;
        }

        return baseUrl.newBuilder()
                .addPathSegments(storagePath)
                .addPathSegment(bucketName)
                .addPathSegments(filePath)
                .build();
    }

    // Return successful upload results on the main UI thread.
    private void postSuccess(UploadCallback callback, String publicUrl) {
        mainHandler.post(() -> callback.onSuccess(publicUrl));
    }

    // Return upload errors on the main UI thread.
    private void postError(UploadCallback callback, String message) {
        mainHandler.post(() -> callback.onError(message));
    }

    // Simple check for empty configuration values.
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}