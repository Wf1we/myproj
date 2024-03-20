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


//打开注册协议弹层
function alertBox(maskid, bosid) {
    $("#" + maskid).show();
    $("#" + bosid).show();
}

//关闭注册协议弹层
function closeBox(maskid, bosid) {
    $("#" + maskid).hide();
    $("#" + bosid).hide();
}

//注册协议确认
$(function () {
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
            // 查看手机号能否注册 ，ajax访问后端
            $.ajax({
                url: contextPath + "/user/phone",
                async: false,
                data: {
                    phone: phone
                },
                dataType: "json",
                success: function (resp) {
                    if (resp.result == false) {
                        showError("phone", resp.msg);
                    }
                },
                error: function () {
                    showError("phone", "请稍后重试");
                }
            })
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

    //验证码文本框
    $("#messageCode").on("blur", function () {
        let code = $.trim($("#messageCode").val());
        if (code === undefined || code === null || code === "") {
            showError("messageCode", "必须输入验证码");
        } else if (code.length != 6) {
            showError("messageCode", "验证码是6位数字");
        } else {
            showSuccess("messageCode");

        }
    });

    //发送验证码的按钮
    $("#messageCodeBtn").on("click", function () {
        //调用phone的blur
        $("#phone").blur();
        let errText = $.trim($("#phoneErr").text());

        if ("" === errText) {
            //可以处理发送短信的业务，发送ajax请求，让服务器给手机号发送短信
            //按钮对象
            let codeBtn = $("#messageCodeBtn");
            if (codeBtn.hasClass("on")) {
                return;
            }
            //添加样式
            codeBtn.addClass("on");
            let second = -1;
            $.leftTime(10, function (d) {
                second = parseInt(d.s);
                if (second == 0) {
                    codeBtn.text("获取验证码")
                    codeBtn.removeClass("on");
                } else {
                    codeBtn.text(second + "秒重新获取")
                }

            });

            //发送ajax请求，发送短信验证码
			$.post( contextPath+"/sms/send/authcode",
				    {
				    	phone: $("#phone").val()
					},
				    function(resp){
				       if(resp.result == false){
				           showError("phone","请重新发送验证码")
                       }
				    },
				    "json" );

        }
    });

    $("#btnRegist").on("click",function(){
       //验证输入的数据phone， 密码， 验证码
        $("#phone").blur();
        $("#loginPassword").blur();
        $("#messageCode").blur();

        //检查错误文本
        let errText = $("[id$='Err']").text();
        if( "" == errText){
            //发起注册的请求
            $.ajax({
                url:contextPath+"/user/register",
                type:"post",
                data:{
                    phone: $.trim( $("#phone").val() ),
                    mima: $.md5( $.trim( $("#loginPassword").val()) ),
                    code: $.trim( $("#messageCode").val() )
                },
                dataType:"json",
                success:function(resp){
                    if(resp.result){
                        //跳转到实名认证页面
                        window.location.href = contextPath + "/user/page/realname";
                    } else {
                        alert("注册结果："+resp.msg);
                    }
                },
                error:function(){
                    alert("请稍后重试")
                }
            })

        }

    })


    $("#agree").click(function () {
        var ischeck = document.getElementById("agree").checked;
        if (ischeck) {
            $("#btnRegist").attr("disabled", false);
            $("#btnRegist").removeClass("fail");
        } else {
            $("#btnRegist").attr("disabled", "disabled");
            $("#btnRegist").addClass("fail");
        }
    });
});
