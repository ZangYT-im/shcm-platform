package com.shcm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shcm.entity.Shop;
import com.shcm.mapper.ShopMapper;
import com.shcm.service.IShopService;
import org.springframework.stereotype.Service;


@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

}
