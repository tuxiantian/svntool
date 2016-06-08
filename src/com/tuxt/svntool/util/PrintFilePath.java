package com.tuxt.svntool.util;

import java.io.File;

public class PrintFilePath {
	/**
	 * 递归输出某个目录下的所有文件路径
	 * 应用场景：rnms项目上线代码列表是一个目录下所有文件的路径，手动获取文件路径很麻烦，于是就有了这个方法
	 * @param file
	 */
	public static void getAllFilePath(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] listFiles = file.listFiles();
				for (File subfile : listFiles) {
					if (subfile.isFile()) {
						System.out.println(subfile.getPath().split("E:\\\\smznew\\\\")[1].replaceAll("\\\\", "/"));
					}else if (subfile.isAbsolute()) {
						getAllFilePath(subfile);
					}
				}
			}
		}
	}
	public static void main(String[] args) {
		String pathname="E:/smznew/rnms/src/main/webapp/css/images/activate";
		File file=new File(pathname);
		getAllFilePath(file);
	}

}
