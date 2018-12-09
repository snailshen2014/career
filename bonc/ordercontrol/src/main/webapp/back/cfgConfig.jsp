<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>

<head>
    <title>渠道配置中心</title>
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
    </style>
</head>
<body>
<%@include file="nav.jsp"%>


<div class="container">
    <h1 style="text-align: center;margin-top: 80px"> 工单配置信息
         <!--<a href="addTenant" class="btn btn-primary btn-lg active" role="button" style="float: right"  >配置新租户信息</a>-->
    </h1>
    <div id="toolbar" >
        <button id="create" class="btn btn-default">添加</button>
        <span class="alert"></span>
    </div>
    <table id="table"
           data-toggle="table"
           data-toolbar="#toolbar"
           data-height="460"
           data-show-columns="true"
           data-search="true"
           data-striped="true"
           data-show-refresh="true"
           data-url="${ctx}back/sysCfg">
        <thead>
        <tr>
            <th data-field="ACTION"
                data-align="center"
                data-width="20%"
                data-formatter="actionFormatter1"
                data-events="actionEvents">操作
            </th>
            <th data-field="CFG_KEY">key</th>
            <th data-field="CFG_VALUE">value</th>
            <th data-field="NOTE">描述</th>

        </tr>
        </thead>
    </table>
</div>


<div id="submodal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title"></h4>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label>CFG_KEY</label>
                    <input type="text" class="form-control" name="CFG_KEY" placeholder="CFG_KEY">
                </div>
                <div class="form-group">
                    <label>CFG_VALUE</label>
                    <input type="text" class="form-control" name="CFG_VALUE" placeholder="CFG_VALUE">
                </div>
                <div class="form-group">
                    <label>NOTE</label>
                    <input type="CFG_VALUE" class="form-control" name="NOTE" placeholder="NOTE">
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
    var $table = $('#table'),
        $button = $('#button'),
        $alert = $('.alert').hide(),
        $modal = $('#submodal');

    $(function () {

        $('#create').click(function () {
            showModal('create')
        })

        $modal.find('.submit').click(function () {
            var row = {};
            $modal.find('input[name]').each(function () {
                console.log($(this).val())
                row[$(this).attr('name')] = $(this).val();
            })
            $.ajax({
                url: '${ctx}back/insertOrUpdateCfg',
                type: 'post',
                contentType: 'application/json',
                data: JSON.stringify(row),
                success: function () {
                    $modal.modal('hide');
                    $table.bootstrapTable("refresh");
                    console.log($modal.data('id'));
                    showAlert(($modal.data('id') ? 'Update' : 'Create') + ' item successful!', 'success');
                },
                error: function () {
                    $modal.modal('hide');
                    showAlert(($modal.data('id') ? 'Update' : 'Create') + ' item error!', 'danger');
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

    window.actionEvents = {
        'click .update': function (e, value, row) {
            showModal('update', row);

        },
        'click .remove': function (e, value, row) {
            console.log(e);
            console.log(value);
            console.log(row);
            if (confirm('真的确定删除这一行吗？')) {
                $.ajax({
                    url: '${ctx}back/delCfgRow?key=' + row.CFG_KEY,
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
        }
    }

    function showModal(title, row) {
        row = row || {CFG_KEY: '', CFG_VALUE: '', NOTE: ''};
        $modal.data("CFG_KEY", row.CFG_KEY);
        $modal.find("modal-title").text(title);
        if (title == 'update') {
            $modal.find('input[name="CFG_KEY"]').attr('disabled', 'true');
        } else {
            $modal.find('input[name="CFG_KEY"]').removeAttr('disabled');
        }
        for (var name in row) {
            $modal.find('input[name="' + name + '"]').val(row[name])
        }
        $modal.modal('show');
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