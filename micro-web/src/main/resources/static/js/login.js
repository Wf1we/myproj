$(function(){
	//手机号
	$("#phone").on("blur", function () {
		let phone = $.trim($("#phone").val());
		if (phone === undefined || phone === null || phone === "") {
			showError("phone", "必须输入手机号");
		} else if (phone.length != 11) {
			showError("phone", "手机号格式不正确");
		} else if (!/^1[1-9]\d{9}$/.test(phone)) {
			showError("phone", "手机号格式不正确");
		} else {
			showSuccess("phone");
		}
	});


	//密码
	$("#loginPassword").on("blur", function () {
		let mima = $.trim($("#loginPassword").val());
		if (mima === undefined || mima === null || mima === "") {
			showError("loginPassword", "必须输入密码");
		} else if (!/^[0-9a-zA-Z]+$/.test(mima)) {
			showError("loginPassword", "密码必须是数字和英文字符组成的");
		} else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(mima)) {
			showError("loginPassword", "密码必须同时有数字和英文");
		} else if (mima.length < 6 || mima.length > 20) {
			showError("loginPassword", "密码是6-20位字符");
		} else {
			showSuccess("loginPassword");
		}
	})

	//登录按钮
	$("#btnLogin").on("click",function(){
		$("#phone").blur();
		$("#loginPassword").blur();

		let errText = $("[id $= 'Err']").text();
		if( errText == ""){
			let phone = $.trim( $("#phone").val() );
			let mima = $.trim( $("#loginPassword").val() );

			$.ajax({
				url: contextPath + "/user/login",
				type:"post",
				data:{
					phone:phone,
					mima: $.md5(mima)
				},
				dataType:"json",
				success:function(resp){
					if(resp.result){
						//回到来源位置
						window.location.href = $("#returnUrl").val()
					} else if( resp.code == 1009 ){
						//跳转的 用户认证页面（给手机号发短信，邮箱发送验证码）
						showError("phone",resp.msg)
					} else {
						showError("phone",resp.msg)
					}
				},
				error:function(){
					alert("请稍后重试")
				}

			})
		}




	})

})


//错误提示
function showError(id, msg) {
	$("#" + id + "Ok").hide();
	$("#" + id + "Err").html("<i></i><p>" + msg + "</p>");
	$("#" + id + "Err").show();
	$("#" + id).addClass("input-red");
}

//错误隐藏
function hideError(id) {
	$("#" + id + "Err").hide();
	$("#" + id + "Err").html("");
	$("#" + id).removeClass("input-red");
}

//显示成功
function showSuccess(id) {
	$("#" + id + "Err").hide();
	$("#" + id + "Err").html("");
	$("#" + id + "Ok").show();
	$("#" + id).removeClass("input-red");
}