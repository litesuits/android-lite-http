package com.litesuits.http.log;

/**
 * the logger
 *
 * @author MaTianyu
 *         2014-1-1下午4:05:39
 */
public final class HttpLog {

    public static boolean isPrint = false;
    public static String defaultTag = "HttpLog";
    public static final String NULL = "NULL";

    private HttpLog() {}

    public static void setTag(String tag) {
        defaultTag = tag;
    }

    public static int i(String tag, Object o) {
        if (isPrint) {
            if (o == null) {
                android.util.Log.w(tag, NULL);
            } else {
                android.util.Log.w(tag, o.toString());
            }
        }
        return -1;
    }

    public static int i(Object o) {
        return i(defaultTag, o);
    }

    //******************** TAG use Object Tag **************************
    public static int v(String msg) {
        return v(defaultTag, msg);
    }

    public static int d(String msg) {
        return d(defaultTag, msg);
    }

    public static int i(String msg) {
        return i(defaultTag, msg);
    }

    public static int w(String msg) {
        return w(defaultTag, msg);
    }

    public static int e(String msg) {
        return e(defaultTag, msg);
    }

    // ******************** Log **************************
    public static int v(String tag, String msg) {
        if (isPrint) {
            if (msg == null) {
                android.util.Log.v(tag, NULL);
            } else {
                android.util.Log.v(tag, msg);
            }
        }
        return -1;
    }

    public static int d(String tag, String msg) {
        if (isPrint) {
            if (msg == null) {
                android.util.Log.d(tag, NULL);
            } else {
                android.util.Log.d(tag, msg);
            }
        }
        return -1;
    }

    public static int i(String tag, String msg) {
        if (isPrint) {
            if (msg == null) {
                android.util.Log.i(tag, NULL);
            } else {
                android.util.Log.i(tag, msg);
            }
        }
        return -1;
    }

    public static int w(String tag, String msg) {
        if (isPrint) {
            if (msg == null) {
                android.util.Log.w(tag, NULL);
            } else {
                android.util.Log.w(tag, msg);
            }
        }
        return -1;
    }

    public static int e(String tag, String msg) {
        if (isPrint) {
            if (msg == null) {
                android.util.Log.e(tag, NULL);
            } else {
                android.util.Log.e(tag, msg);
            }
        }
        return -1;
    }

    // ******************** Log with object list **************************
    public static int v(String tag, Object... msg) {
        return isPrint ? v(tag, getLogMessage(msg)) : -1;
    }

    public static int d(String tag, Object... msg) {
        return isPrint ? d(tag, getLogMessage(msg)) : -1;
    }

    public static int i(String tag, Object... msg) {
        return isPrint ? i(tag, getLogMessage(msg)) : -1;
    }

    public static int w(String tag, Object... msg) {
        return isPrint ? w(tag, getLogMessage(msg)) : -1;
    }

    public static int e(String tag, Object... msg) {
        return isPrint ? e(tag, getLogMessage(msg)) : -1;
    }

    private static String getLogMessage(Object... msg) {
        if (msg != null && msg.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (Object s : msg) {
                sb.append(s.toString()).append(", ");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
        return NULL;
    }

    // ******************** Log with Throwable **************************
    public static int v(String tag, String msg, Throwable tr) {
        return isPrint ? android.util.Log.v(tag, msg, tr) : -1;
    }

    public static int d(String tag, String msg, Throwable tr) {
        return isPrint ? android.util.Log.d(tag, msg, tr) : -1;
    }

    public static int i(String tag, String msg, Throwable tr) {
        return isPrint ? android.util.Log.i(tag, msg, tr) : -1;
    }

    public static int w(String tag, String msg, Throwable tr) {
        return isPrint ? android.util.Log.w(tag, msg, tr) : -1;
    }

    public static int e(String tag, String msg, Throwable tr) {
        return isPrint ? android.util.Log.e(tag, msg, tr) : -1;
    }

    // ******************** TAG use Object Tag **************************
    public static int v(Object tag, String msg) {
        return isPrint ? v(tag.getClass().getSimpleName(), msg) : -1;
    }

    public static int d(Object tag, String msg) {
        return isPrint ? d(tag.getClass().getSimpleName(), msg) : -1;
    }

    public static int i(Object tag, String msg) {
        return isPrint ? i(tag.getClass().getSimpleName(), msg) : -1;
    }

    public static int w(Object tag, String msg) {
        return isPrint ? w(tag.getClass().getSimpleName(), msg) : -1;
    }

    public static int e(Object tag, String msg) {
        return isPrint ? e(tag.getClass().getSimpleName(), msg) : -1;
    }
}
