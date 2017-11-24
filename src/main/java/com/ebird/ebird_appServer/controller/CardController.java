package com.ebird.ebird_appServer.controller;


import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ebird.ebird_appServer.service.BookServiceImpl;
import com.ebird.ebird_appServer.service.CradServiceImpl;
import com.ebird.ebird_appServer.service.PointServiceImpl;
import com.ebird.ebird_appServer.service.SystemService;
import com.ebird.ebird_appServer.service.UserBookServiceImpl;
import com.ebird.ebird_appServer.service.UtilServiceImpl;
import com.ebird.ebird_entity.dto.CardDto;
import com.ebird.ebird_entity.dto.IdDto;
import com.ebird.ebird_entity.entity.BookEntity;
import com.ebird.ebird_entity.entity.CardEntity;
import com.ebird.ebird_entity.entity.CardExerciseDetailEntity;
import com.ebird.ebird_entity.entity.CradAnswersEntity;
import com.ebird.ebird_entity.entity.PointEntity;
import com.ebird.ebird_entity.entity.UserBookEntity;
import com.smartframe.basics.util.EmojiUtil;
import com.smartframe.dto.Result;
import com.smartframe.dto.ResultObject;


@Controller
@RequestMapping("/card")
public class CardController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CardController.class);

	@Autowired
	private CradServiceImpl cradService;
	
	@Autowired
	private PointServiceImpl pointService;
	
	
	@Autowired
	private BookServiceImpl bookService ;	
	
	@Autowired
	private SystemService systemService ;
	
	
	@Autowired
	private UserBookServiceImpl userBookService;
	
	@Autowired
	private UtilServiceImpl utilService;
	
	
	/**
	 * 保存知识卡片
	 * @param request
	 * @param response
	 * @param entity
	 * @return
	 */
	@RequestMapping("/addCard")
	public Result<?> addCard(HttpServletRequest request ,HttpServletResponse response,CardEntity cardEntity ){
		if(null==cardEntity.getPointId()||cardEntity.getPointId().equals("")){
			return ResultObject.warnMessage("所属知识点ID不能为空");
		}
		if(null==cardEntity.getQuestionType()||cardEntity.getQuestionType().equals("")){
			return ResultObject.warnMessage("卡片题型不能为空");
		}
		if(null==cardEntity.getAnswers()||cardEntity.getAnswers().equals("")){
			return ResultObject.warnMessage("卡片答案不能为空");
		}
		
		try {
			String titleText =  EmojiUtil.emojiConvert1(cardEntity.getTitleText());
			String questionText =  EmojiUtil.emojiConvert1(cardEntity.getQuestionText());
			cardEntity.setTitleText(titleText);
			cardEntity.setQuestionText(questionText);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("知识点标题描述 emoji 表情符 转换异常");
			e.printStackTrace();
		}
		
		/**
		 * 加操作权限
		 * */
		PointEntity point = pointService.findLorePoint(cardEntity.getPointId());
		if(null==point){
			return ResultObject.warnMessage("无操作权限");	
		}
		Integer userId = systemService.getCurrentUser().getId();
		if(userId!=point.getCreateId()){
			return ResultObject.warnMessage("无操作权限");	
		}else{
			IdDto idEntity =  cradService.savaLoreCrad(cardEntity);
			 utilService.bookProgress(userId, point.getBookId());
			 LOGGER.info("练习本进度计算统计完成");
			
			return ResultObject.successObject(idEntity, "保存成功");
		}
	}
	
	/**
	 * 根据知识卡片ID 删除知识卡片
	 * @param request
	 * @param response
	 * @param loreCardId
	 * @return
	 */
	@RequestMapping("/delCard")
	public Result<?> delCard(HttpServletRequest request ,HttpServletResponse response,String cardId){
		if(null==cardId||cardId.equals("")){
			return ResultObject.warnMessage("参数不能为空");
		}
		
		/**
		 * 加操作权限
		 * */
		CardEntity entity = cradService.findLoreCrad(Integer.parseInt(cardId));
		if(null==entity){
			return ResultObject.warnMessage("无操作权限");	
		}
		Integer userId = systemService.getCurrentUser().getId();
		if(userId!=entity.getCreateId()){
			return ResultObject.warnMessage("无操作权限");	
		}else{
			int count = cradService.delLoreCrad(Integer.parseInt(cardId));
			if(count==0){
				return ResultObject.successMessage("无操作数据");
			}
			return ResultObject.successMessage("删除成功");
		}
	}
	
	/**
	 * 更新知识卡片信息
	 * @param request
	 * @param response
	 * @param entity
	 * @return
	 */
	@RequestMapping("/editCard")
	public Result<?> editCard(HttpServletRequest request ,HttpServletResponse response,CardEntity entity ){
		if(null==entity.getId()||entity.getId().equals("")){
			return ResultObject.warnMessage("ID不能为空");
		}
		if(null==entity.getQuestionType()||entity.getQuestionType().equals("")){
			return ResultObject.warnMessage("卡片题型不能为空");
		}
		if(null==entity.getAnswers()||entity.getAnswers().equals("")){
			return ResultObject.warnMessage("卡片答案不能为空");
		}
		
		/**
		 * 加操作权限
		 * */
		CardEntity cardEntity = cradService.findLoreCrad(entity.getId());
		if(null==cardEntity){
			return ResultObject.warnMessage("无操作权限");	
		}
		Integer userId = systemService.getCurrentUser().getId();
		if(userId!=cardEntity.getCreateId()){
			return ResultObject.warnMessage("无操作权限");	
		}else{
				try{
					if(null!=entity.getTitleText()||entity.getTitleText().equals("")){
						String	titleText = EmojiUtil.emojiConvert1(entity.getTitleText());
						entity.setTitleText(titleText);
					}
					if(null!=entity.getQuestionText()||entity.getQuestionText().equals("")){
						String questionText =  EmojiUtil.emojiConvert1(entity.getQuestionText());
						entity.setQuestionText(questionText);
					}
				}catch (UnsupportedEncodingException e) {
					LOGGER.error("知识点标题描述 emoji 表情符 转换异常");
					e.printStackTrace();
				}
			
			int count = cradService.editLoreCrad(entity);
			if(count==0){
				return ResultObject.successMessage("无操作数据");
			}
			return ResultObject.successMessage("修改成功");
		}
	}
	
	/**
	 * 根据知识卡片ID ，获取知识卡片信息
	 * @param request
	 * @param response
	 * @param loreCardId
	 * @return
	 */
	@RequestMapping("/findCard")
	public Result<?> findCard(HttpServletRequest request ,HttpServletResponse response,String cardId){
		if(null==cardId||cardId.equals("")){
			return ResultObject.warnMessage("参数不能为空");
		}
		
		/**
		 * 加操作权限
		 * */
		BookEntity bookEntity = bookService.findBookByCardId(Integer.parseInt(cardId));
		if(null==bookEntity){
			return ResultObject.warnMessage("无操作权限");	
		}
		if(bookEntity.getSharedType()==0){
			Integer userId = systemService.getCurrentUser().getId();
 			List<UserBookEntity>  list = userBookService.findUser_userId_bookId(userId, bookEntity.getId());
 			if(list.size()==0){
 				return ResultObject.warnMessage("无操作权限");	
 			}
 			/*	if(userId!=entity.getCreateId()){
				return ResultObject.warnMessage("无操作权限");	
			}*/
			
		}
		
		
		
		CardDto entity = cradService.findLoreCradById(Integer.parseInt(cardId));
		if(null ==entity){
			return ResultObject.successMessage("没有数据");
		}else{
			try{
				if(null!=entity.getTitleText()||entity.getTitleText().equals("")){
					String	titleText = EmojiUtil.emojiRecovery2(entity.getTitleText());
					entity.setTitleText(titleText);
				}
				if(null!=entity.getQuestionText()||entity.getQuestionText().equals("")){
					String questionText =  EmojiUtil.emojiRecovery2(entity.getQuestionText());
					entity.setQuestionText(questionText);
				}
			}catch (UnsupportedEncodingException e) {
				LOGGER.error("知识点标题描述 emoji 表情符 转换异常");
				e.printStackTrace();
			}
		}
		return ResultObject.successObject(entity,null);
	}
	
	/**
	 * 根据知识点ID ,获取知识卡片信息
	 * @param request
	 * @param response
	 * @param lorePointId
	 * @return
	 */
	@RequestMapping("/pointCardList")
	public Result<?> pointCardList(HttpServletRequest request ,HttpServletResponse response,String pointId){
		if(null==pointId||pointId.equals("")){
			return ResultObject.warnMessage("参数不能为空");
		}
		
		/**
		 * 加操作权限
		 * */
		BookEntity bookEntity = bookService.findBookByPointId(Integer.parseInt(pointId));
		
		if(null==bookEntity){
			return ResultObject.warnMessage("无操作权限");	
		}else{
			if(bookEntity.getSharedType()==0){
				Integer userId = systemService.getCurrentUser().getId();
	 			List<UserBookEntity>  list = userBookService.findUser_userId_bookId(userId, bookEntity.getId());
	 			if(list.size()==0){
	 				return ResultObject.warnMessage("无操作权限");	
	 			}
			}
		}
		

		
		List<CardDto> entityList = cradService.findLoreCradByPointId(Integer.parseInt(pointId));
		if(entityList.size()==0){
			return ResultObject.successMessage("没有数据");
		}else{
			for(CardDto dto :entityList){
				try {
					if(null!=dto.getTitleText()||dto.getTitleText().equals("")){
						String	titleText = EmojiUtil.emojiRecovery2(dto.getTitleText());
						dto.setTitleText(titleText);
					}
					if(null!=dto.getQuestionText()||dto.getQuestionText().equals("")){
						String questionText =  EmojiUtil.emojiRecovery2(dto.getQuestionText());
						dto.setQuestionText(questionText);
					}

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return ResultObject.successObject(entityList,null);
	}
	
	/**
	 * 获取所有开放的知识卡片信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/openCard")
	public Result<?> openCard(HttpServletRequest request ,HttpServletResponse response){
		List<CardEntity> entityList = cradService.getOpenLoreCrad();
		if(entityList.size()==0){
			return ResultObject.successMessage("没有数据");
		}
		return ResultObject.successObject(entityList,null);
	}
	
	
	/**
	 * 根据知识卡片ID ，获取知识卡片的练习详情
	 * @param request
	 * @param response
	 * @param lorePointId
	 * @return
	 */
	@RequestMapping("/getCardInfo")
	public Result<?> getCardInfo(HttpServletRequest request ,HttpServletResponse response,String cardId){
		if(null==cardId||cardId.equals("")){
			return ResultObject.warnMessage("参数不能为空");
		}
		
		/**
		 * 加操作权限
		 * */
		BookEntity bookEntity = bookService.findBookByCardId(Integer.parseInt(cardId));
		if(null==bookEntity){
			return ResultObject.warnMessage("无操作权限");	
		}else{
			if(bookEntity.getSharedType()==0){
				Integer userId = systemService.getCurrentUser().getId();
				if(userId!=bookEntity.getCreateId()){
					return ResultObject.warnMessage("无操作权限");	
				}
			}
		}

		CardExerciseDetailEntity entity = cradService.getLoreCradDetailByPointId(Integer.parseInt(cardId));
		if(null==entity){
			return ResultObject.successMessage("没有数据");
		}
		return ResultObject.successObject(entity,null);
	}
	
	/**
	 * 根据卡片ID ，获取卡片的答案信息
	 * @param request
	 * @param response
	 * @param loreCardId
	 * @return
	 */
	@RequestMapping("/getCardAnswer")
	public Result<?> getCardAnswer(HttpServletRequest request ,HttpServletResponse response,String cardId){
		if(null==cardId||cardId.equals("")){
			return ResultObject.warnMessage("参数不能为空");
		}
		
		/**
		 * 加操作权限
		 * */
		BookEntity bookEntity = bookService.findBookByCardId(Integer.parseInt(cardId));
		if(null==bookEntity){
			return ResultObject.warnMessage("无操作权限");	
		}else{
			if(bookEntity.getSharedType()==0){
				Integer userId = systemService.getCurrentUser().getId();
	 			List<UserBookEntity>  list = userBookService.findUser_userId_bookId(userId, bookEntity.getId());
	 			if(list.size()==0){
	 				return ResultObject.warnMessage("无操作权限");	
	 			}
			}
		}

		
		List<CradAnswersEntity> entityList = cradService.getLoreCradAnswerByPointId(Integer.parseInt(cardId));
		if(entityList.size()<1){
			return ResultObject.successMessage("没有数据");
		}
		return ResultObject.successObject(entityList,null);
	}
	
}
