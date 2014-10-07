package talend.ext.images.server.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ReflectionUtil {

	public static Class getClass(String className) throws Exception {

		Class newoneClass = Class.forName(className);

		return newoneClass;

	}

	public static Object getProperty(Object owner, String fieldName)
			throws Exception {
		Class ownerClass = owner.getClass();

		Field field = ownerClass.getField(fieldName);

		Object property = field.get(owner);

		return property;
	}

	public static Object getStaticProperty(String className, String fieldName)
			throws Exception {
		Class ownerClass = Class.forName(className);

		Field field = ownerClass.getField(fieldName);

		Object property = field.get(ownerClass);

		return property;
	}

	public static Object invokeMethod(Object owner, String methodName,
			Object[] args) throws Exception {

		Class ownerClass = owner.getClass();

		Class[] argsClass = new Class[args.length];

		for (int i = 0, j = args.length; i < j; i++) {
			argsClass[i] = args[i].getClass();
		}

		Method method = ownerClass.getMethod(methodName, argsClass);

		return method.invoke(owner, args);
	}

	public static Object invokeStaticMethod(String className,
			String methodName, Object[] args) throws Exception {
		Class ownerClass = Class.forName(className);

		Class[] argsClass = new Class[args.length];

		for (int i = 0, j = args.length; i < j; i++) {
			argsClass[i] = args[i].getClass();
		}

		Method method = ownerClass.getMethod(methodName, argsClass);

		return method.invoke(null, args);
	}

	public static Object newInstance(String className, Object[] args)
			throws Exception {
		Class newoneClass = Class.forName(className);

		Class[] argsClass = new Class[args.length];

		for (int i = 0, j = args.length; i < j; i++) {
			argsClass[i] = args[i].getClass();
		}

		Constructor cons = newoneClass.getConstructor(argsClass);

		return cons.newInstance(args);

	}

	public static boolean isInstance(Object obj, Class cls) {
		return cls.isInstance(obj);
	}

	public static Object getByArray(Object array, int index) {
		return Array.get(array, index);
	}
}