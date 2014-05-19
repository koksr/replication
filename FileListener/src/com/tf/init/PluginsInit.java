package com.tf.init;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import com.tf.util.XmlUtil;

public class PluginsInit {
	private String path;
	public static String getPath(){
		return new PluginsInit().path;
	}
	public PluginsInit() {
		try{
			path = XmlUtil.getNodeValue("plugins");
			File dir= new File(path);
			if(!dir.exists()||!dir.isDirectory())
				throw new NullPointerException();
		}catch(NullPointerException e){
			path=chooseLocation();
			XmlUtil.saveOrUpdate("plugins", path);
		}
	}

	public String chooseLocation() {
		JOptionPane.showMessageDialog(null, "点击确定按钮后选择插件包路径", "错误信息",
				JOptionPane.OK_OPTION);
		JFileChooser jfChooser = new JFileChooser();
		jfChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfChooser.setSelectedFile(new File("plugins"));
		jfChooser.setFileFilter(new PluginFilter());
		jfChooser.setMultiSelectionEnabled(false);
		int res = jfChooser.showOpenDialog(jfChooser);
		if (JFileChooser.APPROVE_OPTION == res) {
			String location = jfChooser.getSelectedFile().getAbsolutePath()+File.separator+"bin";
			return location;
		} else {
			System.exit(0);
			return null;
		}
	}
}

class PluginFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "plugins";
	}

}