package com.litesuits.http.model.api;

import com.litesuits.http.annotation.HttpCacheExpire;
import com.litesuits.http.annotation.HttpCacheMode;
import com.litesuits.http.annotation.HttpMethod;
import com.litesuits.http.annotation.HttpUri;
import com.litesuits.http.model.User;
import com.litesuits.http.request.content.HttpBody;
import com.litesuits.http.request.content.UrlEncodedFormBody;
import com.litesuits.http.request.param.CacheMode;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.request.param.HttpRichParamModel;
import com.litesuits.http.request.query.ModelQueryBuilder;
import com.litesuits.http.request.query.SimpleQueryBuilder;
import com.litesuits.http.sample.MainActivity;

import java.util.concurrent.TimeUnit;

/**
 * POST request， HTTP body：UrlEncodedForm: [id=000&key=xxx]
 * use cache
 */
@HttpUri(MainActivity.userUrl)
@HttpMethod(HttpMethods.Post)
@HttpCacheMode(CacheMode.CacheFirst)
@HttpCacheExpire(value = 10, unit = TimeUnit.SECONDS)
public class RichParam extends HttpRichParamModel<User> {

    private static final ModelQueryBuilder MODEL_QUERY_BUILDER = new SimpleQueryBuilder();

    public long id;
    public String key;

    public RichParam(long id, String key) {
        this.id = id;
        this.key = key;
    }

    @Override
    protected HttpBody createHttpBody() {
        return new UrlEncodedFormBody(getModelQueryBuilder().buildPrimaryPairSafely(this));
    }

    @Override
    protected ModelQueryBuilder createQueryBuilder() {
        return MODEL_QUERY_BUILDER;
    }
}