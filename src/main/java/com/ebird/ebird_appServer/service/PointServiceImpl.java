package com.ebird.ebird_appServer.service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ebird.ebird_appServer.dao.BookDao;
import com.ebird.ebird_appServer.dao.CradDao;
import com.ebird.ebird_appServer.dao.PointDao;
import com.ebird.ebird_entity.dto.IdDto;
import com.ebird.ebird_entity.dto.PointExerciseDetailDto;
import com.ebird.ebird_entity.dto.PonitDto;
import com.ebird.ebird_entity.dto.PonitSkilledDto;
import com.ebird.ebird_entity.entity.BookEntity;
import com.ebird.ebird_entity.entity.PointEntity;
import com.ebird.ebird_entity.entity.PointExerciseDetailEntity;
import com.ebird.ebird_entity.entity.UserBookEntity;


@Service("lorePointService")
public class PointServiceImpl {
	
	@Autowired
	private SystemService systemService ;
	
	@Autowired
	private PointDao pointDao;
	
	@Autowired
	private BookDao bookDao;
	
	@Autowired
	private CradDao cardDao;
	
	@Autowired
	private UserBookServiceImpl userBookService;
	
	@Autowired
	private BookServiceImpl bookService ;	
	
	@Transactional
	public IdDto savaLorePoint(PointEntity entity){
		  entity.setCreateId(systemService.getCurrentUser().getId());
		  entity.setCreateTime(new Date());
		  entity.setNumber(0);
		  if(entity.getSort()==null||entity.getSort().equals("")){
			  entity.setSort(0);  
		  }
		  
		 pointDao.install(entity);
		
		/**
		 * 新方案，加入用户和练习本关连关系，当有新增知识点时会对应把该知识推送到 订阅知识点所属的练习本下所有的用户
		 * */
		//获取订阅练习本下所有的用户
	    List<UserBookEntity> userBookEntity = userBookService.findUser(entity.getBookId());
		pushAddPointToUser(entity.getId() ,entity.getBookId(),userBookEntity);
		
		bookService.updateDetailBybookId(entity.getBookId());//推到练习本更新
		
		IdDto identity = new IdDto();	
		identity.setId(entity.getId());
		return identity;
	}
	
	@Transactional
	public int editLorePoint(PointEntity entity){
		entity.setUpdateId(systemService.getCurrentUser().getId());
		entity.setUpdateTime(new Date());
		int count = pointDao.update(entity);
		bookService.updateDetailBybookId(entity.getBookId());//推到练习本更新
		return count;
	}

	public int batchUpdate(String[] pointIdArray,Integer chapterId){
		Integer[] points =new Integer[pointIdArray.length];
		for (int i=0;i<pointIdArray.length;i++){
			points[i]=Integer.parseInt(pointIdArray[i]);
		}
		int count = pointDao.batchUpdate(points,chapterId);
		return count;
	}
	
	@Transactional
	public int delLorePoint(Integer pointId){
		BookEntity entity = bookDao.findBookByPointId(pointId);
		int count = pointDao.dellById(pointId);
		//同时删除该知识点下所有的知识卡片信息
		cardDao.delCardByPointId(pointId);
		//同时删除知识点对应的用户练习记录
		delPoinDetailtByPointId(pointId);
		bookService.updateDetailBybookId(entity.getId());//推到练习本更新
		return count ;
	}

	public PonitDto findLorePointId(Integer id){
		return pointDao.findById(id);
	}
	
	public PointEntity findLorePoint(Integer id){
		return pointDao.findLorePoint(id);
	}
	
	public List<PonitDto> searchAllLorePoint(){
		return pointDao.searchAllLorePoint();
	}
	
	public PointExerciseDetailDto findPointIdByDetail(Integer id){
		Integer userId =systemService.getCurrentUser().getId();
		
		return pointDao.findPointIdByDetail(id,userId);
	}
	
	
	/**
	 * 根据知识点ID ,练习本Id 新增【推送】到所有订阅用户
	 * @param pointId
	 * @param bookId
	 * @param entityList 订阅该练习本下的所有用户
	 */
	public void pushAddPointToUser(Integer pointId ,Integer bookId,List<UserBookEntity> entityList){
		if(entityList.size()>0){
			List<PointExerciseDetailEntity> detailList = new ArrayList<>();
			for(UserBookEntity entity :entityList){
				//插入知识点练习明细
			    PointExerciseDetailEntity pointDetail = new PointExerciseDetailEntity();
					pointDetail.setUserId(entity.getUserId());
					pointDetail.setPointId(pointId);
					pointDetail.setBookId(entity.getBookId());
					pointDetail.setNextExerciseTime(new Date());
					pointDetail.setSkilled(0);
					pointDetail.setExerciseCycle(0);
					pointDetail.setState(0);
			    detailList.add(pointDetail);
			}
			pointDao.addPointDetailList(detailList);
		}
	}
	
	
	/**
	 * 根据练习本Id  把该练习本下所有知识点【推送】到所有订阅用户
	 * @param bookId
	 */
	public void pushPointToUser(Integer bookId){
		//获取练习本下所有的知识点
		List<PonitDto> entityList = bookService.findExcerciseIdToPonit(bookId);
		//获取订阅练习本下所有的用户
		List<UserBookEntity> userBookEntity = userBookService.findUser(bookId);
        for(PonitDto dto :entityList ){
        	pushAddPointToUser(dto.getId() ,bookId,userBookEntity);
        }
	}
	
	
	/**
	 * 根据用户推送
	 * @param bookId
	 * @param userId
	 */
	public void pushPoint(Integer bookId,Integer userId){
		//获取练习本下所有的知识点
		List<PonitDto> entityList = bookService.findExcerciseIdToPonit(bookId);
		if(entityList.size()>0){
			List<PointExerciseDetailEntity> detailList = new ArrayList<>();
			for(PonitDto entity :entityList){
				//插入知识点练习明细
			    PointExerciseDetailEntity pointDetail = new PointExerciseDetailEntity();
					pointDetail.setUserId(userId);
					pointDetail.setPointId(entity.getId());
					pointDetail.setBookId(bookId);
					pointDetail.setNextExerciseTime(new Date());
					pointDetail.setSkilled(0);
					pointDetail.setExerciseCycle(0);
					pointDetail.setState(0);
			    detailList.add(pointDetail);
			}
			pointDao.addPointDetailList(detailList);
		}
	}
	
	
	/**
	 * 根据知识点ID  删除【推送】到订阅用户的练习明细
	 * @param pointId
	 * @param bookId
	 */
	public void delPoinDetailtByPointId(Integer pointId){
		pointDao.delPoinDetailtByPointId(pointId);
	}
	
	/**
	 * 根据练习本Id 删除【推送】到订阅用户的练习明细
	 * @param pointId
	 * @param bookId
	 */
	public void delPoinDetailtByBookId(Integer bookId){
		pointDao.delPoinDetailtByBookId(bookId);
	}
	
	
	
	/**
	 * 根据练习本Id,查找用户对知识点的练习情况
	 * @param bookId
	 * @return
	 */
	public List<PonitSkilledDto> findBookIdToPonit(Integer bookId){
		Integer userId =  systemService.getCurrentUser().getId();
		return pointDao.findBookIdToPonit(bookId,userId);
	}
	
	/**
	 * 根据练习本Id，查找练习本下所有的知识点
	 * @param bookId
	 * @return
	 */
	public List<PonitSkilledDto> searchLorePointByBookId(Integer bookId){
		return pointDao.searchLorePointByBookId(bookId);
	}
	
}
