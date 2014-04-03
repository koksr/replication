package com.twinflag.mms.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FtpServer implements Serializable{
	private String IP;
	private String Port;
	private String User;
	private String Pwd;
	
	public FtpServer() {
		super();
	}
	
	public FtpServer(String iP, String port, String user, String pwd) {
		super();
		IP = iP;
		Port = port;
		User = user;
		Pwd = pwd;
	}

	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	public String getPort() {
		return Port;
	}
	public void setPort(String port) {
		Port = port;
	}
	public String getUser() {
		return User;
	}
	public void setUser(String user) {
		User = user;
	}
	public String getPwd() {
		return Pwd;
	}
	public void setPwd(String pwd) {
		Pwd = pwd;
	}
	
}
