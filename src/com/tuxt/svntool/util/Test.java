package com.tuxt.svntool.util;

import java.io.File;
import java.nio.file.Paths;

public class Test {

	public static void main(String[] args) {
		//取得根目录路径  
		String rootPath=Test.class.getResource("/").getFile().toString();  
		//当前目录路径  
		String currentPath1=Test.class.getResource(".").getFile().toString();  
		String currentPath2=Test.class.getResource("").getFile().toString();  
		//当前目录的上级目录路径  
		String parentPath=Test.class.getResource("../").getFile().toString();  
		System.out.println(rootPath);
		System.out.println(currentPath1);
		System.out.println(currentPath2);
		System.out.println(parentPath);
		System.out.println(Test.class.getResource("/").getPath());
		String path=Test.class.getResource("/").getPath();
		System.out.println(Paths.get("E:\\eclipse", "svntool/build/classes").toString());
		System.out.println(new File(path).getParentFile().getParent());  
		Paths.get(new File(path).getParentFile().getParent(), "codelist/codelist.txt");
		System.out.println(new File(new File(path).getParentFile().getParent(), "codelist/codelist.txt").getAbsolutePath()); 
		System.err.println(Constants.PROJECT_PATH);
	}


}
