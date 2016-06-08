package com.tuxt.svntool.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 功能：
 * 		1将几个人的要上线代码行文件合并到一个文件中
 * 		2合并单个人的要上线代码行文件到一个文件中
 * 使用treeset：不重复并自带排序
 * @author tuxiantian@163.com
 *
 */
public class CombineCodeLine {
	static TreeSet<String> treeSet=new TreeSet<>();
	/**
	 * 合并一个目录下的几个文件的代码列表
	 * @param file
	 * @throws Exception
	 */
	public static void Combine(File file) throws Exception {
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] listFiles = file.listFiles();
				for (File subfile : listFiles) {
					if (subfile.isFile()) {
						treeSet.addAll(fileToList(subfile));
					} else if (subfile.isDirectory()) {
						Combine(subfile);
					}
				}
			}
		}
	}
	/**
	 * 合并指定文件的代码列表
	 * @param file
	 * @throws Exception
	 */
	public static void CombineSpecified(File file) throws Exception {
		if (file.exists()&&file.isFile()) {
			treeSet.addAll(fileToList(file));
		}
	}
	/**
	 * 代码检查，若有同一个文件的不同版本，则提示该文件版本冲突
	 * @param tree
	 * @return
	 */
	private static boolean checkConflict(Set<String> tree) {
		HashMap<String,String> temp=new HashMap<>();
		int count=0;
		
		for (String string : tree) {
			String str=string.split(",")[0];//路径
			String version=string.split(",")[1];//版本号
			//版本号不同，提示冲突
			if (temp.keySet().contains(str)&&!temp.get(str).equals(version)) {
				System.err.println("版本冲突的文件："+str);
				count++;
			}else {
				temp.put(str, version);
			}
		}
		if (count>0) {
			return false;
		}else {
			return true;
		}
	}
	/**
	 * 按行读取文件中的内容到list，会自动过滤空行，会自动去除路径的前缀"/"
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private static List<String> fileToList(File file) throws Exception {
		List<String> list = new ArrayList<String>();
		BufferedReader rFile = new BufferedReader(new FileReader(file));
		String line = "";

		while ((line = rFile.readLine()) != null) {
			if (!"".equals(line.trim())) {
				if (line.trim().startsWith("/")) {
					list.add(line.trim().substring(1));
				}else {
					list.add(line.trim());
				}
			}
		}
		rFile.close();
		return list;
	}
	/**
	 * 输出treeSet中的代码列表到文件，输出前会进行代码检查，若发现有冲突的文件则不会进行输出
	 * @param filepath
	 * @param set
	 * @param isCheckConflict 在将合并的代码行输出到文件时，是否检查冲突。合并有版本号的代码行可以设为<code>true</code>，合并没有版本号的代码行可以设为<code>false</code>.
	 * @throws FileNotFoundException
	 */
	private static void print2File(String filepath, Set<String> set,boolean isCheckConflict) throws FileNotFoundException {
		if (isCheckConflict) {
			if (!checkConflict(set)) {
				return;
			}
		}
		File print = new File(filepath);
		PrintWriter printWriter = new PrintWriter(print);
		for (String str : set) {
			printWriter.println(str);
		}
		printWriter.close();
	}
	public static void main(String[] args) throws Exception {
		//合并todo文件夹下面的所有人的代码行到total.txt，开启冲突检查
		File file=new File("C:/Users/asus/Desktop/totalcode/todo");
		Combine(file);
		print2File("C:/Users/asus/Desktop/totalcode/total.txt", treeSet,true);
		
		/*File specified=new File("C:/Users/asus/Desktop/codelist/codelist.txt");
		CombineSpecified(specified);
		print2File("C:/Users/asus/Desktop/codelist/combinecodelist.txt", treeSet,false);*/
	}

}
