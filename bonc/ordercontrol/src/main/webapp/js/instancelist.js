
var $table = $('#instance-table');

function delChannelIns(insId){
	$.ajax({
		url:basePath+"cfg/delChannelIns",
		data:{
			"ID":insId
		},
		type:"post",
		success:function(data){
			if(data.code == '0'){
			alert("删除成功!");	
			$table.bootstrapTable('refresh');
		  } else {
			alert("删除失败!"); 
		  }	
		},
		error:function(){
			alert("删除失败!");
		}
	});
}

function tenantIdFormatter(value, row, index) {
	if($("#globalTenantId").val()==value){
		return "公共实例";
	}else{
		return "私有实例";
	}
}

function dateFormatter(value, row, index) {
	return DateUtil.DateTimeConvertToDateTime(value);
}

function operator(value, row, index) {
   return [
		 '<a href="'+basePath+'cfg/instancedefine?insId='+row.insId+'"><i title="配置" class="glyphicon glyphicon-cog"></i></a>',	 
		 '<a class="col-sm-offset-1" href="javascript:void(0)" onclick="delChannelIns(\''+row.insId+'\')"><i title="删除" class="glyphicon glyphicon-trash"></i></a>'
   ].join('');
}