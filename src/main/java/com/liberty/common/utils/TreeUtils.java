package com.liberty.common.utils;


import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.liberty.common.model.BaseTreeNodeModel;

public class TreeUtils {

  /**
   * 格式化list为树形list
   *
   * @param list 原始列表
   * @param flag true 表示全部展开，其他 表示不展开
   * @return 树形list
   */
  public static <T extends BaseTreeNodeModel> List<T> formatTree(List<T> list, Boolean flag) {

    List<T> nodeList = new ArrayList<>();
    for (T node1 : list) {
      boolean mark = false;
      for (T node2 : list) {
        if (node1.getpId() != null && node1.getpId().equals(node2.getId())) {
          node2.setLeaf(false);
          mark = true;
          if (node2.getChildren() == null) {
            node2.setChildren(new ArrayList<BaseTreeNodeModel>());
          }
          node2.getChildren().add(node1);
          node2.setOpen(flag);
          break;
        }
      }
      if (!mark) {
        nodeList.add(node1);
        node1.setOpen(flag);
      }
    }
    return nodeList;
  }

  public static void main(String[] args) {
    List<BaseTreeNodeModel> list = new ArrayList<BaseTreeNodeModel>();
    BaseTreeNodeModel root1 = new BaseTreeNodeModel();
    root1.setId("1");
    BaseTreeNodeModel child1 = new BaseTreeNodeModel();
    child1.setId("11");
    child1.setpId("1");
    BaseTreeNodeModel child11 = new BaseTreeNodeModel();
    child11.setId("111");
    child11.setpId("11");
    BaseTreeNodeModel root2 = new BaseTreeNodeModel();
    root2.setId("2");
    BaseTreeNodeModel child2 = new BaseTreeNodeModel();
    child2.setId("21");
    child2.setpId("2");
    list.add(root1);
    list.add(child1);
    list.add(child11);
    list.add(root2);
    list.add(child2);
    List<BaseTreeNodeModel> treelist = formatTree(list, false);
    String json = JSONArray.toJSONString(treelist);
    System.out.println(json);
  }
}
