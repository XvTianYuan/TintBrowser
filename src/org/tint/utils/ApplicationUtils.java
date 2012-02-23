/*
 * Tint Browser for Android
 * 
 * Copyright (C) 2012 - to infinity and beyond J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.tint.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.tint.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ClipboardManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

public class ApplicationUtils {
	
	private static int sFaviconSize = -1;
	private static int sApplicationButtonSize = -1;
	
	private static int[] sBookmarksdimensions = null;
	
	public static boolean isTablet(Context context) {
		return context.getResources().getBoolean(R.bool.isTablet);
	}
	
	/**
	 * Get the application version code.
	 * @param context The current context.
	 * @return The application version code.
	 */
	public static int getApplicationVersionCode(Context context) {
    	
		int result = -1;
		
		try {
			
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			
			result = info.versionCode;
			
		} catch (NameNotFoundException e) {
			Log.w("ApplicationUtils", "Unable to get application version: " + e.getMessage());
			result = -1;
		}
		
		return result;
	}
	
	/**
	 * Load the changelog string.
	 * @param context The current context.
	 * @return The changelog string.
	 */
	public static String getChangelogString(Context context) {
		return getStringFromRawResource(context, R.raw.changelog);
	}
	
	public static BitmapDrawable getApplicationButtonImage(Activity activity, Bitmap icon) {
		if (icon != null) {
			int buttonSize = getApplicationButtonSize(activity);
			int size = getFaviconSize(activity);
			
			Drawable d = activity.getResources().getDrawable(R.drawable.bookmark_list_favicon_bg);						
			Bitmap bm = Bitmap.createBitmap(buttonSize, buttonSize, Bitmap.Config.ARGB_8888);
			
			Canvas canvas = new Canvas(bm);
			d.setBounds(0, 0, buttonSize, buttonSize);
			d.draw(canvas);

			BitmapDrawable favIcon = new BitmapDrawable(activity.getResources(), icon);
			favIcon.setBounds((buttonSize / 2) - (size / 2), (buttonSize / 2) - (size / 2), (buttonSize / 2) + (size / 2), (buttonSize / 2) + (size / 2));
			favIcon.draw(canvas);
			
			return new BitmapDrawable(activity.getResources(), bm);
		} else {
			return null;
		}
	}
	
	public static int[] getBookmarksThumbnailsDimensions(Context context) {
		if (sBookmarksdimensions == null) {
			Drawable d = context.getResources().getDrawable(R.drawable.browser_thumbnail);
			sBookmarksdimensions = new int[] { d.getIntrinsicWidth(), d.getIntrinsicHeight() };
		}
		
		return sBookmarksdimensions;
	}
	
	/**
	 * Share a page.
	 * @param activity The parent activity.
	 * @param title The page title.
	 * @param url The page url.
	 */
	public static void sharePage(Activity activity, String title, String url) {
    	Intent shareIntent = new Intent(Intent.ACTION_SEND);
    	
    	shareIntent.setType("text/plain");
    	shareIntent.putExtra(Intent.EXTRA_TEXT, url);
    	shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
    	
    	try {
    		activity.startActivity(Intent.createChooser(shareIntent, activity.getString(R.string.ShareChooserTitle)));
        } catch(android.content.ActivityNotFoundException ex) {
            // if no app handles it, do nothing
        }
    }
	
	/**
     * Copy a text to the clipboard.
     * @param context The current context.
     * @param text The text to copy.
     * @param toastMessage The message to show in a Toast notification. If empty or null, does not display notification.
     */
    public static void copyTextToClipboard(Context context, String text, String toastMessage) {
    	ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
    	clipboard.setPrimaryClip(ClipData.newPlainText(text, text));
    	
    	if ((toastMessage != null) &&
    			(toastMessage.length() > 0)) {
    		Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    	}
    }
	
	/**
	 * Get the required size of the favicon, depending on current screen density.
	 * @param activity The current activity.
	 * @return The size of the favicon, in pixels.
	 */
	public static int getFaviconSize(Activity activity) {
		if (sFaviconSize == -1) {
			DisplayMetrics metrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			switch (metrics.densityDpi) {
			case DisplayMetrics.DENSITY_LOW: sFaviconSize = 12; break;
			case DisplayMetrics.DENSITY_MEDIUM: sFaviconSize = 16; break;
			case DisplayMetrics.DENSITY_HIGH: sFaviconSize = 24; break;
			case DisplayMetrics.DENSITY_XHIGH: sFaviconSize = 32; break;
			default: sFaviconSize = 16;
			}
		}
		
		return sFaviconSize;
	}
	
	/**
	 * Get the required size of the application button, depending on current screen density.
	 * @param activity The current activity.
	 * @return The size of the application button, in pixels.
	 */
	public static int getApplicationButtonSize(Activity activity) {
		if (sApplicationButtonSize == -1) {
			DisplayMetrics metrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			switch (metrics.densityDpi) {
			case DisplayMetrics.DENSITY_LOW: sApplicationButtonSize = 36; break;
			case DisplayMetrics.DENSITY_MEDIUM: sApplicationButtonSize = 48; break;
			case DisplayMetrics.DENSITY_HIGH: sApplicationButtonSize = 60; break;
			case DisplayMetrics.DENSITY_XHIGH: sApplicationButtonSize = 72; break;
			default: sApplicationButtonSize = 48;
			}
		}
		
		return sApplicationButtonSize;
	}
	
	/**
	 * Display a standard yes / no dialog.
	 * @param context The current context.
	 * @param icon The dialog icon.
	 * @param title The dialog title.
	 * @param message The dialog message.
	 * @param onYes The dialog listener for the yes button.
	 */
	public static void showYesNoDialog(Context context, int icon, int title, int message, DialogInterface.OnClickListener onYes) {
    	showYesNoDialog(context,
    			icon,
    			title,
    			context.getResources().getString(message),
    			onYes);
	}
	
	/**
	 * Display a standard yes / no dialog.
	 * @param context The current context.
	 * @param icon The dialog icon.
	 * @param title The dialog title.
	 * @param message The dialog message.
	 * @param onYes The dialog listener for the yes button.
	 */
	public static void showYesNoDialog(Context context, int icon, int title, String message, DialogInterface.OnClickListener onYes) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setCancelable(true);
    	builder.setIcon(icon);
    	builder.setTitle(context.getResources().getString(title));
    	builder.setMessage(message);

    	builder.setInverseBackgroundForced(true);
    	builder.setPositiveButton(context.getResources().getString(R.string.Yes), onYes);
    	builder.setNegativeButton(context.getResources().getString(R.string.No), new DialogInterface.OnClickListener() {
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			dialog.dismiss();
    		}
    	});
    	AlertDialog alert = builder.create();
    	alert.show();
	}
	
	/**
	 * Display a continue / cancel dialog.
	 * @param context The current context.
	 * @param icon The dialog icon.
	 * @param title The dialog title.
	 * @param message The dialog message.
	 * @param onContinue The dialog listener for the continue button.
	 * @param onCancel The dialog listener for the cancel button.
	 */
	public static void showContinueCancelDialog(Context context, int icon, String title, String message, DialogInterface.OnClickListener onContinue, DialogInterface.OnClickListener onCancel) {		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setCancelable(true);
    	builder.setIcon(icon);
    	builder.setTitle(title);
    	builder.setMessage(message);

    	builder.setInverseBackgroundForced(true);
    	builder.setPositiveButton(context.getResources().getString(R.string.Continue), onContinue);
    	builder.setNegativeButton(context.getResources().getString(R.string.Cancel), onCancel);
    	AlertDialog alert = builder.create();
    	alert.show();
	}
	
	public static void showErrorDialog(Context context, String title, String message) {
		new AlertDialog.Builder(context)
        .setTitle(title)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setMessage(message)
        .setPositiveButton(R.string.OK, null)
        .show();
	}
	
	public static void showMessageDialog(Context context, String title, String message) {
		new AlertDialog.Builder(context)
        .setTitle(title)
        .setIcon(android.R.drawable.ic_dialog_info)
        .setMessage(message)
        .setPositiveButton(R.string.OK, null)
        .show();
	}
	
	public static void showAddonAskUserDialog(Context context, String title, String message, String positiveButtonText, String negativeButtonText, DialogInterface.OnClickListener onPositive, DialogInterface.OnClickListener onNegative) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setCancelable(true);
    	builder.setIcon(android.R.drawable.ic_dialog_info);
    	builder.setTitle(title);
    	builder.setMessage(message);

    	builder.setInverseBackgroundForced(true);
    	builder.setPositiveButton(positiveButtonText, onPositive);
    	builder.setNegativeButton(negativeButtonText, onNegative);
    	AlertDialog alert = builder.create();
    	alert.show();
	}
	
	/**
	 * Load a raw string resource.
	 * @param context The current context.
	 * @param resourceId The resource id.
	 * @return The loaded string.
	 */
	private static String getStringFromRawResource(Context context, int resourceId) {
		String result = null;
		
		InputStream is = context.getResources().openRawResource(resourceId);
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {					
					sb.append(line).append("\n");
				}
			} catch (IOException e) {
				Log.w("ApplicationUtils", String.format("Unable to load resource %s: %s", resourceId, e.getMessage()));
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					Log.w("ApplicationUtils", String.format("Unable to load resource %s: %s", resourceId, e.getMessage()));
				}
			}
			result = sb.toString();
		} else {        
			result = "";
		}
		
		return result;
	}

}
