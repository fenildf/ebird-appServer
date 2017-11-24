package com.ebird.ebird_appServer.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ebird.ebird_appServer.service.CradServiceImpl;
import com.ebird.ebird_appServer.service.ExcerciseServiceImpl;
import com.ebird.ebird_appServer.service.RecommendServiceImpl;
import com.ebird.ebird_appServer.service.ReviewServiceImpl;
import com.ebird.ebird_appServer.service.SystemService;
import com.ebird.ebird_appServer.service.UtilServiceImpl;
import com.ebird.ebird_entity.dto.CardDto;
import com.ebird.ebird_entity.dto.PointNumDto;
import com.smartframe.basics.util.EmojiUtil;
import com.smartframe.dto.Result;
import com.smartframe.dto.ResultObject;

/**
 * 
 * 用户答题模块
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/review")
public class ReviewController {
	
	@Autowired
	private ReviewServiceImpl reviewService;
	
	@Autowired
	private UtilServiceImpl utilService;
	
	@Autowired
	private  CradServiceImpl cradService;
	
	@Autowired
	private SystemService systemService ;
	
	@Autowired
	private ExcerciseServiceImpl excerciseService;
	
	@Autowired
	private RecommendServiceImpl recommendService;
	
	
	/**
	 * 复习保存
	 * @param request
	 * @param response
	 * @param lorePointId 知识点ID
	 * @param cradId 卡片ID
	 * @param right 回答是否正确 1：正确   0：错误
	 * @return
	 */
	@RequestMapping("/addReview")
	public Result<?> reviewCard(HttpServletRequest request ,HttpServletResponse response,String pointId,String cardId,Integer right){
		if(null==pointId||pointId.equals("")){
			return ResultObject.warnMessage("参数不能为空");
		}
		if(null==cardId||cardId.equals("")){
			return ResultObject.warnMessage("参数不能为空");
		}
		if(null==right||right.equals("")){
			return ResultObject.warnMessage("参数不能为空");
		}
		
		/**
		 * 添加权限
		 * **/
		Integer userId =systemService.getCurrentUser().getId();
		Boolean flag = utilService.getAuthByPointId(Integer.parseInt(pointId), userId);
		if(!flag){
			return ResultObject.warnMessage("无操作权限");
		}
		
		reviewService.reviewCrad(pointId, cardId, right);
		
		return ResultObject.successMessage("保存成功") ;
	}
	
	
	/**
	 * 获取练习本中，复习，错题，新的 的数量
	 * @param request
	 * @param response
	 * @param bookId
	 * @return
	 */
	@RequestMapping("pointNum")
	public Result<?> pointNum(HttpServletRequest request ,HttpServletResponse response,Integer bookId){
		if(null==bookId||bookId.equals("")){
			return ResultObject.warnMessage("参数不能为空");
		}
		
		Integer userId =systemService.getCurrentUser().getId();
		
		PointNumDto dot = excerciseService.getPointNum(userId,bookId);
		return ResultObject.successObject(dot,null) ;
	}
	
	
	
	/**
	 * 根据知识点ID ，随机获取一个卡片信息
	 * @param request
	 * @param response
	 * @param pointId
	 * @return
	 */
	@RequestMapping("/card")
	public Result<?> getCard(HttpServletRequest request ,HttpServletResponse response ,String pointId ){
		if(null==pointId||pointId.equals("")){
			return ResultObject.warnMessage("参数不能为空");
		}
		
		/**
		 * 添加权限
		 * **/
		Integer userId =systemService.getCurrentUser().getId();
		Boolean flag = utilService.getAuthByPointId(Integer.parseInt(pointId), userId);
		if(!flag){
			return ResultObject.warnMessage("无操作权限");
		}
		
		CardDto cardDto = cradService.roundCard(Integer.parseInt(pointId));
		
		//对emoji转换
		try {
			if(null!=cardDto.getTitleText()||cardDto.getTitleText().equals("")){
				String	titleText = EmojiUtil.emojiRecovery2(cardDto.getTitleText());
				cardDto.setTitleText(titleText);
			}
			if(null!=cardDto.getQuestionText()||cardDto.getQuestionText().equals("")){
				String questionText =  EmojiUtil.emojiRecovery2(cardDto.getQuestionText());
				cardDto.setQuestionText(questionText);
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return ResultObject.successObject(cardDto,null) ;
	}
	

}
