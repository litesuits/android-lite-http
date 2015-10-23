#Android网络通信框架LiteHttp 第四节：自定义DataParser和Json序列化库的替换

标签（空格分隔）： litehttp2.x版本系列教程

---
官网： http://litesuits.com

QQ群： 大群 47357508，二群 42960650

本系列文章面向android开发者，展示开源网络通信框架LiteHttp的主要用法，并讲解其关键功能的运作原理，同时传达了一些框架作者在日常开发中的一些最佳实践和经验。

本系列文章目录总览： https://zybuluo.com/liter/note/186513

---

#第四节：LiteHttp之自定义DataParser和Json序列化库的替换

## 1. DataParser机制简介
DataParser即数据解析器，其作用是将网络流对象转换为指定的对象，包括将缓存的存储和读取，甚至Json对象的转化等作用。

lite-http内置五种形式的请求，也就有五种DataParser类型：

- StringRequest  ： 字符串请求，对应StringParser，将stream转为string。
- BytesRequest   ： 字节请求，对应BytesParser，将stream转为byte。
- JsonAbsRequest ： Json对象请求，对应JsonParser，将stream转为Java Model。
- BitmapRequest  ： 位图加载请求，对应BitmapParser，将stream转为bitmap。
- FileRequest    ： 文件下载请求，对应FileParser，将stream转为file。

通过源码可以发现，他们的共同基类是DataParser<T>，它有两个直接子类，将解析器分为两种：

- MemCacheableParser  ：解析并处理可支持**闪存缓存**的数据，包括：StringRequest，BytesRequest和JsonAbsRequest。
- FileCacheableParser ：解析并处理仅支持**文件缓存**的数据，包括：BitmapRequest和FileRequest。

以上请求形式在实际应用中可能出现不能满足某些特定需求的情况，这时需要自定义DataParser来增强lite-http的数据处理能力。

## 2. 自定义DataParser实例

如果文件略大只能支持文件缓存，需要继承FileCacheableParser。
如果文件不大可以支持闪存缓存，则要继承MemCacheableParser。

举个例子，现在要将网络数据解析为JSONObject(android系统自带的org.json包里的JSONObject)，我们首先可以确定返回JSONObject对象不会太大，可以支持闪存、文件两级缓存，代码如下：
```java
/**
 * parse stream to JSONObject
 */
 class CustomJSONParser extends MemCacheableParser<JSONObject> {
    String json;

    /**
     * 实现远程网络流解析
     */
    @Override
    protected JSONObject parseNetStream(InputStream stream, long totalLength,
                                        String charSet) throws IOException {
        return streamToJson(stream, totalLength, charSet);
    }

    /**
     * 实现本地文件流解析
     */
    @Override
    protected JSONObject parseDiskCache(InputStream stream, long length) throws IOException {
        return streamToJson(stream, length, charSet);
    }

    /**
     * 实现文件缓存
     */
    @Override
    protected boolean tryKeepToCache(JSONObject data) throws IOException {
        return keepToCache(json);
    }

    /**
     * 1. 将 stream 转换为 String
     * 2. String 转为 JSONObject
     */
    protected JSONObject streamToJson(InputStream is, long length, String charSet) throws IOException {
        this.json = streamToString(is, length, charSet);
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
```
使用方式：
```java

JsonRequest<JSONObject> jsonRequest = new JsonRequest<JSONObject> (userUrl, JSONObject.class);
jsonRequest.setDataParser(new CustomJSONParser());
liteHttp.executeAsync(jsonRequest.setHttpListener(new HttpListener<JSONObject>() {
    @Override
    public void onSuccess(JSONObject jsonObject, Response<JSONObject> response) {
        
    }
}));
```

## 3. Json序列化库的替换

Json序列化库比较出名的有fastjson，jackson，gson等，初学者往往不知道该选哪个，经过个人较大量的实践测试得出一些结论，仅供参考。

json的序列化及反序列化性能对比：
> 
- fastjson 处理处理少量数据的对象时速度最快；
- jackson 在处理较大量数据的对象时速度最快；
- gson 速度不及前两者。

其他方面：
> 
- fastjson需要getter方法，比较麻烦。
- jackson jar包体积太大，不合适移动端开发。
- gson 速度不是最快，但可以接受，包体积最小。

鉴于gson速度适中，API健全稳定，使用简单且包体积最小，lite-http默认使用gson作为json自动序列化及反序列化类库。

**如果正在使用其他json自动化类库，当然也可以轻松替换**

将gson替换为alibaba的fastjson类库：
```java
// first, set new json framework instance. then, over.

Json.set(new FastJson());
```

FastJson类如何实现？
```java
class FastJson extends Json {

    @Override
    public String toJson(Object src) {
        return JSON.toJSONString(src);
    }

    @Override
    public <T> T toObject(String json, Class<T> claxx) {
        return JSON.parseObject(json, claxx);
    }

    @Override
    public <T> T toObject(String s, Type type) {
        return JSON.parseObject(s, type);
    }

    @Override
    public <T> T toObject(byte[] bytes, Class<T> claxx) {
        return JSON.parseObject(bytes, claxx);
    }
}
```

像往常一样发送请求，json库就换为fastjson了：
```java
// json model convert used #FastJson
liteHttp.executeAsync(new JsonAbsRequest<User>(userUrl) {}.setHttpListener(new HttpListener<User>() {
    @Override
    public void onSuccess(User user, Response<User> response) {
        response.printInfo();
    }
}));
```