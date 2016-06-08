package com.tuxt.svntool.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
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
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
/**
 * svn工具类
 * @author tuxiantian@163.com
 * @since 2015年11月25日上午11:07:34 
 */
public class SvnUtil2 {
	private final static String svnRoot="svn://192.168.100.10/smrznew/src";
	private final static String name="tuxt";
	private final static char[] password="tu123".toCharArray();
	static SVNRepository repository = null;
	
	private static String codeLineExclusiveVersionFile =Paths.get(Constants.PROJECT_PATH, "codelist/codelist.txt").toString();
	private static String codeLineIncludeVersionFile = Paths.get(Constants.PROJECT_PATH, "codelist/svncodelist.txt").toString();
	//"C:/Users/Administrator/Desktop/codelist/svncodelist.txt"
	static{
		FSRepositoryFactory.setup();
		DAVRepositoryFactory.setup(); 
		SVNRepositoryFactoryImpl.setup();
		try {
			repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnRoot));
		} catch (SVNException e) {
			e.printStackTrace();
		}
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);  
		repository.setAuthenticationManager(authManager); 
	}
	/**
	 * 获取要上线的代码列表行并输出到文件
	 * @throws Exception
	 */
	public static void printPublishCodeList() throws Exception{
		Set<String> codelist=ReadFileUtil.readFile2Set(codeLineExclusiveVersionFile);
		System.err.println(codelist.size());
		StringBuilder builder=new StringBuilder();
		for (String line : codelist) {
			System.out.println(line);
			builder.append(line).append(",").append(getFileVersion(line)).append("\n");
		}
		WriteFileUtil.writeFileSingle(codeLineIncludeVersionFile, builder.toString());
	}
	/**
	 * 将代码列表行输出到文件
	 * @param codeLines 代码列表行
	 * @throws Exception
	 */
	public static void printCommitCodeList(Set<String> codeLines) throws Exception{
		String filePath="C:/Users/Administrator/Desktop/codelist/codelist.txt";
		StringBuilder builder=new StringBuilder();
		for (String line : codeLines) {
			builder.append(line).append("\n");
		}
		WriteFileUtil.writeFileSingle(filePath, builder.toString());
	}
	/**
	 * 获取指定文件的版本号
	 * @param filePath 文件路径（含文件名）
	 * @return
	 */
	public static String getFileVersion(String filePath) {
		//此变量用来存放要查看的文件的属性名/属性值列表。  
		SVNProperties fileProperties = new SVNProperties();  
		//此输出流用来存放要查看的文件的内容。
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {  
			//String filePath="rgshcore/src/main/resources/mybatis/orm/workOrder.xml";
			//获得版本库中文件的类型状态（是否存在、是目录还是文件），参数-1表示是版本库中的最新版本。  
			SVNNodeKind nodeKind = repository.checkPath(filePath, -1);  
			
			if (nodeKind == SVNNodeKind.NONE) {  
				System.err.println("要查看的文件"+filePath+"在 '"+svnRoot + "'中不存在.");  
			} else if (nodeKind == SVNNodeKind.DIR) {  
				System.err.println("要查看对应版本的条目在 '" +svnRoot 
						+ "'中是一个目录.");  
			}  
			//获取要查看文件的内容和属性，结果保存在baos和fileProperties变量中。  
			repository.getFile(filePath, -1, fileProperties, baos);  
			
		} catch (SVNException svne) {  
			System.err.println("在获取文件内容和属性时发生错误: " + svne.getMessage());  
			System.exit(1);  
		}  
		
		//获取文件的mime-type  
		String mimeType = fileProperties.getStringValue(SVNProperty.MIME_TYPE);  
		//判断此文件是否是文本文件          
		boolean isTextType = SVNProperty.isTextMimeType(mimeType);  
		/* 
		 * 显示文件的所有属性 
		 */  
		Iterator iterator = fileProperties.nameSet().iterator();  
		while (iterator.hasNext()) {  
			String propertyName = (String) iterator.next();  
			if ("svn:entry:committed-rev".equals(propertyName)) {
				String propertyValue = fileProperties.getStringValue(propertyName);
				System.out.println("文件的属性: " + propertyName + "=" + propertyValue);
				return propertyValue;
			}  
			
		}
		return null;
	}
	/**
	 * 获取一段时间内某个人的提交文件记录
	 * @param startDateStr 起始时间
	 * @param endDateStr 结束时间
	 * @param authorStr 作者
	 * @return
	 * @throws Exception
	 */
	public static Set<SVNLogEntry> getSvnLogs(String startDateStr, String endDateStr, final String authorStr) throws Exception {
		final Date beginDate =DateUtil.string2Date(startDateStr, DateUtil.DATE_PATTERN.YYYY_MM_DD);
		final Date endDate = DateUtil.string2Date(endDateStr, DateUtil.DATE_PATTERN.YYYY_MM_DD);

		Calendar inDate = Calendar.getInstance();
		inDate.setTime(endDate);
		inDate.set(Calendar.HOUR_OF_DAY, 23);
		inDate.set(Calendar.MINUTE, 59);
		inDate.set(Calendar.SECOND, 59);
		long startRevision = repository.getDatedRevision(beginDate) + 1;
		long endRevision = repository.getDatedRevision(inDate.getTime());

		final Set<SVNLogEntry> history = new HashSet<SVNLogEntry>();
		// String[] 为过滤的文件路径前缀，为空表示不进行过滤
		repository.log(new String[] { "" }, startRevision, endRevision, true, true, new ISVNLogEntryHandler() {
			public void handleLogEntry(SVNLogEntry svnlogentry) throws SVNException {
				// 依据提交人过滤
				if (!StringUtil.isEmpty(authorStr)) {
					if (authorStr.equals(svnlogentry.getAuthor())) {
						fillResult(svnlogentry);
					}
				} else {
					fillResult(svnlogentry);
				}
			}

			public void fillResult(SVNLogEntry svnlogentry) {
				//svnlogentry.getChangedPaths().keySet()
				history.add(svnlogentry);
			}
		});
		return history;
	}

	/**
	 * 根据提交记录获取代码列表行
	 * @param logs 提交记录
	 * @return
	 */
	public static Set<String> printSvnLogs(Set<SVNLogEntry> logs){
		Set<String> list=new TreeSet();
				
		for (SVNLogEntry svnLogEntry : logs) {
			Map<String, SVNLogEntryPath> map=svnLogEntry.getChangedPaths();
			for (Map.Entry<String, SVNLogEntryPath> entry : map.entrySet()) {
				String line=entry.getKey();
				line=line.substring("/smrznew/src/".length());
				list.add(line);
				System.out.println(entry.getKey());
			}
		}
		return list;
	}
	public static void checkOutFile() throws SVNException{
		//获取SVN驱动选项
		ISVNOptions options = SVNWCUtil.createDefaultOptions(true);

		// 实例化客户端管理类
		SVNClientManager ourClientManager = SVNClientManager.newInstance((DefaultSVNOptions) options, "tuxt", "tu123");

		SVNURL repositoryURL = null;
		//判断目录是否为null，如果为null则不拼取资源库URL地址，否则反之；
		repositoryURL = SVNURL.parseURIEncoded(svnRoot+"rgsh/src/main/java/com/ai/rgsh/action/");

		// 通过客户端管理类获得updateClient类的实例。
		SVNUpdateClient updateClient = ourClientManager.getUpdateClient();

		//sets externals not to be ignored during the checkout
		updateClient.setIgnoreExternals(true);

		File localDirectory=new File("C:/Users/Administrator/Desktop/codelist/");
		// 执行check out 操作，返回工作副本的版本号。
		long workingVersion = updateClient.doCheckout(repositoryURL, localDirectory,SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY,false);
		//updateClient.
		System.out.println(workingVersion);
	}
	/**
	 * 检查上线代码列表文件是否为最新版本
	 * @param filePath 上线代码列表文件
	 * @throws Exception
	 */
	public static void checkIsHighest(String filePath) throws Exception {
		Map<String, Long> map=ReadFileUtil.readFile2Map(filePath);
		for(Map.Entry<String, Long> entry:map.entrySet()){
			String file=entry.getKey();
			Long version=entry.getValue();
			Long highest=Long.parseLong(getFileVersion(file));
			if (version<highest) {
				System.err.println("注意要上线的"+file+"非最新版本");
			}
		}
	}
	public static void main(String[] args) throws Exception {
		//获取某个人在某段时间提交的代码文件
		//Set<SVNLogEntry> logs=getSvnLogs("2015-01-31", "2015-02-01", "tuxt");
		//Set<String>	codeLines=printSvnLogs(logs);
		//printCommitCodeList(codeLines);
		
		//获取上线的
		printPublishCodeList();
		
		//检查上线的代码列表是否是最新版本
		/*String filePath="C:/Users/asus/Desktop/totalcode/total.txt";
		filePath="C:/Users/asus/Desktop/codelist/svncodelist.txt";
		checkIsHighest(filePath);*/
	}
}
