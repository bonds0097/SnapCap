package com.bonds0097.snapcap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.bonds0097.common.Logging;
import com.saurik.substrate.MS;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class Main {
    static final String TAG = "SnapCap";
    
    static void initialize() {
        Log.w(TAG, "Extension initialized.");
        
        // Load FeedActivity class.
        MS.hookClassLoad("com.snapchat.android.FeedActivity", new MS.ClassLoadHook() {
            
            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public void classLoaded(Class<?> feed) {
                Logging.ClassLoaded(TAG, feed);
                
                // Hook into the ShowImage Method.
                Method showImage;
                
                try {
                    showImage = feed.getDeclaredMethod("showImage");
                    Logging.MethodFound(TAG, feed, showImage);
                } catch (NoSuchMethodException e) {
                    showImage = null;
                    Logging.MethodNotFound(TAG, feed, "showImage");
                }
                
                if (showImage != null) {
                    final MS.MethodPointer old = new MS.MethodPointer();
                    final String methodName = showImage.getName();
                    
                    MS.hookMethod(feed, showImage, new MS.MethodHook() {

                        @Override
                        public Object invoked(Object _this, Object... args)
                                throws Throwable {
                            Logging.HookingIntoMethodWithArgs(TAG, methodName, _this, args);
                            try {
                                Field mClickedSnap = _this.getClass().getDeclaredField("mClickedSnap");
                                Method getImageBitmap = mClickedSnap.getType().getDeclaredMethod(
                                        "getImageBitmap", Context.class);
                                Method getApplicationContext = Context.class.getDeclaredMethod(
                                        "getApplicationContext");
                                Bitmap snapImage = (Bitmap)getImageBitmap.invoke(
                                        mClickedSnap.get(_this), getApplicationContext.invoke(_this));
                                SnapSaver.SaveSnap(snapImage);
                            } catch (NoSuchFieldException e) {
                                Log.e(TAG, "Could not find field.\n" + e.toString());
                            } catch (NoSuchMethodException e) {
                                Log.e(TAG, "Could not find method.\n" + e.toString());
                            }
                            return old.invoke(_this, args);
                        }
                    }, old);
                }
            }
        });
    }
}
