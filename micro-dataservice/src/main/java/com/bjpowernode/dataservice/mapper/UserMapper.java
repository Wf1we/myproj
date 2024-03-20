package com.bjpowernode.dataservice.mapper;

import com.bjpowernode.api.po.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    /**
     * @return 注册总用户数量
     */
    Integer selectRegisterUserCount();

    /**
     * 根据手机号查询用户
     * @param phone
     * @return
     */
    User selectByPhone(@Param("phone") String phone);

    /**
     * 添加用户，并返回主键id值
     * @param userInfo 用户信息
     * @return
     */
    int insertBackNewId(User userInfo);


    /**
     * 登录
     * @param phone
     * @param loginPassword
     * @return
     */
    User selectLoginUser(@Param("phone") String phone, @Param("loginPassword") String loginPassword);

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);



}