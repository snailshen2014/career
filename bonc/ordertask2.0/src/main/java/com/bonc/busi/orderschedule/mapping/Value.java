package com.bonc.busi.orderschedule.mapping;

/**
 * count order table's column value
 * @author yanjunshen
 * @Time 2017-05-23 10:44
 */
abstract public class Value {
	//Storage column,sql condition value
	public Object value;
	public abstract void setValue(Object v);
	public abstract Object getValue();
}
