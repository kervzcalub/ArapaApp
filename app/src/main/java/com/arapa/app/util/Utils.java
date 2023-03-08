package com.arapa.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.arapa.app.MainActivity;
import com.arapa.app.ui.ImageAdapter;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class Utils {

    public static String jsonFile(Context context) {
        File file = new File(context.getFilesDir(), "schools.json");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
        }
        String result = text.toString();
        return result;
    }

    public static void writeToFile(Context context, String content){
        String filename = "schools.json";
        File path = new File(context.getFilesDir().getAbsolutePath());
        File newDir = new File(path,  filename);
        newDir.deleteOnExit();
        try{
            FileOutputStream writer = new FileOutputStream(newDir);
            writer.write(content.getBytes());
            writer.flush();
            writer.close();
            Log.d("TAG", "Wrote to file: "+ newDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] logo_byte_array = stream.toByteArray();

        return logo_byte_array;
    }
    public static boolean logo_exist(Context context, String id, String url) {
        boolean exist = false;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReferenceFromUrl(url);
        String filename = reference.getName();
        File logo = new File(context.getFilesDir().getAbsolutePath() + "/" + id + "/logo/" + filename);

        if (logo.exists()) {
            exist = true;
        }

        return exist;
    }

    public static void downloadLogo(Context context, String url, String id) throws Exception {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        Log.d("LOGO", url);
        StorageReference reference = storage.getReferenceFromUrl(url);
        File path = new File(context.getFilesDir().getAbsolutePath() + "/" + id + "/logo/");


        if (path.exists()) {
            File logo_file = new File(path.toString()+reference.getName());
            if (logo_file.exists()) {
                Log.d("LOGO", "LOGO EXIST");
            } else {
                save_logo(path, reference);
            }
        } else {
            path.mkdirs();
            save_logo(path, reference);
        }
    }

    private static void save_logo(File path, StorageReference reference){
        Log.d("LOGO", "Path: " + path);
        File localFile = new File(path, reference.getName());
        // Create the local file if it doesn't exist
        Log.d("LOGO", "localFile: " + localFile.getAbsolutePath());
        reference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d("LOGO SAVED", localFile.getAbsolutePath());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.e("LOGO ERROR", exception.getMessage());
                int errorCode = ((StorageException) exception).getErrorCode();

                Log.e("LOGO ERROR", "ERROR CODE" + errorCode);

            }
        });
    }

    public static Bitmap getSchoolLogo(Context context, School school) {
        File logoPath = new File(context.getFilesDir().getAbsolutePath() + "/" + school.getSchool_id() + "/logo/");
        String path = "";
        File[] listFiles = logoPath.listFiles();
        for (File file : listFiles) {
            path = file.getAbsolutePath();
        }
        Log.d("LOGO PATH", "LOGO PATH: " + path);
        return  BitmapFactory.decodeFile(path);
    }

    public static void downloadImages(Context context, School school, SchoolImages images, ImageAdapter imageAdapter) throws Exception{
        JSONArray filesArray = getFilesObj(context, school).getJSONArray("Files");
        for (int f = 0; f < filesArray.length(); f++) {
            String url = filesArray.getJSONObject(f).getString("url");
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference reference = storage.getReferenceFromUrl(url);
            // setting path for images
            // sorted by school id (document id)
            File path = new File(context.getFilesDir().getAbsolutePath() + "/" + school.getSchool_id() + "/images/");
            // setting image filenames
            // using the original filename uploaded by the school (user)
            File imgFile = new File(path, reference.getName());
            if (!imgFile.exists()) { // check if image exists together with it's folder
                // if image not exists check the folder/path
                if (!path.exists()) { // if path doesn't exists (i.e. school id)
                    path.mkdirs(); // create folder

                }

                File localFile = new File(path, reference.getName());
                reference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d("IMAGES SAVED", localFile.getAbsolutePath());
                        images.addImagePath(localFile.getAbsolutePath());
                        imageAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Log.e("IMAGES ERROR", exception.getMessage());
                    }
                });
            } else {
                Log.e("IMAGES", "IMAGE ALREADY EXIST. NO NEED TO DOWNLOAD");
                images.addImagePath(imgFile.getAbsolutePath());
            }
        }

    }


    public static void downloadImages(Context context, String id, String url) throws Exception{
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReferenceFromUrl(url);
        // setting path for images
        // sorted by school id (document id)
        File path = new File(context.getFilesDir().getAbsolutePath() + "/" + id + "/images/");
        // setting image filenames
        // using the original filename uploaded by the school (user)
        File imgFile = new File(path, reference.getName());
        if (!imgFile.exists()) { // check if image exists together with it's folder
            // if image not exists check the folder/path
            if (!path.exists()) { // if path doesn't exists (i.e. school id)
                path.mkdirs(); // create folder

            }

            File localFile = new File(path, reference.getName());
            reference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d("IMAGES SAVED", localFile.getAbsolutePath());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.e("IMAGES ERROR", exception.getMessage());
                }
            });
        }

    }

    private static JSONObject getFilesObj(Context context, School school) throws Exception {
        String school_id = school.getSchool_id();
        JSONArray schoolArray = new JSONArray(jsonFile(context));
        JSONObject schoolObj = null;
        for (int i = 0; i < schoolArray.length(); i++) {
            JSONObject object = schoolArray.getJSONObject(i);
            String id = object.getString("school_id");
            if (id.equals(school_id)) {
                schoolObj = object;
            }
        }
        return schoolObj;
    }



}
