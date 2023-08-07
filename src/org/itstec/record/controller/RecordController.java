package org.itstec.record.controller;

import java.lang.reflect.Constructor;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.itstec.common.result.R;
import org.itstec.record.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/record")
public class RecordController {
	
	private static Logger log= LogManager.getLogger(RecordController.class);
	
    @Autowired
    private RecordService service;

    @RequestMapping(value = "/show",method = {RequestMethod.POST})
    public R<?> show(String logPath){
        return service.show(logPath);
    }

    @RequestMapping(value = "/grab",method = {RequestMethod.POST})
    public R<?> logGrab(String logPath){
        return service.grab(logPath);
    }
    
    @RequestMapping(value = "/quickArch",method = {RequestMethod.POST})
    public R<?> logQuickArch(){
        return service.quickArch();
    }
    
    @RequestMapping(value = "/arch",method = {RequestMethod.POST})
    public R<?> logArch(String para){
    	log.info("logArch:{}",para);
        return service.arch(para);
    }
    
    @RequestMapping(value = "/custom",method = {RequestMethod.POST})
    public R<?> custom(HttpServletRequest request){
    	String cName = request.getParameter("cName");
    	R<?> result = R.code("200", "初始未执行");
    	try {
			Class<?> clazz = Class.forName(cName);
			Constructor<?> constructor = clazz.getConstructor();
			RecordService obj = (RecordService)constructor.newInstance();
			result = obj.handle(request);
		} catch (Exception e) {
			return R.code("200", "未找到相应方法");
		}
        return result;
    }
    
}
