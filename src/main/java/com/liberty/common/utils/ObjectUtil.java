package com.liberty.common.utils;

/**
 * Object工具类
 */
public class ObjectUtil {

  /**
   * Object 转 String[]
   * @param object Object对象
   * @return String[]
   */
  public static String[] objectToStringArray(Object object) {
    if (object == null) return null;

    String[] datas;
    if (object instanceof String) {
      datas = new String[]{(String)object};
    } else if (object instanceof String[]) {
      datas = (String[]) object;
    } else {
      datas = new String[] {};
    }

    return datas;
  }

  /**
   * Object 转 String
   * @param object Object对象
   * @return String
   */
  public static String objectToString(Object object) {
    return (object == null)?"":object.toString();
  }

}
