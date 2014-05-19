package com.tf.init;

import java.io.File;

import com.tf.util.XmlUtil;

public class FfmpegInit {
	private String path;
	public static String getPath(){
		return new FfmpegInit().path;
	}
	public FfmpegInit() {
		try{
			path = XmlUtil.getNodeValue("plugins")+File.separator+"ffmpeg.exe";
			if(!new File(path).exists())
				throw new NullPointerException();
		}catch(NullPointerException e){
			String pluginPath=PluginsInit.getPath();
			XmlUtil.saveOrUpdate("plugins", pluginPath);
			path=pluginPath+File.separator+"ffmpeg.exe";
		}
	}

}