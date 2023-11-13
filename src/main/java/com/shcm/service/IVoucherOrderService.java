package com.shcm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shcm.dto.Result;
import com.shcm.entity.VoucherOrder;


public interface IVoucherOrderService extends IService<VoucherOrder> {

    Result seckillVoucher(Long voucherId);
}
