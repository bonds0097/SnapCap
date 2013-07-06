package com.bonds0097.snapcap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.bonds0097.common.Logging;
import com.saurik.substrate.MS;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

public class Main {
    static final String TAG = "SnapCap";
    
    static void initialize() {
        Log.w(TAG, "Extension initialized.");
        
        // Load FeedActivity class.
        MS.hookClassLoad("com.snapchat.android.ui.SnapView", new MS.ClassLoadHook() {
            
            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public void classLoaded(Class<?> snapView) {
                Logging.ClassLoaded(TAG, snapView);
                
                // Hook into the ShowImage Method.
                Method showImage;
                
                try {
                    showImage = snapView.getDeclaredMethod("showImage");
                    Logging.MethodFound(TAG, snapView, showImage);
                } catch (NoSuchMethodException e) {
                    showImage = null;
                    Logging.MethodNotFound(TAG, snapView, "showImage");
                }
                
                if (showImage != null) {
                    final MS.MethodPointer old = new MS.MethodPointer();
                    final String methodName = showImage.getName();
                    
                    MS.hookMethod(snapView, showImage, new MS.MethodHook() {

                        @Override
                        public Object invoked(Object _this, Object... args)
                                throws Throwable {
                            Logging.HookingIntoMethodWithArgs(TAG, methodName, _this, args);
                            try {
                                Field mSnap = _this.getClass().getDeclaredField("mSnap");
                                Method getImageBitmap = mSnap.getType().getDeclaredMethod(
                                        "getImageBitmap", Context.class);
                                Method getContext = View.class.getDeclaredMethod(
                                        "getContext");
                                Bitmap snapImage = (Bitmap)getImageBitmap.invoke(
                                        mSnap.get(_this), getContext.invoke(_this));
                                Field mSender = mSnap.getType().getDeclaredField("mSender");
                                String sender = (String)mSender.get(mSnap.get(_this));
                                SnapSaver.SaveSnap(snapImage, sender);
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
