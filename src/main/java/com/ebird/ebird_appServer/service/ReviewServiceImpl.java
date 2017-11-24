package com.ebird.ebird_appServer.service;


import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ebird.ebird_appServer.dao.PointDao;
import com.ebird.ebird_appServer.dao.ReviewDao;
import com.ebird.ebird_entity.dto.PointExerciseDetailDto;
import com.ebird.ebird_entity.entity.UserExerciseDetailEntity;
import com.ebird.ebird_entity.enums.LearningCycle;
import com.smartframe.basics.util.DateUtil;

/**
 * 保存用户练习
 * @author Administrator
 *
 */
@Service("reviewService")
public class ReviewServiceImpl {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);
	
	@Autowired
	private UtilServiceImpl utilService;
	
	@Autowired
	private ReviewDao reviewDao;
	
	@Autowired
	private PointDao pointDao;
	
	@Autowired
	private SystemService systemService ;
	
	
	/**
	 * 复习保存
	 * @param lorePointId 知识点ID
	 * @param cradId 卡片ID
	 * @param right 回答是否正确 1：正确   0：错误
	 * @return
	 */
	@Transactional
	public void reviewCrad(String lorePointId,String cradId,Integer right){
		Integer userId =systemService.getCurrentUser().getId();
		//保存用户练习流水
		UserExerciseDetailEntity entity = new UserExerciseDetailEntity();
			entity.setEndExerciseTime(new Date());
			entity.setExerciseDate(new Date());
			entity.setStartExerciseTime(new Date());
			entity.setExerciseUpshot(right);
			entity.setCardId(Integer.parseInt(cradId));
			entity.setPointId(Integer.parseInt(lorePointId));
			entity.setUserId(userId);
			reviewDao.savaUserExcerciseDetail(entity);
		//更新用户知识点联系详情
		
		PointExerciseDetailDto  detailEntity = pointDao.pointIdByDetail(Integer.parseInt(lorePointId),userId);
		int exerciseCycle =detailEntity.getExerciseCycle();//获取练习周期
		 if(right==1){//正确
			  /**
			   * 如果答题错误 练习周期往前升一级，熟练度为 +1 ，熟练度最高为3 最低为0 
			   * 
			   * **/
			  int cc =  DateUtil.compareDate(new Date(),detailEntity.getNextExerciseTime());//比较时间大小  -1:小于 ; 1: 大于; 0:相等
			  if(cc!=-1){//***当前时间 大于 或 等于 下次练习时间，即 在这个周期之内
					 
				     if(exerciseCycle==0){
						 detailEntity.setNextExerciseTime(DateUtil.addOrSubHour(new Date(), LearningCycle.FIRST_TIME.timesanmp));//计划下次练习时间
					 }else if(exerciseCycle==1){
						 detailEntity.setNextExerciseTime(DateUtil.addOrSubHour(new Date(), LearningCycle.SECOND_TIME.timesanmp));//计划下次练习时间
					 }else if(exerciseCycle==2){
						 detailEntity.setNextExerciseTime(DateUtil.addOrSubHour(new Date(), LearningCycle.Third_TIME.timesanmp));
					 }else if(exerciseCycle==3){
						 detailEntity.setNextExerciseTime(DateUtil.addOrSubHour(new Date(), LearningCycle.FOURTH_TIME.timesanmp));
					 }else if(exerciseCycle==4){
						 detailEntity.setNextExerciseTime(DateUtil.addOrSubHour(new Date(), LearningCycle.FIFTH_TIME.timesanmp));
					 }else if(exerciseCycle==5){
						 detailEntity.setNextExerciseTime(DateUtil.addOrSubHour(new Date(), LearningCycle.SIXTH_TIME.timesanmp));
					 }else if(exerciseCycle==6){
						 detailEntity.setNextExerciseTime(DateUtil.addOrSubHour(new Date(), LearningCycle.SEVENTH_TIME.timesanmp));
					 }else if(exerciseCycle==7){
						 detailEntity.setNextExerciseTime(DateUtil.addOrSubHour(new Date(), LearningCycle.EIGHTH_TIME.timesanmp));
					 }else if(exerciseCycle==8){
						 detailEntity.setNextExerciseTime(DateUtil.addOrSubHour(new Date(), LearningCycle.NINTH_TIME.timesanmp));
					 }
					
					 detailEntity.setExerciseCycle((exerciseCycle<9)?exerciseCycle+1:exerciseCycle);//练习周期 【周期最高为 9 】
			  }
			 detailEntity.setExerciseNumber(detailEntity.getExerciseNumber()+1); //练习次数
			 detailEntity.setConCorrectNumber(detailEntity.getConCorrectNumber()+1);//连续回答正确次数
			 detailEntity.setConErrorNumber(0);//连续回答错误次数
			 detailEntity.setLastExerciseDate(new Date());//上一次练习的日期
			 detailEntity.setCorrectNumber(detailEntity.getCorrectNumber()+1);//正确数
			 detailEntity.setSkilled((detailEntity.getSkilled()<=3)?detailEntity.getSkilled()+1:detailEntity.getSkilled()); ////熟练度（0，1，2，3，4）
			 detailEntity.setState(2);//正确需要巩固
		
		 }else{
			     /** 错误
			      * 如果答题错误 练习周期往后降一级，熟练度为 0 
			      * 
			      * **/
				 if(exerciseCycle==1){//因为周期最少为0，所以从1开始
					 detailEntity.setNextExerciseTime(new Date());//计划下次练习时间
				 }else if(exerciseCycle==2){
					 detailEntity.setNextExerciseTime(DateUtil.addOrSubHour(new Date(), LearningCycle.FIRST_TIME.timesanmp));//计划下次练习时间
				 }else if(exerciseCycle==3){
					 detailEntity.setNextExerciseTime(DateUtil.addOrSubHour(new Date(), LearningCycle.SECOND_TIME.timesanmp));
				 }else if(exerciseCycle==4){
					 detailEntity.setNextExerciseTime(DateUtil.addOrSubHour(new Date(), LearningCycle.Third_TIME.timesanmp));
				 }else if(exerciseCycle==5){
					 detailEntity.setNextExerciseTime(DateUtil.addOrSubHour(new Date(), LearningCycle.FOURTH_TIME.timesanmp));
				 }else if(exerciseCycle==6){
					 detailEntity.setNextExerciseTime(DateUtil.addOrSubHour(new Date(), LearningCycle.FIFTH_TIME.timesanmp));
				 }else if(exerciseCycle==7){
					 detailEntity.setNextExerciseTime(DateUtil.addOrSubHour(new Date(), LearningCycle.SIXTH_TIME.timesanmp));
				 }else if(exerciseCycle==8){
					 detailEntity.setNextExerciseTime(DateUtil.addOrSubHour(new Date(), LearningCycle.SEVENTH_TIME.timesanmp));
				 }else if(exerciseCycle==9){
					 detailEntity.setNextExerciseTime(DateUtil.addOrSubHour(new Date(), LearningCycle.EIGHTH_TIME.timesanmp));
				 }
			 
				 detailEntity.setExerciseCycle((detailEntity.getExerciseCycle()>1)?detailEntity.getExerciseCycle()-1:detailEntity.getExerciseCycle());//练习周期
				 detailEntity.setExerciseNumber(detailEntity.getExerciseNumber()+1); //练习次数
				 detailEntity.setSkilled(0);//熟练度（0，1，2，3）
				 detailEntity.setConCorrectNumber(0);//连续回答正确次数
				 detailEntity.setConErrorNumber(detailEntity.getConErrorNumber()+1);//连续回答错误次数
				 detailEntity.setErrorNumber(detailEntity.getErrorNumber()+1);//错误数
				 detailEntity.setLastExerciseDate(new Date()); //上一次练习的日期
				 detailEntity.setState(1);//答错后 提示上次答错
		 }	
		 reviewDao.updateLorePointExerciseDetail(detailEntity);
		 LOGGER.info("更新练习明细成功！");
		 utilService.checkErrorCard(detailEntity.getBookId(),detailEntity.getPointId(),Integer.parseInt(cradId),userId,right);
		 LOGGER.info("处理错题库信息成功！");
		 
		 utilService.bookProgress(userId, detailEntity.getBookId());
		 LOGGER.info("练习本进度计算统计完成");
	}

}
