package com.liberty.common.service;

/**
 * 资产Controller（固定、临时）通用接口
 */
public interface IService {

  // 查询
  void get();

  // 查询列表
  void getList();

  // 查询分页
  void index();

  // 新建
  void add();

  // 修改
  void update();

  // 新建 or 修改
  void save();

  // 删除
  void delete();

  // 有效性验证
  void check();

}
