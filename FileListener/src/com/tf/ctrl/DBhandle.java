package com.tf.ctrl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.JOptionPane;

import com.tf.model.Element;
import com.tf.util.Logs;
import com.tf.util.XmlUtil;
import com.tf.view.Listener;

public class DBhandle {

	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	int count = 0;

	public int Insert(Element element) {
		lock.writeLock().lock();
		Connection conn = null;
		int res = -1;
		try {
			conn = ConnectionSource.getConnection();
			String sql = "insert into t_element (CreatorID,FileName,FileSize,FilePath,TYPE,Resolution,TimeLength,ThumbnailUrl,AuditStatus,md5,description,uploadTime,isDeleted) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, element.getuId());
			ps.setString(2, element.getFileName());
			ps.setString(3, element.getFileSize());
			ps.setString(4, element.getFilePath());
			ps.setInt(5, element.getType());
			ps.setString(6, element.getResolution());
			ps.setString(7, element.getTimeLength());
			ps.setString(8, element.getThumbnailUrl());
			ps.setInt(9, getAuditStatus());
			ps.setString(10, element.getMd5());
			ps.setString(11, element.getDescription());
			ps.setTimestamp(12, element.getUploadTime());
			ps.setShort(13, element.getIsDeleted());
			res = ps.executeUpdate();
			conn.close();
		} catch (SQLException e) {
			Listener.area.append("异常警告\r\n" + "SQL异常，正在尝试重新连接\r\n");
			JOptionPane.showMessageDialog(null, "数据库连接异常,请重新填写数据库连接信息", "错误信息",
					JOptionPane.OK_OPTION);
			Logs.WriteLogs(e);
			ConnectionSource.reset();
			Insert(element);
		} finally {
			lock.writeLock().unlock();
		}
		writeOprationLog(element.getFileName(), element.getuId());
		return res;
	}

	public int fail(Element element) {
		lock.writeLock().lock();
		int res = -1;
		Connection conn = null;
		try {
			conn = ConnectionSource.getConnection();
			String sql = "insert into t_element (CreatorID,FileName,TYPE,description,uploadTime,isDeleted,AuditStatus) values(?,?,?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, element.getuId());
			ps.setString(2, element.getFileName());
			ps.setInt(3, element.getType());
			ps.setString(4, element.getDescription());
			ps.setTimestamp(5, element.getUploadTime());
			ps.setShort(6, element.getIsDeleted());
			ps.setInt(7, -1);
			res = ps.executeUpdate();
			conn.close();
		} catch (SQLException e) {
			Listener.area.append("异常警告\r\n" + "SQL异常，正在尝试重新连接\r\n");
			JOptionPane.showMessageDialog(null, "数据库连接异常,请重新填写数据库连接信息", "错误信息",
					JOptionPane.OK_OPTION);
			Logs.WriteLogs(e);
			ConnectionSource.reset();
			fail(element);
		} finally {
			lock.writeLock().unlock();
		}
		writeOprationLog(element.getFileName()+"(上传失败)", element.getuId());
		return res;

	}

	public int getAuditStatus() {
		lock.readLock().lock();
		Connection conn = null;
		int res = 1;
		try {
			conn = ConnectionSource.getConnection();
			String sql = "select IsAuditing from t_module where t_id = 4";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getInt("IsAuditing") == 1) {
					res = 0;
				}
			}
			conn.close();
		} catch (SQLException e) {
			// Listener.area.append("异常警告\r\n" + "SQL异常，正在尝试重新连接\r\n");
			if (count < 10) {
				count++;
				getAuditStatus();
			} else {
				JOptionPane.showMessageDialog(null, "数据库连接异常,请重新填写数据库连接信息",
						"错误信息", JOptionPane.OK_OPTION);
				Logs.WriteLogs(e);
				ConnectionSource.reset();
				getAuditStatus();
			}
		} finally {
			lock.readLock().unlock();
		}
		return res;
	}

	public void setCryptographic() {
		lock.readLock().lock();
		Connection conn = null;
		try {
			conn = ConnectionSource.getConnection();
			String sql = "";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				XmlUtil.saveOrUpdate("cryptographic",
						rs.getString("cryptographic"));
			}
			conn.close();
		} catch (SQLException e) {
			// Listener.area.append("异常警告\r\n" + "SQL异常，正在尝试重新连接\r\n");
			JOptionPane.showMessageDialog(null, "数据库连接异常,请重新填写数据库连接信息", "错误信息",
					JOptionPane.OK_OPTION);
			Logs.WriteLogs(e);
			ConnectionSource.reset();
			setCryptographic();
		} finally {
			lock.readLock().unlock();
		}
	}

	public Element getWebElement(int id) {
		Element element = new Element();
		element.setTId(id);
		lock.readLock().lock();
		Connection conn = null;
		try {
			conn = ConnectionSource.getConnection();
			String sql = "select FileName,FilePath from t_element where t_id = "
					+ id;
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				element.setFileName(rs.getString("FileName"));
				element.setFilePath(rs.getString("FilePath"));
			}
			conn.close();
		} catch (SQLException e) {
			Listener.area.append("异常警告\r\n" + "SQL异常，正在尝试重新连接\r\n");
			JOptionPane.showMessageDialog(null, "数据库连接异常,请重新填写数据库连接信息", "错误信息",
					JOptionPane.OK_OPTION);
			Logs.WriteLogs(e);
			ConnectionSource.reset();
			getWebElement(id);
		} finally {
			lock.readLock().unlock();
		}
		return element;
	}

	public int updateElement(Element element) {
		int res = -1;
		lock.writeLock().lock();
		Connection conn = null;
		try {
			conn = ConnectionSource.getConnection();
			String sql = "update t_element set Resolution=?,ThumbnailUrl=?,IsDeleted=? where t_id = "
					+ element.getTId();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, element.getResolution());
			ps.setString(2, element.getThumbnailUrl());
			ps.setShort(3, Short.parseShort("0"));
			res = ps.executeUpdate();
			conn.close();
		} catch (SQLException e) {
			Listener.area.append("异常警告\r\n" + "SQL异常，正在尝试重新连接\r\n");
			JOptionPane.showMessageDialog(null, "数据库连接异常,请重新填写数据库连接信息", "错误信息",
					JOptionPane.OK_OPTION);
			Logs.WriteLogs(e);
			ConnectionSource.reset();
			updateElement(element);
		} finally {
			lock.writeLock().unlock();
		}
		writeOprationLog(element.getTId());
		return res;
	}

	public int writeLogs(String sql, String mac) {
		int res = -1;
		lock.writeLock().lock();
		Connection conn = null;
		try {
			// insert into t_statistics select null,t_id,name,+sql+,now() from
			// t_terminal where MAC = '88-51-FB-71-3D-91'
			conn = ConnectionSource.getConnection();
			String insert = "insert into t_statistics select null,t_id,name,"
					+ sql.trim().replaceAll(";", "")
					+ ",now() from t_terminal where MAC = '" + mac + "'";
			Logs.WriteLogs("insert:--");
			Logs.WriteLogs(insert);
			PreparedStatement ps = conn.prepareStatement(insert);
			res = ps.executeUpdate();
			conn.close();
		} catch (SQLException e) {
			Logs.WriteLogs(e);
			Listener.area.append("异常警告\r\n" + "统计信息写入异常\r\n");
			return -5;
		} finally {
			lock.writeLock().unlock();
		}
		return res;
	}

	public int writeOprationLog(String elementName, int userId) {
		int res = -1;
		lock.writeLock().lock();
		Connection conn = null;
		try {
			// insert into t_statistics select null,t_id,name,+sql+,now() from
			// t_terminal where MAC = '88-51-FB-71-3D-91'
			conn = ConnectionSource.getConnection();
			String insert = "insert into t_operationlog select null,username,'"+elementName+"',now(),1,4 from t_user where t_id = "+userId;
			PreparedStatement ps = conn.prepareStatement(insert);
			res = ps.executeUpdate();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Listener.area.append("异常警告\r\n" + "SQL异常，正在尝试重新连接\r\n");
			JOptionPane.showMessageDialog(null, "数据库连接异常,请重新填写数据库连接信息", "错误信息",
					JOptionPane.OK_OPTION);
			Logs.WriteLogs(e);
			ConnectionSource.reset();
			writeOprationLog(elementName, userId);
		} finally {
			lock.writeLock().unlock();
		}
		return res;
	}
	public int writeOprationLog( int webElementId) {
		int res = -1;
		lock.writeLock().lock();
		Connection conn = null;
		try {
			// insert into t_statistics select null,t_id,name,+sql+,now() from
			// t_terminal where MAC = '88-51-FB-71-3D-91'
			conn = ConnectionSource.getConnection();
			String insert = "insert into t_operationlog select null ,t_user.username,t_element.FileName,now(),1,4 from t_element ,t_user where t_element.CreatorID = t_user.t_id and t_element.t_id = "+webElementId;
//			String insert = "insert into t_operationlog select null,creatorID,FileName,now(),1,4  from t_element where t_id = "+webElementId;
			PreparedStatement ps = conn.prepareStatement(insert);
			res = ps.executeUpdate();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Listener.area.append("异常警告\r\n" + "SQL异常，正在尝试重新连接\r\n");
			JOptionPane.showMessageDialog(null, "数据库连接异常,请重新填写数据库连接信息", "错误信息",
					JOptionPane.OK_OPTION);
			Logs.WriteLogs(e);
			ConnectionSource.reset();
			writeOprationLog(webElementId);
		} finally {
			lock.writeLock().unlock();
		}
		return res;
	}
}
