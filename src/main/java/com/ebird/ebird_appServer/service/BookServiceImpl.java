package com.ebird.ebird_appServer.service;


import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ebird.ebird_appServer.dao.BookDao;
import com.ebird.ebird_appServer.dao.PointDao;
import com.ebird.ebird_entity.dto.BookDto;
import com.ebird.ebird_entity.dto.BookProgressDto;
import com.ebird.ebird_entity.dto.IdDto;
import com.ebird.ebird_entity.dto.PonitDto;
import com.ebird.ebird_entity.entity.BookEntity;

@Service
public class BookServiceImpl {
	
	@Autowired
	private SystemService systemService ;
	
	@Autowired
	private BookDao bookDao;
	
	@Autowired
	private PointDao lorePointDao;
	
	public IdDto bookSava(BookEntity entity){
		entity.setCreateId(systemService.getCurrentUser().getId());
		entity.setCreateTime(new Date());
		bookDao.bookSava(entity);
		IdDto ideDto = new IdDto();	
		ideDto.setId(entity.getId());
		//同时往用户-练习本 表插入一条关联数据
		
		return ideDto;
	}
	
	/**
	 * 根据练习本ID 删除练习本
	 * @param bookId
	 * @return
	 */
	@Transactional
	public int delBook(String bookId){
		int count = bookDao.delBookById(bookId);
		lorePointDao.delPoinDetailtByBookId(Integer.parseInt(bookId));
		lorePointDao.delPointByBookId(Integer.parseInt(bookId));
		return count;
	}
	
	public int editBook(BookEntity entity){
		entity.setUpdateId(systemService.getCurrentUser().getId());
		entity.setUpdateTime(new Date());
		entity.setUpdateDetailId(systemService.getCurrentUser().getId());
		entity.setUpdateDetailTime(new Date());
		int count = bookDao.editBook(entity);
		return count;
	}
	
	public BookEntity findBook(String id){
		BookEntity entity = bookDao.findBook(Integer.parseInt(id));
		return entity;
	}
	
	public BookDto findBookById(String id){
		BookDto entity = bookDao.findBookById(Integer.parseInt(id));
		return entity;
	}
	
	public List<BookDto> searchAllExcercise(Integer userId){
		List<BookDto> list = bookDao.searchAllExcercise(userId);
		return list;
	}
	
	public List<PonitDto> findExcerciseIdToPonit(Integer bookId){
		return bookDao.findExcerciseIdToPonit(bookId);
	}

	
	/**
	 * 获取所有共享类型练习本
	 * 注：不包含自己所共享出现的练习本
	 * @return
	 */
	public List<BookDto> getOpenBook(){
		Integer userId =  systemService.getCurrentUser().getId();
		List<BookDto> list = bookDao.getOpenBook(userId);
		return list;
	}
	
	public BookProgressDto bookProgress(Integer bookId){
		Integer userId =  systemService.getCurrentUser().getId();
		Integer count = bookDao.bookProgress(bookId,userId);
		Integer yes= bookDao.bookProgressYes(bookId,userId);
		DecimalFormat df=new DecimalFormat("0.00");
		double progress =Double.valueOf(df.format((float)yes/count));
		 BookProgressDto dto = new BookProgressDto();
		 dto.setBookId(bookId);
		 dto.setProgress(progress);
		return dto;
	}
	
	/**
	 * 根据 poindId 查询 知识点所属练习本信息
	 * @param pointId
	 * @return
	 */
	public BookEntity findBookByPointId(Integer pointId){
		return bookDao.findBookByPointId(pointId);
	}
	
    /**
     * 根据 cardId 查询 卡片所属练习本信息
     * @param cardId
     * @return
     */
    public BookEntity findBookByCardId(Integer cardId){
    	return bookDao.findBookByCardId(cardId);
	}
    
    /**
     * 如果创建者 更新了 练习下面的 知识点 或 卡片信息 练习本需要记录 更新时间
     * 
     * @param bookId
     */
    public void updateDetailBybookId(Integer bookId){
    	Integer userId =  systemService.getCurrentUser().getId();
    	bookDao.updateDetail(bookId, userId, new Date());
    }
    
    public void updateDetailBycardId(Integer cardId){
    	Integer userId =  systemService.getCurrentUser().getId();
    	BookEntity entity = findBookByCardId(cardId);
    	bookDao.updateDetail(entity.getId(), userId, new Date());
    }
    
    public void updateDetailByPointId(Integer pointId){
    	Integer userId =  systemService.getCurrentUser().getId();
    	BookEntity entity = findBookByPointId(pointId);
    	bookDao.updateDetail(entity.getId(), userId, new Date());
    }
    
    
    
 

}
