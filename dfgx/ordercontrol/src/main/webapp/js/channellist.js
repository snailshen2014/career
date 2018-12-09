
var $channel = $('#channel-table');
var $subchannel = $('#subchannel-table');

var operator = function(value, row, index) {
   return [
		 '<a href="javascript:;" data-toggle="modal" data-target="#channel" data-dataid=\"'+row.ID+'\"><i title="配置" class="glyphicon glyphicon-cog"></i></a>',
		 '<a href="'+basePath+'cfg/channelinslist?channelId='+row.CHANNEL_ID+' &tenantId='+row.TENANT_ID+'"><i title="渠道实例列表" class="col-sm-offset-1 glyphicon glyphicon glyphicon-list"></i></a>',		 
		 '<a class="col-sm-offset-1 delchannel" href="javascript:;"><i title="删除" class="glyphicon glyphicon-trash"></i></a>'
   ].join('');
};

var dateFormatter = function(value,row,index){
	return DateUtil.DateTimeConvertToDateTime(value);
};

$(function(){
	$('#channel').on('show.bs.modal',
		function(event) {
			var button = $(event.relatedTarget);
			var dataid = button.data('dataid');
			$("#channelIndex").val(dataid);
			
			var row = {ID:dataid};
			
			$.ajax({
				url:basePath+'cfg/findChannel',
				data:JSON.stringify(row),
				contentType:'application/json',
				type:"post",
				success:function(data){
					if(data.code == 0){
						$('#channelId').val(data.row.CHANNEL_ID);
						$('#channelName').val(data.row.CHANNEL_NAME);
					}
				}
			});
			$subchannel.bootstrapTable('refresh');
		}
	);
	
	// save channel info
	$('#saveChannel').on('click',function(){
		var row = {};
		row.ID = $('#channelIndex').val();
		row.channelId=$('#channelId').val().trim();
		row.channelName=$('#channelName').val().trim();
		row.tenantId=$('#tenantId').val().trim();
		$.ajax({
			url:basePath+"cfg/saveChannel",
			data:JSON.stringify(row),
			contentType:'application/json',
			type:"post",
			success:function(data){
				if(data.code == 0){
					$('#channelIndex').val(data.row.ID);
					parent.messager('保存成功!');
				}else{
					parent.messager('保存失败!');
				}
			},
			error:function(){
				parent.messager('保存错误!');
			}
		});
	});

	// save subChannelInfo
	var saveSubChannel = function(row,sync){
		$.ajax({
    		url:basePath+"cfg/saveSubChannel",
    		data:JSON.stringify(row),
    		contentType:'application/json',
    		async:sync,
    		type:"post",
    		success:function(data){
    			if(data.code == '0'){
    				row = data.row;
    			}
    		}
    	});
		return row;
	};
	
	// add subChannel
	$('#addSubChannel').on('click',function(){
		var channelIndex = $('#channelIndex').val();
		if(null==channelIndex||undefined==channelIndex||''==channelIndex){
			parent.messager("请先保存渠道信息！");
			return ;
		}
		var row = {};
		row.TENANT_ID=$('#tenantId').val();
		row.CHANNEL_ID=channelIndex;
		row.SUBCHANNEL_ID='';
		row.SUBCHANNEL_NAME='';
		row = saveSubChannel(row,false);
		if(row.ID==undefined){
			parent.messager("保存失败!");
			return;
		}
		$subchannel.bootstrapTable('insertRow',{index:0,row:row});
	});
	
	window.channelEvents = {
        'click .delchannel': function (e, value, row, index) {
        	$.ajax({
        		url:basePath+"cfg/delChannel",
        		data:JSON.stringify(row),
        		contentType:'application/json',
        		type:"post",
        		success:function(data){
        			if(data.code=='0'){
        				$channel.bootstrapTable('remove',{field:'ID',values:[row.ID]});
        			};
        		}
        	});
        },
        'click .delsubchannel': function (e, value, row, index) {
        	$.ajax({
        		url:basePath+"cfg/delSubChannel",
        		data:JSON.stringify(row),
        		contentType:'application/json',
        		type:"post",
        		success:function(data){
        			if(data.code == '0'){
        				$subchannel.bootstrapTable('remove',{field:'ID',values:[row.ID]});
        			}
        		}
        	});
        }
	};
	
	$('input').iCheck({
	    checkboxClass: 'icheckbox_minimal',
	    radioClass: 'iradio_minimal',
	    increaseArea: '20%' // optional
	});
	
	$subchannel.bootstrapTable({
		url:basePath+"cfg/subChannelData",
		method:'post',
		height:290,
		toolbar:"#subtoolbar",
		queryParams:function(){
			var params = {};
			params.CHANNEL_ID = $("#channelIndex").val();
			return params;
		},
		columns: [
			{
				field: "SUBCHANNEL_ID",
				title: "子渠道ID",
				width:20,
				editable: {
		          type: 'text',
		          emptytext: "输入子渠道ID"
		        }
			},
			{
				field: "SUBCHANNEL_NAME",
				title: "子渠道名称",
				width:20,
				editable: {
		          type: 'text',
		          emptytext: "输入子渠道名称"
		        }
			},{
				field: "OPERATER",
				title: "操作",
				width:80,
				events: channelEvents,
				formatter:function(value,row,index){
					return [
						'<a class="col-sm-offset-1 delsubchannel" href="javascript:void(0)" ><i title="删除" class="glyphicon glyphicon-trash"></i></a>'
					].join('');
				}
			}
		],
		onEditableSave: function (field, row, oldValue, $el) {
			saveSubChannel(row,true);
		}
	});
});


