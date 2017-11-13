package com.msecnyz.tavernjune.legionsupport;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesHelper {
	
	public static String STORE_NAME = "Settings";

	public static void saveString(Context context, String key, String value){
		SharedPreferences settings = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putString(key, value);
		
		editor.commit();
	}
	
	public static String getString(Context context, String key, String defValue){
		SharedPreferences settings = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
		
		return settings.getString(key, defValue);
	}
	
	public static void saveBoolean(Context context, String key, boolean value){
		SharedPreferences settings = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putBoolean(key, value);
		
		editor.commit();
	}
	
	public static boolean getBoolean(Context context, String key, boolean defValue){
		SharedPreferences settings = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
		
		return settings.getBoolean(key, defValue);
	}
	
	public static void saveInt(Context context, String key, int value){
		SharedPreferences settings = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putInt(key, value);
		
		editor.commit();
	}
	
	public static int getInt(Context context, String key, int defValue){
		SharedPreferences settings = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
		
		return settings.getInt(key, defValue);
	}
	
	public static void saveLong(Context context, String key, long value){
		SharedPreferences settings = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putLong(key, value);
		
		editor.commit();
	}
	
	public static long getLong(Context context, String key, long defValue){
		SharedPreferences settings = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
		
		return settings.getLong(key, defValue);
	}
	
	public static void saveList(Context context, String key, List<String> list){
		if (list == null){
			saveString(context, key, "");
		}
		
		StringBuilder csvList = new StringBuilder();
		for(String s : list){
		      csvList.append(s);
		      csvList.append(",");
		}
		
		saveString(context, key, csvList.toString());
	}
	
	public static List<String> getList(Context context, String key){
		String csvList = getString(context, key, "");
		
		if (csvList == null || csvList.length() == 0){
			return null;
		}
		
		String[] items = csvList.split(",");
		
		List<String> list = new ArrayList<String>();
		for(int i=0; i < items.length; i++){
		     list.add(items[i]);     
		}
		return list;
	}
	
	public static void clearList(Context context, String key){
		saveString(context, key, "");
	}
	
	public static void remove(Context context, String key){
		SharedPreferences settings = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.remove(key);
		
		editor.commit();
	}
}
