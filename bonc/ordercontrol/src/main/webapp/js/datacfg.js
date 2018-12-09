var $cfgtable = $("#dataCfgTable");
var cfgReqParam = {};

$(function(){
	// init cfg type item
	$("#PLT_CFG_TYPE").select2({
		ajax: {
			url:basePath+"cfg/codeMap",
			contentType:'application/json',
			type: 'post',
		    data: function () {
		    	return 'PLT_'+$('.modal-title').attr('cfg-type')+'_CONFIG';
		    },
		    dataType: 'json',
		    processResults: function (data) {
		    	var result = [];
		    	for(var ele in data){
		    		var obj = {text:data[ele],id:ele};
		    		result[result.length]=obj;
				}
		    	
		    	return {results: result};
		    }
		},
		placeholder:'配置项',
		width:170
	});
	
	$("#PLT_CFG_TYPE").on('select2:select',function(evt){
		var code = CodeUtil.codeForamter(evt.params.data.id);
		if(Object.keys(code).length==0){
    		// show input cfg
    		if($('.cfg-input-value').hasClass('hide-cfg-block')){
    			$('.cfg-select-value').addClass('hide-cfg-block');
    			$('.cfg-input-value').removeClass('hide-cfg-block');
    		}
		}else{
    		// show select cfg
    		if($('.cfg-select-value').hasClass('hide-cfg-block')){
    			$('.cfg-input-value').addClass('hide-cfg-block');
    			$('.cfg-select-value').removeClass('hide-cfg-block');
    		}
    	}
	});
	
	// init cfg type item
	$("#PLT_CFG_VALUE").select2({
		ajax: {
			url:basePath+"cfg/codeMap",
			contentType:'application/json',
			type: 'post',
		    data: function () {
		    	return $('#PLT_CFG_TYPE').val().trim();
		    },
		    dataType: 'json',
		    processResults: function (data) {
		    	var result = [];
		    	for(var ele in data){
		    		var obj = {text:data[ele],id:ele};
		    		result[result.length]=obj;
				}
		    	
		    	return {results: result};
		    }
		},
		placeholder:'配置值',
		width:170
	});

	// save data cfg function
	var saveDataCfg = function(row ,sync){
		$.ajax({
			url:basePath+'cfg/saveDataCfg',
			type:"post",
			contentType:'application/json',
			data:JSON.stringify(row),
			async:sync,
			success:function(data,textStatus) {
				if(data.code == '0'){
					row = data.row;
				}
			},
			error:function(XMLHttpRequest,textStatus,errorThrown){
				parent.messager("保存失败");
			}
		});
		return row;
	};
	
	// bind add cfg event
	$("#addDataCfg").on('click',function(){
		cfgReqParam.CFG_KEY = $('#PLT_CFG_TYPE').val();
		if($('.cfg-select-value').hasClass('hide-cfg-block')){
			cfgReqParam.CFG_VALUE = $('.cfg-input-value input').val();
		}else{
			cfgReqParam.CFG_VALUE = $('.cfg-select-value select').val();
		}
		
		row = saveDataCfg(cfgReqParam,false);
		if(row.ID!=undefined){
			$(".cfg-input-value input").val('');
			$cfgtable.bootstrapTable('insertRow', {index: 0, row: row});
		}
	});
	
	
	// init cfg table	/*queryParamsType:'limit',*/
	$cfgtable.bootstrapTable({
		url:basePath+"cfg/cfgListData",
		method:'post',
		showRefresh:true,
		search:true,
		toolbarAlign:'right',
		searchAlign:'left',
		buttonsAlign:'left',
		queryParams:function(data){
			data.DATA_TYPE=cfgReqParam.DATA_TYPE;
			data.TYPE_ID=cfgReqParam.TYPE_ID;
			return data;
		},
		striped : true,//行色
		height:400,
		toolbar:"#cfgtoolbar",
		minimumCountColumns:2,
		columns: [
			{
				field: "DATA_TYPE",
				title: "配置类型",
				formatter:CodeUtil.PLT_CFG_TYPE
			},{
				field: "CFG_KEY",
				title: "配置项"
			},{
				field: "CFG_VALUE",
				title: "配置值",
				editable:{
					type: 'text'
				}
			},{
				field: "OPERATER",
				width:20,
				title: "操作",
				events:{
					'click .delcfgitem':function (e, value, row, index) {
				    	var req={"CFG_ID":row.ID};
				    	$.ajax({
				        	 type:"post",
				        	 url:basePath+"cfg/deleteCfg",
				        	 contentType:'application/json',
				        	 data:JSON.stringify(req),
				        	 success:function(data,textStatus) {
				        		if(data.code == '0'){
				        			$cfgtable.bootstrapTable('remove',{field:'ID',values:[row.ID]});
				        		} else {
				        			parent.messager("删除失败");	
				        		}
				        	 },
				        	 error:function(XMLHttpRequest,textStatus,errorThrown){
				        		 parent.messager("删除失败");
				        	 }
				       });
				    }
				},
				formatter:function(value,row,index){
					return [
						'<a class="col-sm-offset-1 delcfgitem" href="#" ><i title="删除" class="glyphicon glyphicon-trash"></i></a>'
					].join('');
				}
			}
		],
		onEditableSave: function (field, row, oldValue, $el) {
			saveDataCfg(row,true);
		}
	});
	
	// init modal
	$('#datacfg').on('show.bs.modal',
		function(event) {
			var button = $(event.relatedTarget);
			var datatype = button.data('datatype');
			var dataid = button.data('dataid');

			cfgReqParam.DATA_TYPE = datatype;
			cfgReqParam.TYPE_ID = dataid;
			var modal = $(this);

			modal.find('.modal-title').text(
					CodeUtil.PLT_CFG_TYPE(datatype) + '属性配置:' + dataid).attr('cfg-type', datatype);
			
			$cfgtable.bootstrapTable('refresh');
		}
	);
	
});
 
