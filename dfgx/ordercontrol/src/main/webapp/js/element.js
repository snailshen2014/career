
var $elementTable = $('#elementTable');

var elementReqParam = {
		ENTITY_ID:-1,
		PAR_ID:0
};
$(function(){
	
	var saveElement = function (row,sync){
		console.log(JSON.stringify(row));
        $.ajax({
        	type:"post",
        	url:basePath+"cfg/saveElement",
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

	window.elementoperateEvents = {
        'click .add-next-element': function (e, value, parRow, index) {
        	var row = {};
        	row.PAR_ID=parRow.ID;
        	row.ENTITY_ID=parRow.ENTITY_ID;
        	row.PAR_ID=parRow.ID;
        	row.LEVEL=parRow.LEVEL+1;
        	row.ELEMENT_NAME='';
        	row.TYPE = '2';
        	row.ELEMENT_DESC='';
        	row.IS_LEAF=1;
        	
        	row = saveElement(row,false);
        	if(row.ID!=undefined){
        		$elementTable.bootstrapTable('insertRow', {index: index+1, row: row});
        		// update parent is_leaf flag
        		if(parRow.IS_LEAF==true||parRow.IS_LEAF==1){
        			parRow.IS_LEAF = 0;
        			$elementTable.bootstrapTable('updateRow', {index: index, row: parRow});
            		saveElement(parRow,true);
        		}
        	}
        },
        'click .del-element': function (e, value, row, index) {
        	if(row.ID !=null) {
        		var param = {};
        		param.ID=row.ID;
        		$.ajax({
        		  type:"post",
        		  url:basePath+"cfg/delElement",
        		  contentType:'application/json',
        		  data:JSON.stringify(param),
        		  success:function(data,textStatus){
        			  if(data.code == '0'){
        				  parent.messager("删除成功");
        				  $elementTable.bootstrapTable('remove',{field: 'ID', values: [row.ID]});
        			  }else{
        				  parent.messager("删除失败");
        			  }
        		  },
        		  error:function(){
        			  parent.messager("删除失败");
        		  }
        		});
        	}else {
        		$elementTable.bootstrapTable('ID', {field: 'ID',values: [row.ID]});
			}  
        }
	};  
	
	// init elementTable
	$elementTable.bootstrapTable({
		url:basePath+"cfg/elementListData",
		method:'post',
		queryParams: elementReqParam,
		striped : true,//行色
		columns: [
			{
				field: "ENTITY_ID",
				width:50,
				title: "实体ID"
			},{
				field: "ELEMENT_NAME",
				editable:{
					type: 'text'
				},
				width:50,
				title: "元素名称"
			},{
				field: "ELEMENT_DESC",
				editable:{
					type: 'text'
				},
				width:120,
				title: "元素描述"
			},{
				field: "PAR_ID",
				width:50,
				title: "父节点",
				formatter: CodeUtil.PLT_CHANNEL_ELEMENT
			},{
				field: "TYPE",
				width:20,
				editable:{
					type: 'select',
					source:function (){
						var sources = [];
						var codes = CodeUtil.codeForamter('PLT_ELEMENT_TYPE');
						for(var key in codes){
							var code={};
							code.value = key;
							code.text = codes[key];
							sources[sources.length]=code;
						}
						return sources;
					}
				},
				title: "类型"
			},{
				field: "HANDLE",
				width:20,
				editable:{
					type: 'select',
					source:function (){
						var sources = [];
						var codes = CodeUtil.codeForamter('PLT_ELEM_HANDLE');
						for(var key in codes){
							var code={};
							code.value = key;
							code.text = codes[key];
							sources[sources.length]=code;
						}
						return sources;
					}
				},
				title: "处理器"
			},{
				field: "LEVEL",
				width:2,
				title: "级别"
			},{
				field: "IS_LEAF",
				width:10,
				title: "叶节点",
				formatter:CodeUtil.PLT_UI_YN_TYPE
			},{
				field: "OPERATER",
				width:70,
				title: "操作",
				events: elementoperateEvents,
				formatter:function(value,row,index){
					return [
						'<a class="col-sm-offset-1" href="javascript:void(0)" data-toggle="modal" data-target="#datacfg" data-datatype="ELEMENT" data-dataid="'+row.ID+'" ><i title="配置" class="glyphicon glyphicon-pencil"></i></a>',
						'<a class="col-sm-offset-1 element-'+row.ID+(row.TYPE==undefined||row.TYPE==null||row.TYPE=='2'?' operator-disable':' add-next-element')+'" href="javascript:void(0)" ><i title="'+(row.TYPE==undefined||row.TYPE==null?'请先设置元素类型':row.TYPE=='2'?'BASE类型无子节点':'新增子节点')+'" class="gray glyphicon glyphicon-plus"></i></a>',
						'<a class="col-sm-offset-1 '+(row.LEVEL==0?'operator-disable':'del-element')+'" href="javascript:void(0)" ><i title="'+(row.LEVEL==0?'根元素无法删除':'删除')+'" class="glyphicon glyphicon-trash"></i></a>'
					].join('');
				}
			}
		],
		onEditableSave: function (field, row, oldValue, $el) {
			if(field=='TYPE'){
				$elementNode = $('.element-'+row.ID);
				if((row.TYPE==undefined||row.TYPE==null||row.TYPE=='2')&&!$elementNode.hasClass('operator-disable')){
					$elementNode.addClass('operator-disable');
					$elementNode.removeClass('add-next-element');
				}else if(!(row.TYPE==undefined||row.TYPE==null||row.TYPE=='2')&&!$elementNode.hasClass('add-next-element')){
					$elementNode.removeClass('operator-disable');
					$elementNode.addClass('add-next-element');
				}
				$elementNode.attr('title',(row.TYPE==undefined||row.TYPE==null?'请先设置元素类型':row.TYPE=='2'?'BASE类型无子节点':'新增子节点'));
			}
			saveElement(row,true);
		}
	});
});
  

