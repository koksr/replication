package com.tf.init;

import java.io.File;

import com.tf.util.XmlUtil;

public class PDFInit {
	private String path;
	public static String getPath(){
		return new PDFInit().path;
	}
	public PDFInit() {
		try{
			path = XmlUtil.getNodeValue("plugins")+File.separator+"office2pdf.exe";
			if(!new File(path).exists())
				throw new NullPointerException();
		}catch(NullPointerException e){
			String pluginPath=PluginsInit.getPath();
			XmlUtil.saveOrUpdate("plugins", pluginPath);
			path=pluginPath+File.separator+"office2pdf.exe";
		}
	}

}
