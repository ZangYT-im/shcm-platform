package com.shcm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shcm.entity.VoucherOrder;
import com.shcm.mapper.VoucherOrderMapper;
import com.shcm.service.IVoucherOrderService;
import org.springframework.stereotype.Service;


@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

}
