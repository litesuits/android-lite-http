package com.litesuits.http.parser;

import com.litesuits.http.LiteHttpClient;
import com.litesuits.http.listener.HttpReadingListener;
import com.litesuits.http.request.Request;

import java.io.IOException;
import java.io.InputStream;

/**
 * 数据处理器
 *
 * @param <D>
 * @author MaTianyu
 *         2014-2-21下午7:26:58
 */
public abstract class DataParser<D> {
    protected int buffSize = LiteHttpClient.DEFAULT_BUFFER_SIZE;
    private   D                   data;
    protected int                 readLength;
    //protected boolean             statistics;
    protected HttpReadingListener httpReadingListener;
    protected Request request;

    public final D readInputStream(InputStream stream, long len, String charSet) throws IOException {
        if (stream != null) {
            try {
                //statistics = LiteHttpClient.doStatistics;
                data = parseData(stream, len, charSet);
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stream.close();
            }
        }
        return data;
    }

    /**
     * 自定义实现解析
     *
     * @param stream      数据流
     * @param totalLength 总长度
     * @return
     * @throws IOException
     */
    protected abstract D parseData(InputStream stream, long totalLength, String charSet) throws IOException;

    /**
     * 获取读取数据的长度，以便统计流量所用
     *
     * @return
     */
    public int getReadedLength() {
        return readLength;
    }

    public final D getData() {
        return data;
    }

    public void setHttpReadingListener(HttpReadingListener httpReadingListener) {
        this.httpReadingListener = httpReadingListener;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return "DataParser{" +
                "buffSize=" + buffSize +
                ", data=" + data +
                ", readLength=" + readLength +
                '}';
    }
}
