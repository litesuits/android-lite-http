package com.litesuits.http.listener;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.log.HttpLog;
import com.litesuits.http.request.AbstractRequest;
import com.litesuits.http.response.Response;

/**
 * @author MaTianyu
 * @date 2014-11-06
 */
public abstract class HttpListener<Data> {
    private static final String TAG = HttpListener.class.getSimpleName();

    private static final int M_START = 1;
    private static final int M_SUCCESS = 2;
    private static final int M_FAILURE = 3;
    private static final int M_CANCEL = 4;
    private static final int M_READING = 5;
    private static final int M_UPLOADING = 6;
    private static final int M_RETRY = 7;
    private static final int M_REDIRECT = 8;
    private static final int M_END = 9;

    private HttpHandler handler;
    private boolean runOnUiThread = true;
    private boolean readingNotify = false;
    private boolean uploadingNotify = false;
    private HttpListener<Data> linkedListener;
    private long delayMillis;

    /**
     * default run on UI thread
     */
    public HttpListener() {
        this(true);
    }

    public HttpListener(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    public HttpListener(boolean runOnUiThread) {
        setRunOnUiThread(runOnUiThread);
    }

    public HttpListener(boolean runOnUiThread, boolean readingNotify, boolean uploadingNotify) {
        this(runOnUiThread);
        this.readingNotify = readingNotify;
        this.uploadingNotify = uploadingNotify;
    }

    public final HttpListener<Data> getLinkedListener() {
        return linkedListener;
    }

    public final HttpListener<Data> setLinkedListener(HttpListener<Data> httpListener) {
        if (this.linkedListener != null) {
            HttpListener<Data> temp = this.linkedListener;
            do {
                if (httpListener == temp) {
                    throw new RuntimeException("Circular refrence:  " + httpListener);
                }
            } while ((temp = temp.getLinkedListener()) != null);
        }
        this.linkedListener = httpListener;
        return this;
    }

    public final boolean isRunOnUiThread() {
        return runOnUiThread;
    }

    public final HttpListener<Data> setRunOnUiThread(boolean runOnUiThread) {
        this.runOnUiThread = runOnUiThread;
        if (runOnUiThread) {
            handler = new HttpHandler(Looper.getMainLooper());
        } else {
            handler = null;
        }
        return this;
    }

    public final boolean isReadingNotify() {
        return readingNotify;
    }

    public final HttpListener<Data> setReadingNotify(boolean readingNotify) {
        this.readingNotify = readingNotify;
        return this;
    }

    public final boolean isUploadingNotify() {
        return uploadingNotify;
    }

    public final HttpListener<Data> setUploadingNotify(boolean uploadingNotify) {
        this.uploadingNotify = uploadingNotify;
        return this;
    }

    public long getDelayMillis() {
        return delayMillis;
    }

    public HttpListener<Data> setDelayMillis(long delayMillis) {
        this.delayMillis = delayMillis;
        return this;
    }

    /**
     * note: hold an implicit reference to outter class
     */
    private class HttpHandler extends Handler {
        private HttpHandler(Looper looper) {
            super(looper);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            if (disableListener()) {
                return;
            }
            Object[] data;
            switch (msg.what) {
                case M_START:
                    onStart((AbstractRequest<Data>) msg.obj);
                    break;
                case M_SUCCESS:
                    data = (Object[]) msg.obj;
                    onSuccess((Data) data[0], (Response<Data>) data[1]);
                    break;
                case M_FAILURE:
                    data = (Object[]) msg.obj;
                    onFailure((HttpException) data[0], (Response<Data>) data[1]);
                    break;
                case M_CANCEL:
                    data = (Object[]) msg.obj;
                    onCancel((Data) data[0], (Response<Data>) data[1]);
                    break;
                case M_READING:
                    data = (Object[]) msg.obj;
                    onLoading((AbstractRequest<Data>) data[0], (Long) data[1], (Long) data[2]);
                    break;
                case M_UPLOADING:
                    data = (Object[]) msg.obj;
                    onUploading((AbstractRequest<Data>) data[0], (Long) data[1], (Long) data[2]);
                    break;
                case M_RETRY:
                    data = (Object[]) msg.obj;
                    onRetry((AbstractRequest<Data>) data[0], (Integer) data[1], (Integer) data[2]);
                    break;
                case M_REDIRECT:
                    data = (Object[]) msg.obj;
                    onRedirect((AbstractRequest<Data>) data[0], (Integer) data[1], (Integer) data[2]);
                    break;
                case M_END:
                    onEnd((Response<Data>) msg.obj);
                    break;
            }
        }
    }

    //____________lite called method ____________
    public final void notifyCallStart(AbstractRequest<Data> req) {
        if (disableListener()) {
            return;
        }
        if (runOnUiThread) {
            Message msg = handler.obtainMessage(M_START);
            msg.obj = req;
            handler.sendMessage(msg);
        } else {
            onStart(req);
        }
        if (linkedListener != null) {
            linkedListener.notifyCallStart(req);
        }
    }

    public final void notifyCallSuccess(Data data, Response<Data> response) {
        delayOrNot();
        if (disableListener()) {
            return;
        }
        if (runOnUiThread) {
            Message msg = handler.obtainMessage(M_SUCCESS);
            msg.obj = new Object[]{data, response};
            handler.sendMessage(msg);
        } else {
            onSuccess(data, response);
        }
        if (linkedListener != null) {
            linkedListener.notifyCallSuccess(data, response);
        }
    }

    public final void notifyCallFailure(HttpException e, Response<Data> response) {
        delayOrNot();
        if (disableListener()) {
            return;
        }
        if (runOnUiThread) {
            Message msg = handler.obtainMessage(M_FAILURE);
            msg.obj = new Object[]{e, response};
            handler.sendMessage(msg);
        } else {
            onFailure(e, response);
        }
        if (linkedListener != null) {
            linkedListener.notifyCallFailure(e, response);
        }
    }

    public final void notifyCallCancel(Data data, Response<Data> response) {
        if (HttpLog.isPrint) {
            HttpLog.w(TAG, "Request be Cancelled!  isCancelled: " + response.getRequest().isCancelled()
                           + "  Thread isInterrupted: " + Thread.currentThread().isInterrupted());
        }
        delayOrNot();
        if (disableListener()) {
            return;
        }
        if (runOnUiThread) {
            Message msg = handler.obtainMessage(M_CANCEL);
            msg.obj = new Object[]{data, response};
            handler.sendMessage(msg);
        } else {
            onCancel(data, response);
        }
        if (linkedListener != null) {
            linkedListener.notifyCallCancel(data, response);
        }
    }

    public final void notifyCallLoading(AbstractRequest<Data> req, long total, long len) {
        if (disableListener()) {
            return;
        }
        if (readingNotify) {
            if (runOnUiThread) {
                Message msg = handler.obtainMessage(M_READING);
                msg.obj = new Object[]{req, total, len};
                handler.sendMessage(msg);
            } else {
                onLoading(req, total, len);
            }
        }
        if (linkedListener != null) {
            linkedListener.notifyCallLoading(req, total, len);
        }
    }

    public final void notifyCallUploading(AbstractRequest<Data> req, long total, long len) {
        if (disableListener()) {
            return;
        }
        if (uploadingNotify) {
            if (runOnUiThread) {
                Message msg = handler.obtainMessage(M_UPLOADING);
                msg.obj = new Object[]{req, total, len};
                handler.sendMessage(msg);
            } else {
                onUploading(req, total, len);
            }
        }
        if (linkedListener != null) {
            linkedListener.notifyCallUploading(req, total, len);
        }
    }

    public final void notifyCallRetry(AbstractRequest<Data> req, int max, int times) {
        if (disableListener()) {
            return;
        }
        if (runOnUiThread) {
            Message msg = handler.obtainMessage(M_RETRY);
            msg.obj = new Object[]{req, max, times};
            handler.sendMessage(msg);
        } else {
            onRetry(req, max, times);
        }
        if (linkedListener != null) {
            linkedListener.notifyCallRetry(req, max, times);
        }
    }

    public final void notifyCallRedirect(AbstractRequest<Data> req, int max, int times) {
        if (disableListener()) {
            return;
        }
        if (runOnUiThread) {
            Message msg = handler.obtainMessage(M_REDIRECT);
            msg.obj = new Object[]{req, max, times};
            handler.sendMessage(msg);
        } else {
            onRedirect(req, max, times);
        }
        if (linkedListener != null) {
            linkedListener.notifyCallRedirect(req, max, times);
        }
    }

    public final void notifyCallEnd(Response<Data> response) {
        if (disableListener()) {
            return;
        }
        if (runOnUiThread) {
            Message msg = handler.obtainMessage(M_END);
            msg.obj = response;
            handler.sendMessage(msg);
        } else {
            onEnd(response);
        }
        if (linkedListener != null) {
            linkedListener.notifyCallEnd(response);
        }
    }

    private boolean delayOrNot() {
        if (delayMillis > 0) {
            try {
                Thread.sleep(delayMillis);
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //____________ developer override method ____________
    public boolean disableListener() {
        return false;
    }

    public void onStart(AbstractRequest<Data> request) {}

    public void onSuccess(Data data, Response<Data> response) {}

    public void onFailure(HttpException e, Response<Data> response) {}

    public void onCancel(Data data, Response<Data> response) {}

    public void onLoading(AbstractRequest<Data> request, long total, long len) {}

    public void onUploading(AbstractRequest<Data> request, long total, long len) {}

    public void onRetry(AbstractRequest<Data> request, int max, int times) {}

    public void onRedirect(AbstractRequest<Data> request, int max, int times) {}

    public void onEnd(Response<Data> response) {}
}
