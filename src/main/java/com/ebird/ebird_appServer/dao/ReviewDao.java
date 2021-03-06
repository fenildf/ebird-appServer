package com.ebird.ebird_appServer.dao;


import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.ebird.ebird_entity.dto.BookDto;
import com.ebird.ebird_entity.dto.CardDto;
import com.ebird.ebird_entity.dto.PointExerciseDetailDto;
import com.ebird.ebird_entity.dto.PonitDto;
import com.ebird.ebird_entity.entity.ErrorWarehouseEntity;
import com.ebird.ebird_entity.entity.UserExerciseDetailEntity;

@Repository
public interface ReviewDao {
	
	public void savaUserExcerciseDetail(UserExerciseDetailEntity entity);
	
	public void updateLorePointExerciseDetail(PointExerciseDetailDto entity);
	
	public List<BookDto> bookList(@Param("userId")Integer userId);
	
	
	/**
	 * 更新错题库里面错题状态
	 */
	public void updateErrorwarehouse(ErrorWarehouseEntity entity);
	
	/**
	 * 往错题库里面插入错题信息
	 */
	public void installErrorwarehouse(@Param("bookId")Integer bookId,
			                          @Param("pointId")Integer pointId,
			                          @Param("cardId")Integer cardId,
			                          @Param("userId")Integer userId ,
			                          @Param("isRight")Integer right);
	
	/**
	 * 往错题库里面查询错题信息
	 */
	public ErrorWarehouseEntity	 findErrorwarehouse(@Param("bookId")Integer bookId,
													@Param("pointId")Integer pointId,
													@Param("cardId")Integer cardId,
													@Param("userId")Integer userId);
	
	
	/**
	 * 删除错题库里面错题
	 */
	public void delErrorwarehouse(@Param("bookId")Integer bookId,
								  @Param("cardId")Integer cardId);
	
	
	
	/**
	 * 查询所有知识点	
	 * @param bookId
	 * @param chapterId
	 * @param userId
	 * @return
	 */
	public List<PonitDto> reviewPointAll(@Param("bookId")Integer bookId,
										 @Param("chapterId")Integer[] chapterId,
										 @Param("userId")Integer userId);
	
	/**
	 * 查询小于或等于 当前时间的知识点 (下次练习时间不为null的数据)
	 * @param bookId
	 * @param chapterId 章节Id
	 * @param userId
	 * @return 返回知识点List
	 */
	public List<PonitDto> reviewPoint(@Param("bookId")Integer bookId,
										@Param("chapterId")Integer[] chapterId,
										@Param("userId")Integer userId);
	
	/**
	 * 查询下次练习时间为null的数据 按 id升序排
	 * @param bookId
	 * @param chapterId
	 * @param userId
	 * @return
	 */
	public List<PonitDto> reviewPointNull(@Param("bookId")Integer bookId,
											@Param("chapterId")Integer[] chapterId,
											@Param("userId")Integer userId);
	
	
	/**
	 * 查询还没有到练习时间的知识点 （按时间升序排序）
	 * @param bookId
	 * @param chapterId
	 * @param userId
	 * @return
	 */
	public List<PonitDto> reviewPointBefore(@Param("bookId")Integer bookId,
											@Param("chapterId")Integer[] chapterId,
											@Param("userId")Integer userId);
	
	
	
	public List<CardDto> roundCard(@Param("pointId")Integer pointId);
	
	public List<PonitDto> roundPoint(@Param("userId")Integer userId,@Param("pointIdArray")Integer[] pointIdArray);
	
	public List<PonitDto> roundPointByUserId(@Param("userId")Integer userId);
	
	
	/**
	 * 根据知识点pointId 获取是否有练习权限
	 * @param pointId
	 * @param userId
	 * @return
	 */
	public List<PointExerciseDetailDto> getAuthByPointId(@Param("pointId")Integer pointId,@Param("userId")Integer userId);
	
	/**
	 * 根据练习本 bookId 获取是否有练习权限
	 * @param bookId
	 * @param userId
	 * @return
	 */
	public List<PointExerciseDetailDto> getAuthByBookId(@Param("bookId")Integer bookId,@Param("userId")Integer userId);
	

}
