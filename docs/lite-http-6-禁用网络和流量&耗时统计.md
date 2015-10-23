#Android网络通信框架LiteHttp 第六节：禁用网络和流量&耗时统计

标签（空格分隔）： litehttp2.x版本系列教程

---
官网： http://litesuits.com

QQ群： 大群 47357508，二群 42960650

本系列文章面向android开发者，展示开源网络通信框架LiteHttp的主要用法，并讲解其关键功能的运作原理，同时传达了一些框架作者在日常开发中的一些最佳实践和经验。

本系列文章目录总览： https://zybuluo.com/liter/note/186513

---

#第六节：LiteHttp之禁用网络和流量&耗时统计

## 1. 禁用网络

开发者可以通过lite-http的config来设置禁用某种网络，比如我们将移动网络和wifi都禁用：
```java
// disable some network
HttpConfig config = liteHttp.getConfig();

// must set context
config.setContext(activity);

// disable mobile(2G/3G/4G) and wifi network
config.setDisableNetworkFlags(HttpConfig.FLAG_NET_DISABLE_MOBILE | HttpConfig.FLAG_NET_DISABLE_WIFI);

liteHttp.executeAsync(new StringRequest(url).setHttpListener(new HttpListener<String>() {
    @Override
    public void onSuccess(String s, Response<String> response) {
        HttpUtil.showTips(activity, "LiteHttp2.0", s);
    }

    @Override
    public void onFailure(HttpException e, Response<String> response) {
        HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
    }
}));
```
这样不管是sim卡网络还是无线wifi下发送请求都会失败，onFailure会触发。
恢复可用状态：
```java
// enable network
HttpConfig config = liteHttp.getConfig();

// enable all  
config.setDisableNetworkFlags(HttpConfig.FLAG_NET_DISABLE_NONE);
```

## 2. 流量和耗时统计

只需要从配置打开统计开关即可：
```java
// Traffic/Time Statistics
String picUrl = "http://pic.33.la/20140403sj/1638.jpg";
   
// turn on
liteHttp.getConfig().setDoStatistics(true);

liteHttp.executeAsync(new FileRequest(picUrl).setHttpListener(new HttpListener<File>() {
    @Override
    public void onSuccess(File file, Response<File> response) {
        String msg = "This request take time:" + response.getUseTime()
                     + ", readed length:" + response.getReadedLength();
        msg += "  Global " + liteHttp.getStatisticsInfo();
        HttpUtil.showTips(activity, "LiteHttp2.0", msg);
    }

    @Override
    public void onFailure(HttpException e, Response<File> response) {
        HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
    }
}));
```
更多数据统计：
> 
response.getRedirectTimes();  // 重定向的次数
response.getRetryTimes();     // 重试的次数
response.getUseTime();        // 本次请求耗时
response.getContentLength();  // header中的数据长度（Content-Length）
response.getReadedLength();   // 实际读取的数据长度

整体数据统计：
> 
StatisticsInfo sta = liteHttp.getStatisticsInfo();
sta.getConnectTime();         // litehttp 实例化后所有请求耗时累计
sta.getDataLength();          // litehttp 实例化后读取数据长度累计

