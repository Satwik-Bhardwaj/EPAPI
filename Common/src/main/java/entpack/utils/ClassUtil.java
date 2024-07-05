package entpack.utils;

import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtil {

	private static final Log log = Log.getLog(ClassUtil.class);

	/**
	 * 获取全局配置类对象
	 *
	 * @param className 配置类
	 * @return 返回对象
	 */
	public static Map<String, Object> GetConfigVal(String className) {
		Map<String, Object> map = new HashMap<>();
		Class constantClass = null;
		try {
			//动态读取全卷常量的属性和值
			constantClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (constantClass != null && constantClass.getFields().length > 0) {
			for (Field field : constantClass.getFields()) {
				try {
					map.put(field.getName(), field.get(field.getName()));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}

	public static <T> List<Class<T>> scanSubClass(Class<T> pclazz) {
		return scanSubClass(pclazz, false);
	}

	public static <T> List<Class<T>> scanSubClass(Class<T> pclazz, boolean mustbeCanNewInstance) {
		if (pclazz == null) {
			log.error("scanClass: parent clazz is null");
			return null;
		}

		List<File> classFileList = new ArrayList<File>();
		scanClass(classFileList, PathKit.getRootClassPath());

		List<Class<T>> classList = new ArrayList<Class<T>>();
		for (File file : classFileList) {

			int start = PathKit.getRootClassPath().length();
			int end = file.toString().length() - 6; // 6 == ".class".length();

			String classFile = file.toString().substring(start + 1, end);
			Class<T> clazz = classForName(classFile.replace(File.separator, "."));

			if (clazz != null && pclazz.isAssignableFrom(clazz)) {
				if (mustbeCanNewInstance) {
					if (clazz.isInterface())
						continue;

					if (Modifier.isAbstract(clazz.getModifiers()))
						continue;
				}
				classList.add(clazz);
			}
		}

		File jarsDir = new File(PathKit.getWebRootPath() + "/WEB-INF/lib");
		if (jarsDir.exists() && jarsDir.isDirectory()) {
			File[] jarFiles = jarsDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					String name = pathname.getName().toLowerCase();
					return name.endsWith(".jar") && name.startsWith("jpress");
				}
			});

			if (jarFiles != null && jarFiles.length > 0) {
				for (File f : jarFiles) {
					classList.addAll(scanSubClass(pclazz, f, mustbeCanNewInstance));
				}
			}
		}

		return classList;
	}

	public static <T> List<Class<T>> scanSubClass(Class<T> pclazz, File f, boolean mustbeCanNewInstance) {
		if (pclazz == null) {
			log.error("scanClass: parent clazz is null");
			return null;
		}

		JarFile jarFile = null;

		try {
			jarFile = new JarFile(f);
			List<Class<T>> classList = new ArrayList<Class<T>>();
			Enumeration<JarEntry> entries = jarFile.entries();

			while (entries.hasMoreElements()) {
				JarEntry jarEntry = entries.nextElement();
				String entryName = jarEntry.getName();
				if (!jarEntry.isDirectory() && entryName.endsWith(".class")) {
					String className = entryName.replace("/", ".").substring(0, entryName.length() - 6);
					Class<T> clazz = classForName(className);
					if (clazz != null && pclazz.isAssignableFrom(clazz)) {
						if (mustbeCanNewInstance) {
							if (clazz.isInterface())
								continue;

							if (Modifier.isAbstract(clazz.getModifiers()))
								continue;
						}
						classList.add(clazz);
					}
				}
			}

			return classList;

		} catch (IOException e1) {
		} finally {
			if (jarFile != null)
				try {
					jarFile.close();
				} catch (IOException e) {
				}
		}

		return null;

	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> classForName(String className) {
		Class<T> clazz = null;
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			clazz = (Class<T>) Class.forName(className, false, cl);
		} catch (Throwable e) {
			log.error("classForName is error，className:" + className);
		}
		return clazz;
	}

	private static void scanClass(List<File> fileList, String path) {
		File files[] = new File(path).listFiles();
		if (null == files || files.length == 0)
			return;
		for (File file : files) {
			if (file.isDirectory()) {
				scanClass(fileList, file.getAbsolutePath());
			} else if (file.getName().endsWith(".class")) {
				fileList.add(file);
			}
		}
	}

}