package com.tf.init;

import javax.swing.JOptionPane;

import com.tf.ctrl.DBhandle;
import com.tf.handle.TempFileUtil;
import com.tf.util.ElementUtil;
import com.tf.util.SourceUtil;

public class InitConifg {
	public static void init(){
		ElementUtil.createDirs();
		SourceUtil.getSource();
		new DBhandle().getAuditStatus();
		new DBhandle().setCryptographic();
		if(IECaptInit.getPath()==null||DBInit.getData()==null||FfmpegInit.getPath()==null){
			JOptionPane.showMessageDialog(null, "程序初始化失败", "错误信息",
					JOptionPane.OK_OPTION);
			System.exit(0);
		}
		new TempFileUtil().start();
		return;
	}
}
