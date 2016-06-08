package com.tuxt.svntool.util;  
import java.io.FileNotFoundException;  
import java.io.FileOutputStream;  
import java.io.OutputStream;  
import java.util.ArrayList;  
import java.util.Collection;  
import java.util.List;  
  


import java.util.Map;

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
import org.tmatesoft.svn.core.wc.SVNWCUtil;  
/**  
 * SVNManager SVN 管理器  
 * 提供了从svn服务器下载指定版本的文件
 * @author tuxiantian@163.com
 * @since 2016年1月26日上午9:10:14  
 */  
public class SVNManager{  
      
    private static String url = "svn://192.168.100.10/smrznew/src"; //svn://192.168.100.10/10085ds/branches/xxq/ 
    private static String username = "tuxt";//  renjh
    private static String password = "tu123";// ren123 
    private static SVNRepository repository;
	private static String savePath = "E:/code/";  
  
    static{
    	try {
			initialize();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
    }
    /**  
     * 初始化操作  
     * @throws Exception  
     */  
    public static void initialize() throws Exception {  
        FSRepositoryFactory.setup();  
        DAVRepositoryFactory.setup();  
        SVNRepositoryFactoryImpl.setup();  
        repository = SVNRepositoryFactoryImpl.create(SVNURL  
                .parseURIEncoded(url));  
        ISVNAuthenticationManager authManager = SVNWCUtil  
                .createDefaultAuthenticationManager(username,  
                        password);  
        repository.setAuthenticationManager(authManager);  
    }  
      
    /**  
     * 从SVN服务器获取文件  
     * @param filePath 相对于仓库根目录的路径  
     * @param outputStream 要输出的目标流，可以是文件流 FileOutputStream  
     * @param version 要checkout的版本号  
     * @return 返回checkout文件的版本号  
     * @throws Exception 可以自定义Exception  
     */  
    public static long getFileFromSVN(String filePath, OutputStream outputStream,  
            long version) throws Exception {  
        SVNNodeKind node = null;  
        try {  
            node = repository.checkPath(filePath, version);  
        } catch (SVNException e) {  
            throw new Exception("SVN检测不到该文件:" + filePath, e);  
        }  
        if (node != SVNNodeKind.FILE) {  
            throw new Exception(node.toString() + "不是文件");  
        }  
        SVNProperties properties = new SVNProperties();  
        try {  
            repository.getFile(filePath, version, properties, outputStream);  
        } catch (SVNException e) {  
            throw new Exception("获取SVN服务器中的" + filePath + "文件失败", e);  
        }  
        return Long.parseLong(properties.getStringValue("svn:entry:revision"));  
    }  
    /**  
     * 获取目录下的所有文件和子目录  
     * @param res 包含目录参数的资源对象.参加{@link Resource#getPath()}  
     * @return 资源列表  
     * @throws Exception  
     */  
    @SuppressWarnings("unchecked")  
    public static List<Resource> getChildren(Resource res) throws Exception {  
        String path = res.getPath();  
        Collection<SVNDirEntry> entries;  
        try {  
            entries = repository.getDir(path, -1, null, (Collection) null);  
        } catch (SVNException e) {  
            throw new Exception("获得" + path + "下级目录失败", e);  
        }  
        List<Resource> result = new ArrayList<Resource>();  
        for (SVNDirEntry entry : entries) {  
            if (containsSpecialFile(entry)) {  
                Resource resource = new Resource();  
                resource.setName(entry.getName());  
                resource.setPath(entry.getURL().getPath());  
                resource.setFile(entry.getKind() == SVNNodeKind.FILE);  
                result.add(resource);  
            }  
        }  
        return result;  
    }  
    /**  
     * 判断文件是否存在  
     * @param entry 要判断的节点.参加{@link SVNDirEntry}  
     * @return   
     * @throws Exception  
     */  
    @SuppressWarnings("unchecked")  
    private static boolean containsSpecialFile(SVNDirEntry entry)  
    throws Exception {  
        if (entry.getKind() == SVNNodeKind.FILE) {  
            return true;  
        } else if (entry.getKind() == SVNNodeKind.DIR) {  
            Collection<SVNDirEntry> entries;  
            String path = entry.getURL().getPath();  
            try {  
                entries = repository.getDir(path, -1, null, (Collection) null);  
            } catch (SVNException e) {  
                throw new Exception("获得" + path + "下级目录失败", e);  
            }  
            for (SVNDirEntry unit : entries) {  
                if (containsSpecialFile(unit)) {  
                    return true;  
                }  
            }  
            return false;  
        }  
        return false;  
    }  
      
  
    public static void downloadFileFromSvn(String outFileName,Long version) {   
        OutputStream outputStream;  
        try {  
            WriteFileUtil.createTempFile(savePath+outFileName);
            outputStream = new FileOutputStream(savePath+outFileName);  
            System.out.println(getFileFromSVN(outFileName,outputStream,version));  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    public static void download(String filePath){
    	try {
			Map<String, Long> codeMap=ReadFileUtil.readFile2Map(filePath);
			for(Map.Entry<String, Long> entry:codeMap.entrySet()){
				downloadFileFromSvn(entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
    }
     
    public static void testGetDir() {  
        try {  
            initialize();  
            Resource res = new Resource();  
            res.setPath("/app1/");  
            List<Resource> rs = getChildren(res);  
            for(Resource r : rs) {  
                System.out.println(r.getFile()?"file:":"directory:" + r.getPath());  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    public static void main(String[] args) {
		/*String outFileName = "ecp/readme.txt";
		downloadFileFromSvn(outFileName,32L);*/
		String filePath="C:/Users/Administrator/Desktop/codelist/downloadlist.txt";
		download(filePath);
	}
}  