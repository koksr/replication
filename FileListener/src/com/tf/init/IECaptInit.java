package com.tf.init;

import java.io.File;

import com.tf.util.XmlUtil;

public class IECaptInit {
	private String path;
	public static String getPath(){
		return new IECaptInit().path;
	}
	public IECaptInit() {
		try{
			path = XmlUtil.getNodeValue("plugins")+File.separator+"IECapt.exe";
			if(!new File(path).exists())
				throw new NullPointerException();
		}catch(NullPointerException e){
			String pluginPath=PluginsInit.getPath();
			XmlUtil.saveOrUpdate("plugins", pluginPath);
			path=pluginPath+File.separator+"IECapt.exe";
		}
	}

}

