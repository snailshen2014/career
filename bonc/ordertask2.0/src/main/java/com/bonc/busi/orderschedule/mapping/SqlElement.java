package com.bonc.busi.orderschedule.mapping;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * compose sql element including table,column,condition
 * @author yanjunshen
 *
 */
class SqlElement {
	//condition value match regex
	public static String regex = "\\$\\{[a-zA-Z_]*\\}";
	
	// element Sequence Number
	private int elementSeq;
	// column type 0:fixed mapping;1:count value(by ColumnValue interface);2: sql block
	private int elementType;
	// count  value
	private Value elementValue;
	//true open,false close
	private boolean enable;
	/**
	 * constructor init element sequence,element type
	 * @param seq
	 * @param type
	 */
	public SqlElement(int seq,int type) {
		this.elementSeq = seq;
		this.elementType = type;
		this.enable = true;
	}
	public int getElementSeq() {
		return this.elementSeq;
	}

	public int getElementType() {
		return this.elementType;
	}

	public void setElementValue(Value v) {
		this.elementValue = v;
	}

	public Value getElementValue() {
		return this.elementValue;
	}
	/**
	 * sub class need overwrite method
	 * @return
	 */
	public String toElement() throws Exception{
		return "";
	}
	/**
	 * match the value with regex
	 * @param value
	 * @return
	 */
	boolean isMatchRegex(String value) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(value);
		return m.find();
	}
	
	boolean getElementEnable() {
		return this.enable;
	}
	void setElementEnable(boolean b) {
		this.enable = b;
	}
}
