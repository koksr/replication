package com.tf.util;

import java.util.ArrayList;
import java.util.List;

public class FfmpegUtil {
	
	public void test(){
		List<String> command = new ArrayList<String>();
		String path = "C:\\Program Files\\OpenOffice 4\\program\\soffice.exe";
		command.add("\"" + path + "\"");
		command.add("-headless");
		command.add("-accept=\"socket,host=127.0.0.1,port=8100;urp;\"");
		command.add("-nofirststartwizard");
		System.out.println(new BuildProcess(command).start());
		
	}

}
