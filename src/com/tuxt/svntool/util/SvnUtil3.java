package com.tuxt.svntool.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * 这个工具类目前的配置是针对ol_java,直接对类运行测试会先获取提交的文件的路径写入文件，然后读取文件中的代码行获得版本号写入新的文件
 * @author jiaotd@asiainfo.com
 * @since 2015年11月25日 上午11:38:52
 */
public class SvnUtil3 {
	// 定义svn版本库的URL。
	private static SVNURL repositoryURL = null;
	// 定义版本库。
	private static SVNRepository repository = null;

	private static final String url = "svn://192.168.100.10/smrznew/src/";
	private static final String name = "tuxt";
	private static final String password = "tu123";
	private static final String startDate = "2016-6-8 00:00:00";
	private static final String endDate = "2016-6-8 22:10:02";

	private static String codeLineExclusiveVersionFile =Paths.get(Constants.PROJECT_PATH, "codelist/codelist.txt").toString();
	private static String codeLineIncludeVersionFile = Paths.get(Constants.PROJECT_PATH, "codelist/svncodelist.txt").toString();
	private static final String codeLineProjectPath = "E:/myeclipse/";//代码行所在项目的本地路经，用于过滤文件夹
	private static final String codeLineProjectName="ol_java";
	@BeforeClass
	public static void beforeClass(){
		setup();
	}
	@Test
	public void testGetSvnLogList() throws Exception{
		getSvnLogList(false);
	}
	
	@Test
	public void testGetLastSvnVersion() throws FileNotFoundException, Exception {
		getLastSvnVersion();
	}

	/**
	 * 获取代码列表的最新版本
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	public void getLastSvnVersion() throws Exception, FileNotFoundException {
		File file = new File(codeLineExclusiveVersionFile);
		Set<String> set = new TreeSet<String>();
		List<String> list = fileToList(file);
		for (String codeLine : list) {
			checkoutCode(codeLine, set);
		}
		//写入svncodelist.txt
		creatFile(set);
	}
	/**
	 * 获取一段时间内提交的代码列表
	 * @throws Exception
	 */
	public void getSvnLogList(boolean showDetail) throws Exception {		
		List<SVNLogEntry> logList = getSvnLogs(null, startDate, endDate, name);
		if (showDetail) {
			for (SVNLogEntry svnLogEntry : logList) {
				printSvnLog(svnLogEntry);
			}
			System.out.println("--------------------------");
		}
		List<String> rtnList = getDistinctPath(logList, codeLineProjectName);
		Set<String> codeLine=new HashSet<>();
		for (String str : rtnList) {
			String temp=str.replace("/smrznew/src/", "");
			File file=new File(codeLineProjectPath, temp);
			if (file.isFile()) {
				System.out.println(temp);
				codeLine.add(temp);
			}
		}
		//写入codelist.txt
		creatFile(codeLineExclusiveVersionFile, codeLine);
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
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name,
				password.toCharArray());
		repository.setAuthenticationManager(authManager);
	}


	public void getSvnFileContent() throws Exception {
		String path = "ol_java/src/com/asiainfo/ol/realname/service/impl/OlRealnameSVImpl.java";
		getFileFromSVN(path, 20, "e:/test_tmp/");
	}

	

	/**
	 * 从SVN服务器获取文件
	 * 
	 * @param filePath
	 *            相对于仓库根目录的路径
	 * @param outputStream
	 *            要输出的目标流，可以是文件流 FileOutputStream
	 * @param version
	 *            要checkout的版本号
	 * @return 返回checkout文件的版本号
	 * @throws Exception
	 *             可以自定义Exception
	 */
	public void getFileFromSVN(String filePath, long version, String basePath) throws Exception {
		SVNNodeKind node = null;
		try {
			node = repository.checkPath(filePath, version);
		} catch (SVNException e) {
			throw new Exception("SVN检测不到该文件:" + filePath, e);
		}
		String baseFilePath = getFilePath(filePath, basePath);
		System.out.println(baseFilePath);
		File file = new File(baseFilePath);
		if (!file.exists()) {
			if (baseFilePath.indexOf(".") < 0) {
				file.mkdirs();
			} else {
				new File(file.getParent()).mkdirs();
			}
		}
		if (node == SVNNodeKind.DIR) {
			// 文件夹
			return;
		}
		SVNProperties properties = new SVNProperties();
		FileOutputStream out = new FileOutputStream(file);
		try {
			repository.getFile(filePath, version, properties, out);
			out.close();
		} catch (Exception e) {
			new File(basePath).delete();
			throw new Exception("获取SVN服务器中的" + filePath + "文件失败", e);
		}
	}

	public String getFilePath(String path, String basePath) throws Exception {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		String rtnStr = null;
		if (basePath.endsWith("/")) {
			rtnStr = basePath + path;
		} else {
			rtnStr = basePath + "/" + path;
		}
		return rtnStr;
	}

	public void getSvnFileContent(String filePath) {
		SVNProperties fileProperties = new SVNProperties();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			SVNNodeKind nodeKind = repository.checkPath(filePath, -1);

			if (nodeKind == SVNNodeKind.NONE) {
				System.err.println("There is no entry at '" + url + "'.");
				System.exit(1);
			} else if (nodeKind == SVNNodeKind.DIR) {
				System.err.println("The entry at '" + url + "' is a directory while a file was expected.");
				System.exit(1);
			}
			repository.getFile(filePath, -1, fileProperties, baos);
		} catch (Exception svne) {
			System.err.println("error while fetching the file contents and properties: " + svne.getMessage());
			System.exit(1);
		}
		String mimeType = fileProperties.getStringValue(SVNProperty.MIME_TYPE);
		boolean isTextType = SVNProperty.isTextMimeType(mimeType);
		if (isTextType) {
			System.out.println("File contents:");
			System.out.println();
			try {
				baos.writeTo(System.out);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else {
			System.out.println(
					"File contents can not be displayed in the console since the mime-type property says that it's not a kind of a text file.");
		}
	}

	/**
	 * 获取提交的代码路径
	 * @param logList
	 * @param containStr 路径中必须包含的字符串
	 * @return
	 */
	public static List<String> getDistinctPath(List<SVNLogEntry> logList, String containStr) {
		List<String> pathList = new ArrayList<String>();
		Iterator<String> keyIt = null;
		Map<String, SVNLogEntryPath> svnPathMap = null;
		String codePath = null;
		for (SVNLogEntry svnLogEntry : logList) {
			svnPathMap = svnLogEntry.getChangedPaths();
			keyIt = svnPathMap.keySet().iterator();
			while (keyIt.hasNext()) {
				codePath = keyIt.next();
				if (codePath.contains(containStr) && !pathList.contains(codePath)) {
					pathList.add(codePath);
				}
			}
		}
		Collections.sort(pathList);
		return pathList;
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
				set.add(codeLine + "," + fileProperties.getStringValue("svn:entry:committed-rev"));
				System.out.println(codeLine + "," + fileProperties.getStringValue("svn:entry:committed-rev"));
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
			if (!StringUtil.isEmpty(line)) {
				list.add(line.trim());
			}
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
	/**
	 * 将集合中的内容输出到文件
	 * @param file
	 * @param set
	 * @throws FileNotFoundException
	 */
	private static void creatFile(String file, Set<String> set) throws FileNotFoundException {
		File print = new File(file);
		PrintWriter printWriter = new PrintWriter(print);
		for (String str : set) {
			printWriter.println(str);
		}
		printWriter.close();
	}
	/**
	 * 将集合中的内容输出到文件
	 * @param file
	 * @param set
	 * @throws FileNotFoundException
	 */
	private static void creatFile(File file, Set<String> set) throws FileNotFoundException {
		File print = new File(file.getParent() + "/svn" + file.getName());
		PrintWriter printWriter = new PrintWriter(print);
		for (String str : set) {
			printWriter.println(str);
		}
		printWriter.close();
	}

	/**
	 * 将集合中的内容输出到指定文件
	 * @param set
	 * @throws FileNotFoundException
	 */
	private static void creatFile(Set<String> set) throws FileNotFoundException {
		File print = new File(codeLineIncludeVersionFile);
		PrintWriter printWriter = new PrintWriter(print);
		for (String str : set) {
			printWriter.println(str);
		}
		printWriter.close();
	}


	public static List<SVNLogEntry> getSvnLogs(String startDateStr, String endDateStr, final String authorStr)
			throws Exception {
		return getSvnLogs(null, startDateStr, endDateStr, authorStr);
	}

	public static List<SVNLogEntry> getSvnLogs(final String filePath, String startDateStr, String endDateStr,
			final String authorStr) throws Exception {
		long startRevision = 0;
		if (!StringUtil.isEmpty(startDateStr)) {
			Date beginDate = DateUtil.formatDate(startDateStr, "yyyy-MM-dd HH:mm:ss");
			startRevision = repository.getDatedRevision(beginDate) + 1;
		}
		long endRevision = -1;
		if (!StringUtil.isEmpty(endDateStr)) {
			Date endDate = DateUtil.formatDate(endDateStr, "yyyy-MM-dd HH:mm:ss");
			Calendar inDate = Calendar.getInstance();
			inDate.setTime(endDate);
			inDate.set(Calendar.HOUR_OF_DAY, 23);
			inDate.set(Calendar.MINUTE, 59);
			inDate.set(Calendar.SECOND, 59);
			endRevision = repository.getDatedRevision(inDate.getTime());
		}

		final List<SVNLogEntry> history = new ArrayList<SVNLogEntry>();
		repository.log(new String[] { "" }, startRevision, endRevision, true, true, new ISVNLogEntryHandler() {
			public void handleLogEntry(SVNLogEntry svnlogentry) throws SVNException {
				if (filterSvnLog(svnlogentry, filePath, authorStr)) {
					history.add(svnlogentry);
				}
			}
		});
		return history;
	}

	public static boolean filterSvnLog(SVNLogEntry logEntry, String filePath, String authorStr) {
		if (!StringUtil.isEmpty(authorStr)) {
			if (!authorStr.equals(logEntry.getAuthor())) {
				return false;
			}
		}
		if (!StringUtil.isEmpty(filePath)) {
			if (logEntry.getChangedPaths().size() > 0) {
				Iterator<Map.Entry<String, SVNLogEntryPath>> it = logEntry.getChangedPaths().entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, SVNLogEntryPath> entry = it.next();
					SVNLogEntryPath entryPath = entry.getValue();
					if (entryPath.getPath().endsWith(filePath)) {
						return true;
					}
				}
			}
			return false;
		}
		return true;
	}

	// http://wiki.svnkit.com/Printing_Out_Repository_History
	public static void printSvnLog(SVNLogEntry logEntry) {
		System.out.println("---------------------------------------------");
		System.out.println("revision: " + logEntry.getRevision());
		System.out.println("author: " + logEntry.getAuthor());
		System.out.println("date: " + logEntry.getDate());
		System.out.println("log message: " + logEntry.getMessage());

		if (logEntry.getChangedPaths().size() > 0) {
			System.out.println("changed paths:");
			Iterator<Map.Entry<String, SVNLogEntryPath>> it = logEntry.getChangedPaths().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, SVNLogEntryPath> entry = it.next();
				SVNLogEntryPath entryPath = entry.getValue();
				System.out.println(" " + entryPath.getType() + " " + entryPath.getPath()
						+ ((entryPath.getCopyPath() != null)
								? " (from " + entryPath.getCopyPath() + " revision " + entryPath.getCopyRevision() + ")"
								: ""));
			}
		}
	}

}
