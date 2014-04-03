package com.tf.view;

import java.awt.Frame;

import javax.swing.JOptionPane;

public class Confirm extends Frame {
	public boolean showOptionDialog() {
		String[] options = { "是", "否" };
		int result = JOptionPane.showOptionDialog(null, "确定要关闭此监听程序么？",
				"警告！", JOptionPane.DEFAULT_OPTION,
				JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		if (result == 0)
			return true;
		else
			return false;
	}
}
