package com.laomachaguan.SideListview;

public class SortModel {

	private String name;   //显示的联系人名称
	private String sortLetters;  //显示数据拼音的首字母
	private String number;//电话号码
	private boolean isSelected;
	public String getNumber() {
		return number;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	public void setNumber(String number) {

		this.number = number;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
}
