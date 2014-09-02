package com.litesuits.http.request.query;

import com.litesuits.http.data.Consts;
import com.litesuits.http.request.param.CustomHttpParam;
import com.litesuits.http.request.param.CustomHttpParam.CustomValueBuilder;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * when uri query parameter's value is complex, build value into default style.
 * in this case, value will intelligently translate to default string.
 * <p/>
 * such as :
 * http://def.so? mapkey={k=v,k1=v1} & arraykey={v1,v2,v3} &
 * fieldName={field1Name=value1, field2Name=value2}
 * <p/>
 * rule is :
 * Map : map={k=v,k1=v1}
 * Array : k={v1,v2,v3}
 * JavaObject(model) : fieldName={field1Name=value1, field2Name=value2}
 *
 * @author MaTianyu
 *         2014-1-4下午5:06:37
 */
public class SimpleQueryBuilder extends AbstractQueryBuilder {

    @Override
    protected CharSequence buildSencondaryValue(Object model) {
        try {
            StringBuilder sb = new StringBuilder();
            if (model instanceof Collection<?> || model instanceof Object[]) {
                // when value is array ,use '[' and ']' to enclose data, use ',' to
                // split array value.
                Object[] objs = model instanceof Collection<?> ? ((Collection<?>) model).toArray() : (Object[]) model;
                buildUriKey(sb, null).append(Consts.ARRAY_ECLOSING_LEFT);
                int i = 0, size = objs.length;
                for (Object v : objs) {
                    buildMoreLevelValue(sb, null, v, ++i == size ? Consts.NONE_SPLIT : Consts.SECOND_LEVEL_SPLIT);
                }
                sb.append(Consts.ARRAY_ECLOSING_RIGHT);
            } else if (model instanceof Map<?, ?>) {
                Map<?, ?> map = (Map<?, ?>) model;
                // when value is map ,use '{' and '}' to enclose data, use ',' to
                // split array value.
                buildUriKey(sb, null).append(Consts.KV_ECLOSING_LEFT);
                int i = 0, size = map.size();
                for (Entry<?, ?> v : map.entrySet()) {
                    if (v.getKey() instanceof CharSequence || v.getKey() instanceof Character) {
                        buildMoreLevelValue(sb, v.getKey().toString(), v.getValue(), ++i == size
                                ? Consts.NONE_SPLIT
                                : Consts.SECOND_LEVEL_SPLIT);
                    } else {
                        buildMoreLevelValue(sb, v.getKey().getClass().getSimpleName(), v.getValue(), ++i == size
                                ? Consts.NONE_SPLIT
                                : Consts.SECOND_LEVEL_SPLIT);
                    }
                }
                sb.append(Consts.KV_ECLOSING_RIGHT);
            } else {
                // find all field.
                ArrayList<Field> fieldList = getAllDeclaredFields(model.getClass());

                // build string
                for (int i = 0, size = fieldList.size() - 1; i <= size; i++) {
                    Field f = fieldList.get(i);
                    f.setAccessible(true);
                    String key = f.getName();
                    Object value = f.get(model);
                    if (value != null) {
                        // value is primitive
                        sb.append(Consts.KV_ECLOSING_LEFT);
                        buildMoreLevelValue(sb, key, value, i == size ? Consts.NONE_SPLIT : Consts.SECOND_LEVEL_SPLIT);
                        sb.append(Consts.KV_ECLOSING_RIGHT);
                    }
                }
            }
            return sb;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void buildMoreLevelValue(StringBuilder sb, String key, Object value, String split)
            throws UnsupportedEncodingException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        // when value is null, just return.
        if (value == null) { return; }
        if (value instanceof Number || value instanceof CharSequence || value instanceof Character) {
            // when value is primitive , build as "key=value"
            buildUriKey(sb, key).append(encode(value.toString())).append(split);
        } else if (value instanceof CustomHttpParam) {
            // when value is inherited from Request.Builder , build as
            // "key="+method.invoke().
            Method methods[] = CustomHttpParam.class.getDeclaredMethods();
            for (Method m : methods) {
                // invoke the method which has specified Annotation
                if (m.getAnnotation(CustomValueBuilder.class) != null) {
                    m.setAccessible(true);
                    Object v = m.invoke(value);
                    if (v != null) {
                        buildUriKey(sb, key).append(encode(v.toString())).append(split);
                    }
                    break;
                }
            }
        } else if (value instanceof Collection<?> || value instanceof Object[]) {
            // when value is array ,use '[' and ']' to enclose data, use ',' to
            // split array value.
            Object[] objs = value instanceof Collection<?> ? ((Collection<?>) value).toArray() : (Object[]) value;
            buildUriKey(sb, key).append(Consts.ARRAY_ECLOSING_LEFT);
            int i = 0, size = objs.length;
            for (Object v : objs) {
                buildMoreLevelValue(sb, null, v, ++i == size ? Consts.NONE_SPLIT : Consts.SECOND_LEVEL_SPLIT);
            }
            sb.append(Consts.ARRAY_ECLOSING_RIGHT).append(split);
        } else if (value instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) value;
            // when value is map ,use '{' and '}' to enclose data, use ',' to
            // split array value.
            buildUriKey(sb, key).append(Consts.KV_ECLOSING_LEFT);
            int i = 0, size = map.size();
            for (Entry<?, ?> v : map.entrySet()) {
                if (v.getKey() instanceof CharSequence || v.getKey() instanceof Character) {
                    buildMoreLevelValue(sb, v.getKey().toString(), v.getValue(), ++i == size
                            ? Consts.NONE_SPLIT
                            : Consts.SECOND_LEVEL_SPLIT);
                } else {
                    buildMoreLevelValue(sb, v.getKey().getClass().getSimpleName(), v.getValue(), ++i == size
                            ? Consts.NONE_SPLIT
                            : Consts.SECOND_LEVEL_SPLIT);
                }
            }
            sb.append(Consts.KV_ECLOSING_RIGHT).append(split);
        } else {
            buildUriKey(sb, key);
            sb.append(Consts.KV_ECLOSING_LEFT);
            // find all field.
            ArrayList<Field> fieldList = getAllDeclaredFields(value.getClass());
            for (int i = 0, size = fieldList.size() - 1; i <= size; i++) {
                Field f = fieldList.get(i);
                f.setAccessible(true);
                String nextKey = f.getName();
                Object nextValue = f.get(value);
                if (nextValue != null) {
                    sb.append(Consts.KV_ECLOSING_LEFT);
                    buildMoreLevelValue(sb, nextKey, nextValue, i == size ? Consts.NONE_SPLIT : Consts.SECOND_LEVEL_SPLIT);
                    sb.append(Consts.KV_ECLOSING_RIGHT);
                }
            }
            sb.append(Consts.KV_ECLOSING_RIGHT).append(split);
        }
    }

}
