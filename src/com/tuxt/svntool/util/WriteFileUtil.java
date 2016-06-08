package com.tuxt.svntool.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Set;


public class WriteFileUtil {
	/**
	 * A方法追加文件：使用RandomAccessFile 追加到尾部
	 * 
	 * @param fileName
	 *            文件名
	 * @param content
	 *            追加的内容
	 * @throws IOException 
	 */
	public static void appendFile(String fileName, String content) throws IOException {
		try {
			RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 将写文件指针移到文件尾。
			randomFile.seek(fileLength);
			randomFile.write(content.getBytes()); 
			randomFile.close();
		} catch (IOException e) {
		}
	}
	
	/**
	 * 将内容一次写入文件
	 * 
	 * @param filename
	 *            文件名称
	 * @param content
	 *            文件内容
	 * @return
	 */
	public static void writeFileSingle(String filepath, String content)  throws Exception{
		if(!"".equals(content)){
			try {
				// 第一步先判断存放文件的临时目录是否存在
				// 第二步判断该文件是否存在
				File file = createTempFile(filepath);
				// 第三步将内容一次写入文件
				FileWriter fileWriter = new FileWriter(file);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(content);
				bufferedWriter.close();
				fileWriter.close();
			} catch (Exception e) {
				throw new Exception();
			}
		}
	}
	public static void writeFileFromTreeSet(String filepath, Set<String> set) throws FileNotFoundException{
		File print = new File(filepath);
		PrintWriter printWriter = new PrintWriter(print);
		for (String str : set) {
			printWriter.println(str);
		}
		printWriter.close();
	}
	/**
	 * 创建临时文件
	 * 
	 * @param filePath
	 * @return
	 */
	public static File createTempFile(String filepath) throws Exception{
		File file = null;
		try {
			file = new File(filepath);
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (Exception e) {
			throw new Exception();
		}
		return file;
	}
	public static void main(String[] args) throws Exception {
		//writeFileSingle("D:\\test1\\BOSSZZZ_BATCHVERIFYYYYYMMDDHHMISS_10022285_CXXX_back", "11111111111");
		createTempFile("D://371/test.txt");
	}
}