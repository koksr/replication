package com.tf.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BuildProcess {
	private List<String> command = new ArrayList<String>();
	public BuildProcess(List<String> command){
		this.command=command;
	}
	public String start(){
		StringBuilder result = new StringBuilder();
		ProcessBuilder builder = new ProcessBuilder();
//		builder.
		builder.redirectErrorStream(true);
		//builder.redirectInput();
		builder.command(command);
		Process p = null;
		try {
			p = builder.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			Scanner scanner = new Scanner(p.getInputStream());
			while (scanner.hasNextLine()) {
				result.append(scanner.nextLine());
			}
			scanner.close();
		}catch (Exception e){
			e.printStackTrace();
		}
			
		return result.toString();
	}
	
}
