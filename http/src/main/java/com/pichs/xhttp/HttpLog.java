package com.pichs.xhttp;

import android.util.Log;

public class HttpLog {
    private static boolean debug = false;
    private static String prefix = "HttpHelper::";

    public static void init(boolean enable) {
        debug = enable;
    }

    public static void d(String tag, String msg) {
        if (debug) {
            Log.d(prefix + tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable cause) {
        if (debug) {
            Log.d(prefix + tag, msg, cause);
        }
    }

    public static void w(String tag, String msg) {
        if (debug) {
            Log.w(prefix + tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable cause) {
        if (debug) {
            Log.w(prefix + tag, msg, cause);
        }
    }

    public static void e(String tag, String msg) {
        if (debug) {
            Log.e(prefix + tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable cause) {
        if (debug) {
            Log.e(prefix + tag, msg, cause);
        }
    }

    public static void i(String tag, String msg) {
        if (debug) {
            Log.i(prefix + tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable cause) {
        if (debug) {
            Log.i(prefix + tag, msg, cause);
        }
    }

    public static void v(String tag, String msg) {
        if (debug) {
            Log.v(prefix + tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable cause) {
        if (debug) {
            Log.v(prefix + tag, msg, cause);
        }
    }
}