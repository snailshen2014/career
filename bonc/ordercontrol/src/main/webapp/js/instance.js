

var channelType="0";

$(function(){
	$('input').iCheck({
		checkboxClass: 'icheckbox_minimal',
	    radioClass: 'iradio_minimal',
	    increaseArea: '20%' // optional
	});
  
	// init select2
	$('.code_select').each(function(){
		$this = $(this);
		id = $this.attr("id");	// this node id

		codes = CodeUtil.codeForamter(id);
		op = [];
		for(var key in codes){
			var obj = {};
			obj.id=key;
			obj.text=codes[key];
			op[op.length] = obj;
		}
		$("#"+id).select2({
			data:op,
			minimumResultsForSearch: Infinity
		});
	});
	
	//加载业务ID
	$('#PLT_BUSI_ID').select2({
		ajax: {
	         type:'GET',
	         dataType: "json",
	         url: basePath+"cfg/getBusiDefineIds",
	         delay: 250,
	         data:function(term,page){
	           return {
	        	  channelId:"6"
	           };
	         },  
	         processResults: function (data, params){
	        	return {
	        		results: data
	        	}; 
	         }
	     },
	     placeholder:'请选择'
	});
	
	$('#saveInstance').on('click',function (){
		var req = {
			tenantId:'${tenantId}',
			channelInsName:$('#channelInsName').val(),
			channelInsMemo:$('#channelInsMemo').val(),
			channelInsStatus:1
		};
		$.ajax({
			url:basePath+'cfg/saveChannelIns',
			contentType:'application/json',
			data:JSON.stringify(req),
			type:"post",
			dataType:"json",
			success:function(data){
				if(data.code == '0'){
					alert("保存成功");
				} else {
				    alert("保存失败");
				}
				
			}
		});
	});
});
