package com.tf.ctrl;

import java.io.File;
import java.io.IOException;

import com.tf.handle.Mediahandle;
import com.tf.handle.WebCapt;
import com.tf.model.Element;
import com.tf.model.ElementType;
import com.tf.util.ElementUtil;
import com.tf.util.Logs;
import com.tf.util.ThreadPool;
import com.tf.view.Listener;

public class WebExecute extends Thread {
	private File tempFile;
	private String name;
	private String id;

	public WebExecute(String name, String id, File file) {
		this.name = name;
		this.id = id;
		this.tempFile = file;
	}

	public synchronized boolean checkCapt(File file) {
		while (true) {
			try {
				this.wait(1000);
			} catch (InterruptedException e) {
				Logs.WriteLogs(e);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (file.exists()) {
				return true;
			}
		}

	}

	public void run() {
		Element element = new DBhandle().getWebElement(Integer.parseInt(id));
		element.setType(ElementType.html.getValue());
		// File temp = new File(System.currentTimeMillis() + ".bat");
		// String location = tempFile.getParentFile().getAbsolutePath() +
		// "/processed";
		// FileWriter fw = null;
		Long TimeStamp = new Double(Math.random() * 10000).longValue()
				+ System.currentTimeMillis();
		// try {
		// File f = new File(location);
		// if (!f.exists()) {
		// f.mkdirs();
		// }
		// fw = new FileWriter(temp);
		// String commend = "D:\r\n cd D:\\ffmpeg\\\r\n IECapt.exe --url=\""
		// + element.getFilePath() + "\" --out=\"" +location + "\\" +TimeStamp +
		// ".jpg\"";
		// fw.write(commend);
		// } catch (Exception e) {
		// Logs.WriteLogs(e);
		// e.printStackTrace();
		// System.exit(0);
		// } finally {
		// if (fw != null) {
		// try {
		// fw.close();
		// } catch (IOException e) {
		// Logs.WriteLogs(e);
		// e.printStackTrace();
		// System.exit(0);
		// }
		// }
		// }
		//
		//
		// try {
		// Listener.area.append(ElementUtil.dateFormat.format(System
		// .currentTimeMillis()) + "	开始生成快照\r\n");
		// Desktop.getDesktop().open(temp);
		// Listener.area.append(ElementUtil.dateFormat.format(System
		// .currentTimeMillis()) + "	清理临时文件\r\n");
		// new TempFileUtil().start();
		// } catch (IOException e) {
		// Logs.WriteLogs(e);
		// e.printStackTrace();
		// }
		File capt = null;
		try {
			capt = WebCapt.Capt(element.getFilePath(), tempFile.getParent(),
					TimeStamp);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		element.setThumbnailUrl("ftpFile\\processed\\" + capt.getName());
		if (capt.exists()) {
			Listener.area.append(ElementUtil.dateFormat.format(System
					.currentTimeMillis()) + "	开始读取快照信息\r\n");
			element = (Mediahandle.getInfo(capt, element));
		}
		if (new DBhandle().updateElement(element) == 1)
			Listener.area.append(ElementUtil.dateFormat.format(System
					.currentTimeMillis()) + "	" + name + "已存入数据库\r\n");
		tempFile.delete();
		if (!tempFile.exists()) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				Logs.WriteLogs(e);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ThreadPool.fileSet.remove(tempFile);
		}

	}

}
