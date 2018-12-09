package com.bonc.common.utils;

import java.util.List;

public class ValueUtils {
	
	public static boolean isEmpty(String value) {
		return value == null || value.equals("");
	}
	
	public static <T> boolean isEmpty(T[] array) {
		return array == null || array.length == 0;
	}
	
	public static boolean isEmpty(List<?> list) {
		return list == null || list.size() == 0;
	}
	
	public static boolean isNotEmpty(String value) {
		return value != null && !value.equals("");
	}
	
	public static <T> boolean isNotEmpty(T[] array) {
		return array != null && array.length != 0;
	}
	
	public static boolean isNotEmpty(List<?> list) {
		return list != null && list.size() > 0;
	}
}
