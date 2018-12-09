var $busiTable = $('#table');

var $busiTestTable = $('#busiTestTable');

var busiTestReq = {
	BUSI_ID : -1
};

$(function(){
	var saveBusiItem = function(row, sync){
		$.ajax({
			type:"post",
			url: basePath+"cfg/busiSave",
			contentType:'application/json',
			data:JSON.stringify(row),
			async:sync,
			success:function(data,textStatus) {
				if(data.code == '0'){
					row = data.row;
				} else {
					parent.messager("保存失败");
				}
			},
			error:function(XMLHttpRequest,textStatus,errorThrown){
				parent.messager("保存失败");
			}
		});
		return row;
	};
	
	$(".addBusi").on('click',function(){
		var row = {};
		row.INSTANCE_ID = $('#insId').val();
		row.BUSI_MODE=$('#busiMode input[name="busiMode"]:checked').val();
		row = saveBusiItem(row,false);
		if(row.ID != undefined)
			$busiTable.bootstrapTable('insertRow', {index: 0, row: row});
	});
	
	window.busidefineoperateEvents = {
	    'click .delbusidefine': function (e, value, row, index) {
	    	if(row.ID !=null) {
	    		var param = {};
	    		param.ID=row.ID;
	    		$.ajax({
	    		  type:"post",
	    		  url:basePath+"cfg/busiDel",
	    		  contentType:'application/json',
	    		  data:JSON.stringify(param),
	    		  success:function(data,textStatus){
	    			  if(data.code == '0'){
	    				  parent.messager("删除成功");
	    				  $busiTable.bootstrapTable('refresh',{silent: true});
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
	    		$busiTable.bootstrapTable('remove', {
	                field: 'BUSI_CODE',
	                values: [row.BUSI_CODE]
	            });
			}  
	    },
	    'click .configbusientity':function(e, value, row, index){
    		entityReqParam.BUSI_ID = row.ID;
    		$entityTable.bootstrapTable('refresh');
	    }
	};
	
	// init busiTable
	$busiTable.bootstrapTable({
		url:basePath+"cfg/busiListData",
		method:'post',
		queryParams:function(){
			var params = {};
			params.INSTANCE_ID = $("#insId").val();
			return params;
		},
		striped : true,//行色
		columns: [
			{
				field: "ID",
				title: "业务ID",
				width:20
			},
			{
				field: "BUSI_MODE",
				title: "业务模式",
				width:20,
				editable: {
					type: 'select',
					source:function (){
						var sources = [];
						var codes = CodeUtil.codeForamter('PLT_BUSI_MODE');
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
				field: "BUSI_TYPE",
				title: "业务类型",
				width:20,
				editable: {
					type: 'select',
					source:function (){
						var sources = [];
						var codes = CodeUtil.codeForamter('PLT_BUSI_TYPE');
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
				field: "BUSI_CODE",
				title: "业务编码",
				width:20,
				editable: {
		          type: 'text',
		        }
			},{
				field: "TRANS",
				title: "传输类型",
				width:20,
				editable: {
					type: 'select',
					source:function (){
						var sources = [];
						var codes = CodeUtil.codeForamter('PLT_BUSI_TRANS');
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
				field: "MEMO",
				title: "描述",
				width:120,
				formatter:function(value){
						return null==value||undefined==value?"":value;
				},
				editable: {
					emptytext: "空",
					type: 'textarea'
		        }
			},{
				field: "OPERATER",
				title: "操作",
				width:100,
				events: busidefineoperateEvents,
				formatter:function(value,row,index){
					return [
						'<a class="col-sm-offset-1" href="javascript:void(0)" data-toggle="modal" data-target="#datacfg" data-datatype="BUSI" data-dataid="'+row.ID+'"><i title="设置属性" class="glyphicon glyphicon-pencil"></i></a>',
						'<a class="col-sm-offset-1 configbusientity" href="javascript:void(0)" ><i title="配置" class="glyphicon glyphicon-cog"></i></a>',
						'<a class="col-sm-offset-1 delbusidefine" href="javascript:void(0)" ><i title="删除" class="glyphicon glyphicon-trash"></i></a>',
						'<a class="col-sm-offset-1" href="javascript:void(0)" data-toggle="modal" data-target="#busitest" data-busicode="'+row.BUSI_CODE+'" data-dataid="'+row.ID+'"><i title="测试业务" class="glyphicon glyphicon-screenshot"></i></a>'
					].join('');
				}
			}
		],
		onEditableSave: function (field, row, oldValue, $el) {
			saveBusiItem(row,true);
		}
	});
	
	
	$busiTestTable.bootstrapTable({
		url:basePath+"cfg/busiTestData",
		method:'post',
		queryParams:function(){
			return busiTestReq;
		},
		uniqueId:'ID',
		striped : true,//行色
		height:350,
		toolbarAlign:'right',
		toolbar:'#busitesttoolbar',
		columns: [
			{
				field: "ELEMENT_NAME",
				title: "请求参数属性",
				width:20
			},
			{
				field: "ELEMENT_VALUE",
				title: "请求参数值",
				editable: {
		          type: 'text',
		        },
				width:20
			},
			{
				field: "PAR_ID",
				title: "父节点",
				width:20,
				formatter: CodeUtil.PLT_CHANNEL_ELEMENT
			},
			{
				field: "OPERATER",
				title: "操作",
				width:20,
				events: busidefineoperateEvents,
				formatter:function(value,row,index){
					return [
						'<a class="col-sm-offset-1 delbusidefine" href="javascript:void(0)" ><i title="删除" class="glyphicon glyphicon-trash"></i></a>'
					].join('');
				}
			}
		]
	});
	
	// init modal
	$('#busitest').on('show.bs.modal',
		function(event) {
			var button = $(event.relatedTarget);
			var dataid = button.data('dataid');
			var busicode = button.data('busicode');

			busiTestReq.BUSI_ID = dataid;
			var modal = $(this);

			modal.find('#busitesturl').html(basePath+'channel/'+busicode);
			modal.find('#busitesturl').attr('data-testurl',basePath+'task/');
			
			$busiTestTable.bootstrapTable('refresh');
		}
	);
	
	$("#busiTestReq").on('click',function(){
		var data = $busiTestTable.bootstrapTable('getData');
		var req = {};
		for(var index in data){
			var row = data[index];
			if(row.LEVEL==1&&row.ELEMENT_VALUE!=undefined){
				req[row.ELEMENT_NAME] = row.ELEMENT_VALUE;
			}
		}
		
		$.ajax({
			url:$('#busitesturl').data('testurl')+busiTestReq.BUSI_ID,
			type:"post",
			contentType:'application/json',
			data:JSON.stringify(req),
			success:function(data,textStatus){
				console.log(JSON.stringify(data));
				if(data.code == '000000'){
					parent.messager("测试成功"+JSON.stringify(data));
				}else{
					parent.messager("测试失败"+JSON.stringify(data));
				}
			},
			error:function(){
				parent.messager("测试失败"+JSON.stringify(data));
			}
		});
	});
});




