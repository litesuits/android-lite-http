package com.litesuits.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class ReceiveFile
 */
public class ReceiveFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String filePath;//存放上传文件的目录
	private String tempFilePath;//存放临时文件的目录

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReceiveFile() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		filePath = "E:/upload";
		System.out.println("filePath1 " + filePath);
		tempFilePath = "E:/upload/temp";
		System.out.println("tempFilePath1 " + tempFilePath);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("doget");
		response.getWriter().println("hello world");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		System.out.println("doPost");
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		//向客户端发送响应正文
		PrintWriter outNet = response.getWriter();
		try {
			//创建一个基于硬盘的FileItem工厂
			DiskFileItemFactory factory = new DiskFileItemFactory();
			//设置向硬盘写数据时所用的缓冲区的大小，此处为4K
			factory.setSizeThreshold(4 * 1024);
			//设置临时目录
			factory.setRepository(new File(tempFilePath));
			//创建一个文件上传处理器
			ServletFileUpload upload = new ServletFileUpload(factory);
			//设置允许上传的文件的最大尺寸，此处为4M
			upload.setSizeMax(4 * 1024 * 1024);
			List items = upload.parseRequest(request);
			Iterator iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				if (item.isFormField()) {
					processFormField(item, outNet); //处理普通的表单域
				} else {
					processUploadedFile(item, outNet); //处理上传文件
				}
			}
			outNet.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("doPost over");
		response.getWriter().print("upload over. ");
	}

	private void processFormField(FileItem item, PrintWriter writer) {
		String name = item.getFieldName();
		String value = item.getString();
		writer.println(name + ":" + value + "\r\n");
	}

	private void processUploadedFile(FileItem item, PrintWriter writer) throws Exception {
		String filename = item.getName();
		int index = filename.lastIndexOf("\\");
		filename = filename.substring(index + 1, filename.length());
		long fileSize = item.getSize();
		if (filename.equals("") && fileSize == 0) return;
		File uploadFile = new File(filePath + "/" + filename);
		item.write(uploadFile);
		writer.println(filename + "is saved.");
		writer.println("The size of " + filename + " is " + fileSize + "\r\n");
	}
}
