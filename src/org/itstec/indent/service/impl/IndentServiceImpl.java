package org.itstec.indent.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.itstec.common.result.R;
import org.itstec.common.security.SHA1Util;
import org.itstec.indent.entify.Indent;
import org.itstec.indent.mapper.IndentMapper;
import org.itstec.indent.service.IndentService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@Service
public class IndentServiceImpl implements IndentService {

    private IndentMapper mapper;

    public IndentServiceImpl(IndentMapper indentMapper){
        this.mapper = indentMapper;
    }

	@Override
    public R<?> create(Indent indent) {
        R<Indent> r = this.get(indent);
        if(r.getData() != null){
            return R.code("201","订单已存在，请勿重复下单");
        }
        String orderNo;
        UUID uuid=UUID.randomUUID();
		try {
			orderNo = SHA1Util.shaEncode(uuid.toString());
			if("".equals(orderNo)){
				throw new Exception("订单号加密异常");
			}
		} catch (Exception e) {
			return R.code("202","订单号异常");
		}
        indent.setOrderNo(orderNo);
        indent.setPayStatus("0");
        indent.setCreateTime(new Date());
        indent.setUpdateTime(new Date());
        mapper.insert(indent);
        return R.data(indent);
    }

    @Override
    public R<Indent> get(Indent indent) {
        QueryWrapper<Indent> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Indent::getOrderNo,indent.getOrderNo());
        Indent orderDb = mapper.selectOne(queryWrapper);
        if(orderDb == null){
            return R.code("201","订单不存在");
        }
        return R.data(orderDb);
    }

    @Override
    public R<?> getByIdCardNo(Indent indent) {
        QueryWrapper<Indent> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Indent::getIdCardNo,indent.getIdCardNo());
        Indent orderDb = mapper.selectOne(queryWrapper);
        if(orderDb == null){
            return R.code("201","订单不存在");
        }
        return R.data(orderDb);
    }

    @Override
    public List<Indent> export(String beginTime,String endTime) {
        QueryWrapper<Indent> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(Indent::getPayTime,beginTime,endTime);
        List<Indent> list = mapper.selectList(queryWrapper);
        if(!CollectionUtils.isEmpty(list)){
            list.forEach(e -> {
                if("0".equals(e.getPayType())){
                    e.setPayType("支付宝");
                }else{
                    e.setPayType("微信");
                }
                if("0".equals(e.getPayStatus())){
                    e.setPayType("待支付");
                }else if("1".equals(e.getPayStatus())){
                    e.setPayType("支付成功");
                }else if("2".equals(e.getPayStatus())){
                    e.setPayType("支付失败");
                }
            });
        }
        return list;
    }

    @Override
    public IPage<?> exportByPage(Integer page,Integer pageSize) {
        IPage<Indent> oPage = new Page<>(page,pageSize);
        return mapper.selectPage(oPage,null);
    }
}
