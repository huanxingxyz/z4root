package com.z4mod.z4root2;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;

public final class DebugLog {

    public static final boolean ENABLED = true;

    private static final String DEFAULT_MESSAGE = "execute";
    private static final String LINE_SEPARATOR = System
            .getProperty("line.separator");
    private static final int JSON_INDENT = 4;

    private static final int V = 0x1;
    private static final int D = 0x2;
    private static final int I = 0x3;
    private static final int W = 0x4;
    private static final int E = 0x5;
    private static final int A = 0x6;
    private static final int JSON = 0x7;

    private static final String FILE_UNCAUGHT_EXCEPITON_LOG = "UncaughtException.log";

    public static int v(String TAG, String msg) {
        return ENABLED ? Log.v(TAG, msg) : 0;
    }

    public static int v() {
        return ENABLED ? printLog(V, null, DEFAULT_MESSAGE) : 0;
    }

    public static int v(String msg) {
        return ENABLED ? printLog(V, null, msg) : 0;
    }

    public static int d(String tag, String msg) {
        return ENABLED ? Log.d(tag, msg) : 0;
    }

    public static int d() {
        return ENABLED ? printLog(D, null, DEFAULT_MESSAGE) : 0;
    }

    public static int d(String msg) {
        return ENABLED ? printLog(D, null, msg) : 0;
    }

    public static int i() {
        return ENABLED ? printLog(I, null, DEFAULT_MESSAGE) : 0;
    }

    public static int i(String msg) {
        return ENABLED ? printLog(I, null, msg) : 0;
    }

    public static int i(String tag, String msg) {
        return ENABLED ? printLog(I, tag, msg) : 0;
    }

    public static int w() {
        return ENABLED ? printLog(W, null, DEFAULT_MESSAGE) : 0;
    }

    public static int w(String msg) {
        return ENABLED ? printLog(W, null, msg) : 0;
    }

    public static int w(String tag, String msg) {
        return ENABLED ? printLog(W, tag, msg) : 0;
    }

    public static int e(String tag, String msg) {
        return ENABLED ? Log.e(tag, msg) : 0;
    }

    public static int e() {
        return ENABLED ? printLog(E, null, DEFAULT_MESSAGE) : 0;
    }

    public static int e(String msg) {
        return ENABLED ? printLog(E, null, msg) : 0;
    }

    public static int e(String tag, String msg, Throwable tr) {
        return ENABLED ? Log.e(tag, msg, tr) : 0;
    }

    public static int a() {
        return ENABLED ? printLog(A, null, DEFAULT_MESSAGE) : 0;
    }

    public static int a(String msg) {
        return ENABLED ? printLog(A, null, msg) : 0;
    }

    public static int a(String tag, String msg) {
        return ENABLED ? printLog(A, tag, msg) : 0;
    }

    public static int json(String jsonFormat) {
        return ENABLED ? printLog(JSON, null, jsonFormat) : 0;
    }

    public static int json(String tag, String jsonFormat) {
        return ENABLED ? printLog(JSON, tag, jsonFormat) : 0;
    }

    public static void registerUncaughtExceptionHandler(Context context) {
        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(
                context));
    }


    private static String getFormatDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINESE)
                .format(System.currentTimeMillis());
    }

    public static String getExceptionTrace(Throwable e) {
        if (e != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            return stringWriter.toString();
        }
        return null;
    }



    private static int printLog(int type, String tagStr, Object objectMsg) {
        String msg;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 4;
        String className = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();

        String tag = (tagStr == null ? className : tagStr);
        methodName = methodName.substring(0, 1).toUpperCase()
                + methodName.substring(1);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ (").append(className).append(":")
                .append(lineNumber).append(")#").append(methodName)
                .append(" ] ");

        if (objectMsg == null) {
            msg = "Log with null Object";
        } else {
            msg = objectMsg.toString();
        }
        if (msg != null && type != JSON) {
            stringBuilder.append(msg);
        }

        String logStr = stringBuilder.toString();
        switch (type) {
            case V:
                Log.v(tag, logStr);
                break;
            case D:
                Log.d(tag, logStr);
                break;
            case I:
                Log.i(tag, logStr);
                break;
            case W:
                Log.w(tag, logStr);
                break;
            case E:
                Log.e(tag, logStr);
                break;
        
            case JSON: {
                if (TextUtils.isEmpty(msg)) {
                    Log.d(tag, "Empty or Null json content");
                    return 1;
                }

                String message = null;

                try {
                    if (msg.startsWith("{")) {
                        JSONObject jsonObject = new JSONObject(msg);
                        message = jsonObject.toString(JSON_INDENT);
                    } else if (msg.startsWith("[")) {
                        JSONArray jsonArray = new JSONArray(msg);
                        message = jsonArray.toString(JSON_INDENT);
                    }
                } catch (JSONException e) {
                    e(tag, e.getCause().getMessage() + "\n" + msg);
                    return 1;
                }

                printLine(tag, true);
                message = logStr + LINE_SEPARATOR + message;
                String[] lines = message.split(LINE_SEPARATOR);
                StringBuilder jsonContent = new StringBuilder();
                for (String line : lines) {
                    jsonContent.append("").append(line).append(LINE_SEPARATOR);
                }
                Log.d(tag, jsonContent.toString());
                printLine(tag, false);
            }
            break;
        }
        return 1;
    }

    private static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.d(tag,
                    "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.d(tag,
                    "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }

    private static class MyUncaughtExceptionHandler implements
            Thread.UncaughtExceptionHandler {

        private Context mContext;

        public MyUncaughtExceptionHandler(Context context) {
            mContext = context;
        }

        @Override
        public void uncaughtException(Thread thread, final Throwable ex) {
            new Thread() {

                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(mContext, "很抱歉,程序异常",
                            Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }.start();

            ThreadPoolManager.EXECUTOR.execute(new Runnable() {

                @Override
                public void run() {

                    Log.e("DebugLog", "exception:" + getExceptionTrace(ex));
                    ex.printStackTrace();
//                    Process.killProcess(Process.myPid());
//                    System.exit(1);
                }
            });
        }
    }
}
