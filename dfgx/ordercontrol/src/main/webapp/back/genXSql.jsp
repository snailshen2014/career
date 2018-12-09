<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>

<head>
    <title>工单行云拼接sql</title>
    <%@include file="../common/common.jsp" %>
    <style>
        .update {
            color: #333;
        }
        .remove {
            color: red;
        }
        .alert {
            padding: 0 14px;
            margin-bottom: 0;
            display: inline-block;
        }
        #select_table{ font-size: small}
    </style>
</head>
<body>
<%@include file="nav.jsp"%>


<div class="container">
    <h1 style="text-align: center;margin-top: 80px"> 工单行云拼接sql</h1>
    <h3 >select部分  <button id="create" class="btn glyphicon glyphicon-plus"></button> <span class="alert"></span></h3>

    <table id="select_table"
           data-toggle="table"
           data-toolbar="#toolbar"
           data-show-columns="true"
           data-search="true"
           data-striped="true"
           data-show-refresh="true"
           data-url="${ctx}back/XSqlSelect?tenantId=${sessionScope.tenantId}">
        <thead>
        <tr>
            <th data-field="ACTION"
                data-align="center"
                data-formatter="actionFormatter1"
                data-events="actionEvents">操作
            </th>
            <th data-field="ORDER_COLUMN_SEQ">SEQ</th>
            <th data-field="IN_USE">IN_USE</th>
            <th data-field="COLUMN_TYPE">TYPE</th>
            <th data-field="ORDER_COLUMN">ORDER_COLUMN</th>
            <th data-field="ORDER_COLUMN_DES">ORDER_COLUMN_DES</th>
            <th data-field="SOURCE_TABLE_ALIAS">ALIAS</th>
            <th data-field="SOURCE_TABLE_COLUMN">SOURCE_TABLE_COLUMN</th>
            <th data-field="SQL_BLOCK">SQL_BLOCK</th>
        </tr>
        </thead>
    </table>
    <h3 >table部分<button id="table_create" class="btn glyphicon glyphicon-plus"></button> <span class="alert"></span></h3>
    <table id="table_table"
           data-toggle="table"
           data-toolbar="#toolbar"
           data-height="250"
           data-show-columns="true"
           data-search="true"
           data-striped="true"
           data-show-refresh="true"
           data-url="${ctx}back/XSqlTable?tenantId=${sessionScope.tenantId}">
        <thead>
        <tr>
            <th data-field="ACTION"
                data-align="center"
                data-width="20%"
                data-formatter="actionFormatter2"
                data-events="actionEvents">操作
            </th>
            <th data-field="SOURCE_TABLE">SOURCE_TABLE</th>
            <th data-field="TABLE_SEQ">TABLE_SEQ</th>
            <th data-field="TABLE_ALIAS">TABLE_ALIAS</th>
            <th data-field="TABLE_TYPE">TABLE_TYPE</th>
        </tr>
        </thead>
    </table>
    <h3 >where部分 <button id="where_create" class="btn glyphicon glyphicon-plus"></button> <span class="alert"></span></h3>
    <table id="where_table"
           data-toggle="table"
           data-toolbar="#toolbar"
           data-height="350"
           data-show-columns="true"
           data-search="true"
           data-striped="true"
           data-show-refresh="true"
           data-url="${ctx}back/XSqlWhere?tenantId=${sessionScope.tenantId}">
        <thead>
        <tr>
            <th data-field="ACTION"
                data-align="center"
                data-width="20%"
                data-formatter="actionFormatter3"
                data-events="actionEvents">操作
            </th>
            <th data-field="CON_SEQ">CON_SEQ</th>
            <th data-field="CON_SQL">CON_SQL</th>
            <th data-field="CON_TYPE">CON_TYPE</th>
            <th data-field="CON_ADD">CON_ADD</th>
        </tr>
        </thead>
    </table>
</div>
<div id="select_modal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title"></h4>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label>ORDER_COLUMN_SEQ</label>
                    <input type="text" class="form-control" name="ORDER_COLUMN_SEQ" placeholder="ORDER_COLUMN_SEQ">
                </div>
                <div class="form-group">
                    <label>IN_USE</label>
                    <input type="text" class="form-control" name="IN_USE" placeholder="IN_USE">
                </div>
                <div class="form-group">
                    <label>COLUMN_TYPE</label>
                    <input type="text" class="form-control" name="COLUMN_TYPE" placeholder="COLUMN_TYPE">
                </div>
                <div class="form-group">
                    <label>ORDER_COLUMN</label>
                    <input type="text" class="form-control" name="ORDER_COLUMN" placeholder="ORDER_COLUMN">
                </div>
                <div class="form-group">
                    <label>ORDER_COLUMN_DES</label>
                    <input type="text" class="form-control" name="ORDER_COLUMN_DES" placeholder="ORDER_COLUMN_DES">
                </div>
                <div class="form-group">
                    <label>SOURCE_TABLE_ALIAS</label>
                    <input type="text" class="form-control" name="SOURCE_TABLE_ALIAS" placeholder="SOURCE_TABLE_ALIAS">
                </div>
                <div class="form-group">
                    <label>SOURCE_TABLE_COLUMN</label>
                    <input type="text" class="form-control" name="SOURCE_TABLE_COLUMN" placeholder="SOURCE_TABLE_COLUMN">
                </div>
                <div class="form-group">
                    <label>SQL_BLOCK</label>
                    <input type="text" class="form-control" name="SQL_BLOCK" placeholder="SQL_BLOCK">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary submit">Submit</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<div id="table_modal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title"></h4>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label>SOURCE_TABLE</label>
                    <input type="text" class="form-control" name="SOURCE_TABLE" placeholder="SOURCE_TABLE">
                </div>
                <div class="form-group">
                    <label>TABLE_SEQ</label>
                    <input type="text" class="form-control" name="TABLE_SEQ" placeholder="TABLE_SEQ">
                </div>
                <div class="form-group">
                    <label>TABLE_ALIAS</label>
                    <input type="text" class="form-control" name="TABLE_ALIAS" placeholder="TABLE_ALIAS">
                </div>
                <div class="form-group">
                    <label>TABLE_TYPE</label>
                    <input type="text" class="form-control" name="TABLE_TYPE" placeholder="TABLE_TYPE">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary submit">Submit</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<div id="where_modal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title"></h4>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label>CON_SEQ</label>
                    <input type="text" class="form-control" name="CON_SEQ" placeholder="CON_SEQ">
                </div>
                <div class="form-group">
                    <label>CON_SQL</label>
                    <input type="text" class="form-control" name="CON_SQL" placeholder="CON_SQL">
                </div>
                <div class="form-group">
                    <label>CON_TYPE</label>
                    <input type="text" class="form-control" name="CON_TYPE" placeholder="CON_TYPE">
                </div>
                <div class="form-group">
                    <label>CON_ADD</label>
                    <input type="text" class="form-control" name="CON_ADD" placeholder="CON_ADD">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary submit">Submit</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->


<script>
    var $table = $('#select_table'),
        $alert = $('.alert').hide(),
        $modal = $('#select_modal');
        $table_modal = $('#table_modal');
        $where_modal = $('#where_modal');

    $(function () {

        $('#create').click(function () {
            showModal('create','',$modal);
        })
        $('#table_create').on('click',function () {
            showModal('create','',$table_modal)
        })
        $('#where_create').click(function () {
            showModal('create','',$where_modal)
        })

        $modal.find('.submit').click(function () {
            var row = {};
            row.TENANT_ID = '${sessionScope.tenantId}'
            $modal.find('input[name]').each(function () {
                row[$(this).attr('name')] = $(this).val();
            })
            $.ajax({
                url: '${ctx}back/insertOrUpdateXSQL',
                type: 'post',
                contentType: 'application/json',
                data: JSON.stringify(row),
                success: function () {
                    $modal.modal('hide');
                    $('#select_table').bootstrapTable("refresh");
                    console.log($modal.data('id'));
                    showAlert(($modal.data('id') ? 'Update' : 'Create') + ' item successful!', 'success');
                },
                error: function () {
                    $modal.modal('hide');
                    showAlert(($modal.data('id') ? 'Update' : 'Create') + ' item error!', 'danger');
                }
            });
        })
        $table_modal.find('.submit').click(function () {
            var row = {};
            row.TENANT_ID = '${sessionScope.tenantId}'
            $table_modal.find('input[name]').each(function () {
                row[$(this).attr('name')] = $(this).val();
            })
            $.ajax({
                url: '${ctx}back/insertOrUpdateXSQL',
                type: 'post',
                contentType: 'application/json',
                data: JSON.stringify(row),
                success: function () {
                    $table_modal.modal('hide');
                    $('#table_table').bootstrapTable("refresh");
                    console.log($table_modal.data('id'));
                    showAlert(($table_modal.data('id') ? 'Update' : 'Create') + ' item successful!', 'success');
                },
                error: function () {
                    $table_modal.modal('hide');
                    showAlert(($table_modal.data('id') ? 'Update' : 'Create') + ' item error!', 'danger');
                }
            });
        })
        $where_modal.find('.submit').click(function () {
            var row = {};
            row.TENANT_ID = '${sessionScope.tenantId}'
            $where_modal.find('input[name]').each(function () {
                row[$(this).attr('name')] = $(this).val();
            })
            $.ajax({
                url: '${ctx}back/insertOrUpdateXSQL',
                type: 'post',
                contentType: 'application/json',
                data: JSON.stringify(row),
                success: function () {
                    $where_modal.modal('hide');
                    $('#where_table').bootstrapTable("refresh");
                    console.log($where_modal.data('id'));
                    showAlert(($where_modal.data('id') ? 'Update' : 'Create') + ' item successful!', 'success');
                },
                error: function () {
                    $where_modal.modal('hide');
                    showAlert(($where_modal.data('id') ? 'Update' : 'Create') + ' item error!', 'danger');
                }
            });
        })

    });


    function actionFormatter1(value) {
        return [
            '<a class="update" href="javascript:" title="Update Item"><i class="glyphicon glyphicon-edit"></i></a>',
            '<a class="remove" href="javascript:" title="Delete Item"><i class="glyphicon glyphicon-remove-circle"></i></a>',
        ].join('')
    }
    function actionFormatter2(value) {
        return [
            '<a class="update2" href="javascript:" title="Update Item"><i class="glyphicon glyphicon-edit"></i></a>',
            '<a class="remove2" href="javascript:" title="Delete Item"><i class="glyphicon glyphicon-remove-circle"></i></a>',
        ].join('')
    }

    function actionFormatter3(value) {
        return [
            '<a class="update3" href="javascript:" title="Update Item"><i class="glyphicon glyphicon-edit"></i></a>',
            '<a class="remove3" href="javascript:" title="Delete Item"><i class="glyphicon glyphicon-remove-circle"></i></a>',
        ].join('')
    }



    window.actionEvents = {
        'click .update': function (e, value, row) {
            console.log(e,value);
            showModal('update', row,$modal);

        },
        'click .update2': function (e, value, row) {
            console.log(e,value);
            showModal('update', row ,$table_modal);

        },
        'click .update3': function (e, value, row) {
            console.log(e,value);
            showModal('update', row , $where_modal);

        },
        'click .remove': function (e, value, row) {
            if (confirm('真的确定删除这一行吗？')) {
                $.ajax({
                    url: '${ctx}back/delSelectRow?key=' + row.ORDER_COLUMN_SEQ +'&tenantId=' +'${sessionScope.tenantId}',
                    type: 'get',
                    success: function () {
                        $table.bootstrapTable('refresh');
                        showAlert('删除成功', 'success');
                    },
                    error: function () {
                        showAlert('删除失败', 'danger');
                    }
                })
            }
        },
        'click .remove2': function (e, value, row) {
            if (confirm('真的确定删除这一行吗？')) {
                $.ajax({
                    url: '${ctx}back/delTableRow?key=' + row.TABLE_SEQ +'&tenantId=' +'${sessionScope.tenantId}',
                    type: 'get',
                    success: function () {
                        $('#table_table').bootstrapTable('refresh');
                        showAlert('删除成功', 'success');
                    },
                    error: function () {
                        showAlert('删除失败', 'danger');
                    }
                })
            }
        },
        'click .remove3': function (e, value, row) {
            if (confirm('真的确定删除这一行吗？')) {
                $.ajax({
                    url: '${ctx}back/delWhereRow?key=' + row.CON_SEQ +'&tenantId=' +'${sessionScope.tenantId}',
                    type: 'get',
                    success: function () {
                        $('#where_table').bootstrapTable('refresh');
                        showAlert('删除成功', 'success');
                    },
                    error: function () {
                        showAlert('删除失败', 'danger');
                    }
                })
            }
        }

    }

    function showModal(title, row , modal) {
        console.log("showModal  "+JSON.stringify(row))
        modal.data("ORDER_COLUMN_SEQ", row.CFG_KEY);
        modal.find("modal-title").text(title);
        if (title == 'update') {
            modal.find('input[name="ORDER_COLUMN_SEQ"]').attr('disabled', 'true');
        } else {
            modal.find('input[name="ORDER_COLUMN_SEQ"]').removeAttr('disabled');
        }
        for (var name in row) {
            modal.find('input[name="' + name + '"]').val(row[name])
        }
        modal.modal('show');
    }


    function showAlert(title, type) {
        $alert.attr('class', 'alert alert-' + type || 'success')
            .html('<i class="glyphicon glyphicon-check"></i> ' + title).show();
        setTimeout(function () {
            $alert.hide();
        }, 3000);
    }
</script>


</body>


</html>