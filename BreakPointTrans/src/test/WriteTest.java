package test;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import util.LogUtil;

public class WriteTest {
	public static void main(String[] args) {
		long [] startPos = {0, 5007, 10014};
		long [] endPos = {5007, 10014, 15021};
		
		try {
			DataOutputStream output = new DataOutputStream(new FileOutputStream(new File("C:\\Users\\wds\\Desktop\\testsuccess.tmp")));
			output.writeInt(startPos.length);
			for(int i = 0; i < startPos.length; i++){
				output.writeLong(startPos[i]);
				output.writeLong(endPos[i]);
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
}
