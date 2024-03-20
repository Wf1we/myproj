package com.bjpowernode.front.interceptors;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.api.po.User;
import com.bjpowernode.code.RespCode;
import com.bjpowernode.consts.YLBConsts;
import com.bjpowernode.vo.Result;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 登录的拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断用户是否登录
        User user = (User) request.getSession().getAttribute(YLBConsts.SESSION_USER);
        if( user == null ){
            //X-Requested-With: XMLHttpRequest
            if("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                //来源是ajax,返回数据
                Result<String> result  = new Result<>();
                result.setResult(false);
                result.setData("");
                result.setEnum(RespCode.LOGIN_REQUIRED);

                //输出result
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.println(JSONObject.toJSONString(result));
                out.flush();
                out.close();
            } else {
                // 需要进入登录页面
                response.sendRedirect( request.getContextPath() + "/user/page/login");
            }

            return false;
        }
        return true;
    }
}
