package com.tuxt.svntool.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 检测多个properties文件中是否含有重复的key,过滤掉#注释的配置项.
 * 若配置文件中含有多个相同的key值，默认取第一个key,具体应用中会因此出现问题。
 * @author tuxiantian@163.com
 *
 */
public class CheckPropertiesRepeat {

	public static void main(String[] args) throws IOException {
		String[] pathArr={"",""};
		Set<String> treeSet=new HashSet<>();
		for (int i = 0; i < pathArr.length; i++) {
			List<String> keys=ReadFileUtil.readPropertisKey2List(pathArr[i]);
			for (String string : keys) {
				if (treeSet.contains(string)) {
					System.err.println(pathArr[i]+":"+string);
				}else {
					treeSet.add(string);
				}
			}
		}
	}

}
