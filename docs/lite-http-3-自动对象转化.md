#Android网络通信框架LiteHttp 第三节：自动对象转化

标签（空格分隔）： litehttp2.x版本系列教程

---
官网： http://litesuits.com

QQ群： 大群 47357508，二群 42960650

本系列文章面向android开发者，展示开源网络通信框架LiteHttp的主要用法，并讲解其关键功能的运作原理，同时传达了一些框架作者在日常开发中的一些最佳实践和经验。

本系列文章目录总览： https://zybuluo.com/liter/note/186513

---

#第三节：LiteHttp之自动对象转化

下面是一个真实有效的API地址：
```java
public static final String userUrl = "http://litesuits.com/mockdata/user_get";
```
它的响应体是个Json结构的字符串：
```json
{
    "api": "com.xx.get.userinfo",
    "v": "1.0",
    "result": {
        "code": 200,
        "message": "success"
    },
    "data": {
        "age": 18,
        "name": "qingtianzhu",
        "girl_friends": [
            "xiaoli",
            "fengjie",
            "lucy"
        ]
    }
}
```

## 1. java对象转为http参数
假设是一个GET请求，就是说将参数拼接在URL里,类似：http://{userUrl}?id=168&key=md5，自动拼接参数的示范代码如下：
```java
/**
 * Param Model: will be converted to: http://...?id=168&key=md5
 */
 class UserParam implements HttpParamModel {
    // static final property will be ignored.
    private static final long serialVersionUID = 123L;
    private long id;
    public String key;
    @NonHttpParam
    protected String ignored = "Ignored by @NonHttpParam ";

    public UserParam(long id, String key) {
        this.id = id;
        this.key = key;
    }
}

// build as http://{userUrl}?id=168&key=md5
liteHttp.executeAsync(new StringRequest(userUrl, new UserParam(18, "md5"))
        .setHttpListener(new HttpListener<String>() {
            @Override
            public void onSuccess(String data, Response<String> response) {
                Log.i(TAG, "USER: " + data);
            }
        }));
```
上面示范了将UserParam对象转化为URL参数，并得到String响应的方式，注意：

- static final 的字段将被忽略；
- @NonHttpParam 标注的字段将被忽略；
- private 等权限修饰符不影响字段转为参数；
- 值为 null 的对象将被忽略，未赋值的基础数据类型传递默认值；

## 2. json string转为java对象

```java
/**
 * Request Model : json string translate to user object
 */
class UserRequest extends JsonAbsRequest<User> {
    public UserRequest(String url, Http) {
        super(url);
    }
}

// build as http://{userUrl}?id=168&key=md5
UserRequest userRequest = new UserRequest(userUrl, new UserParam(18, "md5"));
userRequest.setHttpListener(new HttpListener<User>() {
    @Override
    public void onSuccess(User user, Response<User> response) {
        // data has been translated to user object
    }
});
liteHttp.executeAsync(userRequest);
```
上面示范了将UserParam对象转化为URL参数，并直接得到User响应对象的方式，注意：

- 参数类 UserParam 实现 HttpParamModel
- 请求类 UserRequest 继承 JsonAbsRequest，当然也可以简化为：
```java
User user = liteHttp.executeAsync(new JsonAbsRequest<User>(userUrl) {});
```
- User类无要求，建议统一继承基类，比如这里的User：
```java
/**
 * base model
 */
public abstract class BaseModel implements Serializable {
}

/**
 * api model： base structure
 */
public class ApiModel<T> extends BaseModel {
    /**
     * 不变的部分：写在API基类中
     */
    private String api;
    private String v;

    protected int code;
    protected String message;

    /**
     * 变化的部分：使用泛型，数据类型的确认延迟到子类里。
     */
    protected T data;

    // getter setter toString 等方法已删减掉...
}

/**
 * response string will be converted to this model
 */
public class User extends ApiModel<User.Data> {

    // 泛型设置：定义一个公共静态内部类（也可以定义外部类）
    public static class Data extends BaseModel{
        public int age;
        public String name;
        public ArrayList<String> girl_friends;
        
        // getter setter toString 等方法已删减掉...
    }
}
```

Model Parameter String 之间的转化比较灵活，请各位玩家多看源码和案例，依据实际需求，挖掘更多玩法。
