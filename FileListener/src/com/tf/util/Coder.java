package com.tf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.tf.view.Listener;

public class Coder {
	public boolean execute(CodeUtil util) {
		try {
			DESKeySpec dks = new DESKeySpec("twinflagcoder".getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(dks);
			return util.doTransfer(secretKey);
		} catch (Exception e) {
			Logs.WriteLogs(e);
			e.printStackTrace();
			return false;
		}catch(Throwable e){
			Listener.area.append("error");
			Listener.area.append(e.getMessage());
			return false;
		}
	}

	public boolean encode(final String path) {
		return execute(new CodeUtil() {
			@Override
			public boolean doTransfer(Key key) throws Exception {
				File sourceFile = new File(path + ".codr");
				File tempFile = new File(path);
				if (!ElementUtil.renameTO(tempFile, sourceFile)) {
					return false;
				}
				;
				File destFile = new File(path);
				if (sourceFile.exists() && sourceFile.isFile()) {
					if (!destFile.getParentFile().exists()) {
						destFile.getParentFile().mkdirs();
					}
					destFile.createNewFile();
					InputStream in = new FileInputStream(sourceFile);
					OutputStream out = new FileOutputStream(destFile);
					Cipher cipher = Cipher.getInstance("DES");
					cipher.init(Cipher.ENCRYPT_MODE, key);
					CipherInputStream cin = new CipherInputStream(in, cipher);
					byte[] cache = new byte[1024];
					int nRead = 0;
					while ((nRead = cin.read(cache)) != -1) {
						out.write(cache, 0, nRead);
						out.flush();
					}
					out.close();
					cin.close();
					in.close();
					return (sourceFile.delete());
				} else {
					return false;
				}
			}
		});
	}

	public boolean encode(final String Sourcepath,final String destPath) {
		return execute(new CodeUtil() {
			@Override
			public boolean doTransfer(Key key) throws Exception {
				File sourceFile=new File(Sourcepath);
				File destFile = new File(destPath);
				if (sourceFile.exists() && sourceFile.isFile()) {
					if (!destFile.getParentFile().exists()) {
						destFile.getParentFile().mkdirs();
					}
					InputStream in = new FileInputStream(sourceFile);
					OutputStream out = new FileOutputStream(destFile);
					Cipher cipher = Cipher.getInstance("DES");
					cipher.init(Cipher.ENCRYPT_MODE, key);
					CipherInputStream cin = new CipherInputStream(in, cipher);
					byte[] cache = new byte[1024];
					int nRead = 0;
					while ((nRead = cin.read(cache)) != -1) {
						out.write(cache, 0, nRead);
						out.flush();
					}
					out.close();
					cin.close();
					in.close();
					return (sourceFile.delete());
				} else {
					return false;
				}
			}
		});
	}

	public boolean decode(final String path) {
		return execute(new CodeUtil() {
			@Override
			public boolean doTransfer(Key key) throws Exception {
				File sourceFile = new File(path + ".codr");
				File tempFile = new File(path);
				if (!ElementUtil.renameTO(tempFile, sourceFile)) {
					Thread.sleep(5000);
					return doTransfer(key);
				};
				File destFile = new File(path);
				if (sourceFile.exists() && sourceFile.isFile()) {
					if (!destFile.getParentFile().exists()) {
						destFile.getParentFile().mkdirs();
					}
					destFile.createNewFile();
					InputStream in = new FileInputStream(sourceFile);
					OutputStream out = new FileOutputStream(destFile);
					Cipher cipher = Cipher.getInstance("DES");
					cipher.init(Cipher.DECRYPT_MODE, key);
					CipherOutputStream cout = new CipherOutputStream(out,
							cipher);
					byte[] cache = new byte[1024];
					int nRead = 0;
					while ((nRead = in.read(cache)) != -1) {
						cout.write(cache, 0, nRead);
						cout.flush();
					}
					cout.close();
					out.close();
					in.close();
					return (sourceFile.delete());
				}
				return false;
			}
		});

	}

}
