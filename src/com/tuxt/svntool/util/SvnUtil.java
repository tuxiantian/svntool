package com.tuxt.svntool.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * @author jiaotd@asiainfo.com
 * @since 2015年11月25日 上午11:38:52
 */
public class SvnUtil {
	// 定义svn版本库的URL。
	private static SVNURL repositoryURL = null;
	// 定义版本库。
	private static SVNRepository repository = null;

	private static final String url = "svn://192.168.100.4/smrz/src/";
	private static final String name = "tuxt";
	private static final String password = "tu123";
	private static final String localPath = "C:/Users/asus/Desktop/codelist/";

	public static void main(String[] args) throws Exception {
		// 初始化库。 必须先执行此操作。具体操作封装在init方法中。
		setup();
		checkout(new File(localPath));
	}

	public static void checkout(File file) throws Exception {
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] listFiles = file.listFiles();
				for (File subfile : listFiles) {
					if (subfile.isFile()) {
						if (!subfile.getName().startsWith("svn")) {
							checkoutCode(subfile);
						}
					} else if (subfile.isDirectory()) {
						checkout(subfile);
					}
				}
			}
		}
	}

	private static void checkoutCode(File file) throws Exception, FileNotFoundException {
		Set<String> set = new TreeSet<String>();
		List<String> list = fileToList(file);
		for (String codeLine : list) {
			checkoutCode(codeLine, set);
		}
		creatFile(file, set);
	}

	private static void checkoutCode(String codeLine, Set<String> set) {
		// 此变量用来存放要查看的文件的属性名/属性值列表。
		SVNProperties fileProperties = new SVNProperties();

		try {
			// 获得版本库中文件的类型状态（是否存在、是目录还是文件），参数-1表示是版本库中的最新版本。
			SVNNodeKind nodeKind = repository.checkPath(codeLine, -1);

			if (nodeKind == SVNNodeKind.NONE) {
				System.err.println("要查看的文件 '" + codeLine + "'不存在.");
				System.exit(1);
			} else if (nodeKind == SVNNodeKind.DIR) {
				listEntries(repository, codeLine, set);
			} else if (nodeKind == SVNNodeKind.FILE) {
				// 获取要查看文件的内容和属性，结果保存在fileProperties变量中。
				repository.getFile(codeLine, -1, fileProperties, null);
				set.add("/" + codeLine + "," + fileProperties.getStringValue("svn:entry:committed-rev"));
				System.out.println("/" + codeLine + "," + fileProperties.getStringValue("svn:entry:committed-rev"));
			}
		} catch (SVNException svne) {
			System.err.println("在获取文件内容和属性时发生错误: " + svne.getMessage());
			System.exit(1);
		}
	}

	/**
	 * 将文件中的信息转为list
	 * 
	 * @author jiaotd@asiainfo.com
	 * @throws Exception
	 * @since 2015年11月25日 上午10:47:05
	 */
	private static List<String> fileToList(File file) throws Exception {
		List<String> list = new ArrayList<String>();
		BufferedReader rFile = new BufferedReader(new FileReader(file));
		String line = "";

		while ((line = rFile.readLine()) != null) {
			if (!"".equals(line.trim())) {
				list.add(line.trim());
			}
			line = rFile.readLine();

		}
		rFile.close();
		return list;
	}

	/*
	 * 此函数递归的获取版本库中某一目录下的所有条目。
	 */
	public static void listEntries(SVNRepository repository, String path, Set<String> set) throws SVNException {
		// 获取版本库的path目录下的所有条目。参数－1表示是最新版本。
		Collection<?> entries = repository.getDir(path, -1, null, (Collection<?>) null);
		Iterator<?> iterator = entries.iterator();
		while (iterator.hasNext()) {
			SVNDirEntry entry = (SVNDirEntry) iterator.next();
			set.add("/" + path + entry.getName() + "," + entry.getRevision());
			System.out.println("/" + path + entry.getName() + "," + entry.getRevision());
			/*
			 * 检查此条目是否为目录，如果为目录递归执行
			 */
			if (entry.getKind() == SVNNodeKind.NONE) {
				System.err.println("要查看的文件 '" + (path + "/") + "'不存在.");
				System.exit(1);
			} else if (entry.getKind() == SVNNodeKind.DIR) {
				listEntries(repository, (path.equals("")) ? entry.getName() : path + "/" + entry.getName(), set);
			}
		}
	}

	private static void creatFile(File file, Set<String> set) throws FileNotFoundException {
		File print = new File(file.getParent() + "/svn" + file.getName());
		PrintWriter printWriter = new PrintWriter(print);
		for (String str : set) {
			printWriter.println(str);
		}
		printWriter.close();
	}

	/*
	 * 初始化库
	 */
	private static void setup() {
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
		try {
			// 获取SVN的URL。
			repositoryURL = SVNURL.parseURIEncoded(url);
			// 根据URL实例化SVN版本库。
			repository = SVNRepositoryFactory.create(repositoryURL);
		} catch (SVNException svne) {
			System.err.println("创建版本库实例时失败，版本库的URL是 '" + url + "': " + svne.getMessage());
			System.exit(1);
		}

		/*
		 * 对版本库设置认证信息。
		 */
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
		repository.setAuthenticationManager(authManager);
	}
}
