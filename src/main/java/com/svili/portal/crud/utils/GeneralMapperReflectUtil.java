package com.svili.portal.crud.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.ibatis.reflection.ReflectionException;
import org.springframework.util.ReflectionUtils;

import com.svili.portal.crud.common.StringUtil;

/**
 * 映射工具类
 * 
 * @author svili
 * @date 2016年11月8日
 *
 */
public class GeneralMapperReflectUtil {

	/**
	 * 获取pojo表名
	 * <p>
	 * 下划线风格
	 * </p>
	 * 
	 * @param clazz
	 *            pojo类class对象
	 * @return tableName
	 */
	public static String getTableName(Class<?> clazz) {
		// 驼峰转下划线
		String tableName = StringUtil.camelToUnderline(clazz.getName());
		// 判断是否有Table注解
		if (clazz.isAnnotationPresent(Table.class)) {
			// 获取注解对象
			Table table = clazz.getAnnotation(Table.class);
			// 设置了name属性
			if (!table.name().trim().equals("")) {
				return table.name();
			}
		}
		return tableName;
	}

	/**
	 * 获取pojo主键字段
	 * <p>
	 * 不存在返回null
	 * </p>
	 * 
	 * @param clazz
	 *            pojo类-class对象
	 * @return Field
	 */
	public static Field getPrimaryFieldNotCareNull(Class<?> clazz) {
		Field field = FieldReflectUtil.findField(clazz, Id.class);
		if (field != null) {
			return field;
		} else {
			return null;
		}
	}

	/**
	 * 获取pojo主键字段
	 * <p>
	 * 主键必须存在，不存在抛异常
	 * </p>
	 * 
	 * @param clazz
	 *            pojo类-class对象
	 * @return Field
	 * @throws ReflectionExceptio
	 * @see org.apache.ibatis.reflection.ReflectionException
	 */
	public static Field getPrimaryField(Class<?> clazz) {
		Field field = getPrimaryFieldNotCareNull(clazz);
		if (field != null) {
			return field;
		} else {
			throw new ReflectionException(
					"no search result for javax.persistence.Id annotation from " + clazz.getName());
		}
	}

	/**
	 * 获取pojo主键字段名
	 * <p>
	 * 下划线风格
	 * </p>
	 * 
	 * @param clazz
	 *            pojo类-class对象
	 * @return underline-columnName
	 * @throws ReflectionExceptio
	 * @see org.apache.ibatis.reflection.ReflectionException
	 */
	public static String getPrimaryKey(Class<?> clazz) {
		return StringUtil.camelToUnderline(getPrimaryField(clazz).getName());
	}

	/**
	 * 获取pojo所有字段名
	 * <p>
	 * 下划线风格
	 * </p>
	 * 
	 * @param clazz
	 *            pojo类-class对象
	 * @return columnNames
	 */
	public static List<String> getAllColumns(Class<?> clazz) {
		List<String> columns = new ArrayList<String>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			columns.add(StringUtil.camelToUnderline(field.getName()));
		}
		return columns;
	}

	/**
	 * 获取pojo除主键外的所有字段名
	 * <p>
	 * 列名为下划线风格
	 * </p>
	 * 
	 * @param clazz
	 *            pojo类-class对象
	 * @return columnNames
	 */
	public static List<String> getAllColumnsExceptPrimaryKey(Class<?> clazz) {
		List<String> columns = new ArrayList<String>();
		Field primaryField = getPrimaryFieldNotCareNull(clazz);
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (primaryField != null && !field.equals(primaryField)) {
				columns.add(StringUtil.camelToUnderline(field.getName()));
			}
		}
		return columns;
	}

	/**
	 * 获取pojo除空值外的所有字段名-字段值mapping
	 * <p>
	 * 字段名为下划线风格
	 * </p>
	 * 
	 * @param <T>
	 *            pojo类
	 * 
	 * @param t
	 *            pojo对象
	 * @return columnName-value
	 * @throws Exception
	 */
	public static <T> Map<String, Object> getFieldValueMappingExceptNull(T t) throws Exception {
		Map<String, Object> mapping = new LinkedHashMap<String, Object>();

		Field[] fields = t.getClass().getDeclaredFields();

		for (Field field : fields) {
			Object fieldValue = FieldReflectUtil.getFieldValue(t, field);
			if (fieldValue != null) {
				mapping.put(StringUtil.camelToUnderline(field.getName()), fieldValue);
			}
		}

		return mapping;
	}

	/**
	 * 获取pojo所有字段名-字段值mapping
	 * <p>
	 * 字段名为下划线风格
	 * </p>
	 * 
	 * @param <T>
	 *            pojo类
	 * 
	 * @param t
	 *            pojo对象
	 * @return columnName-value
	 * @throws Exception
	 * @see getFieldStringValue
	 */
	public static <T> Map<String, Object> getAllFieldValueMapping(T t) throws Exception {
		Map<String, Object> mapping = new LinkedHashMap<String, Object>();

		Field[] fields = t.getClass().getDeclaredFields();

		for (Field field : fields) {
			Object fieldValue = FieldReflectUtil.getFieldValue(t, field);
			mapping.put(StringUtil.camelToUnderline(field.getName()), fieldValue);
		}

		return mapping;
	}

	/**
	 * 获取pojo字段名-字段值mapping
	 * <p>
	 * 字段名为下划线风格
	 * </p>
	 * 
	 * @param <T>
	 *            pojo类
	 * 
	 * @param t
	 *            pojo对象
	 * @param isContainsPrimaryKey
	 *            是否包含主键
	 * @param isContainsNullValue
	 *            是否包含空值
	 * @return columnName-value
	 * @throws Exception
	 */
	public static <T> Map<String, Object> getFieldValueMapping(T t, boolean isContainsPrimaryKey,
			boolean isContainsNullValue) throws Exception {
		Map<String, Object> mapping = new LinkedHashMap<String, Object>();

		List<Field> fields = Arrays.asList(t.getClass().getDeclaredFields());

		// 主键字段
		Field primaryField = getPrimaryField(t.getClass());

		if (!isContainsPrimaryKey && primaryField != null) {
			// 不包含主键字段，移除
			fields.remove(primaryField);
		}

		for (Field field : fields) {
			// 字段值
			Object fieldValue = FieldReflectUtil.getFieldValue(t, field);

			if (fieldValue == null) {
				// 空值
				if (isContainsNullValue) {
					// 包含空值 添加至结果集
					mapping.put(field.getName(), fieldValue);
				}

			} else {
				// 非空值 直接添加至结果集
				mapping.put(field.getName(), fieldValue);
			}

		}

		return mapping;
	}

	/**
	 * 字段-值 键值对转为pojo
	 * 
	 * @param mapping
	 *            field-value mapping
	 * @param clazz
	 *            待转换的pojo类-class对象
	 * @throws Exception
	 *             InstantiationException, IllegalAccessException
	 */
	public static <T> T parseToBean(Map<String, Object> mapping, Class<T> clazz) throws Exception {
		T t = clazz.newInstance();
		if (mapping != null) {
			for (Entry<String, Object> entry : mapping.entrySet()) {

				// oracle返回的columnName为大写
				String columnName = entry.getKey().toLowerCase();
				// 字段名 下划线转为驼峰风格
				String fieldName = StringUtil.underlineToCamel(columnName);

				Field field = ReflectionUtils.findField(clazz, fieldName);

				if (field != null) {
					FieldReflectUtil.setFieldValue(t, field, entry.getValue());
				}
			}
		}
		return t;
	}

}
