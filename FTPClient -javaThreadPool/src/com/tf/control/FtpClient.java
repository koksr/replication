package com.tf.control;

import java.io.IOException;
import java.sql.Connection;

import org.apache.commons.net.ftp.FTPClient;

import com.tf.model.FtpStatus;

public class FtpClient {
	private FtpCtrl ctrl;
	private Connection conn;
	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public FtpCtrl getCtrl() {
		return ctrl;
	}

	public void setCtrl(FtpCtrl ctrl) {
		this.ctrl = ctrl;
	}

	public FtpClient() {
		super();
		this.ctrl = new FtpCtrl();
	}

	public FtpStatus upload(final String local, final String remote, final int row) throws IOException {
		return ctrl.connect(new FtpClientCallback<FtpStatus>() {
			public FtpStatus doTransfer(FTPClient ftpClient) throws IOException {
				return ctrl.upload(ftpClient, local, remote, row);
			}
		});
	}

//	public boolean checkFile(String md5){
//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			conn = DriverManager.getConnection(
//					"jdbc:mysql://192.168.10.95:3306/mms?useUnicode=true&characterEncoding=UTF-8", "root", "twinflag");
//			String sql = "select count(*) from t_element where MD5 = '"+md5+"'";
//			PreparedStatement ps = conn.prepareStatement(sql);
//			ResultSet rs = ps.executeQuery();
//			if(rs.next()&&rs.getInt("count(*)")>=1){
//				return true;
//			}
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally{
//			try {
//				conn.close();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return false;
//	}
}
