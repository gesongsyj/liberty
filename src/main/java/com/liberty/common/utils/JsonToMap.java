package com.liberty.common.utils;

import com.alibaba.druid.util.StringUtils;
import com.google.gson.*;

import java.util.*;
import java.util.Map.Entry;

/** 
 * 使用Gson把json字符串转成Map 
 * @author lianqiang 
 * @date 2014/06/12 
 */  
public class JsonToMap {  

	/** 
	 * 获取JsonObject 
	 * @param json 
	 * @return 
	 */
	public static JsonObject parseJson(String json) {
		JsonParser parser = new JsonParser();
		JsonObject jsonObj = parser.parse(json).getAsJsonObject();
		return jsonObj;
	}

	/**
	 * 判断 字符串是否是Json格式
	 * @param json
	 * @return
   */
	public static boolean isGoodJson(String json) {
		try {
			new JsonParser().parse(json);
			return true;
		} catch (JsonParseException e) {
//			System.out.println("bad json: " + json);
			return false;
		}
	}

	public static Map<String, String> toPara(String json) {
		Map<String, Object> map = toMap(json);
		Map<String, String> paras = new HashMap<>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			paras.put(entry.getKey(), entry.getValue().toString());
		}

		return paras;
	}

	/** 
	 * 根据json字符串返回Map对象 
	 * @param json 
	 * @return 
	 */  
	public static Map<String,Object> toMap(String json){  
		return JsonToMap.toMap(JsonToMap.parseJson(json));  
	}  

	/** 
	 * 将JSONObjec对象转换成Map-List集合 
	 * @param json 
	 * @return 
	 */  
	public static Map<String, Object> toMap(JsonObject json){  
		Map<String, Object> map = new HashMap<String, Object>();  
		Set<Entry<String, JsonElement>> entrySet = json.entrySet();
		for (Iterator<Entry<String, JsonElement>> iter = entrySet.iterator(); iter.hasNext(); ){  
			Entry<String, JsonElement> entry = iter.next();  
			String key = entry.getKey();  
			JsonElement value = entry.getValue();  
			if(value instanceof JsonArray)  
				map.put(key, toList((JsonArray) value));  
			else if(value instanceof JsonObject)  
				map.put(key, toMap((JsonObject) value));  
			else if (value instanceof JsonPrimitive) {
				String string=value.getAsString();
				if (string.equals("true"))
					map.put(key, true);  
				else if (string.equals("false")) 
					map.put(key, false);  
				else if (!string.equals("0") && !StringUtils.isEmpty(string)) {
					map.put(key, string);  
				}
			}
			else
				map.put(key, value);  
		}  
		return map;  
	}  

	/** 
	 * 将JSONArray对象转换成List集合 
	 * @param json 
	 * @return 
	 */  
	public static List<Object> toList(JsonArray json){  
		List<Object> list = new ArrayList<Object>();  
		for (int i=0; i<json.size(); i++){  
			Object value = json.get(i);  
			if(value instanceof JsonArray){  
				list.add(toList((JsonArray) value));  
			}  
			else if(value instanceof JsonObject){  
				list.add(toMap((JsonObject) value));  
			}  
			else{  
				list.add(value);  
			}  
		}  
		return list;  
	}  

}  