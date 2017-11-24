package com.ebird.ebird_appServer.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ebird.ebird_api.UserService;
import com.ebird.ebird_appServer.conn.Constant;
import com.ebird.ebird_entity.entity.UserEntity;
import com.smartframe.basics.encrypt.AccountEncoder;
import com.smartframe.basics.util.DES;
import com.smartframe.basics.util.DataUtil;
import com.smartframe.dto.Result;
import com.smartframe.dto.ResultObject;
import com.smartframe.entity.UserCur;

@Controller
public class InitController {
	
	@Autowired
	private UserService userService ;	

	/**
	 * 用户登录
	 * @param request
	 * @param response
	 * @return
	 */
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
			String token = AccountEncoder.Md5(userName+Constant.APP_KEY+new Date());//获取用户TOKEN
			userCur.setToken(token);
			request.getSession().setAttribute(token, userCur);//TOKEN保存到session中
			request.getSession().getAttribute(token);
			return  ResultObject.successObject(userCur,"登录成功") ;
		}
	}
	
	/**
	 * 退出登录
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/logout")
	public Result<?> logout(HttpServletRequest request ,HttpServletResponse response){
		HttpSession session = request.getSession(false);//防止创建Session
		session = request.getSession(); 
		if(null == session){
			return ResultObject.successMessage("退出成功") ;	
		}else{
			String token = request.getHeader("token");
			if(null==token||token.equals("")){
				return ResultObject.sucreMessage("非法请求") ;	
			}
			request.getSession().removeAttribute(token);
			return ResultObject.successMessage("退出成功") ;
		}
	}
	
	
	/**
	 * 注册用户
	 * @param request
	 * @param response
	 * @param userentity
	 * @return
	 */
	@RequestMapping("/signup")
	public Result<?> savaUser(HttpServletRequest request ,HttpServletResponse response,UserEntity userentity ){
		
		if(null==userentity.getUsername()||userentity.getUsername().equals("")){
			return ResultObject.warnMessage("用户名不能为空");
		}else if(userentity.getUsername().length()>20){
			   return ResultObject.warnMessage("用户名过长");
		}
		
		if(null!=userentity.getTelphone()&&!userentity.getTelphone().equals("")){
			if(userentity.getTelphone().length()>12){
				return ResultObject.warnMessage("电话号码异常");
			}
			if(!DataUtil.isNumeric(userentity.getTelphone())){
				return ResultObject.warnMessage("电话号码异常");
			}
		}else if(userentity.getTelphone().equals("")){
			userentity.setTelphone(null);
		}
		
		if(null==userentity.getPassword()||userentity.getPassword().equals("")){
			return ResultObject.warnMessage("密码不能为空");
		}else if(userentity.getPassword().length()>20){
		   return ResultObject.warnMessage("密码过长");
		}
		
		List<UserEntity> list = userService.getUserByName(userentity.getUsername().replaceAll(" ",""));
		if(list.size()>0){
			return ResultObject.warnMessage("用户名已经存在");
		}
		
		userentity.setUsername(userentity.getUsername().replaceAll(" ",""));
		String desPassword =userentity.getPassword().replaceAll(" ","");
		try {
			desPassword = DES.DESAndBase64Encrypt(desPassword, "w#_L9~za", "UTF-8");//DES加密处理
			userentity.setPassword(desPassword);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		userService.savaUser(userentity);
		return ResultObject.successMessage("注册成功") ;
	}
	
}
