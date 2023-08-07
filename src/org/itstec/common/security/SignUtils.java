package org.itstec.common.security;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @description: 签名工具
 */
public class SignUtils {

    private static Logger logger = LoggerFactory.getLogger(SignUtils.class);

    public static String signA(Map<String, String> data, String key) {

        String unsignString = "";
        List<String> nameList = new ArrayList<String>(data.keySet());
        Collections.sort(nameList);
        Iterator<String> var5 = nameList.iterator();

        while(var5.hasNext()) {
            String name = (String)var5.next();
            String value = String.valueOf(data.get(name));
            if (!StringUtils.isEmpty(value) && !"null".equals(value.trim()) && !"sign".equals(name)) {
                unsignString = unsignString + name + "=" + value + "&";
            }
        }
        String resp = md5(unsignString, key);
        if(StringUtils.isEmpty(resp)){
            return null;
        }
        return resp.toUpperCase();
    }

    //验签
    public static String signB(Map<String, String[]> data, String key) {

        String unsignString = "";
        List<String> nameList = new ArrayList<String>(data.keySet());
        Collections.sort(nameList);
        Iterator<String> var5 = nameList.iterator();

        while(var5.hasNext()) {
            String name = (String)var5.next();
            String value = data.get(name)[0];
            if (!StringUtils.isEmpty(value) && !"null".equals(value.trim().toLowerCase()) && !"sign".equals(name)) {
                unsignString = unsignString + name + "=" + value + "&";
            }
        }
        if(logger.isDebugEnabled()){
        	logger.debug("按照顺序计算签名前串:{}",unsignString);
        }
        String resp = md5(unsignString, key);
        if(StringUtils.isEmpty(resp)){
            return null;
        }
        if(logger.isDebugEnabled()){
        	logger.debug("平台计算签名 :[{}]",resp.toUpperCase());
        }
        return resp.toUpperCase();
    }

    public static String md5(String value, String salt){
        value = md5Hex(value + salt);
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = value.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = value.charAt(i / 3 * 2 + 1);
        }
        return new String(cs);
    }
    
    @SuppressWarnings("static-access")
	private static String md5Hex(String src) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(src.getBytes());
            return new String(new Hex().encode(bs));
        } catch (Exception e) {
        	logger.error("md5Hex异常");
            return "";
        }
    }
    
    public static String getSalt(){
    	Random r = new Random();
        StringBuilder sb = new StringBuilder(16);
        sb.append(r.nextInt(99999999)).append(r.nextInt(99999999));
        int len = sb.length();
        if (len < 16) {
            for (int i = 0; i < 16 - len; i++) {
                sb.append("0");
            }
        }
        return sb.toString();
    }
    
}
