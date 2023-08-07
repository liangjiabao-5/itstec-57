package org.itstec.record.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javax.servlet.http.HttpServletRequest;

import org.itstec.common.result.R;
import org.itstec.common.util.DateUtil;
import org.itstec.common.util.FileUtil;
import org.itstec.common.util.ZipUtils;
import org.itstec.record.service.RecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

@Service
public class RecordServiceImpl implements RecordService{

	private Logger logger = LoggerFactory.getLogger(RecordServiceImpl.class);
	
	@Override
	public R<?> show(String lPath) {
		boolean f = false;
		f=FileUtil.getInstance().isFolder(lPath);
		String showContent="<tr bgColor='#FFFFFF'><td colspan=2>";
		if(f){
			String[][] s=FileUtil.getInstance().showFolder(lPath);
			boolean t=false;
			for(int i=0;i<s[0].length;i++){
				if(s[0][i]!=null){
					showContent+="&nbsp;&nbsp;子目录:&nbsp;&nbsp;<a href='#' onclick=_showFolderByHref('"+s[0][i]+"') >/"+s[0][i]+"</a><br>";
					t=true;
				}
			}
			for(int i=0;i<s[1].length;i++){
				if(s[1][i]!=null){
					showContent+="&nbsp;&nbsp;文&nbsp;&nbsp;&nbsp;&nbsp;件:&nbsp;&nbsp;"+s[1][i]+"<br>";
					t=true;
				}
			}
			if(!t){
				showContent+="&nbsp;&nbsp;该目录下没有子目录和文件存在<br>";
			}
			showContent+="</td></tr>";
		}else{
			return R.code("200","输入的查看目录不存在，请核实！");
		}

		return R.code("200",showContent);
	}

	@Override
	public R<?> grab(String lPath) {
        String downPath="";
		String fileName=DateUtil.getDateTimeStr();
		try {
			downPath = ResourceUtils.getURL("classpath:").getPath();
			FileUtil fileUtil = FileUtil.getInstance();
			ZipUtils obj = new ZipUtils(lPath+"/"+fileName+".zip");
	    	obj.compress(lPath); 
	    	
			fileUtil.copyFile(lPath+"/"+fileName+".zip",downPath+fileName+".zip");
			
		}catch (Exception e) {
			logger.error("日志抓取异常");
		}
		return R.code("200",downPath+fileName);
	}

	@Override
	public R<?> quickArch() {
		String[] cmd = new String[3];
		cmd[0] = "cmd.exe";
		cmd[1] = "/c";
		cmd[2] = "";
		try {
			cmd[2]=match("glob:**/logQuickArch.bat","D:/itstec");
			Process process = Runtime.getRuntime().exec(cmd);
			execute(process.getInputStream());
		} catch (Exception e) {
			logger.error("日志归档异常：", "glob:**/logQuickArch.bat","D:/itstec");
			return R.code("200","日志归档异常");
		}
		
		return R.code("200","归档成功");
	}
	
	@Override
	public R<?> arch(String para) {
		String[] cmd = new String[3];
		cmd[0] = "cmd.exe";
		cmd[1] = "/c";
		cmd[2] = "D:/itstec/logArch.bat";
		try {
			cmd[2]=cmd[2]+" "+para;
			Process process = Runtime.getRuntime().exec(cmd);
			execute(process.getInputStream());
		} catch (Exception e) {
			logger.error("日志归档程序异常");
			return R.code("200","日志归档异常");
		}
		
		return R.code("200","归档成功");
	}

	private static String match(String glob, String location) throws IOException {
	    StringBuilder result = new StringBuilder();
	    PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(glob);
	    Files.walkFileTree(Paths.get(location), new SimpleFileVisitor<Path>() {

	        @Override
	        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
	            if (pathMatcher.matches(path)) {
	                result.append(path.toString());
	                return FileVisitResult.TERMINATE;
	            }
	            return FileVisitResult.CONTINUE;
	        }
	    });

	    return result.toString();
	}

	private static void execute(final InputStream input) {
		//执行日志归档程序 略
	}

	@Override
	public R<?> handle(HttpServletRequest request) {
		// 自定义操作 在需要的业务实现类中实现
		return R.code("200", "未实现");
	}
	
}
