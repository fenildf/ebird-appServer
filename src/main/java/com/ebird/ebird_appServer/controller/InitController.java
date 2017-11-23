package com.ebird.ebird_appServer.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ebird.ebird_api.UserService;
import com.ebird.ebird_appServer.conn.Constant;
import com.smartframe.basics.encrypt.AccountEncoder;
import com.smartframe.dto.Result;
import com.smartframe.dto.ResultObject;
import com.smartframe.entity.UserCur;

@Controller
public class InitController {
	
	@Autowired
	private UserService userService ;	

	@RequestMapping("/login")
	public Result<?> login(HttpServletRequest request ,HttpServletResponse response){
		
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		if(null==userName||userName.equals("")||null==password||password.equals("")){
			return ResultObject.sucreMessage("用户名或密码不能为空!") ;
		}
		UserCur userCur = userService.findUserLogin(userName, password);//查询用户是否存现
		
		if(null == userCur){
			return ResultObject.sucreMessage("用户名或密码错误!") ;
		}else{
			userService.updateLasterLoginTime(userCur);
			String token = AccountEncoder.Md5(userName+Constant.APP_KEY);//获取用户TOKEN
			userCur.setToken(token);
			request.getSession().setAttribute(token, userCur);//TOKEN保存到session中
			return  ResultObject.successObject(userCur,"登录成功") ;
		}
	}
	
}
