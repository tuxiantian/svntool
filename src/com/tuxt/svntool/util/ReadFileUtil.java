package com.tuxt.svntool.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ReadFileUtil {
	/**
	 * 按行读取文件中的内容放入List 去除路径的前缀‘/’
	 */
	public static List<String> readFile2List(String filePath) throws Exception {
		File file=new File(filePath);
		if (!file.exists()) {
			System.out.println(filePath+"不存在");
			return null;
		}
		List<String> list = new ArrayList<String>();
		InputStream is = new FileInputStream(file);// Thread.currentThread().getContextClassLoader().getResourceAsStream("test/"+fileName);
		InputStreamReader reader = new InputStreamReader(is, "gb2312");

		BufferedReader rFile = new BufferedReader(reader);
		String line = rFile.readLine();

		while (line != null) {
			line=line.trim();
			if (!"".equals(line)) {
				if (line.startsWith("/")) {
					list.add(line.substring(1));
				}else {
					list.add(line);
				}
			}
			line = rFile.readLine();
		}
		rFile.close();
		return list;
	}
	/**
	 * 读取propertis文件中的key到list集合中 不包含注释掉的配置项
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static List<String> readPropertisKey2List(String filePath) throws IOException {
		File file=new File(filePath);
		if (!file.exists()) {
			System.out.println(filePath+"不存在");
			return null;
		}
		List<String> list = new ArrayList<String>();
		InputStream is = new FileInputStream(file);// Thread.currentThread().getContextClassLoader().getResourceAsStream("test/"+fileName);
		InputStreamReader reader = new InputStreamReader(is, "gb2312");

		BufferedReader rFile = new BufferedReader(reader);
		String line = rFile.readLine();

		while (line != null) {
			line=line.trim();
			if (!"".equals(line)) {
				if (!line.startsWith("#")) {
					list.add(line.split("=")[0]);
				}
			}
			line = rFile.readLine();
		}
		rFile.close();
		return list;
	}
	/**
	 * 按行读取文件中的内容放入Set 去除路径的前缀‘/’
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static Set<String> readFile2Set(String filePath) throws Exception {
		File file=new File(filePath);
		if (!file.exists()) {
			System.out.println(filePath+"不存在");
			return null;
		}
		Set<String> set = new TreeSet<String>();
		InputStream is = new FileInputStream(file);// Thread.currentThread().getContextClassLoader().getResourceAsStream("test/"+fileName);
		InputStreamReader reader = new InputStreamReader(is, "gb2312");

		BufferedReader rFile = new BufferedReader(reader);
		String line = rFile.readLine();

		while (line != null) {
			line=line.trim();
			if (!"".equals(line)) {
				if (line.startsWith("/")) {
					set.add(line.substring(1));
				}else {
					set.add(line);
				}
			}
			line = rFile.readLine();
		}
		rFile.close();
		return set;
	}
	/**
	 * 读取代码行列表到Map.
	 * key:路径
	 * value:版本号
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Long> readFile2Map(String filePath) throws Exception {
		File file=new File(filePath);
		if (!file.exists()) {
			System.out.println(filePath+"不存在");
			return null;
		}
		Map<String, Long> map = new HashMap<String, Long>();
		InputStream is = new FileInputStream(file);// Thread.currentThread().getContextClassLoader().getResourceAsStream("test/"+fileName);
		InputStreamReader reader = new InputStreamReader(is, "gb2312");

		BufferedReader rFile = new BufferedReader(reader);
		String line = rFile.readLine();

		while (line != null) {
			if (!"".equals(line.trim())) {
				if (line.trim().startsWith("/")) {
					line=line.substring(1);
				}
				map.put(line.split(",")[0],Long.parseLong(line.split(",")[1]));
			}
			line = rFile.readLine();

		}
		rFile.close();
		return map;
	}
}
