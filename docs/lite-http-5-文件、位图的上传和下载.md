#Android网络通信框架LiteHttp 第五节：文件、位图的上传和下载

标签（空格分隔）： litehttp2.x版本系列教程

---
官网： http://litesuits.com

QQ群： 大群 47357508，二群 42960650

本系列文章面向android开发者，展示开源网络通信框架LiteHttp的主要用法，并讲解其关键功能的运作原理，同时传达了一些框架作者在日常开发中的一些最佳实践和经验。

本系列文章目录总览： https://zybuluo.com/liter/note/186513

---

#第五节：LiteHttp之文件、位图的上传和下载

## 1. 文件下载

先准备一张网络图片，比如：
```java
  public static final String picUrl = "http://pic.33.la/20140403sj/1638.jpg";
```

然后下载之
> 第一步：
liteHttp.executeAsync(new FileRequest(picUrl, "sdcard/aaa.jpg"));

> 第二步：
不好意思，已经下载好了。

下载位图并监听进度
```java
liteHttp.executeAsync(
        new BitmapRequest(picUrl).setHttpListener(
        new HttpListener<Bitmap>(true, true, false) {
            @Override
            public void onLoading(AbstractRequest<Bitmap> request, long total, long len) {
                HttpLog.i(TAG, total + "  total   " + len + " len");
            }

            @Override
            public void onSuccess(Bitmap bitmap, Response<Bitmap> response) {
                downProgress.dismiss();
                AlertDialog.Builder b = HttpUtil.dialogBuilder(activity, "LiteHttp2.0", "");
                ImageView iv = new ImageView(activity);
                iv.setImageBitmap(bitmap);
                b.setView(iv);
                b.show();
            }

            @Override
            public void onFailure(HttpException e, Response<Bitmap> response) {
                HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
            }
        }));
```
迅雷不及掩耳之势，别停，继续...

## 2. 单文件上传（application/octet-stream）
为方便测试，我在SD卡上放置一张名为aaa.jpg的图片，然后开撸：
```java
// 替换自己的服务器地址
public static final String uploadUrl = "http://192.168.0.0:8080/upload";

HttpListener uploadListener = new HttpListener<String>(true, false, true) {
    @Override
    public void onSuccess(String s, Response<String> response) {
        response.printInfo();
    }

    @Override
    public void onFailure(HttpException e, Response<String> response) {
        response.printInfo();
    }

    @Override
    public void onUploading(AbstractRequest<String> request, long total, long len) {
    }
};

final StringRequest upload = new StringRequest(uploadUrl)
        .setMethod(HttpMethods.Post)
        .setHttpListener(uploadListener)
        .setHttpBody(new FileBody(new File("/sdcard/aaa.jpg")));
        
liteHttp.executeAsync(upload);
```

通过流上传：
```java
// uploadListener上面已经定义，不在重复写

FileInputStream fis = null;
try {
    fis = new FileInputStream(new File("/sdcard/aaa.jpg"));
} catch (Exception e) {
    e.printStackTrace();
}

final StringRequest upload = new StringRequest(uploadUrl)
        .setMethod(HttpMethods.Post)
        .setHttpListener(uploadListener)
        .setHttpBody(new InputStreamBody(fis));
        
liteHttp.executeAsync(upload);
```
**服务器端** 可以这样接收 **单个文件** 的上传：
```java
private void processEntity(String dir, HttpServletRequest request) {
    try {
        File uploadFile = new File(dir + "a-upload");
        InputStream input = request.getInputStream();
        OutputStream output = new FileOutputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int n = -1;
        while ((n = input.read(buffer)) != -1) {
            if (n > 0) {
                output.write(buffer, 0, n);
            }
        }
        output.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```
如果是 Servlet ，那么在 doPost 中合适时机调用此方法即可，文件夹位置根据实际情况设置，比如测试时windows系统设置dir = "D:\\Downloads", Mac系统设置dir = "/Users/.../Downloads"。由于MD文件的格式化可能导致斜杠不显示，注意斜杠方向和数量要正确。

另外，服务器需要apache提供的jar包支持接收文件，比如：commons-fileupload-1.3.1.jar 等。

上面就是单文件上传的两种方式，mimetype为application/octet-stream，还有一种文件、表单混合，并且可以传多文件的上传方式，mimetype为multipart/form-data，即：表单上传。

## 2. 表单（多文件）上传（multipart/form-data）

多文件上传，我又在SD卡下面又放了一个文本文件 litehttp.txt：
```java
// litehttp.txt使用流上传；aaa.jpg使用文件式上传。
fis = null;
try {
    fis = new FileInputStream(new File("/sdcard/litehttp.txt"));
} catch (Exception e) {
    e.printStackTrace();
}
                                        
MultipartBody body = new MultipartBody();
body.addPart(new StringPart("key1", "hello"))
    .addPart(new StringPart("key2", "很高兴见到你", "utf-8", null))
    .addPart(new BytesPart("key3", new byte[]{1, 2, 3}))
    .addPart(new FilePart("pic", new File("/sdcard/aaa.jpg"), "image/jpeg"))
    .addPart(new InputStreamPart("litehttp", fis, "litehttp.txt", "text/plain"));

// uploadListener上面已经定义，不在重复写
final StringRequest upload = new StringRequest(uploadUrl)
        .setMethod(HttpMethods.Post)
        .setHttpListener(uploadListener)
        .setHttpBody(body);
        
liteHttp.executeAsync(upload);
```

**服务器端** 可以这样接收 **多个文件** 的上传：
```java
// String fileDir = "D:\\Downloads";
// 这是我的Mac笔记本上的位置，开发者设置为合适自己的文件夹，尤其windows系统。
String fileDir = "/Users/.../Downloads";
        
String contentType = request.getContentType();
if (contentType.startsWith("multipart")) {
    //向客户端发送响应正文
    try {
        //创建一个基于硬盘的FileItem工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //设置向硬盘写数据时所用的缓冲区的大小，此处为4K
        factory.setSizeThreshold(4 * 1024);
        //设置临时目录
        factory.setRepository(new File(fileDir));
        //创建一个文件上传处理器
        ServletFileUpload upload = new ServletFileUpload(factory);

        //设置允许上传的文件的最大尺寸，此处为100M
        upload.setSizeMax(100 * 1024 * 1024);
        Map<String, List<FileItem>> itemMap = upload.parseParameterMap(request);
        for (List<FileItem> items : itemMap.values()) {
            Iterator iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (item.isFormField()) {
                    processFormField(item, writer); //处理普通的表单域
                } else {
                    processUploadedFile(fileDir, item, writer); //处理上传文件
                }
            }
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}

/**
 * 处理表单字符数据
 */
private void processFormField(FileItem item, PrintWriter writer) {
    String name = item.getFieldName();
    String value = item.getString();

    writer.println("Form Part [" + name + "] value :" + value + "\r\n");
}

/**
 * 处理表单文件
 */
private void processUploadedFile(String filePath, FileItem item, PrintWriter writer) throws Exception {
    String filename = item.getName();
    int index = filename.lastIndexOf("\\");
    filename = filename.substring(index + 1, filename.length());
    long fileSize = item.getSize();
    if (filename.equals("") && fileSize == 0)
        return;
    File uploadFile = new File(filePath + "/" + filename);
    item.write(uploadFile);
    writer.println(
            "File Part [" + filename + "] is saved." + " contentType: " + item
                    .getContentType() + " , size: " + fileSize + "\r\n");
}
```

好了，到此为止下载和上传都搞定了，喝杯水休息下...