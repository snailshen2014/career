/** 
 *  
 * @项目名称：channelmanager 
 * @文件名称：channelmanager 
 * @类描述：js工具类 
 * @创建人：gaoyang 
 * @创建时间：2017年7月8日  
 */  
var codeUrl = basePath + "cfg/codeMap";

var CodeUtil = {
	code:{
		PLT_CHANNEL_ELEMENT:{}
	},
	
	// load element name
	PLT_CHANNEL_ELEMENT : function(key){
		if(CodeUtil.code.PLT_CHANNEL_ELEMENT[key]==null||CodeUtil.code.PLT_CHANNEL_ELEMENT[key]==undefined){
			$.ajax({
				url: basePath + "cfg/elementListData",
				data:"{}",
				async:false,
				dataType:"json",
				type:"post",
				contentType:"application/json",
				success:function(data){
					for(var ele in data){
						CodeUtil.code.PLT_CHANNEL_ELEMENT[data[ele].ID]=data[ele].ELEMENT_NAME;
					}
				}
			});
		}
		return CodeUtil.code.PLT_CHANNEL_ELEMENT[key];
	},
	
	// load code 
	codeForamter : function(codeType){
		if(CodeUtil.code[codeType]==undefined||CodeUtil.code[codeType]==null){
			$.ajax({
				url:codeUrl,
				data:codeType,
				async:false,
				dataType:"json",
				type:"post",
				contentType:"application/json",
				success:function(data){
					CodeUtil.code[codeType]=data;
				},
				error:function(data){
					CodeUtil.code[codeType]={};
				}
			});
		}
		return CodeUtil.code[codeType];
	},
	PLT_BUSI_TYPE:function(value){
		if(value==undefined||value==null){
			return CodeUtil.codeForamter("PLT_BUSI_TYPE");
		}
		return CodeUtil.codeForamter("PLT_BUSI_TYPE")[value];
	},
	PLT_CHANNEL_TYPE:function(value){
		if(value==undefined||value==null){
			return CodeUtil.codeForamter("PLT_CHANNEL_TYPE");
		}
		return CodeUtil.codeForamter("PLT_CHANNEL_TYPE")[value];
	}, 
	PLT_BUSI_MODE:function(value){
		if(value==undefined||value==null){
			return CodeUtil.codeForamter("PLT_BUSI_MODE");
		}
		return CodeUtil.codeForamter("PLT_BUSI_MODE")[value];
	},
	PLT_CFG_TYPE:function(value){
		if(value==undefined||value==null){
			return CodeUtil.codeForamter("PLT_CFG_TYPE");
		}
		return CodeUtil.codeForamter("PLT_CFG_TYPE")[value];
	},
	PLT_UI_YN_TYPE:function(value){
		if(value==undefined||value==null){
			return CodeUtil.codeForamter("PLT_UI_YN_TYPE");
		}
		return CodeUtil.codeForamter("PLT_UI_YN_TYPE")[value||value=='1'?'1':'0'];
	}
};