package com.shcm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shcm.dto.Result;
import com.shcm.entity.Voucher;

public interface IVoucherService extends IService<Voucher> {

    Result queryVoucherOfShop(Long shopId);

    void addSeckillVoucher(Voucher voucher);
}
