package com.ebird.ebird_appServer.service;


import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ebird.ebird_appServer.dao.BookDao;
import com.ebird.ebird_appServer.dao.CradDao;
import com.ebird.ebird_appServer.dao.ReviewDao;
import com.ebird.ebird_entity.dto.CardDto;
import com.ebird.ebird_entity.dto.IdDto;
import com.ebird.ebird_entity.entity.BookEntity;
import com.ebird.ebird_entity.entity.CardEntity;
import com.ebird.ebird_entity.entity.CardExerciseDetailEntity;
import com.ebird.ebird_entity.entity.CradAnswersEntity;


@Service("loreCradService")
public class CradServiceImpl {
	
	@Autowired
	private ReviewDao reviewDao;

	@Autowired
	private CradDao cradDao;
	
	@Autowired
	private BookDao bookDao;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private BookServiceImpl bookService ;
	
	@Transactional
	public IdDto savaLoreCrad(CardEntity entity){
		   entity.setCreateId(systemService.getCurrentUser().getId());
		   entity.setCreateTime(new Date());
		 cradDao.addPonitNumber(entity.getPointId());
		 cradDao.savaLoreCrad(entity);
		 //保存卡片的联系详情
		 CardExerciseDetailEntity cardExerciseDetail = new CardExerciseDetailEntity();
		   cardExerciseDetail.setUserId(systemService.getCurrentUser().getId()); //-============该处需要需求 后面修改为 该卡片练习人的ID
		   cardExerciseDetail.setCardId(entity.getId());
		 cradDao.savaCardExerciseDetail(cardExerciseDetail);
		 IdDto idEntity = new IdDto();
		 idEntity.setId(entity.getId());
		 bookService.updateDetailByPointId(entity.getPointId());
		 return idEntity;
	}
	
	@Transactional
	public int delLoreCrad(Integer loreCardId){
		BookEntity entity = bookDao.findBookByCardId(loreCardId);
		int count = cradDao.delPonitNumber(loreCardId);
		cradDao.dellById(loreCardId);
		bookService.updateDetailBybookId(entity.getId());
		//同时删除 错题库里面的 卡片信息
		reviewDao.delErrorwarehouse(entity.getId(), loreCardId);
		return count;
	}
	
	@Transactional
	public int editLoreCrad(CardEntity entity){
		 entity.setUpdateId(systemService.getCurrentUser().getId());
		 entity.setUpdateTime(new Date());
		int count = cradDao.editLoreCrad(entity);
		bookService.updateDetailBycardId(entity.getId());
		return count;
	}
	
	public CardDto findLoreCradById(Integer loreCardId){
	   return cradDao.findLoreCradById(loreCardId);
	}
	
	public CardEntity findLoreCrad(Integer loreCardId){
		   return cradDao.findLoreCrad(loreCardId);
	}
	
	public List<CardDto> findLoreCradByPointId(Integer lorePointId){
		return cradDao.findLoreCradByPointId(lorePointId);
	}
	
	public List<CardEntity> getOpenLoreCrad(){
		return cradDao.getOpenLoreCrad();
	}
	
	public CardExerciseDetailEntity getLoreCradDetailByPointId(Integer loreCardId){
		return cradDao.getLoreCradDetailByPointId(loreCardId);
	}
	
	public List<CradAnswersEntity> getLoreCradAnswerByPointId(Integer loreCardId){
		return cradDao.getLoreCradAnswerByPointId(loreCardId);
	}
	
	/**
	 * 根据知识点ID ，随机获取一个卡片信息
	 * @param request
	 * @param response
	 * @param pointId
	 * @return
	 */
	public CardDto roundCard(Integer pointId){
		  List<CardDto> cardList = reviewDao.roundCard(pointId);
		  if(cardList.size()>0){
		      java.util.Random random = new java.util.Random();
		      int randomPos = random.nextInt(cardList.size()); 
		      return cardList.get(randomPos);
		  }else{
			  return null;
		  }
	}
	
}
