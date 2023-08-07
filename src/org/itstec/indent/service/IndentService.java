package org.itstec.indent.service;

import java.util.List;

import org.itstec.common.result.R;
import org.itstec.indent.entify.Indent;

import com.baomidou.mybatisplus.core.metadata.IPage;

public interface IndentService {

    R<?> create(Indent order);

    R<?> get(Indent order);

    R<?> getByIdCardNo(Indent order);

    List<Indent> export(String beginTime, String endTime);

    IPage<?> exportByPage(Integer page, Integer pageSize);
    
}
