package com.imall.common.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.capaxit.imagegenerator.Alignment;
import org.capaxit.imagegenerator.Margin;
import org.capaxit.imagegenerator.Style;
import org.capaxit.imagegenerator.TextImage;
import org.capaxit.imagegenerator.imageexporter.ImageType;
import org.capaxit.imagegenerator.imageexporter.ImageWriter;
import org.capaxit.imagegenerator.imageexporter.ImageWriterFactory;
import org.capaxit.imagegenerator.impl.TextImageImpl;

import com.imall.common.enums.TextAlignEnum;
import com.imall.common.enums.WordStyleEnum;

/**
 * @author jianxunji
 */
public class ImageUtils {
	
	/**
	 * @param text
	 * @param font
	 * @param lineSpacingRatio
	 * @return
	 */
	public static double[] getTextImageSize(String text, Font font, double lineSpacingRatio){
		double width = 0;
		double height = 0;
		if(StringUtils.isBlank(text) || font == null){
			return null;
		}
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics();
//		double oneEnglishCharactorWith = fm.stringWidth("d");
		double oneChineseCharactorWith = fm.stringWidth("胖");
		for(String line : text.split("\n")){
			double temp = fm.stringWidth(line);
			//如果有空格，这个方法不算空格占的宽度，必须额外加上
			int blankNumber = ObjectUtils.containStringNumber(line, " ");
//			temp += blankNumber * oneEnglishCharactorWith;
			temp += blankNumber * oneChineseCharactorWith;
			if(temp > width){
				width = temp;
			}
			height += fm.getHeight() * lineSpacingRatio;
		}
        g2d.dispose();
//        if(width % 4 != 0){
//        	width += (4 - (width % 4));
//        }
//        if(height % 4 != 0){
//        	height += (4 - (height % 4));
//        }
        return new double[]{width, height};
	}
	
	/**
	 * @param text 要生成的文案，可以有换行符
	 * @param font 字体，为空的时候默认为Arial字体
	 * @param backgroundColor 背景颜色，默认透明
	 * @param textColor 文字颜色，默认黑色
	 * @param hAlign 水平对齐方式，默认居中
	 * @param wordStyle 文字的格式
	 * @param isUnderline
	 * @param wordSpacingRatio
	 * @param lineSpacingRatio
	 * @param fontImageLeft 增加left的距离
	 * @param fontImageTop 增加top的距离
	 * @param outputStream
	 * @return
	 * @throws IOException
	 */
	public static OutputStream getTextImage(String text, Font font, 
			Color backgroundColor, Color textColor, TextAlignEnum hAlign, 
			WordStyleEnum wordStyle, boolean isUnderline, double wordSpacingRatio, 
			double lineSpacingRatio, double fontImageLeft, double fontImageTop, 
			OutputStream outputStream) throws IOException{
		if(StringUtils.isBlank(text)){
			return null;
		}
		if(font == null){
			font = new Font("Arial", Font.BOLD, 90);
		}
		if(textColor == null){
			textColor = Color.BLACK;
		}
		if(hAlign == null){
			hAlign = TextAlignEnum.CENTER;
		}
		if(WordStyleEnum.VERTICAL.equals(wordStyle)){
			text = text.replace("\n", "");
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < text.length(); i ++){
				sb.append(text.charAt(i));
				if(i < (text.length() - 1)){
					sb.append("\n");
				}
			}
			text = sb.toString();
		}
		double[] size = getTextImageSize(text, font, lineSpacingRatio);
		double width = size[0] + fontImageLeft;
		double height = size[1] + fontImageTop;
		
		TextImage textImage = new TextImageImpl((int)width + (font.getSize() / 15), (int)height, backgroundColor, textColor, 
				new Margin((int) fontImageLeft, (int) ((font.getSize() / 8) + fontImageTop)));
		textImage.setLetterSpacingRatio(wordSpacingRatio);
		textImage.setLineSpacingRatio(lineSpacingRatio);
		textImage.withFont(font);
		if(isUnderline){
			textImage.withFontStyle(Style.UNDERLINED);
		}
		int lineNumber = 1;
		String[] lines = text.split("\n");
		int lineNumbers = lines.length;
		for(String line : lines){
			if(lineNumber == 1 && lineNumbers == 1){
				textImage.setTextAligment(Alignment.LEFT);
			}else{
				if(TextAlignEnum.LEFT.equals(hAlign)){
					textImage.setTextAligment(Alignment.LEFT);
				}else if(TextAlignEnum.RIGHT.equals(hAlign)){
					textImage.setTextAligment(Alignment.RIGHT);
				}else{
					textImage.setTextAligment(Alignment.CENTER);
				}
			}
			textImage.write(line);
			if(lineNumber < lineNumbers && lineNumbers > 1){
				textImage.newLine();
			}
			lineNumber ++;
		}
		ImageWriter imageWriter = ImageWriterFactory.getImageWriter(ImageType.PNG);
		if(outputStream == null){
			outputStream = new ByteArrayOutputStream();
		}
		imageWriter.writeImageToOutputStream(textImage, outputStream);
		
		return outputStream;
	}
	
	public static void runExample() throws Exception {
		boolean isV = false;//是否纵向显示
		Font sansSerifBoldBig = new Font("SansSerif", Font.BOLD, 40);
		
		String lines = "1.你好1aa;\n2.你好2bb;\n3.我们好312cc";
		if(isV){
			lines = lines.replace("\n", "");
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < lines.length(); i ++){
				sb.append(lines.charAt(i));
				if(i < (lines.length() - 1)){
					sb.append("\n");
				}
			}
			lines = sb.toString();
		}
		double[] size = getTextImageSize(lines, sansSerifBoldBig, 1.25);
		double width = size[0];
		double height = size[1];
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setFont(sansSerifBoldBig);
		FontMetrics fm = g2d.getFontMetrics();
		for(String line : lines.split("\n")){
			int temp = fm.stringWidth(line);
			if(temp > width){
				width = temp;
			}
			height += fm.getHeight();
		}
        g2d.dispose();
        
		TextImage textImage = new TextImageImpl((int)width + 12, (int)height + 8, null, Color.RED, new Margin(6, 10));
		textImage.withFont(sansSerifBoldBig);
		for(String line : lines.split("\n")){
			textImage.setTextAligment(Alignment.CENTER).write(line).newLine();
		}
        ImageWriter imageWriter = ImageWriterFactory.getImageWriter(ImageType.PNG);
        imageWriter.writeImageToFile(textImage, new File("simple.png"));
	}
	
	/**
	 * @param source
	 * @param destination
	 * @param connectionTimeout
	 * @param readTimeout
	 * @throws IOException
	 */
	public static void copyURLToFile(URL source, File destination,
			int connectionTimeout, int readTimeout) throws IOException {
		URLConnection connection = source.openConnection();
		connection.setConnectTimeout(connectionTimeout);
		connection.setReadTimeout(readTimeout);
		InputStream input = connection.getInputStream();
		try {
			FileOutputStream output = FileUtils.openOutputStream(destination);
			try {
				IOUtils.copy(input, output);
			} finally {
				IOUtils.closeQuietly(output);
			}
		} finally {
			IOUtils.closeQuietly(input);
		}
    }
	
	/**
	 * @param filePath
	 * @return
	 */
	public static int[] getImageSize(String filePath){
		int[] size = null;
		File file = new File(filePath);
		if(file.exists()){
			try {
				FileInputStream fis = new FileInputStream(file);
				BufferedImage bufferedImg = ImageIO.read(fis);
				int imgWidth = bufferedImg.getWidth();
				int imgHeight = bufferedImg.getHeight();
				return new int[]{imgWidth, imgHeight};
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return size;
	}
}
