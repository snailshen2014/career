package com.bonc.busi.code.mapper;

import com.bonc.busi.code.model.CodeReq;

public class CodeSelectGen {

	public String getXcloudCode(CodeReq req) {
        return req.getTable();
	}
}
