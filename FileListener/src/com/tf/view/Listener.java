package com.tf.view;

import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import com.sun.awt.AWTUtilities;
import com.tf.util.ElementUtil;
import com.tf.util.Logs;
import com.tf.util.ThreadPool;

public class Listener extends JFrame {
	public static final JTextArea area = new JTextArea(20, 30);
	public static final JLabel lab = new JLabel();
	static String status = "start";

	public static void main(String[] args) {
		ElementUtil.createDirs();
		try {
			BeautyEyeLNFHelper.launchBeautyEyeLNF();
		} catch (Exception e) {
			Logs.WriteLogs(e);
			Listener.area.append("BeautyEyeLNF运行失败，原因是：" + e.getMessage()
					+ "\r\n");
		}
		UIManager.put("RootPane.setupButtonVisible", false);
		final JButton start = new JButton("开始");
		final Listener win = new Listener();
		win.setTitle("ftp服务器文件校验程序");
		AWTUtilities.setWindowOpaque(win, false);
		win.setSize(450, 500);
		win.setLayout(new FlowLayout());
		win.add(start);
		JScrollPane sp = new JScrollPane();
		sp.getViewport().add(area);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// sp.setSize(300, 1300);
		win.add(sp);
		// win.add(area,BorderLayout.CENTER);
		start.setText("停止");
		status = "stop";
		area.append(ElementUtil.dateFormat.format(System
				.currentTimeMillis()) + "	开始运行\r\n");
		ThreadPool.runStatus = true;
		ThreadPool.getInstance().start();
		start.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {

				if (status == "start") {
					start.setText("停止");
					status = "stop";
					area.append(ElementUtil.dateFormat.format(System
							.currentTimeMillis()) + "	开始运行\r\n");
					ThreadPool.runStatus = true;
					ThreadPool.getInstance().start();
				} else if (status == "stop") {
					Confirm confirm = new Confirm();
					boolean result = confirm.showOptionDialog();
					if (result) {
						System.exit(0);
					}
				}

			}
		});

		win.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

		});
		win.setVisible(true);
	}

}