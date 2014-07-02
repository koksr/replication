package com.tf.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import com.tf.control.FtpClient;
import com.tf.control.FtpCtrl;
import com.tf.control.ThreadPool;
import com.tf.control.WebRequest;
import com.tf.model.FileData;
import com.tf.model.FtpStatus;
import com.tf.util.MD5Util;

@SuppressWarnings("serial")
public class UploadApp extends JApplet {

	private JButton select = new JButton("选择文件");
	private MyTableModel tableModel = new MyTableModel();
	private JButton upload = new JButton("上传");
	private JButton stop = new JButton("停止");
	private JTable table = new JTable(tableModel);
	private TableColumnModel column = table.getColumnModel();
	private List<File> fileList;
	private List<FileData> fileDatas = new ArrayList<FileData>();
	@SuppressWarnings("unused")
	private int createrID;
	public static String url;
	static int row = 0;
	public static boolean destory = false;
	public static JOptionPane confirm;
	private List<Write> threadList = new ArrayList<Write>();
	private static String currentPath = null;
	public static int countUpload = 0;

	public void init() {
		try {
			BeautyEyeLNFHelper.launchBeautyEyeLNF();
		} catch (Exception e) {
			WebRequest.writeLog(e);
		}
		 url = getParameter("url");

		//url = "http://192.168.13.95:8080/mms/";

		getContentPane().setLayout(new BorderLayout());
		// getContentPane().setSize(800,300);
		JPanel jp = new JPanel();
		this.getColumn().getColumn(0).setPreferredWidth(35);
		this.getColumn().getColumn(0).setMaxWidth(35);
		this.getColumn().getColumn(0).setMinWidth(35);
		this.getColumn().getColumn(5).setPreferredWidth(50);
		this.getColumn().getColumn(5).setMaxWidth(50);
		this.getColumn().getColumn(5).setMinWidth(50);
		getContentPane().add(jp);
		this.getSelect().setBounds(0, 0, 30, 50);
		this.getUpload().setBounds(60, 0, 30, 50);
		this.getStop().setBounds(120, 0, 30, 50);
		jp.add(this.getSelect());
		jp.add(this.getUpload());
		// jp.add(this.getStop());
		JScrollPane scrollpane = new JScrollPane();
		scrollpane.setPreferredSize(new Dimension(500, 230));
		this.getTable().setCellSelectionEnabled(true);
		scrollpane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollpane.getViewport().add(this.getTable());
		jp.add(scrollpane);

		this.getSelect().addMouseListener(new java.awt.event.MouseAdapter() {
			@SuppressWarnings("static-access")
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (!WebRequest.connected()) {
					confirm.showMessageDialog(null, "网络连接异常", "错误信息",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				getUpload().setText("正在校验文件，请稍等");
				JFileChooser jfChooser;
				if (currentPath != null) {
					jfChooser = new JFileChooser(currentPath);
				} else {
					jfChooser = new JFileChooser();
				}
				jfChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfChooser.setMultiSelectionEnabled(true);
				int res = jfChooser.showOpenDialog(jfChooser);
				if (JFileChooser.APPROVE_OPTION == res) {
					File[] files = jfChooser.getSelectedFiles();
					currentPath = files[0].getParentFile().getAbsolutePath();
					int size = files.length;
					for (int i = 0; i < size; i++) {
						if (files[i].getName().length() > 50) {
							JOptionPane.showMessageDialog(
									null,
									files[i].getName()
											+ "文件名过长，请修改后重新上传\r\n只允许小于50个字符的文件名",
									"错误信息", JOptionPane.ERROR_MESSAGE);
							if (i == (size - 1)) {
								getUpload().setText("上传");
							}
							continue;
						}
						FileData fd = new FileData();
						fd.setRow(row);
						fd.setFile(files[i]);
						fd.setCreaterID(getCreaterID());
						getColumn().getColumn(2).setCellRenderer(
								new MyTableCellRenderProcessBar());
						getColumn().getColumn(5).setCellRenderer(
								new MyTableCellRenderButton("", row));
						boolean chooser = false;
						try {
							fd.setMd5(getFileName(files[i]));
							for (FileData data : getFileDatas()) {
								if (data.getMd5().equals(fd.getMd5())) {
									JOptionPane.showMessageDialog(null,
											"已选择与文件" + files[i].getName()
													+ "相同文件，请重新选择", "错误信息",
											JOptionPane.ERROR_MESSAGE);
									chooser = true;
								}
							}
							fd.setStatus(WebRequest.exists(files[i].getName(),
									fd.getMd5()));
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							WebRequest.writeLog(e1);
						}
						if (chooser) {
							getUpload().setText("上传");
							continue;
						}
						if (fd.getStatus() == 2) {
							getTableModel().addRow(
									new Object[] { row, files[i].getName(),
											100, getSize(files[i]), "文件已存在",
											"删除" });
							getFileDatas().add(fd);
						} else {
							getTableModel().addRow(
									new Object[] { row, files[i].getName(), 0,
											getSize(files[i]), "等待上传", "删除" });
							if (fd.getStatus() != 1) {
								countUpload++;
							}
							getFileDatas().add(fd);
						}
						row++;
						getUpload().setText("上传");

					}
				} else {
					getUpload().setText("上传");
				}
			}
		});
		getUpload().addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				System.out.println(countUpload);
				if (countUpload == 0) {
					return;
				}
				if (countUpload != 0) {
					System.out.println("hide");
					getUpload().setVisible(false);
				}
				// getUpload().setEnabled(false);
				destory = false;
				for (int i = 0; i < getFileDatas().size(); i++) {
					if (Integer.parseInt(getTableModel().getValueAt(i, 2)
							.toString()) >= 100) {
						continue;
					}
					if (getFileDatas().get(i).getStatus() == 1) {
						// FtpCtrl.pro.put(i, 100L);
						if (new WebRequest().insert(getFileDatas().get(i))) {
							getTableModel().setValueAt("上传成功", i, 4);
							getTableModel().setValueAt(100, i, 2);
						} else {
							getTableModel().setValueAt("上传失败", i, 4);
							getTableModel().setValueAt(0, i, 2);
						}
					} else {
						FtpCtrl.pro.put(i, 0L);
						FtpClient client = new FtpClient();
						getTableModel().setValueAt("正在上传", i, 4);
						Write w = new Write(client, i);
						// w.start();
						Read r = new Read(client, i);
						// r.start();
						ThreadPool.getInstance().start(w, r);
						threadList.add(w);
					}

					// threadList.add(r);
				}
			}
		});
		getStop().addMouseListener(new java.awt.event.MouseAdapter() {
			@SuppressWarnings("static-access")
			public void mouseClicked(java.awt.event.MouseEvent e) {
				// destory = true;
				for (Write thread : threadList) {
					if (thread == null) {
						continue;
					}
					try {
						thread.getClient().getCtrl().getFtpClient()
								.disconnect();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					if (!thread.interrupted()) {
						thread.interrupt();
					}
				}
				threadList = new ArrayList<Write>();
			}
		});
		setVisible(true);
	}

	public int getCreaterID() {
		 return Integer.parseInt(getParameter("createrID"));
		//return 67;
	}

	public void setCreaterID(int createrID) {
		this.createrID = createrID;
	}

	public List<Write> getThreadList() {
		return threadList;
	}

	public void setThreadList(List<Write> threadList) {
		this.threadList = threadList;
	}

	public JButton getSelect() {
		return select;
	}

	public void setSelect(JButton select) {
		this.select = select;
	}

	public MyTableModel getTableModel() {
		return tableModel;
	}

	public void setTableModel(MyTableModel tableModel) {
		this.tableModel = tableModel;
	}

	public JButton getUpload() {
		return upload;
	}

	public void setUpload(JButton upload) {
		this.upload = upload;
	}

	public JButton getStop() {
		return stop;
	}

	public void setStop(JButton stop) {
		this.stop = stop;
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	public TableColumnModel getColumn() {
		return column;
	}

	public void setColumn(TableColumnModel column) {
		this.column = column;
	}

	public List<File> getFileList() {
		return fileList;
	}

	public void setFileList(List<File> fileList) {
		this.fileList = fileList;
	}

	public List<FileData> getFileDatas() {
		return fileDatas;
	}

	public void setFileDatas(List<FileData> fileDatas) {
		this.fileDatas = fileDatas;
	}

	public static int getRow() {
		return row;
	}

	public static void setRow(int row) {
		UploadApp.row = row;
	}

	public static String getSize(File file) {
		// try {
		// return new FileInputStream(file).available() / 1024 + " kb";
		// } catch (FileNotFoundException e) {
		// WebRequest.writeLog(e);
		// return null;
		// } catch (IOException e) {
		// WebRequest.writeLog(e);
		// return null;
		// }
		return String.valueOf(file.length() / 1024) + " kb";
	}

	public String getFileName(File file) throws IOException {
		// int splitIndex = file.getName().lastIndexOf(".");
		return MD5Util.getFileMD5String(file);
	}

	class MyTableModel extends DefaultTableModel {

		// 使用提供的默认数据模型，但是稍微的更改一下，主要是为了设定某些列可以不可以更改之类的

		boolean isEdit[] = { false, false, false, false, false, false };// 设置所有列都不可以更改;

		public MyTableModel() {

			super(null, new String[] { "标号", "文件名", "进度", "大小", "状态", "操作" });

		}

		public boolean isCellEditable(int row, int col) {

			return isEdit[col];

		}

	}

	class MyTableCellRenderButton extends JButton implements TableCellRenderer {
		@SuppressWarnings("unused")
		private int rowNum = 0;

		public MyTableCellRenderButton(String text, int row) {
			super(text);

		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			this.setText(value.toString());
			if (isSelected
					&& getTableModel().getValueAt(row, 4).toString()
							.equals("正在上传")) {
				// JOptionPane.showMessageDialog(null,
				// "该文件正在上传，无法删除", "警告",
				// JOptionPane.ERROR_MESSAGE);
			} else if (isSelected) {
				/*
				 * System.out.println("isSelected"); int choose =
				 * JOptionPane.showConfirmDialog(null, "确定删除此行么", "删除",
				 * JOptionPane.YES_NO_OPTION); //
				 * this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); if
				 * (choose == JOptionPane.YES_OPTION) { //
				 * System.out.println(JOptionPane.QUESTION_MESSAGE);
				 * 
				 * System.out.println("Result=Yes"); System.out.println(row +
				 * "       " + column); //System.exit(0); // JOptionPane. //
				 * return null; isSelected=false; hasFocus=false;
				 */
				// System.out.println(getTableModel().getValueAt(row+1, 1));
				int rowNum = Integer.parseInt(getTableModel()
						.getValueAt(row, 0).toString());
				FileData remove = new FileData();
				for (FileData f : getFileDatas()) {
					if (f.getRow() == rowNum) {
						remove = f;
					}
				}
				if (getTableModel().getValueAt(row, 4).toString()
						.equals("等待上传")) {
					countUpload--;
				}

				getTableModel().removeRow(row);
				getFileDatas().remove(remove);
				// for(FileData f :getFileDatas()){
				// System.out.println(f.getRow()+""+f.getFile().getName());
				// }
				// }
				// System.out.println("selected");
				// System.out.println(row+"         "+column);
			}
			return this;
		}
	}

	class MyTableCellRenderProcessBar extends JProgressBar implements
			TableCellRenderer {

		public MyTableCellRenderProcessBar() {

			super(0, 100);

			this.setForeground(new Color(45, 147, 192));

			this.setStringPainted(true);

			this.setBorderPainted(false);

		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (value != null) {
				this.setValue(Integer.parseInt(value.toString()));
			} else {
				value = 0;
			}
			return this;
		}

	}

	class Write extends Thread {
		private FtpClient client;
		private Integer i;

		public Write(FtpClient client, Integer i) {
			this.client = client;
			this.i = i;
		}

		public FtpClient getClient() {
			return client;
		}

		@SuppressWarnings("static-access")
		public void run() {
			try {
				FtpStatus status = client.upload(getFileDatas().get(i)
						.getFile().getAbsolutePath(), getFileDatas().get(i)
						.getMd5()
						+ "&_^"
						+ getFileDatas().get(i).getFile().getName()
						+ "&_@"
						+ getFileDatas().get(i).getCreaterID() + ".temp~", i);
				getTableModel().setValueAt(status.getDescription(), i, 4);
				countUpload--;
				if (countUpload == 0) {
					// getUpload().setEnabled(true);
					getUpload().setVisible(true);
				}
				// System.out.println(status.getDescription());
			} catch (java.io.IOException e) {
				confirm.showMessageDialog(null, "文件写入异常，请稍候再试", "错误信息",
						JOptionPane.ERROR_MESSAGE);
				getTableModel().setValueAt("停止", i, 4);
				WebRequest.writeLog(e);
			}

			if (getTableModel().getValueAt(i, 4).toString()
					.equals(FtpStatus.Upload_New_File_Success.getDescription())
					|| !getTableModel().getValueAt(i, 4).toString()
							.equals(FtpStatus.File_Exits.getDescription())
					|| !getTableModel()
							.getValueAt(i, 4)
							.toString()
							.equals(FtpStatus.Upload_From_Break_Success
									.getDescription())) {
				getTableModel().setValueAt(100, i, 2);
				getFileDatas().remove(i);
			}
		}
	}

	@SuppressWarnings("unused")
	class Read extends Thread {
		private FtpClient client;
		private Integer i;

		public Read(FtpClient client, Integer i) {
			this.client = client;
			this.i = i;
		}

		@SuppressWarnings("static-access")
		public void run() {
			if (!getTableModel().getValueAt(i, 4).toString()
					.equals(FtpStatus.Upload_New_File_Success.getDescription())
					|| !getTableModel().getValueAt(i, 4).toString()
							.equals(FtpStatus.File_Exits.getDescription())
					|| !getTableModel()
							.getValueAt(i, 4)
							.toString()
							.equals(FtpStatus.Upload_From_Break_Success
									.getDescription())) {
				// TODO client.getCtrl().pro.get(i)==null
				while ((FtpCtrl.pro.get(i) == null || FtpCtrl.pro.get(i) < 100)) {
					try {
						this.sleep(500);
						getTableModel().setValueAt(FtpCtrl.pro.get(i), i, 2);
						if (FtpCtrl.pro.get(i) >= 100) {
							this.interrupt();
							break;
						}
					} catch (InterruptedException e) {
						System.err.println("InterruptedException,Thread.Read");
						WebRequest.writeLog(e);
					}

				}
			}
		}

	}
}
