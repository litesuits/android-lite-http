package com.litesuits.server;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.*;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

/**
 * Servlet implementation class PostReceiver
 */
@WebServlet(description = "接受Post请求", urlPatterns = { "/PostReceiver" })
public class PostReceiver extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PostReceiver() {
        super();
        // TODO Auto-generated constructor stub
    }

    private void sendRequest(HttpEntity entity){
		try {
			HttpPost post = new HttpPost("http://localhost:8080/HttpServer/PostReceiver?a=1&b=b.34&a=4");
			post.setEntity(entity);
			DefaultHttpClient hc = new DefaultHttpClient();
			hc.execute(post);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	private HttpEntity getMultipartEntity(String path) throws UnsupportedEncodingException, FileNotFoundException{
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("stringKey", new StringBody("StringBody", "text/plain", Charset.forName("utf-8")));
		byte[] bytes = new byte[]{1,2,3};
		entity.addPart("bytesKey", new ByteArrayBody(bytes,"bytesfilename"));
		entity.addPart("fileKey",new FileBody(new File(path+"well.png")));
		entity.addPart("isKey",new InputStreamBody(new FileInputStream(new File(path+"well.png")), "iswell.png"));
		
		return entity;
	}
	private HttpEntity getUrlEncodedFormEntity() throws UnsupportedEncodingException{
		ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("d", "d123"));
		list.add(new BasicNameValuePair("e", "e456"));
		HttpEntity en = new  UrlEncodedFormEntity(list,"utf-8");
		return en;
	}
	
	private HttpEntity getStringEntity(){
		StringEntity se = new StringEntity("哈哈，2013年1月23日 - I'm using Android to send English.","utf-8");
		return se;
	}
	
	@SuppressWarnings("deprecation")
	private HttpEntity getByteArrayEntity() throws UnsupportedEncodingException{
		ByteArrayEntity be = new ByteArrayEntity(new byte[]{1,2,3,4,5,6,7});
		return be;
	}
	
	@SuppressWarnings("deprecation")
	private HttpEntity getFileEntity(String path) throws UnsupportedEncodingException{
		HttpEntity en = new FileEntity(new File(path+"well.png"),"image/jpeg" );
		return en;
	}
	
	@SuppressWarnings("deprecation")
	private HttpEntity getInputStreamEntity(String path) throws UnsupportedEncodingException, FileNotFoundException{
		HttpEntity en = new InputStreamEntity(new FileInputStream(new File(path+"well.png")));
		return en;
	}
	
	@SuppressWarnings("deprecation")
	private HttpEntity getSerializableEntity() throws UnsupportedEncodingException{
		HashMap<String, List<String>> map = new HashMap<>();
		List<String> list1  = new ArrayList<>();
		list1.add("abc");
		list1.add("11");
		list1.add("1110");
		List<String> list2  = new ArrayList<>();
		list1.add("def");
		list1.add("22");
		list1.add("2220");
		map.put("key1" , list1);
		map.put("key2" , list2);
		HttpEntity en = new SerializableEntity(map);
		return en;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getServletContext().getRealPath("/");
		
//		sendRequest(getMultipartEntity(path));
		sendRequest(getUrlEncodedFormEntity());
		
		
//		sendRequest(getStringEntity());
//		sendRequest(getByteArrayEntity());
//		sendRequest(getFileEntity(path));
//		sendRequest(getInputStreamEntity(path));
//		sendRequest(getSerializableEntity());
		
		
//		sendRequest(get);
	}
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "/Users/MaTianyu/Code/";
		
		String contentType = request.getContentType();
		System.out.println("request content type: " + contentType);
		
		// 接受一般参数
		
		Map<String, String[]> map = request.getParameterMap();
		for(Entry<String, String[]> en : map.entrySet()){
			System.out.println("post p "+en.getKey() +" : " + Arrays.toString(en.getValue()));
		}
		
		// 接受文件和流
		if(contentType != null && contentType.startsWith("multipart/form-data")){
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			//向客户端发送响应正文
			PrintWriter writer = response.getWriter();
			try {
				//创建一个基于硬盘的FileItem工厂
				DiskFileItemFactory factory = new DiskFileItemFactory();
				//设置向硬盘写数据时所用的缓冲区的大小，此处为4K
				factory.setSizeThreshold(4 * 1024);
				//设置临时目录
				factory.setRepository(new File(path));
				//创建一个文件上传处理器
				ServletFileUpload upload = new ServletFileUpload(factory);
				
				//设置允许上传的文件的最大尺寸，此处为10M
				upload.setSizeMax(10 * 1024 * 1024);
				Map<String, List<FileItem>> itemMap = upload.parseParameterMap(request);
				for(List<FileItem> items : itemMap.values()){
					Iterator iter = items.iterator();
					while (iter.hasNext()) {
						FileItem item = (FileItem) iter.next();
						if (item.isFormField()) {
							processFormField(item, writer); //处理普通的表单域
						} else {
							processUploadedFile(path, item, writer); //处理上传文件
						}
					}
				}
				writer.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			System.out.println("doPost over");
		}else{
			BufferedReader reader = request.getReader();
			StringBuilder sb = new StringBuilder();
			String line;
			while((line = reader.readLine() )!= null){
				sb.append(line);
				sb.append("\n\r");
			}
			System.out.println("content body: " + sb);
		}
		response.getWriter().print("upload over. ");
	}
	
	private void processFormField(FileItem item, PrintWriter writer) {
		String name = item.getFieldName();
		String value = item.getString();
		
		writer.println("Form Part ["+name + "] value :" + value + "\r\n");
		System.out.println("Form Part ["+name + "] value :" + value + "\r\n");
	}

	private void processUploadedFile(String filePath, FileItem item, PrintWriter writer) throws Exception {
		String filename = item.getName();
		int index = filename.lastIndexOf("\\");
		filename = filename.substring(index + 1, filename.length());
		long fileSize = item.getSize();
		if (filename.equals("") && fileSize == 0) return;
		File uploadFile = new File(filePath + "/" + filename);
		item.write(uploadFile);
		writer.println("File Part ["+filename + "] is saved." +" The size of " + filename + " is " + fileSize + "\r\n");
		System.out.println("File Part ["+filename + "] is saved." +" The size of " + filename + " is " + fileSize + "\r\n");
	}
	
}
