package org.test.test_druid.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * json工具类
 * @use fast-json
 * @author Jake
 */
public abstract class JsonUtils {
	
	/**
	 * 对象转换成json字符串
	 * @param obj Object
	 * @return String
	 */
	public static String object2JsonString(Object obj) {
		return JSON.toJSONString(obj);
	}

	/**
	 * 对象转换成格式化显示的json字符串
	 * @param obj Object
	 * @return String
	 */
	public static String object2PrettyJsonString(Object obj) {
		return JSON.toJSONString(obj, SerializerFeature.PrettyFormat);
	}
	
	/**
	 * json字符串转换成对象
	 * @param jsonString String
	 * @param valueType 对象类型
	 * @return T
	 */
	public static <T> T jsonString2Object(String jsonString, Class<T> valueType) {
		return JSON.parseObject(jsonString, valueType);
	}
	
	/**
	 * json字符串转换成对象
	 * @param jsonString String
	 * @param valueTypeRef 对象类型
	 * @return T
	 */
	public static <T> T jsonString2Object(String jsonString, TypeReference<T> valueTypeRef) {
		return JSON.parseObject(jsonString, valueTypeRef);
	}
	
	/**
	 * 对象转换成字节数组
	 * @param obj Object
	 * @return byte[]
	 */
	public static byte[] object2Bytes(Object obj) {
		return JSON.toJSONBytes(obj);
	}
	
	/**
	 * 字节数组转换成对象
	 * @param data  字节数组
	 * @return Object
	 */
	public static Object bytes2Object(byte[] data) {
		if (data == null) {
			return null;
		}
		
		return JSON.parse(data);
	}
	

}