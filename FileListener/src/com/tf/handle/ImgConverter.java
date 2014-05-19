package com.tf.handle;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

import com.tf.util.ElementUtil;

public class ImgConverter {
/*	 public static List<String> pdf2Imgs(String pdfPath, String imgDirPath) throws Exception {
	        Document document = new Document();
	        document.setFile(pdfPath);
	        float scale = 2f;
	        float rotation = 0f;
	        List<String> imgNames = new ArrayList<String>();
	        int pageNum = document.getNumberOfPages();
	        File imgDir = new File(imgDirPath);
	        if (!imgDir.exists()) {
	            imgDir.mkdirs();
	        }
	        for (int i = 0; i < pageNum; i++) {
	            BufferedImage image = (BufferedImage) document.getPageImage(i, GraphicsRenderingHints.SCREEN,
	                    Page.BOUNDARY_CROPBOX, rotation, scale);
	            RenderedImage rendImage = image;
	            try {
	                String filePath = imgDirPath + File.separator +ElementUtil.getRealName(pdfPath)+"\\"+pageNum+ "_"+(i+1) + ".jpg";
	                System.out.println(filePath);
	                File file = new File(filePath);
	                if(!file.getParentFile().exists()){
	                	file.getParentFile().mkdirs();
	                }
	                ImageIO.write(rendImage, "jpg", file);
	                imgNames.add(FilenameUtils.getName(filePath));
	            } catch (IOException e) {
	                e.printStackTrace();
	                return null;
	            }
	            image.flush();
	        }
	        document.dispose();
	        return imgNames;
	    }*/
	 
	 public static List<File> Convert(File pdfFile) {
	        Document document = new Document();
	        try {
				document.setFile(pdfFile.getAbsolutePath());
			} catch (PDFException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (PDFSecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        float scale = 2f;
	        float rotation = 0f;
	        List<File> imgs = new ArrayList<File>();
	        int pageNum = document.getNumberOfPages();
	        File imgDir = new File(pdfFile.getParent()+File.separator+pageNum+"_"+ElementUtil.getRealName(pdfFile.getAbsolutePath()));
	        if (!imgDir.exists()) {
	            imgDir.mkdirs();
	        }
	        for (int i = 0; i < pageNum; i++) {
	            BufferedImage image = (BufferedImage) document.getPageImage(i, GraphicsRenderingHints.SCREEN,
	                    Page.BOUNDARY_CROPBOX, rotation, scale);
	            RenderedImage rendImage = image;
	            try {
	                String filePath = imgDir +File.separator+pageNum+ "_"+(i+1) + ".jpg";
	                File file = new File(filePath);
	                if(!file.getParentFile().exists()){
	                	file.getParentFile().mkdirs();
	                }
	                ImageIO.write(rendImage, "jpg", file);
	                imgs.add(file);
	            } catch (IOException e) {
	                e.printStackTrace();
	                return null;
	            }
	            image.flush();
	        }
	        document.dispose();
	        return imgs;
	    }
	 
}
