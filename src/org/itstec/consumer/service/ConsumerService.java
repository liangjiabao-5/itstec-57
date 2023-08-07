package org.itstec.consumer.service;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.itstec.common.result.R;
import org.itstec.consumer.entity.Consumer;
import org.itstec.consumer.entity.ConsumerInfo;
import org.springframework.web.multipart.MultipartFile;

public interface ConsumerService{

    R<?> register(HttpServletRequest request, HttpServletResponse response, Consumer consumer);

    R<?> login(HttpServletRequest request, String name, String pwd);
    
    String loginAutoRedi(HttpServletRequest request, String name, String pwd, String url);
    
    R<?> logout(HttpServletRequest request, String name);
    
    R<?> query(HttpServletRequest request, String name, String status);
    
    R<?> updateInfo(ConsumerInfo consumer);
    
    R<?> uploadImg(MultipartFile img, String name);
    
    int updateBatchInfo(File excel) throws IOException;
    
    int importBatchInfo(File excel, int mSize);
    
}
