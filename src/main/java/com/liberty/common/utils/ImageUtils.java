package com.liberty.common.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class ImageUtils {
  /**
   * 将网络图片进行Base64位编码
   * 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
   *
   * @param imageUrl
   *			图片的url路径，如http://.....xx.jpg
   * @return
   */
  @SuppressWarnings("Duplicates")
  public static String encodeImageToBase64(URL imageUrl) {
    ByteArrayOutputStream outputStream = null;
    try {
      BufferedImage bufferedImage = ImageIO.read(imageUrl);
      outputStream = new ByteArrayOutputStream();
      ImageIO.write(bufferedImage, "jpg", outputStream);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // 对字节数组Base64编码，返回Base64编码过的字节数组字符串
    return Encodes.encodeBase64(outputStream.toByteArray());
  }

  /**
   * 将本地图片进行Base64位编码
   * 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
   *
   * @param imageFile
   *			图片的本地路径，如E://.....xx.jpg
   * @return
   */
  @SuppressWarnings("Duplicates")
  public static String encodeImageToBase64(File imageFile) {
    ByteArrayOutputStream outputStream = null;
    try {
      BufferedImage bufferedImage = ImageIO.read(imageFile);
      outputStream = new ByteArrayOutputStream();
      ImageIO.write(bufferedImage, "jpg", outputStream);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // 对字节数组Base64编码，返回Base64编码过的字节数组字符串
    return Encodes.encodeBase64(outputStream.toByteArray());
  }

  /**
   * 将Base64位编码的图片进行解码，并保存到指定目录
   *
   * @param base64
   *			base64编码的图片信息
   * @return
   */
  public static void decodeBase64ToImage(String base64, String path, String imgName) {
    try {
      FileOutputStream write = new FileOutputStream(new File(path + imgName));
      byte[] decoderBytes = Encodes.decodeBase64(base64);
      write.write(decoderBytes);
      write.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
