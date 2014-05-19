package com.tf.handle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.tf.util.Logs;

public class TempFileUtil extends Thread{
	/**
	 * 
	 * @param file
	 * @return 毫秒数
	 */
	public static long getCreateTime(File file){  
        String filePath = file.getAbsolutePath();  
        long time = 0L;  
        try {  
            Process p = Runtime.getRuntime().exec("cmd /C dir "           
                    + filePath  
                    + "/tc" );  
            InputStream is = p.getInputStream();   
            BufferedReader br = new BufferedReader(new InputStreamReader(is));             
            String line;  
            while((line = br.readLine()) != null){  
                if(line.endsWith(".bat")){  
                	System.out.println(line);
                    time = new SimpleDateFormat("yyyy/MM/dd  HH:mm").parse( line.substring(0,17)).getTime();  
                    break;  
                }                             
             }   
        } catch (IOException e) {  
        	Logs.WriteLogs(e);
            e.printStackTrace();  
        } catch (ParseException e) {
        	Logs.WriteLogs(e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}         
        return time;
    }  
	public  void run(){
		while(true){
			File[] files = new File(System.getProperty("user.dir")).listFiles();
			for(File f : files){
				if(f.getName().endsWith(".temp~")){
					if(System.currentTimeMillis()-getCreateTime(f)>(1000*60*60*24*30)){
						f.delete();
					}
				}
			}
			try {
				Thread.sleep(1000*60*60*12);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
}
