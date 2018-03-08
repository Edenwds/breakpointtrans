package action;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import util.FileUtil;
import util.LogUtil;

/**
 * 用于分段传输
 * 使用HTTP协议的首部字段实现
 * @author wds
 *
 */
public class FileSplitFetch implements Runnable{
	
	protected String url;               // 文件所在url
	protected long startPos;            // 分段传输的开始位置
	protected long endPos;              // 结束位置
	protected int threadID;             // 线程编号
	protected boolean downOver = false; // 下载完成标志
	protected boolean stop = false;     // 当前分段结束标志
	FileUtil fileUtil = null;           // 文件工具
	
	public FileSplitFetch(String url, long startPos, long endPos, int threadID, String fileName) throws IOException {
		super();
		this.url = url;
		this.startPos = startPos;
		this.endPos = endPos;
		this.threadID = threadID;
		fileUtil = new FileUtil(fileName, startPos);
	}


	@Override
	public void run() {
		while(startPos < endPos && !stop){
			try {
				URL ourl = new URL(url);
				HttpURLConnection httpConnection = (HttpURLConnection) ourl.openConnection();
				String prop = "bytes=" + startPos + "-";
				httpConnection.setRequestProperty("RANGE", prop); //设置请求首部字段 RANGE
				
				LogUtil.log(prop);
				
				InputStream input = httpConnection.getInputStream(); 
				byte[] b = new byte[1024];
				int bytes = 0;
				while((bytes = input.read(b)) > 0 && startPos < endPos && !stop){
					startPos += fileUtil.write(b, 0, bytes);
				}
				
				LogUtil.log("Thread" + threadID + " is done");
				downOver = true;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	/**
	 * 打印响应的头部信息
	 * @param conn
	 */
	public void printResponseHeader(HttpURLConnection conn){
		for(int i = 0; ; i++){
			String fieldsName = conn.getHeaderFieldKey(i);
			if(fieldsName != null){
				LogUtil.log(fieldsName + ":" + conn.getHeaderField(fieldsName));
			}else{
				break;
			}
		}
	}
	
	/**
	 * 停止分段传输
	 */
	public void setSplitTransStop(){
		stop = true;
	}
	
	
	
}
