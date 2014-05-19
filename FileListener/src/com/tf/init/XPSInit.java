package com.tf.init;

import java.io.File;

import com.tf.util.XmlUtil;

public class XPSInit {
	private String path;
	public static String getPath(){
		return new XPSInit().path;
	}
	public XPSInit() {
		try{
			path = XmlUtil.getNodeValue("plugins")+File.separator+"pdf2xps.exe";
			if(!new File(path).exists())
				throw new NullPointerException();
		}catch(NullPointerException e){
			String pluginPath=PluginsInit.getPath();
			XmlUtil.saveOrUpdate("plugins", pluginPath);
			path=pluginPath+File.separator+"pdf2xps.exe";
		}
	}

}
