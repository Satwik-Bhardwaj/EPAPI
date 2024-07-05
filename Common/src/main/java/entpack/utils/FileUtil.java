package entpack.utils;

import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 文件工具类
 */
public class FileUtil {

	private static final Log log = Log.getLog(ClassUtil.class);

	/**
	 * 从网络Url中下载文件
	 *
	 * @param urlStr   文件地址
	 * @param savePath 儲存的目录
	 * @throws IOException
	 */
	public static String downLoadFromUrl(String urlStr, String savePath) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		//设置超时间为30秒
		conn.setConnectTimeout(30 * 1000);
		//防止屏蔽程序抓取而返回403错误
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.78 Safari/537.36");

		//得到输入流
		InputStream inputStream = conn.getInputStream();
		//获取自己数组
		byte[] getData = readInputStream(inputStream);

		//文件儲存位置
		File saveDir = new File(savePath);
		if (!saveDir.exists()) {
			saveDir.mkdir();
		}
		String fileName = urlStr.substring(urlStr.lastIndexOf("/") + 1);
		File file = new File(saveDir + File.separator + fileName);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(getData);
		if (fos != null) {
			fos.close();
		}
		if (inputStream != null) {
			inputStream.close();
		}

		System.out.println("info:" + url + " download success");
		return file.getPath();
	}


	/**
	 * 从输入流中获取字节数组
	 *
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}

	public static String getSuffix(String fileName) {
		if (fileName != null && fileName.contains(".")) {
			return fileName.substring(fileName.lastIndexOf("."));
		}
		return null;
	}


	public static String removePrefix(String src, String prefix) {
		if (src != null && src.startsWith(prefix)) {
			return src.substring(prefix.length());
		}
		return src;
	}


	public static String removeRootPath(String src) {
		return removePrefix(src, PathKit.getWebRootPath());
	}

	public static String readString(File file) {
		ByteArrayOutputStream baos = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			for (int len = 0; (len = fis.read(buffer)) > 0; ) {
				baos.write(buffer, 0, len);
			}
			return new String(baos.toByteArray(), JFinal.me().getConstants().getEncoding());
		} catch (Exception e) {
		} finally {
			close(fis, baos);
		}
		return null;
	}

	public static void writeString(File file, String string) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file, false);
			fos.write(string.getBytes(JFinal.me().getConstants().getEncoding()));
		} catch (Exception e) {
		} finally {
			close(null, fos);
		}
	}

	private static void close(InputStream is, OutputStream os) {
		if (is != null)
			try {
				is.close();
			} catch (IOException e) {
			}
		if (os != null)
			try {
				os.close();
			} catch (IOException e) {
			}
	}

	public static void unzip(String zipFilePath) throws IOException {
		String targetPath = zipFilePath.substring(0, zipFilePath.lastIndexOf("."));
		unzip(zipFilePath, targetPath);
	}

	public static void unzip(String zipFilePath, String targetPath) throws IOException {
		ZipFile zipFile = new ZipFile(zipFilePath);
		try {
			Enumeration<?> entryEnum = zipFile.entries();
			if (null != entryEnum) {
				while (entryEnum.hasMoreElements()) {
					OutputStream os = null;
					InputStream is = null;
					try {
						ZipEntry zipEntry = (ZipEntry) entryEnum.nextElement();
						if (!zipEntry.isDirectory()) {
							File targetFile = new File(targetPath + File.separator + zipEntry.getName());
							if (!targetFile.getParentFile().exists()) {
								targetFile.getParentFile().mkdirs();
							}
							os = new BufferedOutputStream(new FileOutputStream(targetFile));
							is = zipFile.getInputStream(zipEntry);
							byte[] buffer = new byte[4096];
							int readLen = 0;
							while ((readLen = is.read(buffer, 0, 4096)) > 0) {
								os.write(buffer, 0, readLen);
							}
						}
					} finally {
						if (is != null)
							is.close();
						if (os != null)
							os.close();
					}
				}
			}
		} finally {
			zipFile.close();
		}
	}

	/**
	 * 生成文件流
	 *
	 * @param inputStream 文件流
	 * @param targetFile  目标文件
	 */
	public static void createFileByInputStream(InputStream inputStream, File targetFile) {
		try {
			FileOutputStream fout = new FileOutputStream(targetFile);

			byte[] b = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(b)) != -1) {
				fout.write(b, 0, len);
			}

			fout.flush();
			inputStream.close();
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成文件
	 *
	 * @param tmpFileName 文件名
	 * @param tmpFileExt  扩展名
	 * @param inputStream 文件流
	 * @return 临时文件的绝对路径
	 */
	public static String createFileToTmp(String tmpFileName, String tmpFileExt, InputStream inputStream) {
		if (StringUtil.areNotEmpty(tmpFileName, tmpFileExt) && inputStream != null) {
			try {
				File tmpFile = File.createTempFile(tmpFileName, tmpFileExt);
				FileOutputStream fout = new FileOutputStream(tmpFile);

				byte[] b = new byte[1024];
				int len = 0;
				while ((len = inputStream.read(b)) != -1) {
					fout.write(b, 0, len);
				}

				fout.flush();
				inputStream.close();
				fout.close();

				return tmpFile.getAbsolutePath();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 刪除文件
	 *
	 * @param path 路径
	 * @return 返回布尔值表示刪除是否成功
	 */
	public static boolean delFile(String path) throws IOException {
		try {
			File file = new File(path);
			// 路径为文件且不为空则进行刪除
			if (file.isFile() && file.exists()) {
				return file.delete();
			}
			return false;
		} catch (Exception ex) {
			log.error("刪除文件出错", ex);
			return false;
		}
	}

	/**
	 * 递归刪除目录（包括子目录和文件）
	 *
	 * @param path 路径
	 * @return 返回布尔值表示刪除是否成功
	 * @throws IOException
	 */
	public static boolean delFolder(String path) throws IOException {
		try {
			//如果path不以文件分隔符结尾，自动新增文件分隔符
			if (!path.endsWith(File.separator)) {
				path = path + File.separator;
			}
			File dirFile = new File(path);
			//如果dir对应的文件不存在，或者不是一个目录，则退出
			if (!dirFile.exists() || !dirFile.isDirectory()) {
				return false;
			}
			boolean flag = true;
			//刪除文件夹下的所有文件(包括子目录)
			File[] files = dirFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					//刪除子文件
					flag = delFile(files[i].getAbsolutePath());
					if (!flag) break;
				} else {
					//刪除子目录
					flag = delFolder(files[i].getAbsolutePath());
					if (!flag) break;
				}
			}
			if (!flag) return false;
			//刪除当前目录
			return dirFile.delete();
		} catch (IOException ex) {
			log.error("刪除目录出错", ex);
			return false;
		}
	}

	/**
	 * 复制文件
	 *
	 * @param source 源文件
	 * @param target 目标文件
	 */
	public static void copyFile(File source, File target) {
		try {
			copyFile(new FileInputStream(source), new FileOutputStream(target));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 复制文件流
	 *
	 * @param source 源文件流
	 * @param target 目标文件流
	 */
	public static void copyFile(FileInputStream source, FileOutputStream target) {
		try {
			FileChannel in = null;
			FileChannel out = null;

			try {
				in = source.getChannel();
				out = target.getChannel();
				in.transferTo(0, in.size(), out);
			} finally {
				source.close();
				in.close();
				target.close();
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取文件名的后缀
	 *
	 * @param fileName 文件名
	 * @return 文件名的后缀或null
	 */
	public static String getFileExt(String fileName) {
		if (fileName == null || fileName.trim().equals(""))
			return null;
		int dot = fileName.lastIndexOf('.');
		if (dot == -1 || dot + 1 == fileName.length())
			return null;

		return fileName.substring(dot + 1);
	}

	/**
	 * 修改文件后缀
	 *
	 * @param fileName 文件名
	 * @param ext      后缀
	 * @return 返回新文件名
	 */

	public static String changeFileExt(String fileName, String ext) {
		if (fileName == null || fileName.trim().equals(""))
			return null;
		int dot = fileName.lastIndexOf('.');
		if (dot == -1 || dot + 1 == fileName.length()) return null;

		String fileNameStr = fileName.substring(0, dot + 1);
		return fileNameStr + ext;
	}

	public static final List<String> fileList = new ArrayList<>();

	/**
	 * 获取某路径下的所有文件的绝对路径
	 *
	 * @param path 绝对路径
	 */
	public static void getAllFiles(String path) {
		File dir = new File(path);
		File[] files = dir.listFiles();

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					getAllFiles(files[i].getAbsolutePath());
				} else {
					fileList.add(files[i].getAbsolutePath());
				}
			}
		}
	}
}