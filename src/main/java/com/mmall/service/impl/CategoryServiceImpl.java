package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iCategoryService")
public class CategoryServiceImpl {
    @Autowired
    CategoryMapper categoryMapper;

    public ServerResponse addCategory(String categoryName, Integer parentId){
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("Add category argument error");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true); //category valid

        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccessMessage("Add category success");
        }
        return ServerResponse.createByErrorMessage("Add category fail");
    }

    public ServerResponse updateCategoryName(Integer categoryId, String categoryName){
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("Update category argument error");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccessMessage("Update category success");
        }
        return ServerResponse.createByErrorMessage("Update category fail");
    }
}
