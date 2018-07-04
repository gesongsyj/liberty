package com.liberty.common.model;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.*;
import com.jfinal.upload.UploadFile;
import com.liberty.common.sql.DynamicSQL;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BaseModel<T extends Model<T>> extends Model<T> {
  /**
   * @Fields serialVersionUID : TODO
   */
  private static final long serialVersionUID = 1L;

  protected static DynamicSQL select = new DynamicSQL();

  protected static DynamicSQL sqlExceptSelect = new DynamicSQL();


  @SuppressWarnings("unchecked")
  @Override
  public T put(Map<String, Object> map) {
    for (String attr : map.keySet()) {
      if (!attr.toLowerCase().equals("id")) {
        try {
          super.set(attr, map.get(attr));
        } catch (ActiveRecordException e) {
          // The attribute name does not exist
          // e.printStackTrace();
        }
      }
    }

    return (T) this;
  }

  public void NewId() {
    this.set("id", UUID.randomUUID().toString().toUpperCase());
  }

}
