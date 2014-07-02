package com.tf.ctrl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

import com.tf.init.DBInit;
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
//		File f = new File(System.getProperty("user.dir") + "\\DBConf.xml");
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder builder;
		try {
//			builder = factory.newDocumentBuilder();
//			Document doc = builder.parse(f);
//			String url = doc.getElementsByTagName("url").item(0)
//					.getFirstChild().getNodeValue();
//			String port = "3306";
//			Object temp = doc.getElementsByTagName("port").item(0)
//					.getFirstChild();
//			if (doc.getElementsByTagName("port").item(0).getFirstChild() != null)
//				port = doc.getElementsByTagName("port").item(0).getFirstChild()
//						.getNodeValue();
//			String name = doc.getElementsByTagName("name").item(0)
//					.getFirstChild().getNodeValue();
//			String user = doc.getElementsByTagName("user").item(0)
//					.getFirstChild().getNodeValue();
//			String pwd = doc.getElementsByTagName("pwd").item(0)
//					.getFirstChild().getNodeValue();
			Properties p = new Properties();
			p.setProperty("driverClassName", "com.mysql.jdbc.Driver");
			p.setProperty("url", "jdbc:mysql://"+DBInit.getData().getUrl()+":"+DBInit.getData().getPort()+"/"+DBInit.getData().getName()+"?useUnicode=true&characterEncoding=utf-8&autoReconnect=true&failOverReadOnly=false");
			p.setProperty("password", DBInit.getData().getPwd());
			p.setProperty("username", DBInit.getData().getUser());
			System.out.println(DBInit.getData().getPwd());
			System.out.println(DBInit.getData().getUser());
			
			
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

		}catch (Exception e) {
			Logs.WriteLogs(e);
			Listener.area.append(ElementUtil.dateFormat.format(System
					.currentTimeMillis()) + "	"+e.getMessage()+"\r\n");
		}
	}

	public static synchronized Connection getConnection() throws SQLException {
		if (dataSource == null) {
			init();
		}
		Connection conn = null;
		if (dataSource != null) {
				conn = dataSource.getConnection();
//			} catch (SQLException e) {
//				Logs.WriteLogs(e);
//				DBInit.getData().InputMsg();
//				init();
//				Listener.area.append(ElementUtil.dateFormat.format(System
//						.currentTimeMillis()) + "	"+e.getMessage()+"\r\n");
//				
//			}
		}
		return conn;
	}
	public static synchronized void reset(){
		DBInit.getData().InputMsg();
		init();
	}
}
