package bean;

import java.io.Serializable;

/**
 * 要抓取文件的信息
 * @author wds
 *
 */
public class SiteInfo implements Serializable {

	private static final int SPLIT_COUNT = 5; // 默认次数为5次
	
	private String url;        // 文件所在站点的url
	private String filePath;   // 文件保存的路径
	private String fileName;   // 文件的名字
	private int splits;        // 分段下载文件的次数
	
	public SiteInfo(){
		this("","","",SPLIT_COUNT);
	}

	public SiteInfo(String url, String filePath, String fileName, int splits) {
		this.url = url;
		this.filePath = filePath;
		this.fileName = fileName;
		this.splits = splits;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getSplits() {
		return splits;
	}

	public void setSplits(int splits) {
		this.splits = splits;
	}
	
	public String getSimpleName(){
		String[] names = fileName.split("\\.");
		return names[0];
	}
	
	
	
}

