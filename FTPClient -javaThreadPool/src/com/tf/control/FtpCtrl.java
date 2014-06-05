package com.tf.control;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.tf.model.FtpStatus;
import com.tf.view.UploadApp;
import com.twinflag.mms.entity.FtpServer;

public class FtpCtrl {
	private static String DEFAULT_REMOTE_CHARSET = "UTF-8";
	private static String DEFAULT_LOCAL_CHARSET = "iso-8859-1";
	private FTPClient ftpClient;
	public static Map<Integer, Long> pro = new HashMap<Integer,Long>();

	@SuppressWarnings("static-access")
	public <T> T connect(FtpClientCallback<T> callback)throws IOException {
		ftpClient = new FTPClient();
		FtpServer config =WebRequest.ServerConfig();
		ftpClient.setDataTimeout(7200);
		try {
			ftpClient.connect(config.getIP(), Integer.parseInt(config.getPort()));
			ftpClient.setControlEncoding(DEFAULT_REMOTE_CHARSET);
			if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				if (ftpClient.login(config.getUser(), config.getPwd())) {
					ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
					return callback.doTransfer(ftpClient);
				}
			}

		} catch (SocketException e) {
			System.out.println("initException");
			UploadApp.destory=true;
			UploadApp.confirm.showMessageDialog(null, "网络连接异常,请稍候再试", "错误信息", JOptionPane.ERROR_MESSAGE);
			WebRequest.writeLog(e);
		}  finally {
			try {
				ftpClient.logout();
				disconnect(ftpClient);
			} catch (IOException e) {
				WebRequest.writeLog(e);
			}
		}
		return null;
	}

	public boolean isConnect() {
		return this.getFtpClient().isConnected();
	}

	public void disconnect(FTPClient ftpClient) throws IOException {
		if (ftpClient.isConnected()) {
			ftpClient.disconnect();
		}
	}

	public void stop() throws IOException {
		this.getFtpClient().disconnect();
	}

	public FtpStatus mkDir(String remote, FTPClient ftpClient)
			throws IOException {
		FtpStatus status = FtpStatus.Create_Directory_Success;
		String dir = remote.substring(0, remote.lastIndexOf("/") + 1);
		if (!dir.equalsIgnoreCase("/")
				&& !ftpClient.changeWorkingDirectory(new String(dir
						.getBytes(DEFAULT_REMOTE_CHARSET),
						DEFAULT_LOCAL_CHARSET))) {
			int start = 0;
			int end = 0;
			if (dir.startsWith("/")) {
				start = 1;
			} else {
				start = 0;
			}
			end = dir.indexOf("/", start);
			while (true) {
				String subDir = new String(remote.substring(start, end)
						.getBytes(DEFAULT_REMOTE_CHARSET),
						DEFAULT_LOCAL_CHARSET);
				if (!ftpClient.changeWorkingDirectory(subDir)) {
					if (ftpClient.makeDirectory(subDir)) {
						ftpClient.changeWorkingDirectory(subDir);
					} else {
						return FtpStatus.Create_Directory_Fail;
					}
				}
				start = end + 1;
				end = dir.indexOf("/", start);
				if (end <= start) {
					break;
				}
			}
		}
		return status;
	}

	@SuppressWarnings({ "static-access", "resource" })
	public FtpStatus uploadSeparateFile(String remoteFile, File localFile,
			FTPClient ftpClient, long remoteSize, int row) throws IOException {
		FtpStatus status;
		Long step = localFile.length() / 100;
		Long process = 0L;
		Long localReadBytes = 0L;
		RandomAccessFile raf = new RandomAccessFile(localFile, "r");
		String remote = new String(remoteFile.getBytes(DEFAULT_REMOTE_CHARSET),
				DEFAULT_LOCAL_CHARSET);
		OutputStream os = ftpClient.appendFileStream(remote);
		if (os == null) {
			String message = ftpClient.getReplyString();
			throw new RuntimeException(message);
		}
		if (remoteSize > 0) {
			ftpClient.setRestartOffset(remoteSize);
			process = remoteSize / step;
			raf.seek(remoteSize);
			localReadBytes = remoteSize;
		}

		byte[] bytes = new byte[1024];
		int c;
		try {
			while ((c = raf.read(bytes)) != -1) {
				os.write(bytes, 0, c);
				localReadBytes += c;
				if (localReadBytes / step != process) {
					process = localReadBytes / step;
				//	remoteFile.renameTo(new File(remoteFile.getAbsolutePath().split(".temp~")[0]));
					// TODO 进度条callback
					pro.put(row, process);
					//System.out.println("sys               " + pro);
				}
			}
		} catch (java.net.SocketException e) {
			System.out.println("socketException");
			ftpClient.disconnect();
			if(UploadApp.destory==false){
				UploadApp.destory=true;
				UploadApp.confirm.showMessageDialog(null, "网络连接异常,请稍候再试", "错误信息", JOptionPane.ERROR_MESSAGE);
			}
		}
		os.flush();
		raf.close();
		os.close();
		boolean result = ftpClient.completePendingCommand();
		if (remoteSize > 0) {
			if(result){
				ftpClient.rename(remote,remote.split(".temp~")[0]);
				status=FtpStatus.Upload_From_Break_Success;
			}else{
				status=FtpStatus.Upload_From_Break_Failed;
			}
			
		} else {
			if(result){
				ftpClient.rename(remote, remote.split(".temp~")[0]);
				status=FtpStatus.Upload_New_File_Success;
			}else{
				status=FtpStatus.Upload_New_File_Failed;
			}
		}

		return status;

	}

	@SuppressWarnings("static-access")
	public FtpStatus upload(FTPClient ftpClient, String localFilePath,
			String remoteFilePath, int row) throws IOException {

		ftpClient.enterLocalPassiveMode();
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		ftpClient.setControlEncoding(DEFAULT_REMOTE_CHARSET);
		FtpStatus result;
		String remoteFileName = remoteFilePath;
		if (remoteFilePath.contains("/")) {
			remoteFileName = remoteFilePath.substring(remoteFilePath
					.lastIndexOf("/" + 1));
			if (mkDir(remoteFilePath, ftpClient) == FtpStatus.Create_Directory_Fail) {
				return FtpStatus.Create_Directory_Fail;
			}
		}
		FTPFile[] files = ftpClient.listFiles(new String(remoteFileName
				.getBytes(DEFAULT_REMOTE_CHARSET), DEFAULT_LOCAL_CHARSET));
		if (files.length == 1) {
			long remoteSize = files[0].getSize();
			File file = new File(localFilePath);
			long localSize = file.length();
			if (remoteSize == localSize) {
				this.pro.put(row, 100L);
				return FtpStatus.File_Exits;

			} else if (remoteSize > localSize) {
				return FtpStatus.Remote_Bigger_Local;
			}
			result = uploadSeparateFile(remoteFileName, file, ftpClient,
					remoteSize, row);
			if (result == FtpStatus.Upload_From_Break_Failed) {
				if (!ftpClient.deleteFile(remoteFileName)) {
					return FtpStatus.Delete_Remote_Faild;
				}
				result = uploadSeparateFile(remoteFileName, file, ftpClient, 0,
						row);
			}
		} else {
			result = uploadSeparateFile(remoteFileName,
					new File(localFilePath), ftpClient, 0, row);

		}

		return result;
	}

	public FTPClient getFtpClient() {
		return ftpClient;
	}

	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}

}
