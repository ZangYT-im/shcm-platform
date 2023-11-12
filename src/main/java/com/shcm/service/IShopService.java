package com.shcm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shcm.dto.Result;
import com.shcm.entity.Shop;


public interface IShopService extends IService<Shop> {

    Result queryById(Long id);
}
