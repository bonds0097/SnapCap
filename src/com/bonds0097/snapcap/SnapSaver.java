package com.bonds0097.snapcap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class SnapSaver {
    static final String TAG = "SnapCap-Saver";
    
    static void SaveSnap(Bitmap snapImage) {
        String fileName = System.currentTimeMillis() + ".jpg";
        
        String extRoot = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File path = new File(extRoot + "/snapchat");
        File snapFile = new File(path, fileName);
        

        path.mkdirs();
        
        FileOutputStream out;
        try {
            out = new FileOutputStream(snapFile);
            snapImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Error accessing file.\n" + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Error accessing file.\n" + e.toString());
        }     
        
        Log.d(TAG, "Snap successfully saved.");
    }
}
