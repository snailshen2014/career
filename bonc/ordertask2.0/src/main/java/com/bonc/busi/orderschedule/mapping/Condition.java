package com.bonc.busi.orderschedule.mapping;
/**
 * sql condition define
 * @author yanjunshen
 * @Time 2017-05-24 10:08
 */
class Condition extends SqlElement {
	private String conSql;
	private String conAdd;
	/**
	 * constructor
	 * @param sql
	 * @param seq
	 * @param type
	 * @param conAdd
	 */
	public Condition(String sql, int seq, int type, String conAdd) {
		super(seq,type);
		this.conSql = sql;
		this.conAdd = conAdd;
	}
	
	private String getConSql() {
		return this.conSql;
	}

	public String getConAdd(){
		return this.conAdd;
	}
	/**
	 * redefine interface method
	 */
	public String toElement() throws Exception{
		StringBuilder sb = new StringBuilder();
		if (1 == getElementType()) { //replace value
			if (!isMatchRegex(getConSql())) {
				System.out.println("Condition configure is not regex ruler error," + getConSql());
				throw new Exception();
			}
			if(getElementValue() == null || getElementValue().getValue() == null) {
				System.out.println("No set condition value handel,or value no set");
				throw new Exception();
			}
			this.conSql = getConSql().replaceAll(regex, (String)getElementValue().getValue());
			sb.append(getConSql());
		} else if (0 == getElementType()) { //fixed table name
			sb.append(getConSql());
		} else {
			System.out.println(getConSql() + " Error condition type");
			throw new Exception();
		}
		return sb.toString();
	}
}
