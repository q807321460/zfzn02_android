package com.jia.ezcamera.utils;


import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.text.MessageFormat;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * ToastUtils
 * 
 */
public class ToastUtils {

	private ToastUtils() {
		throw new AssertionError();
	}

	public static void show(Context context, int resId) {
		show(context, context.getResources().getText(resId), Toast.LENGTH_SHORT);
	}

	public static void show(Context context, int resId, int duration) {
		show(context, context.getResources().getText(resId), duration);
	}

	public static void show(Context context, CharSequence text) {
		show(context, text, Toast.LENGTH_SHORT);
	}

	public static void show(Context context, CharSequence text, int duration) {
		Toast.makeText(context, text, duration).show();
	}

	public static void show(Context context, int resId, Object... args) {
		show(context,
				String.format(context.getResources().getString(resId), args),
				Toast.LENGTH_SHORT);
	}

	public static void show(Context context, String format, Object... args) {
		show(context, String.format(format, args), Toast.LENGTH_SHORT);
	}

	public static void show(Context context, int resId, int duration,
			Object... args) {
		show(context,
				String.format(context.getResources().getString(resId), args),
				duration);
	}

	public static void show(Context context, String format, int duration,
			Object... args) {
		show(context, String.format(format, args), duration);
	}

	private static void show(final Activity activity, final int resId,
			final int duration) {
		if (activity == null)
			return;

		final Context context = activity.getApplication();
		activity.runOnUiThread(new Runnable() {

			public void run() {
				Toast.makeText(context, resId, duration).show();
			}
		});
	}

	private static void show(final Activity activity, final String message,
			final int duration) {
		if (activity == null)
			return;
		if (TextUtils.isEmpty(message))
			return;

		final Context context = activity.getApplication();
		activity.runOnUiThread(new Runnable() {

			public void run() {
				Toast.makeText(context, message, duration).show();
			}
		});
	}

	/**
	 * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
	 *
	 * @param activity
	 * @param resId
	 */
	public static void showLong(final Activity activity, int resId) {
		show(activity, resId, 1000);
	}

	/**
	 * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
	 *
	 * @param activity
	 * @param resId
	 */
	public static void showShort(final Activity activity, final int resId) {
		show(activity, resId, LENGTH_SHORT);
	}

	/**
	 * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
	 *
	 * @param activity
	 * @param message
	 */
	public static void showLong(final Activity activity, final String message) {
		show(activity, message, LENGTH_LONG);
	}

	/**
	 * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
	 *
	 * @param activity
	 * @param message
	 */
	public static void showShort(final Activity activity, final String message) {
		show(activity, message, LENGTH_SHORT);
	}

	/**
	 * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
	 *
	 * @param activity
	 * @param message
	 * @param args
	 */
	public static void showLong(final Activity activity, final String message,
			final Object... args) {
		String formatted = MessageFormat.format(message, args);
		show(activity, formatted, LENGTH_LONG);
	}

	/**
	 * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
	 *
	 * @param activity
	 * @param message
	 * @param args
	 */
	public static void showShort(final Activity activity, final String message,
			final Object... args) {
		String formatted = MessageFormat.format(message, args);
		show(activity, formatted, LENGTH_SHORT);
	}

	/**
	 * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
	 *
	 * @param activity
	 * @param resId
	 * @param args
	 */
	public static void showLong(final Activity activity, final int resId,
			final Object... args) {
		if (activity == null)
			return;

		String message = activity.getString(resId);
		showLong(activity, message, args);
	}

	/**
	 * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
	 * 
	 * @param activity
	 * @param resId
	 * @param args
	 */
	public static void showShort(final Activity activity, final int resId,
			final Object... args) {
		if (activity == null)
			return;

		String message = activity.getString(resId);
		showShort(activity, message, args);
	}
}
