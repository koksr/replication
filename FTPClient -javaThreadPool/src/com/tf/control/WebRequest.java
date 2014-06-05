package com.tf.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import com.tf.model.FileData;
import com.tf.view.UploadApp;
import com.twinflag.mms.entity.FtpServer;

public class WebRequest {
	public static FtpServer ServerConfig() {
		FtpServer fcon = null;
		ObjectInputStream objStream = null;
		try {
			URL url = new URL(UploadApp.url + "ftpServerConfig.action");
			//URL url = new URL("http://localhost:8080/mms/ftpServerConfig.action");
			URLConnection con = url.openConnection();
			con.setUseCaches(false);
			objStream = new ObjectInputStream(con.getInputStream());
			fcon = (FtpServer) objStream.readObject();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				objStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fcon;
	}
	public static boolean connected(){
		boolean flag = false;
		URL url;
		try {
			url = new URL(UploadApp.url);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			if(con.getResponseCode()==200){
				flag = true;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}
	// public boolean checkFile(String md5) {
	// System.out.println("checkFile");
	// InputStream in = null;
	// boolean flag = false;
	// try {
	// URL url = new URL(UploadApp.url + "element/checkFile.action?md5="
	// + md5);
	// // URL url = new
	// // URL("http://localhost:8080/mms/element/checkFile.action?md5="+md5);
	// URLConnection con = url.openConnection();
	// con.setUseCaches(false);
	// in = con.getInputStream();
	// StringBuffer out = new StringBuffer();
	// byte[] b = new byte[1024];
	// for (int n; (n = in.read(b)) != -1;) {
	// out.append(new String(b, 0, n));
	// }
	// flag = Boolean.parseBoolean(new String(out));
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// in.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// return flag;
	// }

	public boolean insert(FileData data) {
		boolean flag = false;
		URL url;
		InputStream in = null;
		try {
			url = new URL(UploadApp.url + "element/insert.action");
			String params = "md5=" + data.getMd5() + "&name="
					+ URLEncoder.encode(data.getFile().getName(), "utf-8") + "&creater="
					+ data.getCreaterID();
			URLConnection con = url.openConnection();
			con = url.openConnection();
			con.setRequestProperty("accept", "*/*");
			con.setRequestProperty("connection", "Keep-Alive");
			// con.setRequestProperty("user-agent",
			// "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			con.setUseCaches(false);
			con.setDoOutput(true);
			con.setDoInput(true);
			con.connect();
			PrintWriter pw = new PrintWriter(con.getOutputStream());
			pw.print(params);
			pw.flush();
			in = con.getInputStream();
			StringBuffer out = new StringBuffer();
			byte[] b = new byte[1024];
			for (int n; (n = in.read(b)) != -1;) {
				out.append(new String(b, 0, n));
			}
			flag = Boolean.parseBoolean(new String(out));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return flag;
	}

	/**
	 * @return code </br> switch(-1):http连接异常</br>switch（0）：素材文件不存在; </br>
	 *         switch(1)：素材文件存在，但是当前用户没有上传过;</br> switch（2）：素材文件存在且当前用户上传过;
	 */
	public static int exists(String name, String md5) {
		URL url = null;
		String params = null;
		PrintWriter out = null;
		int flag = -1;
		InputStream in = null;
		URLConnection con = null;
		try {
			// url = new URL("http://localhost:8080/mms/element/exists.action");
			url = new URL(UploadApp.url + "element/exists.action");
			params = "name=" + URLEncoder.encode(name, "utf-8")+ "&md5=" + md5;
			con = url.openConnection();
			con.setRequestProperty("accept", "*/*");
			con.setRequestProperty("connection", "Keep-Alive");
			//con.set
			// con.setRequestProperty("user-agent",
			// "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			con.setUseCaches(false);
			con.setDoOutput(true);
			con.setDoInput(true);
			con.connect();
			out = new PrintWriter(con.getOutputStream());
			out.print(params);
			out.flush();
			in = con.getInputStream();
			StringBuffer outStr = new StringBuffer();
			byte[] b = new byte[1024];
			for (int n; (n = in.read(b)) != -1;) {
				outStr.append(new String(b, 0, n));
			}
			flag = Integer.parseInt(new String(outStr));

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return flag;
	}

	public static void writeLog(Exception exception) {
		URL url;
		PrintWriter out = null;
		String params = null;
		URLConnection con;
		try {
			url = new URL(UploadApp.url + "appletLogs/appletLogAction.action");
			// url = new
			// URL("http://localhost:8080/mms/appletLogs/appletLogAction.action");
			params = "ip=" + InetAddress.getLocalHost().getHostAddress()
					+ "&msg=" + URLEncoder.encode(exception.getMessage(),"utf-8");
			con = url.openConnection();
			con.setRequestProperty("accept", "*/*");
			con.setRequestProperty("connection", "Keep-Alive");
			con.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");

			con.setDoOutput(true);
			con.setDoInput(true);
			out = new PrintWriter(con.getOutputStream());
			out.print(params);
			exception.printStackTrace(out);
			out.flush();
			con.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}
}
