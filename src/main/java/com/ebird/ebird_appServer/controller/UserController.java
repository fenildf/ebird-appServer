package com.ebird.ebird_appServer.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ebird.ebird_api.UserService;
import com.ebird.ebird_appServer.service.SystemService;
import com.ebird.ebird_entity.entity.UserEntity;
import com.smartframe.basics.util.DES;
import com.smartframe.basics.util.DataUtil;
import com.smartframe.dto.Result;
import com.smartframe.dto.ResultObject;
import com.smartframe.entity.UserCur;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService ;
	
	@Autowired
	private SystemService systemService ;
	
	
	/**
	 * 修改用户信息
	 * @param request
	 * @param response
	 * @param userentity
	 * @return
	 */
	@RequestMapping("/editUser")
	public Result<?> editUser(HttpServletRequest request ,HttpServletResponse response,UserEntity userentity ){

		if(null!=userentity.getTelphone()&&!userentity.getTelphone().equals("")){
			if(userentity.getTelphone().length()>12){
				return ResultObject.warnMessage("电话号码异常");
			}
			if(!DataUtil.isNumeric(userentity.getTelphone())){
				return ResultObject.warnMessage("电话号码异常");
			}
		}
		
		if(null!=userentity.getPassword()&&!userentity.getPassword().equals("")){
			if(userentity.getPassword().length()>20){
				 return ResultObject.warnMessage("密码过长");
			}else {
				String desPassword =userentity.getPassword().replaceAll(" ","");
				try {
					desPassword = DES.DESAndBase64Encrypt(desPassword, "w#_L9~za", "UTF-8");//DES加密处理
					userentity.setPassword(desPassword);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		
		String username =  systemService.getCurrentUser().getUsername();
		
		if(null!=userentity.getUsername()&&!userentity.getUsername().equals("")){
			
		    if(userentity.getUsername().length()>20){
				   return ResultObject.warnMessage("用户名过长");
			}
		    
		    if(!username.equals(userentity.getUsername())){
				List<UserEntity> list = userService.getUserByName(userentity.getUsername().replaceAll(" ",""));
				if(list.size()>0){
					return ResultObject.warnMessage("用户名已经存在");
				}else{
					userentity.setUsername(userentity.getUsername().replaceAll(" ",""));
				}
		    }

		}
		
		Integer curId = systemService.getCurrentUser().getId();//获取当前登录用户信息
		
		userentity.setId(curId);
		
		userService.editUser(userentity);
		
		return ResultObject.successMessage("修改成功") ;
	}

	
	/**
	 * 修改密码
	 * @param request
	 * @param response
	 * @param password
	 * @return
	 */
	@RequestMapping("/updatePassword")
	public Result<?> updatePassword(HttpServletRequest request ,HttpServletResponse response,String oldPassword,String newPassword ){
		
		if(null==oldPassword||oldPassword.equals("")){
			return ResultObject.sucreMessage("原密码不能为空!") ;
		}
		
		if(null==newPassword||newPassword.equals("")){
			return ResultObject.sucreMessage("新密码不能为空!") ;
		}
		
		String userName= systemService.getCurrentUser().getUsername();
		UserCur userCur = userService.findUserLogin(userName, oldPassword);//查询用户是否存现
		if(null == userCur){
			return ResultObject.sucreMessage("原密码错误!") ;
		}else{
			Integer userId= systemService.getCurrentUser().getId();
			userService.updatePassword(userId, newPassword);
		}
		return  ResultObject.successMessage("修改成功");
	}
	
}
