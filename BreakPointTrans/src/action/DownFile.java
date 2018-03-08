package action;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import bean.SiteInfo;
import util.LogUtil;

public class DownFile {
	
	private static final int NOACCESS  = -2; // 文件不可访问
	
	SiteInfo siteInfo;                     // 文件信息
	long [] startPos;                      // 开始位置
	long [] endPos;                        // 结束位置
	FileSplitFetch [] fileSplitFetchs;     // 多线程分段传输的线程集合
	long fileLen;                          // 文件长度
	boolean firstDown = true;              // 是否第一次下载文件
	boolean stop = false;                  // 停止标志
	File infoFile;                         // 保存文件信息的临时文件
	
	public DownFile(SiteInfo siteInfo){
		this.siteInfo = siteInfo;
		infoFile = new File(siteInfo.getFilePath() + File.separator + siteInfo.getSimpleName() + ".tmp");
		if(infoFile.exists()){
			firstDown = false;
			readInfo();
		}else{
			startPos = new long[siteInfo.getSplits()];
			endPos = new long[siteInfo.getSplits()];
		}
	}
	
	/**
	 * 开始下载文件
	 * 1. 获取文件长度
	 * 2. 分割文件
	 * 3. 实例化分段下载子线程
	 * 4. 启动子线程
	 * 5. 等待子线程的返回
	 * @throws IOException 
	 */
	public void startDown(){
		
		if(firstDown){
			fileLen = getFileSize();
			if(fileLen == -1){
				LogUtil.log("文件大小未知");
				return;
			}else if(fileLen == -2){
				LogUtil.log("文件不可访问");
				return;
			}
			else{
				
				// 设置每次分段下载的开始位置
				for(int i = 0; i < startPos.length; i++){
					startPos[i] = i * (fileLen / startPos.length);
				}
				
				//设置每次分段下载的结束位置
				for(int i = 0; i < endPos.length - 1; i++){
					endPos[i] = startPos[i + 1];
				}
				endPos[endPos.length - 1] = fileLen;
				
			}
			
		}
		
		//启动分段下载子线程
		
		try {
				fileSplitFetchs = new FileSplitFetch[startPos.length];
				for(int i = 0; i < startPos.length; i++){
					System.out.println(startPos[i] + " " + endPos[i]);
					fileSplitFetchs[i] = new FileSplitFetch(siteInfo.getUrl(), startPos[i], endPos[i], i, 
							siteInfo.getFilePath() + File.separator + siteInfo.getFileName());
					LogUtil.log("Threa " + i + ", start= " + startPos[i] + ",  end= " + endPos[i]);
					new Thread(fileSplitFetchs[i]).start();
				}
				
				//保存文件下载信息
				saveInfo();
				//循环判断所有文件是否下载完毕
				boolean breakWhile = false;
				while(!stop){
					
					LogUtil.sleep(500);
					breakWhile = true;
					
					for(int i = 0; i < startPos.length; i++){
						if(! fileSplitFetchs[i].downOver){
							breakWhile = false; // 还存在未下载完成的线程
							break;
						}
					}
					
					if(breakWhile)
						break;
				}
		} catch (IOException e) {
			LogUtil.log(e.getMessage());
			e.printStackTrace();
		}
		
		LogUtil.log("文件下载完成");
	}

	/**
	 * 保存文件下载信息
	 */
	private void saveInfo() {
		try {
			DataOutputStream output = new DataOutputStream(new FileOutputStream(infoFile));
			output.writeInt(startPos.length);
			for(int i = 0; i < startPos.length; i++){
				output.writeLong(fileSplitFetchs[i].startPos);
				output.writeLong(fileSplitFetchs[i].endPos);
			}
			output.close();
		} catch (FileNotFoundException e) {
			LogUtil.log(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LogUtil.log(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 获取文件的大小
	 * @return
	 */
	private long getFileSize() {
		int len = -1;
		try {
			URL url = new URL(siteInfo.getUrl());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("User-Agent", "custom");
			
			int respCode = connection.getResponseCode();
			if(respCode >= 400){
				LogUtil.log("Error Code : " + respCode);
				return NOACCESS; // 代表文件不可访问
			}
			
			String header = null;
			for(int i = 1; ; i++){
				header = connection.getHeaderFieldKey(i);
				if(header != null){
					if("Content-Length".equals(header)){
						len = Integer.parseInt(connection.getHeaderField(header));
						break;
					}
				}else{
					break;
				}
			}
		} catch (MalformedURLException e) {
			LogUtil.log(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LogUtil.log(e.getMessage());
			e.printStackTrace();
		}
		
		LogUtil.log("文件大小为" + len);
		return len;
	}

	/**
	 * 读取文件下载保存的信息
	 */
	private void readInfo() {
		try {
			DataInputStream input = new DataInputStream(new FileInputStream(infoFile));
			int count = input.readInt();
			startPos = new long[count];
			endPos = new long[count];
			for(int i = 0; i < count; i++){
				startPos[i] = input.readLong();
				endPos[i] = input.readLong();
			}
			
			input.close();
		} catch (FileNotFoundException e) {
			LogUtil.log(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LogUtil.log(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 停止下载
	 */
	public void setStop(){
		stop = true;
		for(int i = 0; i < startPos.length; i++){
			fileSplitFetchs[i].setSplitTransStop();
		}
	}
}
