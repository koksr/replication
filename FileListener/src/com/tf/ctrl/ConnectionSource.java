package com.tf.ctrl;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.w3c.dom.Document;

import com.tf.util.ElementUtil;
import com.tf.util.Logs;
import com.tf.view.Listener;

public class ConnectionSource {
	private static BasicDataSource dataSource = null;

	public ConnectionSource() {
	}

	public static void init() {

		if (dataSource != null) {
			try {
				dataSource.close();
			} catch (Exception e) {
				Logs.WriteLogs(e);
			}
			dataSource = null;
		}
		File f = new File(System.getProperty("user.dir") + "\\DBConf.xml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(f);
			String url = doc.getElementsByTagName("url").item(0)
					.getFirstChild().getNodeValue();
			String port = "3306";
			Object temp = doc.getElementsByTagName("port").item(0)
					.getFirstChild();
			if (doc.getElementsByTagName("port").item(0).getFirstChild() != null)
				port = doc.getElementsByTagName("port").item(0).getFirstChild()
						.getNodeValue();
			String name = doc.getElementsByTagName("name").item(0)
					.getFirstChild().getNodeValue();
			String user = doc.getElementsByTagName("user").item(0)
					.getFirstChild().getNodeValue();
			String pwd = doc.getElementsByTagName("pwd").item(0)
					.getFirstChild().getNodeValue();
		
			Properties p = new Properties();
			p.setProperty("driverClassName", "com.mysql.jdbc.Driver");
			p.setProperty("url", "jdbc:mysql://"+url+":"+port+"/"+name+"?useUnicode=true&characterEncoding=utf-8&autoReconnect=true&failOverReadOnly=false");
			p.setProperty("password", pwd);
			p.setProperty("username", user);
			
			
			
			p.setProperty("maxIdle", "2");
			p.setProperty("maxActive", "10");
			p.setProperty("maxWait", "1000");
			p.setProperty("testWhileIdle", "true");
			p.setProperty("removeAbandoned", "true");
			p.setProperty("removeAbandonedTimeout", "120");
			p.setProperty("testOnBorrow", "true");
			p.setProperty("logAbandoned", "true");
			p.setProperty("timeBetweenEvictionRunsMillis", "30000");
			p.setProperty("minEvictableIdleTimeMillis", "60000");

			dataSource = (BasicDataSource) BasicDataSourceFactory
					.createDataSource(p);

		} catch (Exception e) {
			Logs.WriteLogs(e);
			Listener.area.append(ElementUtil.dateFormat.format(System
					.currentTimeMillis()) + "	"+e.getMessage()+"\r\n");
		}
	}

	public static synchronized Connection getConnection()  {
		if (dataSource == null) {
			init();
		}
		Connection conn = null;
		if (dataSource != null) {
			try {
				conn = dataSource.getConnection();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Logs.WriteLogs(e);
				Listener.area.append(ElementUtil.dateFormat.format(System
						.currentTimeMillis()) + "	"+e.getMessage()+"\r\n");
			}
		}
		return conn;
	}
}
