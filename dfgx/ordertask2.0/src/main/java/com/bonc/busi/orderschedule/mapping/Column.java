package com.bonc.busi.orderschedule.mapping;


/**
 * order tables's column define
 * @author yanjunshen
 * @Time 2017-05-23 10:44
 */
class Column extends SqlElement {
	//column name
	private String columnName;
	//column des
	private String columnDes;
	//source table alias
	private String alias;
	//source table column
	private String sourceTableColumn;
	//get column value by sql block
	private String sqlBlock;
	/**
	 * constructor ,set value
	 * @param seq
	 * @param name
	 * @param des
	 * @param alias
	 * @param souTabCol
	 * @param type
	 */
	public Column(int seq,String name,String des,String alias,String souTabCol,int type,String block) {
		super(seq,type);
		this.columnName = name;
		this.columnDes = des;
		this.alias = alias;
		this.sourceTableColumn = souTabCol;
		this.sqlBlock = block;
	}
	
	public String getColumnName() {
		return this.columnName;
	}
	private String getColumnDes() {
		return this.columnDes;
	}
	public String getAlias() {
		return this.alias;
	}
	public String getSourceTableColumn() {
		return this.sourceTableColumn;
	}
	public String getSqlBlock() {
		return this.sqlBlock;
	}
	
	/**
	 * redefine interface method
	 */
	public String toElement() throws Exception {
		StringBuilder sb = new StringBuilder();
		if (1 == getElementType()) { //count value
			if(getElementValue() == null) {
				System.out.println("No set column value handel");
				throw new Exception();
			}
			sb.append(getElementValue().getValue());
			sb.append(" as " );
			sb.append(getColumnName());
		} else if (0 == getElementType()) { //other table value
			sb.append(getAlias() + "." + getSourceTableColumn());
			sb.append(" as " );
			sb.append(getColumnName());
		} else if (2 == getElementType())  {//sql block
			sb.append("( ");
			sb.append(getSqlBlock());
			sb.append(" )");
			sb.append(" as " );
			sb.append(getColumnName());
			
		} else {
			System.out.println(getColumnName() + getColumnDes() + " Error column type");
			throw new Exception();
		}
		return sb.toString();
	}
}
