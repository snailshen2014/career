package com.bonc.busi.orderschedule.mapping;
/**
 * source tables for providing data
 * @author yanjunshen
 * @Time 2017-05-24 10:08
 */
class Table extends SqlElement{
	private String sourceTableName;
	private String alias;
	/**
	 * constructor
	 * @param name
	 * @param seq
	 * @param alias
	 * @param type
	 */
	public Table(String name,int seq,String alias,int type) {
		super(seq,type);
		this.sourceTableName = name;
		this.alias = alias;
	}
	
	private String getSourceTableName() {
		return this.sourceTableName;
	}
	private String getAlias() {
		return this.alias;
	}
	/**
	 * redefine interface method
	 */
	public String toElement() throws Exception{
		StringBuilder sb = new StringBuilder();
		if (1 == getElementType()) { //replace value
			if(getElementValue() == null) {
				System.out.println("No set table value handel");
				throw new Exception();
			}
			this.sourceTableName = (String)getElementValue().getValue();
			sb.append(this.sourceTableName);
			sb.append("  " );
			sb.append(getAlias());
		} else if (0 == getElementType()) { //fixed table name
			sb.append(getSourceTableName() );
			sb.append(" " );
			sb.append(getAlias());
		} else {
			System.out.println(getSourceTableName() + " Error table type");
		}
		return sb.toString();
	}
}
