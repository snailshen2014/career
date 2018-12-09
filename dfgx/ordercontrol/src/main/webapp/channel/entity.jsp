<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<h3>实体定义</h3>
<form class="form-horizontal">
	<div class="form-group">
		<label class="col-sm-2 control-label" for="inputEmail">业务ID</label>
		<div class="col-xs-2">
			<select class="form-control" id="PLT_BUSI_ID"></select>
		</div>
		<div class="col-xs-2">
			<select class="code_select form-control" id="PLT_ENTITY_TYPE" ></select>
		</div>
		<div class="col-xs-2">
			<select class="code_select form-control" id="PLT_ENTITY_FORMAT"></select>
		</div>
		<div class="col-xs-2">
			<select class="code_select form-control" id="PLT_ENTITY_MODE"></select>
		</div>
		<div class="col-xs-1">
			<input type="button" class="btn btn-default addEntity" value="增加" />
		</div>
	</div>
</form>	
<div class="row" style="margin:0px 50px 0px;">
	  <table id="entityTable"></table>				  
</div>

