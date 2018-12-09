<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<style>
.hide-cfg-block{
	display:none;
}
</style>
<div class="modal" id="datacfg" role="dialog" aria-labelledby="数据配置" >
  <div class="modal-dialog modal-lg" role="document" data-show="true">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" ></h4>
      </div>
      <div class="modal-body"  style="margin:0px 25px 0px;">
			  <div class="form-group">
					<div id="cfgtoolbar">
						<div class="col-sm-5">
		   				<select id="PLT_CFG_TYPE" class="form-control" >
			      		<option></option>
			      	</select>
		   			</div>
		      	<div class="cfg-select-value col-sm-5">
			      	<select id="PLT_CFG_VALUE" class="form-control" >
			      		<option></option>
			      	</select>
	      		</div>
	      		<div class="cfg-input-value hide-cfg-block col-sm-5">
			      	<input class="form-control" style="width:170px;" placeholder='配置值'/>
	      		</div>
	      		<div class="col-sm-2">
	      			<a class="btn btn-success" id="addDataCfg">新增</a>
	      		</div>
					</div>
					<table id="dataCfgTable"></table>
				</div>
      </div>
    </div>
  </div>
</div>