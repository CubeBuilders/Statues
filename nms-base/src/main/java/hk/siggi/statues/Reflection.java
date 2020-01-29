package hk.siggi.statues;

import java.lang.reflect.Field;

public class Reflection {

	private Reflection() {
	}

	public static void setField(Object object, String field, Object value) throws NoSuchFieldException, IllegalAccessException {
		setField(object.getClass(), object, field, value);
	}

	public static void setField(Class clazz, Object object, String field, Object value) throws NoSuchFieldException, IllegalAccessException {
		Field f = clazz.getDeclaredField(field);
		f.setAccessible(true);
		f.set(object, value);
	}

	public static Object getField(Object object, String field) throws NoSuchFieldException, IllegalAccessException {
		return getField(object.getClass(), object, field);
	}

	public static Object getField(Class clazz, Object object, String field) throws NoSuchFieldException, IllegalAccessException {
		Field f = clazz.getDeclaredField(field);
		f.setAccessible(true);
		return f.get(object);
	}

	public static int getInt(Object object, String field) throws NoSuchFieldException, IllegalAccessException {
		return getInt(object.getClass(), object, field);
	}

	public static int getInt(Class clazz, Object object, String field) throws NoSuchFieldException, IllegalAccessException {
		Field f = clazz.getDeclaredField(field);
		f.setAccessible(true);
		return f.getInt(object);
	}
}
