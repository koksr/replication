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
import com.tf.view.Listener;

public class DBhandle {

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

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
			Listener.area.append("异常警告\r\n" + "SQL异常，正在尝试重新连接\r\n");
			JOptionPane.showMessageDialog(null, "数据库连接异常,请重新填写数据库连接信息", "错误信息",
					JOptionPane.OK_OPTION);
			Logs.WriteLogs(e);
			ConnectionSource.reset();
			getAuditStatus();
		} finally {
			lock.readLock().unlock();
		}
		return res;
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
		return res;
	}
}
