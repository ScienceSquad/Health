package com.sciencesquad.health;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a generic type {@code T}.
 *
 * <p>Assumes {@code Type} implements {@code equals()} and {@code hashCode()}
 * as a value (as opposed to identity) comparison.
 *
 * Also implements {@link #isAssignableFrom(Type)} to check type-safe
 * assignability.
 *
 * <p>Clients create an empty anonymous subclass. Doing so embeds the type
 * parameter in the anonymous class's type hierarchy so we can reconstitute
 * it at runtime despite erasure.
 *
 * <p>For example:
 * <code>
 * {@literal TypeToken<List<String>> t = new TypeToken<List<String>>}(){}
 * </code>
 *
 * @author Bob Lee
 * @author Sven Mawson
 */
public abstract class TypeToken<T> {

	final Class<? super T> rawType;
	final Type type;

	/**
	 * Constructs a new type token. Derives represented class from type
	 * parameter.
	 */
	@SuppressWarnings("unchecked")
	protected TypeToken() {
		this.type = getSuperclassTypeParameter(getClass());
		this.rawType = (Class<? super T>) getRawType(type);
	}

	/**
	 * Unsafe. Constructs a type token manually.
	 */
	@SuppressWarnings("unchecked")
	private TypeToken(Type type) {
		this.rawType = (Class<? super T>)getRawType(Objects.requireNonNull(type, "type"));
		this.type = type;
	}

	/**
	 * Gets type from super class's type parameter.
	 */
	static Type getSuperclassTypeParameter(Class<?> subclass) {
		Type superclass = subclass.getGenericSuperclass();
		if (superclass instanceof Class) {
			throw new RuntimeException("Missing type parameter.");
		}
		return ((ParameterizedType) superclass).getActualTypeArguments()[0];
	}

	/**
	 * Gets type token from super class's type parameter.
	 */
	static TypeToken<?> fromSuperclassTypeParameter(Class<?> subclass) {
		return new SimpleTypeToken<>(subclass);
	}

	@SuppressWarnings("unchecked")
	private static Class<?> getRawType(Type type) {
		if (type instanceof Class<?>) {
			// type is a normal class.
			return (Class<?>) type;
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;

			// I'm not exactly sure why getRawType() returns Type instead of Class.
			// Neal isn't either but suspects some pathological case related
			// to nested classes exists.
			Type rawType = parameterizedType.getRawType();
			if (rawType instanceof Class<?>) {
				return (Class<?>) rawType;
			} else {
				throw buildUnexpectedTypeError(rawType, Class.class);
			}
		} else if (type instanceof GenericArrayType) {
			return Array.newInstance(getRawType(((GenericArrayType)type).getGenericComponentType()), 0).getClass();
		} else {
			throw buildUnexpectedTypeError(
					type, ParameterizedType.class, GenericArrayType.class);
		}
	}

	/**
	 * Gets the raw type.
	 */
	public Class<? super T> getRawType() {
		return rawType;
	}

	/**
	 * Gets underlying {@code Type} instance.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Check if this type is assignable from the given class object.
	 */
	public boolean isAssignableFrom(Class<?> cls) {
		return isAssignableFrom((Type) cls);
	}

	/**
	 * Check if this type is assignable from the given Type.
	 */
	public boolean isAssignableFrom(Type from) {
		if (from == null) {
			return false;
		}

		if (type.equals(from)) {
			return true;
		}

		if (type instanceof Class) {
			return rawType.isAssignableFrom(getRawType(from));
		} else if (type instanceof ParameterizedType) {
			return isAssignableFrom(from, (ParameterizedType) type,
					new HashMap<String, Type>());
		} else if (type instanceof GenericArrayType) {
			return rawType.isAssignableFrom(getRawType(from))
					&& isAssignableFrom(from, (GenericArrayType) type);
		} else {
			throw buildUnexpectedTypeError(
					type, Class.class, ParameterizedType.class, GenericArrayType.class);
		}
	}

	/**
	 * Check if this type is assignable from the given type token.
	 */
	public boolean isAssignableFrom(TypeToken<?> token) {
		return isAssignableFrom(token.getType());
	}

	/**
	 * Private helper function that performs some assignability checks for
	 * the provided GenericArrayType.
	 */
	private static boolean isAssignableFrom(Type from, GenericArrayType to) {
		Type toGenericComponentType = to.getGenericComponentType();
		if (toGenericComponentType instanceof ParameterizedType) {
			Type t = from;
			if (from instanceof GenericArrayType) {
				t = ((GenericArrayType) from).getGenericComponentType();
			} else if (from instanceof Class) {
				Class<?> classType = (Class<?>) from;
				while (classType.isArray()) {
					classType = classType.getComponentType();
				}
				t = classType;
			}
			return isAssignableFrom(t, (ParameterizedType) toGenericComponentType,
					new HashMap<String, Type>());
		} else {
			// No generic defined on "to"; therefore, return true and let other
			// checks determine assignability
			return true;
		}
	}

	/**
	 * Private recursive helper function to actually do the type-safe checking
	 * of assignability.
	 */
	private static boolean isAssignableFrom(Type from, ParameterizedType to,
											Map<String, Type> typeVarMap) {

		if (from == null) {
			return false;
		}

		if (to.equals(from)) {
			return true;
		}

		// First figure out the class and any type information.
		Class<?> clazz = getRawType(from);
		ParameterizedType ptype = null;
		if (from instanceof ParameterizedType) {
			ptype = (ParameterizedType) from;
		}

		// Load up parameterized variable info if it was parameterized.
		if (ptype != null) {
			Type[] tArgs = ptype.getActualTypeArguments();
			TypeVariable<?>[] tParams = clazz.getTypeParameters();
			for (int i = 0; i < tArgs.length; i++) {
				Type arg = tArgs[i];
				TypeVariable<?> var = tParams[i];
				while (arg instanceof TypeVariable) {
					TypeVariable<?> v = (TypeVariable<?>) arg;
					arg = typeVarMap.get(v.getName());
				}
				typeVarMap.put(var.getName(), arg);
			}

			// check if they are equivalent under our current mapping.
			if (typeEquals(ptype, to, typeVarMap)) {
				return true;
			}
		}

		for (Type itype : clazz.getGenericInterfaces()) {
			if (isAssignableFrom(itype, to, new HashMap<String, Type>(typeVarMap))) {
				return true;
			}
		}

		// Interfaces didn't work, try the superclass.
		Type sType = clazz.getGenericSuperclass();
		if (isAssignableFrom(sType, to, new HashMap<String, Type>(typeVarMap))) {
			return true;
		}

		return false;
	}

	/**
	 * Checks if two parameterized types are exactly equal, under the variable
	 * replacement described in the typeVarMap.
	 */
	private static boolean typeEquals(ParameterizedType from,
									  ParameterizedType to, Map<String, Type> typeVarMap) {
		if (from.getRawType().equals(to.getRawType())) {
			Type[] fromArgs = from.getActualTypeArguments();
			Type[] toArgs = to.getActualTypeArguments();
			for (int i = 0; i < fromArgs.length; i++) {
				if (!matches(fromArgs[i], toArgs[i], typeVarMap)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Checks if two types are the same or are equivalent under a variable mapping
	 * given in the type map that was provided.
	 */
	private static boolean matches(Type from, Type to,
								   Map<String, Type> typeMap) {
		if (to.equals(from)) return true;

		if (from instanceof TypeVariable) {
			return to.equals(typeMap.get(((TypeVariable<?>)from).getName()));
		}

		return false;
	}

	@Override public int hashCode() {
		return type.hashCode();
	}

	@Override public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof TypeToken<?>)) {
			return false;
		}
		TypeToken<?> t = (TypeToken<?>) o;
		return type.equals(t.type);
	}

	@Override public String toString() {
		return type instanceof Class<?>
				? ((Class<?>) type).getName()
				: type.toString();
	}

	private static AssertionError buildUnexpectedTypeError(
			Type token, Class<?>... expected) {

		// Build exception message
		StringBuilder exceptionMessage =
				new StringBuilder("Unexpected type. Expected one of: ");
		for (Class<?> clazz : expected) {
			exceptionMessage.append(clazz.getName()).append(", ");
		}
		exceptionMessage.append("but got: ").append(token.getClass().getName())
				.append(", for type token: ").append(token.toString()).append('.');

		return new AssertionError(exceptionMessage.toString());
	}

	/**
	 * Gets type token for the given {@code Type} instance.
	 */
	public static TypeToken<?> get(Type type) {
		return new SimpleTypeToken<Object>(type);
	}

	/**
	 * Gets type token for the given {@code Class} instance.
	 */
	public static <T> TypeToken<T> get(Class<T> type) {
		return new SimpleTypeToken<T>(type);
	}

	/**
	 * Private static class to not create more anonymous classes than
	 * necessary.
	 */
	private static class SimpleTypeToken<T> extends TypeToken<T> {
		public SimpleTypeToken(Type type) {
			super(type);
		}
	}
}