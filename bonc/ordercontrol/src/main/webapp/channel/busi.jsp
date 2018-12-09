<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="modal" id="busitest" role="dialog" aria-labelledby="业务测试" >
  <div class="modal-dialog modal-lg" role="document" data-show="true">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" >业务测试   URL:<span id="busitesturl"></span></h4>
      </div>
      <div class="modal-body"  style="margin:0px 25px 0px;">
			  <div class="form-group">
			  	<div id="busitesttoolbar">
			  	
			  		<div class="col-sm-5">
			      	<input class="form-control" id="testtenantid" placeholder='租户ID'/>
	      		</div>
	      		<div class="col-sm-5">
			      	<input class="form-control" id="testchannelid" placeholder='渠道ID'/>
	      		</div>
	      		<div class="col-sm-2">
	      			<a class="btn btn-success" id="busiTestReq">请求</a>
	      		</div>
					</div>
					<table id="busiTestTable"></table>
				</div>
      </div>
    </div>
  </div>
</div>

<h3>业务定义</h3>
<form class="form-horizontal">
	<div class="form-group">
		<label class="col-sm-2 control-label">业务模式</label>
		<div class="col-xs-2 help-block" id="busiMode">
			<input type="radio" name="busiMode" value="1" checked> <span>主动模式</span>
		</div>
		<div class="col-xs-2 help-block" id="busiMode">
			<input type="radio" name="busiMode" value="0"> <span>被动模式</span>
		</div>
		<div class="col-xs-1">
			<input type="button" class="btn btn-default addBusi" value="增加" />
		</div>
	</div>
</form>
<div class="row" style="margin:0px 50px 0px;">
	  <table id="table"></table>
</div>
