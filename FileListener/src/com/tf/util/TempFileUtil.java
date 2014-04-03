package com.tf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
		File[] files = new File(System.getProperty("user.dir")).listFiles();
		for(File f : files){
			if(f.getName().endsWith(".bat")){
				if(System.currentTimeMillis()-getCreateTime(f)>(1000*60*60*24)){
					f.delete();
				}
			}
		}
		
		
	}
}
