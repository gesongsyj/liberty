package com.liberty.common.model;

import java.util.List;

public class BaseTreeNodeModel<T extends BaseModel<T>> extends BaseModel<T> {

  private static final long serialVersionUID = -9189631784252440402L;

  private String id;       //节点id
  private String pId;      //节点父id
  private String name;     //节点名称

  private Boolean open = true;      //是否展开，默认true,展开
  private Boolean leaf = true;      //是否为叶子节点，true表示是叶子节点，false表示不是叶子节点

  private Boolean checked = false;  //是否选中(角色已分配的菜单)，true表示已分配，false表示未分配
  private int check = 0;

  private List<BaseTreeNodeModel> children; //子节点

  public BaseTreeNodeModel() {

  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getpId() {
    return pId;
  }

  public void setpId(String pId) {
    this.pId = pId;
    super.put("pId", this.pId);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    super.put("name", this.name);
  }

  public Boolean getOpen() {
    return open;
  }

  public void setOpen(Boolean open) {
    this.open = open;
    super.put("open", this.open);
  }

  public Boolean getLeaf() {
    return leaf;
  }

  public void setLeaf(Boolean leaf) {
    this.leaf = leaf;
  }

  public Boolean getChecked() {
    return checked;
  }

  public void setChecked(Boolean checked) {
    this.checked = checked;
    super.put("checked", this.checked);
  }

  public int getCheck() {
		return check;
	}

	public void setCheck(int check) {
		this.check = check;
		super.put("check", this.check);
	}

	public List<BaseTreeNodeModel> getChildren() {
    return children;
  }

  public void setChildren(List<BaseTreeNodeModel> children) {
    this.children = children;
    super.put("children", this.children);
  }

}
