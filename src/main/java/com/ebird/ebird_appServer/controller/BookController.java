package com.ebird.ebird_appServer.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ebird.ebird_appServer.service.BookServiceImpl;
import com.ebird.ebird_appServer.service.PointServiceImpl;
import com.ebird.ebird_appServer.service.SystemService;
import com.ebird.ebird_appServer.service.UserBookServiceImpl;
import com.ebird.ebird_entity.dto.BookDto;
import com.ebird.ebird_entity.dto.BookProgressDto;
import com.ebird.ebird_entity.dto.IdDto;
import com.ebird.ebird_entity.entity.BookEntity;
import com.ebird.ebird_entity.entity.UserBookEntity;
import com.smartframe.dto.Result;
import com.smartframe.dto.ResultObject;


/**
 * 练习本控制器
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/book")
public class BookController {
	
	@Autowired
	private BookServiceImpl bookService ;	
	
	
	@Autowired
	private SystemService systemService ;
	
	@Autowired
	private UserBookServiceImpl	userBookService;
	
	@Autowired
	private PointServiceImpl pointService;
	
	
	/**
	 * 
	 * 新增保存练习本节点
	 * @param request
	 * @param response
	 * @param entity
	 * @return
	 */
	@RequestMapping("/addBook")
	public Result<?> savaExcercise(HttpServletRequest request ,HttpServletResponse response,BookEntity entity){
		
		if(null==entity.getBookName()||entity.getBookName().equals("")){
			return ResultObject.warnMessage("练习本名称不能为空");
		}
		
		if(null==entity.getLanguage()||entity.getLanguage().equals("")){
			return ResultObject.warnMessage("语言不能为空");
		}
		
		if(null==entity.getArea()||entity.getArea().equals("")){
			return ResultObject.warnMessage("领域不能为空");
		}
		
		IdDto identity = bookService.bookSava(entity);
		UserBookEntity userBookEntity = new UserBookEntity();
					userBookEntity.setBookId(identity.getId());
					userBookEntity.setUserId(systemService.getCurrentUser().getId());
		userBookService.addUserBook(userBookEntity);
		return ResultObject.successObject(identity,"保存成功");
	}
	
	/**
	 * 根据练习本ID删除 练习本节点
	 * @param request
	 * @param response
	 * @param excerciseId 练习本ID
	 * @return
	 */
	@RequestMapping("/delBook")
	public Result<?> delExcercise(HttpServletRequest request ,HttpServletResponse response,String bookId){
		if(null==bookId||bookId.equals("")){
			return ResultObject.warnMessage("参数不能为空");
		}
		/**
		 * 加操作权限
		 * */
		BookEntity entity = bookService.findBook(bookId);
		if(null==entity){
			return ResultObject.warnMessage("无操作权限");	
		}
		Integer userId = systemService.getCurrentUser().getId();
		if(userId!=entity.getCreateId()){
			return ResultObject.warnMessage("无操作权限");
		}else{
			int count = bookService.delBook(bookId);
			if(count==0){
				return ResultObject.successMessage("无操作数据");
			}
			//同时删除用户练习本关联关系表数据
			userBookService.delUserBookBybooKId(Integer.parseInt(bookId));	
			return ResultObject.successMessage("删除成功");
		}
		
	}
	
	/**
	 * 更新练习本节点数据
	 * @param request
	 * @param response
	 * @param entity
	 * @return
	 */
	@RequestMapping("/editBook")
	public Result<?> editBook(HttpServletRequest request ,HttpServletResponse response,BookEntity entity){
		if(null==entity.getId()||entity.getId().equals("")){
			return ResultObject.warnMessage("主键ID不能为空");
		}
		
		if(null==entity.getBookName()||entity.getBookName().equals("")){
			return ResultObject.warnMessage("练习本名称不能为空");
		}
		
		/**
		 * 加操作权限
		 * */
		BookEntity bookEntity = bookService.findBook(entity.getId().toString());
		if(null==bookEntity){
			return ResultObject.warnMessage("无操作权限");	
		}
		Integer userId = systemService.getCurrentUser().getId();
		if(userId!=bookEntity.getCreateId()){
			return ResultObject.warnMessage("无操作权限");	
		}else{
			int count = bookService.editBook(entity);
			if(count==0){
				return ResultObject.successMessage("无操作数据");
			}
		  return ResultObject.successMessage("修改成功");		
		}
	}
	
	/**
	 * 根据练习本ID 查询练习本信息
	 * @param request
	 * @param response
	 * @param excerciseId
	 * @return
	 */
	@RequestMapping("/findBook")
	public Result<?> findBook(HttpServletRequest request ,HttpServletResponse response,String bookId){
		if(null==bookId||bookId.equals("")){
			return ResultObject.warnMessage("参数不能为空");
		}
		
		/**
		 * 加操作权限
		 * */
		BookDto entity = bookService.findBookById(bookId);
		if(null==entity){
			return ResultObject.warnMessage("无操作权限");
		}else{
			if(entity.getSharedType()==0){
				Integer userId = systemService.getCurrentUser().getId();
	 			List<UserBookEntity>  list = userBookService.findUser_userId_bookId(userId, entity.getId());
	 			if(list.size()==0){
	 				return ResultObject.warnMessage("无操作权限");	
	 			}
			}
		}

		
		return ResultObject.successObject(entity,null);	
	}
	
	/**
	 * 根据用户ID 查询所有练习本信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/bookList")
	public Result<List<BookDto>> searchAllExcercise(HttpServletRequest request ,HttpServletResponse response){
		Integer userId =systemService.getCurrentUser().getId();
		List<BookDto> entityList = bookService.searchAllExcercise(userId);
		return ResultObject.successObject(entityList,null);
	}
	

    
	/**
	 * 根据练习本ID ， 查询用户练习本练习进度
	 * @param request
	 * @param response
	 * @param excerciseId
	 * @return
	 */
	@RequestMapping("/bookProgress")
	public Result<?> bookProgress(HttpServletRequest request ,HttpServletResponse response,String bookId){
		if(null==bookId||bookId.equals("")){
			return ResultObject.warnMessage("参数不能为空");
		}
		
		/**
		 * 加操作权限
		 * */
		BookEntity entity = bookService.findBook(bookId);
		if(null==entity){
			return ResultObject.warnMessage("无操作权限");	
		}
		if(entity.getSharedType()==0){
			Integer userId = systemService.getCurrentUser().getId();
 			List<UserBookEntity>  list = userBookService.findUser_userId_bookId(userId, entity.getId());
 			if(list.size()==0){
 				return ResultObject.warnMessage("无操作权限");	
 			}
 			/*	if(userId!=entity.getCreateId()){
				return ResultObject.warnMessage("无操作权限");	
			}*/
			
		}
		
		
		BookProgressDto bPentity = bookService.bookProgress(Integer.parseInt(bookId));
		return ResultObject.successObject(bPentity,null); 
	}
	
	/**
	 * 用户订阅共享练习本接口
	 * @param request
	 * @param response
	 * @param bookId
	 * @return
	 */
	@RequestMapping("/subscribe")
	public Result<?> addUserBook(HttpServletRequest request ,HttpServletResponse response,String bookId){
		if(null==bookId||bookId.equals("")){
			return ResultObject.warnMessage("参数不能为空");
		}
		
		
		/**
		 * 加操作权限
		 * */
		BookEntity entity = bookService.findBook(bookId);
		if(null==entity){
			return ResultObject.warnMessage("无操作权限");	
		}
		if(entity.getSharedType()==0){
			Integer userId = systemService.getCurrentUser().getId();
 			List<UserBookEntity>  list = userBookService.findUser_userId_bookId(userId, entity.getId());
 			if(list.size()==0){
 				return ResultObject.warnMessage("无操作权限");	
 			}
 			/*	if(userId!=entity.getCreateId()){
				return ResultObject.warnMessage("无操作权限");	
			}*/
			
		}
		
		List<UserBookEntity> list = userBookService.findUser_userId_bookId(systemService.getCurrentUser().getId(), Integer.parseInt(bookId));
		if(list.size()>0){
			return ResultObject.warnMessage("练习本已经订阅");
		}else{
			UserBookEntity userBookEntity = new UserBookEntity();
			  userBookEntity.setBookId(Integer.parseInt(bookId));
			  userBookEntity.setUserId(systemService.getCurrentUser().getId());
	    	userBookService.addUserBook(userBookEntity);
		    pointService.pushPoint(Integer.parseInt(bookId),systemService.getCurrentUser().getId());//根据bookId推送到订阅的用户
		   return ResultObject.successMessage("订阅成功");
		}
	}
	
	/**
	 * 获取所有共享类型练习本
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/getShareBook")
	public Result<?> getShareBook(HttpServletRequest request ,HttpServletResponse response){
		List<BookDto> entityList = bookService.getOpenBook();
		return ResultObject.successObject(entityList,null);
	}
	
	
	/**
	 * 取消对练习本的订阅
	 * @param request
	 * @param response
	 * @param bookId
	 * @return
	 */
	@RequestMapping("/unsubscribe")
	public Result<?>  unsubscribe(HttpServletRequest request ,HttpServletResponse response ,String bookId){
		if(null==bookId||bookId.equals("")){
			return ResultObject.warnMessage("参数不能为空");
		}
		Integer userId = systemService.getCurrentUser().getId();
		
		/**
		 * 加操作权限
		 * */
		BookEntity entity = bookService.findBook(bookId);
		if(null==entity){
			return ResultObject.warnMessage("无操作权限");	
		}
		if(entity.getCreateId()==userId){
			return ResultObject.warnMessage("原创不能取消");	
		}
		
		
		int count = userBookService.delUserBook(userId,Integer.parseInt(bookId));
		if(count==0){
			return ResultObject.successMessage("该用户没有订阅");
		}
		return ResultObject.successMessage("取消订阅成功");
	}
	
	
	
	
	
	
}
