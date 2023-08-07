package org.itstec.indent.controller;

import org.itstec.common.json.JsonUtil;
import org.itstec.common.result.R;
import org.itstec.common.security.CryptoDecryptionSignSecurity;
import org.itstec.indent.entify.Indent;
import org.itstec.indent.service.IndentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/indent")
public class PayController {

    @Autowired
    private IndentService service;

    /**
     * 下单
     * @param order
     * @return
     */
    @CryptoDecryptionSignSecurity
    @PostMapping(value = "/create")
    public R<?> create(Indent order){
        return service.create(order);

    }

    /**
     * 查询订单
     * @param order
     * @return
     */
    @PostMapping(value = "/getById")
    public R<?> getById(Indent order){
        return service.get(order);
    }

    /**
     * 查询订单-根据身份证号查询订单
     * @param order
     * @return
     */
    @CryptoDecryptionSignSecurity(requestDecryption=false,requestSign=false,partialCrySign = {"idCardNo"})
    @PostMapping(value = "/getByIdCardNo")
    public R<?> getByIdCardNo(Indent order){
        return service.getByIdCardNo(order);
    }

    /**
     * 按页导出订单
     * @param page
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/exportByPage")
    public R<?> exportByPage(Integer page,Integer pageSize){
        return R.data(JsonUtil.toJson(service.exportByPage(page,pageSize).getRecords()).getBytes());
    }

    /**
     * 按时间段导出订单
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping(value = "/exportByTime")
    public R<?> exportByTime(String beginTime, String endTime) {
        return R.data(JsonUtil.toJson(service.export(beginTime,endTime)).getBytes());
    }
}
