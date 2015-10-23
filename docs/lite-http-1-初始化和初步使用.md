#Android网络通信框架LiteHttp 第九节：POST方式的多种类型数据传输

标签（空格分隔）： litehttp2.x版本系列教程

---
官网： http://litesuits.com

QQ群： 大群 47357508，二群 42960650

本系列文章面向android开发者，展示开源网络通信框架LiteHttp的主要用法，并讲解其关键功能的运作原理，同时传达了一些框架作者在日常开发中的一些最佳实践和经验。

本系列文章目录总览： https://zybuluo.com/liter/note/186513

---

#第九节：LiteHttp之POST方式的多种类型数据传输

POST方式可以传递大量的数据到服务器，包括图片、音乐、文本等各种多媒体文件，这节主要来说明下lite-http的集中数据传输形式，包括：
> 
- 字符串上传
- UrlEncodedForm上传
- 对象自动转JSON上传
- 对象序列化后上传
- 字节上传
- 单文件上传
- 单输入流上传
- 多文件（表单）上传

话不多讲，看代码：
```java
// POST Multi-Form Data

final ProgressDialog postProgress = new ProgressDialog(this);
postProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
postProgress.setIndeterminate(false);

AlertDialog.Builder builder = new AlertDialog.Builder(this);
builder.setTitle("POST DATA TEST");
String[] array = new String[]{
        "字符串上传",
        "UrlEncodedForm上传",
        "对象自动转JSON上传",
        "对象序列化后上传",
        "字节上传",
        "单文件上传",
        "单输入流上传",
        "多文件（表单）上传"
};

final StringRequest postRequest = new StringRequest(uploadUrl)
        .setMethod(HttpMethods.Post)
        .setHttpListener(new HttpListener<String>(true, false, true) {
            @Override
            public void onStart(AbstractRequest<String> request) {
                super.onStart(request);
                postProgress.show();
            }

            @Override
            public void onUploading(AbstractRequest<String> request, long total, long len) {
                postProgress.setMax((int) total);
                postProgress.setProgress((int) len);
            }

            @Override
            public void onEnd(Response<String> response) {
                postProgress.dismiss();
                if (response.isConnectSuccess()) {
                    HttpUtil.showTips(activity, "Upload Success", response.getResult() + "");
                } else {
                    HttpUtil.showTips(activity, "Upload Failure", response.getException() + "");
                }
                response.printInfo();
            }
        });

builder.setItems(array, new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case 0:
                postRequest.setHttpBody(new StringBody("hello lite: 你好，Lite！"));
                break;
            case 1:
                LinkedList<NameValuePair> pList = new LinkedList<NameValuePair>();
                pList.add(new NameValuePair("key1", "value-haha"));
                pList.add(new NameValuePair("key2", "value-hehe"));
                postRequest.setHttpBody(new UrlEncodedFormBody(pList));
                break;
            case 2:
                postRequest.setHttpBody(new JsonBody(new UserParam(168, "haha-key")));
                break;
            case 3:
                ArrayList<String> list = new ArrayList<String>();
                list.add("a");
                list.add("b");
                list.add("c");
                postRequest.setHttpBody(new SerializableBody(list));
                break;
            case 4:
                postRequest.setHttpBody(new ByteArrayBody(new byte[]{1, 2, 3, 4, 5, 15, 18, 127}));
                break;
            case 5:
                postRequest.setHttpBody(new FileBody(new File("/sdcard/litehttp.txt")));
                break;
            case 6:
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(new File("/sdcard/aaa.jpg"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                postRequest.setHttpBody(new InputStreamBody(fis));
                break;
            case 7:
                fis = null;
                try {
                    fis = new FileInputStream(new File("/sdcard/litehttp.txt"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                MultipartBody body = new MultipartBody();
                body.addPart(new StringPart("key1", "hello"));
                body.addPart(new StringPart("key2", "很高兴见到你", "utf-8", null));
                body.addPart(new BytesPart("key3", new byte[]{1, 2, 3}));
                body.addPart(new FilePart("pic", new File("/sdcard/aaa.jpg"), "image/jpeg"));
                body.addPart(new InputStreamPart("litehttp", fis, "litehttp.txt", "text/plain"));
                postRequest.setHttpBody(body);
                break;
        }

        liteHttp.executeAsync(postRequest);
    }
});
AlertDialog dialog = builder.create();
dialog.setCanceledOnTouchOutside(true);
dialog.show();
```

服务器端接受代码大致如下，以Servlet为例，其他语言自行脑补：
```java
/**
 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
 */
protected void doPost(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {

    //String fileDir = "D:\\Downloads";
    //这是我的Mac笔记本上的位置，开发者设置为合适自己的文件夹，尤其windows系统。
    String fileDir = "/Users/Matianyu/Downloads";

    String contentType = request.getContentType();

    // 接受一般参数

    Map<String, String[]> map = request.getParameterMap();
    if (map.size() > 0) {
        for (Entry<String, String[]> en : map.entrySet()) {
            System.out.println(en.getKey() + " : " + Arrays.toString(en.getValue()));
        }
    }

    response.setContentType("text/plain");
    response.setCharacterEncoding("UTF-8");
    // 接受文件和流

    PrintWriter writer = response.getWriter();
    writer.println("contentType:" + contentType);

    if (contentType != null) {
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
        } else if (contentType.contains("text")
                   || contentType.contains("json")
                   || contentType.contains("application/x-www-form-urlencoded")
                   || contentType.contains("xml")) {
            processString(request);
        } else {
            processEntity(fileDir, request);
        }
    } else {
        processString(request);
    }
    writer.print("upload over. ");
}

private void processString(HttpServletRequest request) {
    try {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            sb.append("\n\r");
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

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
当然，需要引入 apache的开源项目commons-fileupload等jar包。
此节比较枯燥，满眼是代码。
shut up, just show me the code.





