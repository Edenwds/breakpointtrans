package test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import util.LogUtil;

public class ReadTest {
	public static void main(String[] args) {
		try {
			DataInputStream input = new DataInputStream(new FileInputStream(new File("C:\\Users\\wds\\Desktop\\testsuccess.tmp")));
			int count = input.readInt();
			long [] startPos = new long[count];
			long [] endPos = new long[count];
			for(int i = 0; i < count; i++){
				startPos[i] = input.readLong();
				endPos[i] = input.readLong();
			}
			
			for(long i : startPos){
				System.out.println(i);
			}
			
			for(long j : endPos){
				System.out.println(j);
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
}
