package com.tf.init;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.tf.util.XmlUtil;

public class DBInit {
	private static DBInit data;
	private String url;
	private String port = "3306";
	private String name;
	private String user;
	private String pwd;

	public static synchronized DBInit getData() {
		if (data == null) {
			data = new DBInit();
		}
		return data;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public DBInit() {
		try {
			this.url=XmlUtil.getNodeValue("url");
			this.name=XmlUtil.getNodeValue("name");
			String temp = XmlUtil.getNodeValue("port");
			this.port=temp==null?"3306":temp;
			this.user=XmlUtil.getNodeValue("user");
			this.pwd=XmlUtil.getNodeValue("pwd");
		} catch (NullPointerException e) {
			InputMsg();
		} 
	}

	public void InputMsg() {
		JTextField urlInput = new JTextField(10);
		urlInput.setText("127.0.0.1");
		JTextField portInput = new JTextField(10);
		portInput.setText("3306");
		JTextField nameInput = new JTextField(10);
		nameInput.setText("mms");
		JTextField userInput = new JTextField(10);
		JPasswordField pwdInput = new JPasswordField(10);
		// pwdInput.set

		int res = JOptionPane.showConfirmDialog(null, new Object[] { "IP:",
				urlInput, "Port:", portInput, "数据库名:", nameInput, "用户名:",
				userInput, "密码:", pwdInput }, "请输入数据库信息",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (res == JOptionPane.OK_OPTION) {
			this.setUrl(urlInput.getText());
			this.setPort(portInput.getText());
			this.setName(nameInput.getText());
			this.setUser(userInput.getText());
			this.setPwd(new String(pwdInput.getPassword()).toString());
			XmlUtil.saveOrUpdate("url", this.url);
			XmlUtil.saveOrUpdate("port", this.port);
			XmlUtil.saveOrUpdate("name", this.name);
			XmlUtil.saveOrUpdate("user", this.user);
			XmlUtil.saveOrUpdate("pwd", this.pwd);
			//ConnectionSource.getConnection();
		}else{
			System.exit(0);
			return;
		}
		
	}

}
