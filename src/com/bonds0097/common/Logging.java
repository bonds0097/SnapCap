package com.bonds0097.common;

import java.lang.reflect.Method;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;

import com.saurik.substrate.MS.MethodPointer;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Logging {
	public static void ClassLoaded(String TAG, Class<?> _class) {
		Log.i(TAG, "Class Loaded: " + _class.getName());
	}
	
	public static void MethodNotFound(String TAG, Class<?> _class, String methodName) {
		Log.e(TAG, "Method '" + methodName + "' not found for class: " + _class.getName());
	}
	
	public static void MethodFound(String TAG, Class<?> _class, Method method) {
		Log.v(TAG, "Method '" + method.getName() + "' found for class: " + _class.getName());
	}
	
	public static void HookingIntoMethod(String TAG, String methodName, Object _this) {
		Log.d(TAG, GetMethodHeader(methodName, _this));
	}
	
	public static void HookingIntoMethodWithArgs(String TAG, String methodName, Object _this, Object[] args) {
		StringBuilder message = new StringBuilder();
		
		message.append(GetMethodHeader(methodName, _this));
		message.append(GetMethodArguments(args));
		
		Log.d(TAG, message.toString());
	}
	
	public static void LogClassDeclaredMethods(String TAG, Class<?> _class) {
		for (Method method : _class.getDeclaredMethods()) {
			StringBuilder output = new StringBuilder();
			output.append("Method: ").append(method.getName());
			output.append(" - ");
			for (Class<?> arg : method.getParameterTypes()) {
				output.append(arg.getName()).append(" ");
			}
			output.append("Returns: ").append(method.getReturnType().getName());
			Log.w(TAG, output.toString());
		}
	}
	
	public static void LogClassMethods(String TAG, Class<?> _class) {
		for (Method method : _class.getMethods()) {
			StringBuilder output = new StringBuilder();
			output.append("Method: ").append(method.getName());
			output.append(" - ");
			for (Class<?> arg : method.getParameterTypes()) {
				output.append(arg.getName()).append(" ");
			}
			output.append("Returns: ").append(method.getReturnType().getName());
			Log.w(TAG, output.toString());
		}
	}
	
	public static String GetMethodHeader(String methodName, Object _this) {
		StringBuilder message = new StringBuilder();
		
		message.append("*** Hooking into Method ***\n");
		message.append("Method Name: " + methodName + "\n");
		
		if (_this != null) {
			message.append("Method Caller: " + _this.toString() + "\n");
		}	
		
		return message.toString();
	}
	
	public static String GetMethodArguments(Object[] args) {
		StringBuilder message = new StringBuilder();
		
		// Get the method signature and list argument types.
		if (args.length > 0) {
			message.append("Argument Types: ");		
			for (Object argument : args) {
				if (argument != null) {
					message.append(argument.getClass().getName()).append(", ");
				} else {
					message.append("null, ");
				}				
			}
			message.delete(message.length() - 2, message.length());
			message.append("\n");
		}		
		
		// Print out each argument in human-readable form.
		for (int i = 0; i < args.length; i++) {			
			
			message.append("Argument " + i + ": ");
			
			if (args[i] instanceof Object[]) {
				Object[] argument = (Object[])args[i];				
				for (Object object : argument) {
					message.append(object + ", ");
				}
				
				message.delete(message.length() - 2, message.length());
			} else if (args[i] instanceof java.security.Key) {
				Key key = (Key)args[i];
				message.append("Instance of ").append(key.getClass().getName()).append(" - ");
				message.append(key.getAlgorithm()).append(" (Algorithm), ");
				message.append(key.getFormat()).append(" (Format), ");
				message.append(ByteToHex(key.getEncoded())).append(" (Encoded)");
			} else if (args[i] instanceof java.security.SecureRandom) {
				SecureRandom random = (SecureRandom)args[i];
				message.append("Instance of ").append(random.getClass().getName()).append(" - ");
				message.append(random.getAlgorithm()).append(" (Algorithm), ");
				message.append(random.getProvider()).append(" (Provider)");
			} else if (args[i] instanceof byte[]) {				
				byte[] byteData = (byte[])args[i];
				
				message.append(ByteToHex(byteData));				
			} else if (args[i] instanceof org.apache.http.client.methods.HttpPost) {
				HttpPost post = (HttpPost)args[i];
				message.append("Instance of ").append(post.getClass().getName()).append(" - ");
				message.append(post.getMethod()).append(" (Method)");
			} else if (args[i] instanceof org.apache.http.protocol.BasicHttpContext) {
				BasicHttpContext context = (BasicHttpContext)args[i];
				message.append("Instance of ").append(context.getClass().getName()).append(" - ");
				message.append(context.getAttribute(ExecutionContext.HTTP_REQUEST)).append(" (Request)");
			} else {			
				message.append(args[i]);
			}
			message.append("\n");
		}
		
		return message.toString();
	}

	public static String GetCipherData(Object _this) {
		Cipher cipher = (Cipher)_this;
		
		StringBuilder message = new StringBuilder();
		
		message.append("Cipher Algorithm: " + cipher.getAlgorithm() + "\n");
		message.append("Cipher Provider: " + cipher.getProvider() + "\n");
		message.append("Cipher Block Size: " + cipher.getBlockSize() + "\n");
		
		return message.toString();
	}
	
	public static String GetSQLData(Object _this) {
		SQLiteDatabase db = (SQLiteDatabase)_this; 
		
		StringBuilder message = new StringBuilder();
		
		message.append("DB Path: " + db.getPath() + "\n");
		
		return message.toString();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void HookingIntoCipher(String TAG, String methodName,
			Object _this, Object[] args, MethodPointer old) {
		StringBuilder message = new StringBuilder();
		
		message.append(GetMethodHeader(methodName, _this));
		message.append(GetCipherData(_this));
		
		try {
			int opmode = Integer.valueOf((Integer)args[0]);
			String mode;
			
			switch (opmode) {
			case 1:
				mode = "Encrypt";
				break;
			case 2:
				mode = "Decrypt";
				break;
			default:
				mode = "Unknown";
				break;					
			}
			
			message.append("Opmode: ").append(mode).append("\n");
		} catch (Exception e) {			
		}
		
		message.append(GetMethodArguments(args));			

		if (!methodName.equals("init")) {
			try {
				message.append("Returned: ");
				Object returned = old.invoke(_this, args);
				if (returned instanceof byte[]) {
					byte[] byteData = (byte[])returned;				
					message.append(ByteToHex(byteData)).append(" (Hex), ");
					message.append(new String(byteData, "UTF8")).append(" (UTF8?)");
				} else {
					message.append(returned.toString());
				}
				message.append("\n");
			} catch (Throwable e) {
				Log.e(TAG, "Method " + methodName + " threw exception: " + e.toString());
			}
		}
		
		Log.d(TAG, message.toString());
	}
	
	public static String ByteToHex(byte[] bytes) {
		StringBuilder hex = new StringBuilder();
		
		for (byte b : bytes) {
			hex.append(String.format("%02X ", b));
		}
		
		return hex.toString();
	}

	public static void HookingIntoSQL(String TAG, String methodName,
			Object _this, Object[] args) {
		StringBuilder message = new StringBuilder();
		
		message.append(GetMethodHeader(methodName, _this));
		message.append(GetSQLData(_this));
		message.append(GetMethodArguments(args));	
		
		Log.d(TAG, message.toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void MethodReturn(String TAG, MethodPointer old,
			Object _this, Object[] args) throws Throwable {
		StringBuilder message = new StringBuilder();
		
		message.append("Method Return: ");
		message.append(old.invoke(_this, args).toString());
		
		Log.d(TAG, message.toString());		
	}
}
