package edu.heuet.android.logindemo.dao;

import edu.heuet.android.logindemo.dataobject.NewsDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NewsDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(NewsDO record);

    int insertSelective(NewsDO record);

    NewsDO selectByPrimaryKey(Long id);

    List<NewsDO> selectByTitle(@Param("title") String title);

    List<NewsDO> selectAll();

    int updateByPrimaryKeySelective(NewsDO record);

    int updateByPrimaryKey(NewsDO record);
}