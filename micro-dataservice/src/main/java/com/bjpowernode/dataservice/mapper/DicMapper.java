package com.bjpowernode.dataservice.mapper;

import com.bjpowernode.api.po.DicInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DicMapper {

    /**
     * @param category 字典类别
     * @return  字典数据
     */
    List<DicInfo> selectListByCatetory(@Param("cg") String category);
}
