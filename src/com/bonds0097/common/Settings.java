package com.bonds0097.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

public class Settings {
	private static final String TAG = "bonds0097-Settings";
	
	public static JSONObject getSettings(String fileName) {
		JSONObject settings;		
		File settingsFile = new File(fileName);
		
		if (settingsFile.isFile()) {
			// If file exists, then open it.
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(settingsFile));
				String line = null;
				StringBuilder json = new StringBuilder();
				
				while ((line = reader.readLine()) != null) {
					json.append(line);
				}
				reader.close();
				Log.d(TAG, "Read settings file.");
				
				settings = new JSONObject(json.toString());
			} catch (FileNotFoundException e) {
				settings = new JSONObject();
				Log.w(TAG, "Settings file not found. New one will be created on exit.\n" + e.toString());
			} catch (IOException e) {
				settings = new JSONObject();
				Log.e(TAG, "Error reading settings file.\n" + e.toString());
			} catch (JSONException e) {
				settings = new JSONObject();
				Log.e(TAG, "Settings file could not be read: Invalid JSON.\n" + e.toString());
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						Log.e(TAG, "Could not close Input Stream." + e.toString());
					}
				}				
			}
		} else {
			// Otherwise, just report that file does not exist.
			settings = new JSONObject();
			Log.w(TAG, "Settings file not found. New one will be created on exit.");
		}		
		
		return settings;
	}
	
	public static void SaveSettings(JSONObject settings, String fileName) {
		File settingsFile = new File(fileName);
		
		try {
			PrintWriter writer = new PrintWriter(settingsFile);
			writer.println(settings.toString(4));
			writer.close();
			
			settingsFile.setReadable(true);
			
			if (settingsFile.isFile()) {
				Log.d(TAG, "Settings saved.");
			} else {
				Log.d(TAG, "Settings not saved.");
			}			
		}  catch (JSONException e) {
			Log.e(TAG, "Error converting JSON to string. Settings not saved.\n" + e.toString());
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Error saving settings. File could not be found.\n" + e.toString());
		}     		
	}
	
	public static List<String> GetAppProcesses(Context context) {
		final List<ApplicationInfo> appList = 
				context.getPackageManager().getInstalledApplications(0);
		
		List<String> processes = new ArrayList<String>();
		for (ApplicationInfo info : appList) {
			processes.add(info.processName);
		}
		
		Collections.sort(processes);
		
		return processes;
	}
}
