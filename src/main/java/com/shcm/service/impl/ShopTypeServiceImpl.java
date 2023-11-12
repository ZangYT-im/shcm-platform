package com.shcm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shcm.entity.ShopType;
import com.shcm.mapper.ShopTypeMapper;
import com.shcm.service.IShopTypeService;
import org.springframework.stereotype.Service;


@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

}
