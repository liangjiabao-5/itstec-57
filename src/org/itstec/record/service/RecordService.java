package org.itstec.record.service;

import javax.servlet.http.HttpServletRequest;

import org.itstec.common.result.R;

public interface RecordService {

	R<?> show(String lPath);
	
	R<?> grab(String lPath);
	
	R<?> quickArch();
	
	R<?> arch(String para);
	
	R<?> handle(HttpServletRequest request);
	
}
