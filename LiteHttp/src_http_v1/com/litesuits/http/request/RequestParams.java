package com.litesuits.http.request;

import java.io.File;
import java.io.InputStream;

/**
 * define the interface, method, file parameter and input stream parameter, etc.
 * 
 * @author MaTianyu
 * 2014-1-18上午2:40:43
 */
public class RequestParams {

	public static class FileParam {
		public File file;
		public String contentType;

		public FileParam(File file, String contentType) {
			this.file = file;
			this.contentType = contentType;
		}
	}

	public static class InputStreamParam {
		public InputStream inputStream;
		public String name;
		public String contentType;

		public InputStreamParam(InputStream inputStream, String name, String contentType) {
			this.inputStream = inputStream;
			this.name = name;
			this.contentType = contentType;
		}
	}
}
