package org.itstec.common.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SHA1Util {

	private static Logger logger = LoggerFactory.getLogger(SHA1Util.class);
	
    public static String shaEncode(String inStr) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (Exception e) {
        	logger.error("SHA1异常");
            return "";
        }

        byte[] byteArray;
        StringBuffer hexValue = new StringBuffer();
		try {
			byteArray = inStr.getBytes("UTF-8");
			byte[] shaBytes = md.digest(byteArray);
	        for (int i = 0; i < shaBytes.length; i++) {
	            int val = ((int) shaBytes[i]) & 0xff;
	            if (val < 16) {
	                hexValue.append("0");
	            }
	            hexValue.append(Integer.toHexString(val));
	        }
		} catch (UnsupportedEncodingException e) {
			logger.error("SHA1编码异常");
			return "";
		}
        return hexValue.toString();
    }
    
}
