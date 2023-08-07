package org.itstec.common.json;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.MapType;

public class JsonUtil {

	private static Logger log = LoggerFactory.getLogger(JsonUtil.class);

	public static <T> String toJson(T value) {
		try {
			return getInstance().writeValueAsString(value);
		} catch (JsonProcessingException e) {
			log.error("toJson异常");
			return null;
		}
	}

	public static <T> T parse(String content, Class<T> valueType) {
		try {
			return getInstance().readValue(content, valueType);
		} catch (JsonProcessingException e) {
			log.error("parse异常");
			return null;
		}
	}

	public static <T> T parse(byte[] bytes, Class<T> valueType) {
		try {
			return getInstance().readValue(bytes, valueType);
		} catch (IOException e) {
			log.error("parse转换异常");
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T parse(byte[] bytes, TypeReference<?> typeReference) {
		try {
			return (T) getInstance().readValue(bytes, typeReference);
		} catch (IOException e) {
			log.error("parse读取转换异常");
			return null;
		}
	}

	public static MapType getMapType(Class<?> keyClass, Class<?> valueClass) {
		return getInstance().getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(String content) {
		try {
			return getInstance().readValue(content, Map.class);
		} catch (IOException e) {
			log.error("toMap读取异常");
			return null;
		}
	}

	private static ObjectMapper INSTANCE = new JacksonObjectMapper();
	
	private JsonUtil(){}
	
	public static ObjectMapper getInstance() {
		return INSTANCE;
	}

	private static class JacksonObjectMapper extends ObjectMapper {
		private static final long serialVersionUID = 4288193147502386170L;

		private static final Locale CHINA = Locale.CHINA;

		public JacksonObjectMapper() {
			super();
			//设置地点为中国
			super.setLocale(CHINA);
			//去掉默认的时间戳格式
			super.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			//设置为中国上海时区
			super.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
			//序列化时，日期的统一格式
			super.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA));
			// 单引号
			super.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
			super.findAndRegisterModules();
			//失败处理
			super.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			super.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			//单引号处理
			super.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
			//反序列化时，属性不存在的兼容处理s
			super.getDeserializationConfig().withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			
			super.findAndRegisterModules();
		}
	}

}
