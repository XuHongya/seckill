<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <!-- 引入jstl -->
<%@include file="common/tag.jsp" %>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
	<%@include file="common/head.jsp" %>
    <title>秒杀详情页面</title>

  </head>
  <body>
	<div class="container">
		<div class="panel panel-default text-center">
			<div class="panel-heading">
				<h1>${seckill.name}</h1>
			</div>
			<div class="panel-body">
				<h2 class="text-danger">
                <%--显示time图标--%>
                <span class="glyphicon glyphicon-time"></span>
                <%--展示倒计时--%>
                <span class="glyphicon" id="seckill-box"></span>
            </h2>
			</div>
		</div>
	</div>
	<%--登录弹出层 输入电话--%>
	<div id="killPhoneModal" class="modal fade">
	
	    <div class="modal-dialog">
	
	        <div class="modal-content">
	            <div class="modal-header">
					<h3 class="modal-title text-center">
						秒杀电话
					</h3>
				</div>

				<div class="modal-body">
					<div class="row">
						<div class="col-xs-8 col-xs-offset-2">
							<input type="text" name="killPhone" id="killPhoneKey" placeholder="填写手机号" class="form-control">
						</div>
					</div>
				</div>
				<div class="modal-content">
					<div class="modal-header">
						<h3 class="modal-title text-center">
							收货人姓名
						</h3>
					</div>

					<div class="modal-body">
						<div class="row">
							<div class="col-xs-8 col-xs-offset-2">
								<input type="text" name="userName" id="userName" placeholder="填写收货人姓名" class="form-control">
							</div>
						</div>
					</div>

					<div class="modal-content">
						<div class="modal-header">
							<h3 class="modal-title text-center">
								收货人地址
							</h3>
						</div>

						<div class="modal-body">
							<div class="row">
								<div class="col-xs-8 col-xs-offset-2">
									<input type="text" name="userAddress" id="userAddress" placeholder="收货人地址" class="form-control">
								</div>
							</div>
						</div>

	            <div class="modal-footer">
	                <%--验证信息--%>
	                <span id="killPhoneMessage" class="glyphicon"> </span>
	                <button type="button" id="killPhoneBtn" class="btn btn-success">
	                    <span class="glyphicon glyphicon-phone"></span>
	                    Submit
	                </button>
	            </div>
	
	        </div>
	    </div>
	</div>
  </body>
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="//cdn.bootcss.com/jquery/1.11.3/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="//cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

<%--使用CDN 获取公共js http://www.bootcdn.cn/--%>
<%--jQuery Cookie操作插件--%>
<script src="http://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<%--jQuery countDown倒计时插件--%>
<script src="http://cdn.bootcss.com/jquery.countdown/2.1.0/jquery.countdown.min.js"></script>
<!-- 引入js逻辑 -->
<script src="/resources/js/seckill.js" type="text/javascript"></script>
<script type="text/javascript">
	$(function() {
		//使用EL传入参数
		seckill.detail.init({
			seckillId : ${seckill.seckillId},
			startTime : ${seckill.startTime.time}, //毫秒时间
			endTime : ${seckill.endTime.time} //毫秒时间 
		});
	});
</script>
</html>