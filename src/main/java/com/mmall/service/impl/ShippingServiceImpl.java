package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if(rowCount > 0){
            Map result = Maps.newHashMap();
            result.put("shippingId", shipping.getId());
            return ServerResponse.createBySuccess("Create address success", result);
        }
        return ServerResponse.createByErrorMessage("Create address fail");
    }

    public ServerResponse del(Integer userId, Integer shippingId){
        int resultCount = shippingMapper.deleteByShipingIdUserId(userId, shippingId);
        if(resultCount > 0){
            return ServerResponse.createBySuccess("Delete address success");
        }
        return ServerResponse.createByErrorMessage("Delete address fail");
    }

    public ServerResponse update(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByShipping(shipping);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("update address success");
        }
        return ServerResponse.createByErrorMessage("update address fail");
    }

    public ServerResponse<Shipping> select(Integer userId, Integer shippingId){
        Shipping shipping = shippingMapper.selectByShippingIdUSerId(userId,shippingId);
        if(shipping == null){
            return ServerResponse.createByErrorMessage("cannot find the address");
        }
        return ServerResponse.createBySuccess("find address success", shipping);
    }

    public ServerResponse<PageInfo> list(Integer userId, int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
