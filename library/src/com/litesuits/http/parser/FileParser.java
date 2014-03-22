package com.litesuits.http.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.litesuits.android.log.Log;

/**
 * parse inputstream to file.
 * 
 * @author MaTianyu
 *         2014-2-21下午8:56:59
 */
public class FileParser extends DataParser<File> {
	private File file;

	public FileParser(String path) {
		this(new File(path));
	}

	public FileParser(File file) {
		this.file = file;
	}

	@Override
	public File parseData(InputStream stream, int len, String charSet) throws IOException {
		return streamToFile(stream);
	}

	private File streamToFile(InputStream is) throws IOException {
		FileOutputStream fos = null;
		try {
			if (!file.exists() && file.getParentFile() != null) {
				file.getParentFile().mkdirs();
			}
			fos = new FileOutputStream(file);
			final byte[] tmp = new byte[buffSize];
			int l;
			while (!Thread.currentThread().isInterrupted() && (l = is.read(tmp)) != -1) {
				fos.write(tmp, 0, l);
				if (statistics) readLength += l;
			}
			if (Log.isPrint && file != null) Log.i("FileParser", "file len: " + file.length());
		} finally {
			if (fos != null) fos.close();
		}
		return file;
	}
}
