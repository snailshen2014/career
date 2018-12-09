
var $entityTable = $('#entityTable');

var entityReqParam = {BUSI_ID:-1};

$(function(){
	$('#PLT_BUSI_ID').select2({
		ajax: {
	         type:'GET',
	         dataType: "json",
	         url: basePath + "cfg/getBusiDefineIds",
	         delay: 250,
	         data:function(term,page){
	           return {
	        	   insId:$('#insId').val()
	           };
	         },  
	         processResults: function (data, params){
	        	return {
	        		results: data
	        	}; 
	         }
	     }
	});
	
	$('#PLT_BUSI_ID').on('change',function(){
		console.log($('#PLT_BUSI_ID').val());
		entityReqParam.BUSI_ID = $('#PLT_BUSI_ID').val();
		$entityTable.bootstrapTable('refresh');
	});
	
	var saveEntityItem = function(row){
		var dataRow = null;
        console.log(JSON.stringify(row));
        $.ajax({
        	type:"post",
        	url:basePath+"cfg/saveEntity",
        	contentType:'application/json',
        	data:JSON.stringify(row),
        	async:false,
        	success:function(data,textStatus) {
	     		if(data.code == '0'){
	     			parent.messager("保存成功");
	     			dataRow = data;
	     		} else {
	     			parent.messager("保存失败");
	     		}
        	},
        	error:function(XMLHttpRequest,textStatus,errorThrown){
        		parent.messager("保存失败");
        	}
        });
        return dataRow;
	};
	
	$(".addEntity").on('click',function(){
		if(entityReqParam.BUSI_ID==-1){
			parent.messager("请选择业务实体！");
			return;
		}
		entityReqParam.TYPE=$('#PLT_ENTITY_TYPE').val();
		entityReqParam.DATA_FORMAT=$('#PLT_ENTITY_FORMAT').val();
		entityReqParam.DATA_MODE=$('#PLT_ENTITY_MODE').val();
		data = saveEntityItem(entityReqParam);
		if(data != null){
			$entityTable.bootstrapTable('insertRow', {index: 0, row: data.row});
			elementReqParam.ENTITY_ID=data.row.ID;
			$elementTable.bootstrapTable('refresh');
		}
	});
	
	window.entityoperateEvents = {
        'click .delentity': function (e, value, row, index) {
        	if(row.ID !=null) {
        		var param = {};
        		param.ID=row.ID;
        		$.ajax({
        		  type:"post",
        		  url: basePath+"cfg/delEntity",
        		  contentType:'application/json',
        		  data:JSON.stringify(param),
        		  success:function(data,textStatus){
        			  if(data.code == '0'){
        				  parent.messager("删除成功");
        				  $entityTable.bootstrapTable('refresh',{silent: true});
        			  }else{
        				  parent.messager("删除失败");
        			  }
        		  },
        		  error:function(){
        			  parent.messager("删除失败");
        		  }
        		});
        	}
        	else {
        		$entityTable.bootstrapTable('remove', {
                    field: 'BUSI_ID',
                    values: [row.BUSI_ID]
                });
			}
        },
	    'click .configelement':function(e, value, row, index){
	    	elementReqParam.ENTITY_ID = row.ID;
	    	$elementTable.bootstrapTable('refresh');
	    }
	};
	
	// init entityTable
	$entityTable.bootstrapTable({
		url:basePath+"cfg/entityListData",
		method:'post',
		queryParams:entityReqParam,
		striped : true,//行色
		columns: [
			{
				field: "BUSI_ID",
				width:50,
				title: "业务ID"
			},{
				field: "TYPE",
				width:50,
				title: "实体类型",
				editable: {
					type: 'select',
					source:function (){
						var sources = [];
						var codes = CodeUtil.codeForamter('PLT_ENTITY_TYPE');
						for(var key in codes){
							var code={};
							code.value = key;
							code.text = codes[key];
							sources[sources.length]=code;
						}
						return sources;
					}
				}
			},{
				field: "DATA_FORMAT",
				width:50,
				title: "实体格式",
				editable: {
					type: 'select',
					source:function (){
						var sources = [];
						var codes = CodeUtil.codeForamter('PLT_ENTITY_FORMAT');
						for(var key in codes){
							var code={};
							code.value = key;
							code.text = codes[key];
							sources[sources.length]=code;
						}
						return sources;
					}
				}
			},{
				field: "DATA_MODE",
				width:50,
				title: "实体存储模式",
				editable: {
					type:'select',
					source:function (){
						var sources = [];
						var codes = CodeUtil.codeForamter('PLT_ENTITY_MODE');
						for(var key in codes){
							var code={};
							code.value = key;
							code.text = codes[key];
							sources[sources.length]=code;
						}
						return sources;
					}
				}
			},{
				field: "OPERATER",
				width:50,
				title: "操作",
				events: entityoperateEvents,
				formatter:function(value,row,index){
					return [
						'<a class="col-sm-offset-1" href="javascript:void(0)" data-toggle="modal" data-target="#datacfg" data-datatype="ENTITY" data-dataid="'+row.ID+'"><i title="设置属性" class="glyphicon glyphicon-pencil"></i></a>',
						'<a class="col-sm-offset-1 configelement" href="javascript:void(0)"><i title="配置" class="glyphicon glyphicon-cog"></i></a>',
						'<a class="col-sm-offset-1 delentity" href="javascript:void(0)" ><i title="删除" class="glyphicon glyphicon-trash"></i></a>'
					].join('');
				}
			}
		],
		onEditableSave: function (field, row, oldValue, $el) {
			saveEntityItem(row);
		}
	});
});

