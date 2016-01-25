package com.horizon.flake.util;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;
/**
 * @author : David.Song/Java Engineer
 * @date : 2016/1/15 17:39
 * @see
 * @since : 1.0.0
 */
public class JsonUtil {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String ,Object> jsonToObject(String jsonStr){
		Map jsonObject = JSON.parseObject(jsonStr) ;
		Map<String, Object> outMap = new HashMap<String, Object>();
		for (Object o : jsonObject.entrySet()) { 
			Map.Entry<String,String> entry = (Map.Entry<String,String>)o; 
			outMap.put(entry.getKey(), entry.getValue());
		}
		return outMap;
	}	
	
	public static String ObjectToJson(Object object){
		return JSON.toJSONString(object) ;
	}
}
