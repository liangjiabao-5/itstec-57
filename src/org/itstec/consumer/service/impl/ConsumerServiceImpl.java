package org.itstec.consumer.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.itstec.base.db.DBBeanBase;
import org.itstec.common.result.R;
import org.itstec.common.security.SHA1Util;
import org.itstec.common.util.PassUtil;
import org.itstec.common.util.Poi4Util;
import org.itstec.common.util.SecurityUtil;
import org.itstec.consumer.entity.Consumer;
import org.itstec.consumer.entity.ConsumerInfo;
import org.itstec.consumer.mapper.ConsumerMapper;
import org.itstec.consumer.service.ConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monitorjbl.xlsx.StreamingReader;

@Service
public class ConsumerServiceImpl implements ConsumerService {

	private Logger logger = LoggerFactory.getLogger(ConsumerServiceImpl.class);

	@Autowired
	private ConsumerMapper userMapper;

	@Override
	public R<?> register(HttpServletRequest request, HttpServletResponse response, Consumer consumer) {
		String password = consumer.getPassword();
		if (StringUtils.isEmpty(consumer.getUsername())) {
			return R.code("201", "用户名为空");
		}
		if ("0".equals(PassUtil.checkPwd(password))) {
			return R.code("202", "密码校验未通过");
		}
		HttpSession session = request.getSession();
		QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
		queryWrapper.lambda().eq(Consumer::getUsername, consumer.getUsername());
		Consumer userDB = userMapper.selectOne(queryWrapper);
		if (userDB != null) {
			return R.code("203", "用户已存在");
		}

		String enPassword = SHA1Util.shaEncode(password);
		consumer.setPassword(enPassword);
		consumer.setRegisterTime(new Date());
		int i = userMapper.insert(consumer);
		if (i < 0) {
			return R.code("204", "用户注册失败，未通过数据校验");
		}
		session.setAttribute(String.valueOf(consumer.getId()), consumer.getId());
		Cookie cookie =new Cookie("user",consumer.getUsername()+"#"+consumer.getMobilephone());
		cookie.setMaxAge(24*60*60);
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
		return R.code("200", "注册成功");
	}

	@Override
	public R<?> login(HttpServletRequest request, String username, String password) {
		QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
		queryWrapper.lambda().eq(Consumer::getUsername, username);
		HttpSession session = request.getSession();
		//session.setMaxInactiveInterval(-1);
		Consumer user = userMapper.selectOne(queryWrapper);
		if (user == null) {
			return R.code("201", "用户名不存在，登录失败!");
		} else {
			String epassword = SHA1Util.shaEncode(password);
			session = request.getSession(true);
			session.setAttribute(String.valueOf(user.getId()), user.getId());
			if (!user.getPassword().equals(epassword)) {
				logger.info(username+"登录失败，"+password);
				return R.code("202", "密码不正确，登录失败!");
			}
		}
		session.setAttribute(String.valueOf(user.getId()), user.getId());
		return R.data(user);
	}

	@Override
	public String loginAutoRedi(HttpServletRequest request, String username, String password, String url) {
		String newUrl = "/login/error";
		QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
		queryWrapper.lambda().eq(Consumer::getUsername, username);
		HttpSession session = request.getSession();
		Consumer user = userMapper.selectOne(queryWrapper);
		if (user == null) {
			newUrl = "/login/fail";
		} else {
			password = SHA1Util.shaEncode(password);
			if (!user.getPassword().equals(password)) {
				newUrl = "/login/fail";
			} else {
				session.invalidate();
				session = request.getSession(true);
				session.setAttribute(String.valueOf(user.getId()), user.getId());
				newUrl = url;
			}
		}
		return newUrl;
	}

	@Override
	public R<?> logout(HttpServletRequest request, String username) {
		HttpSession session = request.getSession();
		if (session.getAttribute(username) != null) {
			session.removeAttribute(username);
			session.invalidate();
		}
		return R.code("200", "退出成功");
	}

	@Override
	public R<?> query(HttpServletRequest request, String username, String status) {
		QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
		queryWrapper.lambda().eq(Consumer::getUsername, username);
		queryWrapper.lambda().eq(Consumer::getStatus, status);
		Consumer user = userMapper.selectOne(queryWrapper);
		if (user == null) {
			return R.code("201", "用户名不存在，查询失败!");
		}
		return R.data(user);
	}

	@Override
	public R<?> updateInfo(ConsumerInfo consumerInfo) {
		QueryWrapper<Consumer> queryWrapper = new QueryWrapper<>();
		queryWrapper.lambda().eq(Consumer::getId, consumerInfo.getId());
		Consumer userDB = userMapper.selectOne(queryWrapper);
		if (userDB == null) {
			return R.code("202", "用户不存在");
		} else {
			Consumer user = new Consumer();
			user.setId(consumerInfo.getId());
			user.setCity(consumerInfo.getCity());
			user.setDeliveryAddress(consumerInfo.getDeliveryAddress());
			user.setEmail(consumerInfo.getEmail());
			user.setMobilephone(consumerInfo.getMobilephone());
			userMapper.updateById(user);
		}
		return R.code("200", "用户信息更新");
	}

	@Override
	public R<?> uploadImg(MultipartFile multifile, String username) {
		String UPLOADED_FOLDER = "/itstec/tmp/";

		if (multifile.isEmpty()) {
			return R.data("请选择一张图片上传");
		}

		String fileName = multifile.getOriginalFilename();
		Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
		Matcher matcher = pattern.matcher(fileName);
		fileName = matcher.replaceAll("");
		String Suffix = fileName.substring(fileName.lastIndexOf("."));
		String mimeType = multifile.getContentType();
		String filePath = UPLOADED_FOLDER + fileName;

		String[] picSuffixList = { ".jpg", ".png", ".jpeg", ".gif", ".bmp", ".ico" };
		boolean suffixFlag = false;
		for (String white_suffix : picSuffixList) {
			if (Suffix.toLowerCase().equals(white_suffix)) {
				suffixFlag = true;
				break;
			}
		}
		if (!suffixFlag) {
			logger.error("图片格式（后缀名）非法");
			deleteFile(filePath);
			return R.data("图片格式非法");
		}

		String[] mimeTypeBlackList = { "text/html", "text/javascript", "application/javascript",
				"application/ecmascript", "text/xml", "application/xml" };
		for (String blackMimeType : mimeTypeBlackList) {
			if (SecurityUtil.replaceSpecialStr(mimeType).toLowerCase().contains(blackMimeType)) {
				logger.error("图片格式（mimeType）非法");
				deleteFile(filePath);
				return R.data("图片格式非法");
			}
		}

		try {
			byte[] bytes = multifile.getBytes();
			Path path = Paths.get(filePath);
			Files.write(path, bytes);
		} catch (IOException e) {
			logger.error("图片上传失败,IOException");
			deleteFile(filePath);
			return R.data("图片上传失败" + e);
		}

		return R.data("图片上传成功");
	}

	private void deleteFile(String filePath) {
		File delFile = new File(filePath);
		if (delFile.isFile() && delFile.exists()) {
			if (delFile.delete()) {
				return;
			}
		}
	}

	@Override
	public int updateBatchInfo(File excel) throws IOException {
		int t = 0;

		Poi4Util poi;
		FileInputStream is = null;

		String[] colum = new String[] { "id", "username", "mobilephone", "email", "delivery_address" };
		String update = "update user ";
		String set = "";
		String where = "";
		String tmp = "";
		DBBeanBase dbbean = new DBBeanBase(true);
		try {
			is = new FileInputStream(excel.getAbsolutePath());

			poi = new Poi4Util(is, "xlsx", "Sheet1");

			for (int i = 1; i < 10000; i++) {
				if ("".equals(poi.getCellStringValue(i, 1))) {
					break;
				}
				set = "set ";
				for (int j = 2; j <= 4; j++) {
					if ("rgb(146,208,80)".equals(poi.getCellBackgroundColor(i, j))
							|| "rgb(146,208,79)".equals(poi.getCellBackgroundColor(i, j))) {
						try {
							tmp = poi.getCellStringValue(i, j);
						} catch (java.lang.IllegalStateException e) {
							tmp = poi.getCellStringValue(i, j);
						}
						set = set + " " + colum[j] + "='" + tmp + "',";
					}
				}
				if (!"set ".equals(set)) {
					set = set.substring(0, set.length() - 1) + " ";
					where = "where id=" + poi.getCellStringValue(i, 0);
					int j = dbbean.executeUpdate(update + set + where);
					if (j > 0) {
						t++;
					}
				}
			}
			dbbean.close();
		} catch (FileNotFoundException e) {
			logger.error("文件未找到");
			return -404;
		} catch (IOException e) {
			logger.error("IO异常");
			return -500;
		} catch (Exception e) {
			logger.error("数据库执行异常");
			dbbean.rollback();
			return -1;
		}finally{
			if(is!=null){
				is.close();
			}
			
		}

		return t;
	}

	@Override
	public int importBatchInfo(File excel, int memorySize) {
		int insRows = 0;
		String values = "";
		DBBeanBase dbbean = new DBBeanBase();
		try {
			String[] colum = new String[] { "username", "password", "mobilephone", "email", "delivery_address" };
			List<Map<String, Object>> datas = readBigExcel(excel.getAbsolutePath(),
					"username,password,mobilephone,email,delivery_address", "Sheet1", 1, 0, memorySize);
			String insert = "insert into user (username,password,mobilephone,email,delivery_address) values (";
			for (int i = 0; i < datas.size(); i++) {
				values = "";
				for (int j = 0; j < colum.length; j++) {
					values = values + "'" + datas.get(i).get(colum[j]) + "',";
				}
				values = values.substring(0, values.length() - 1) + ")";
				int t = dbbean.executeUpdate(insert + values);
				if (t > 0) {
					insRows++;
				}
			}
		} catch (Exception e) {
			logger.error("批量导入异常");
			return -1;
		} finally {
			dbbean.close();
		}
		return insRows;
	}

	private static List<Map<String, Object>> readBigExcel(String excel, String rowname, String stasheetName,
			int starowNum, int stacolumn, int memorySize) throws Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		InputStream inputStream = new FileInputStream(excel);
		try (Workbook wk = StreamingReader.builder().rowCacheSize(1000)
				.bufferSize(memorySize)
				.open(inputStream);) {
			inputStream.close();
			Sheet sheet = wk.getSheet(stasheetName);
			String[] rownameSplit = rowname.split(",");
			int columnlength = rownameSplit.length;
			Cell cell = null;
			for (Row row : sheet) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				if (row.getRowNum() >= starowNum) {
					for (int j = stacolumn; j < columnlength; j++) {
						cell = row.getCell(j);
						if (cell != null) {
							paramMap.put(rownameSplit[j], cell.getStringCellValue());
						} else {
							paramMap.put(rownameSplit[j], null);
						}

					}
					resultList.add(paramMap);
				}
			}
		}
		return resultList;
	}

}
