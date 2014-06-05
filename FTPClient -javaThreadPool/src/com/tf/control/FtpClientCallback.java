package com.tf.control;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

public interface FtpClientCallback<T> {
	public T doTransfer(FTPClient ftp)throws IOException;
}
