/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.metadata;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.util.ArrayListHolder;
import com.amalto.xmlserver.interfaces.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.*;

import javax.xml.XMLConstants;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class ClassRepository extends MetadataRepository {

    public static final String LINK = "LINK"; //$NON-NLS-1$

    public static final String MAP_TYPE_NAME = "map"; //$NON-NLS-1$

    public static final SimpleTypeMetadata STRING = new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, "string"); //$NON-NLS-1$

    private static final Logger LOGGER = Logger.getLogger(ClassRepository.class);

    private static final String GETTER_PREFIX = "get"; //$NON-NLS-1$

    private static final String JAVA_LANG_PREFIX = "java.lang."; //$NON-NLS-1$

    private static final int MAJ_DIFF = 'A' - 'a';

    public static final String EMBEDDED_XML = "embeddedXml"; //$NON-NLS-1$

    private final ComplexTypeMetadata MAP_TYPE;

    private final Stack<ComplexTypeMetadata> typeStack = new Stack<ComplexTypeMetadata>();

    private final Map<String, Class> entityToJavaClass = new HashMap<String, Class>();

    private final Map<Class, Iterable<Class>> registeredSubClasses = new HashMap<Class, Iterable<Class>>();

    private int listCounter = 0;

    public ClassRepository() {
        // Create a type for MAPs
        ComplexTypeMetadata internalMapType = new ComplexTypeMetadataImpl(getUserNamespace(), MAP_TYPE_NAME, false);
        SimpleTypeMetadata embeddedXml = new SimpleTypeMetadata(StringUtils.EMPTY, EMBEDDED_XML);
        embeddedXml.addSuperType(STRING, this);
        embeddedXml.setData(MetadataRepository.DATA_MAX_LENGTH, String.valueOf(Integer.MAX_VALUE));
        internalMapType.addField(new SimpleTypeFieldMetadata(internalMapType, false, false, false, "key", STRING, Collections.<String>emptyList(), Collections.<String>emptyList()));
        internalMapType.addField(new SimpleTypeFieldMetadata(internalMapType, false, false, false, "value", embeddedXml, Collections.<String>emptyList(), Collections.<String>emptyList()));
        MAP_TYPE = (ComplexTypeMetadata) internalMapType.freeze(DefaultValidationHandler.INSTANCE);
        addTypeMetadata(MAP_TYPE);
        // Register known subclasses
        registeredSubClasses.put(IWhereItem.class, Arrays.<Class>asList(CustomWhereCondition.class,
                WhereAnd.class,
                WhereCondition.class,
                WhereLogicOperator.class,
                WhereOr.class));
    }

    public void load(Class... classes) {
        // Parse classes
        for (Class clazz : classes) {
            loadClass(clazz);
        }
        // Freeze (and validate) types
        for (TypeMetadata typeMetadata : getTypes()) {
            typeMetadata.freeze(DefaultValidationHandler.INSTANCE);
        }
    }

    public Class getJavaClass(String entityTypeName) {
        return entityToJavaClass.get(entityTypeName);
    }

    private TypeMetadata loadClass(Class clazz) {
        String typeName = getTypeName(clazz);
        if (getType(typeName) != null) { // If already defined return it.
            return getType(typeName);
        } else if (getNonInstantiableType(StringUtils.EMPTY, typeName) != null) {
            return getNonInstantiableType(StringUtils.EMPTY, typeName);
        }
        entityToJavaClass.put(typeName, clazz);
        if (Map.class.isAssignableFrom(clazz)) {
            return MAP_TYPE;
        }
        boolean isEntity = typeStack.isEmpty();
        if (ArrayListHolder.class.equals(clazz)) {
            typeName = typeName + listCounter++;
        }
        ComplexTypeMetadata classType = new ComplexTypeMetadataImpl(StringUtils.EMPTY, typeName, isEntity);
        addTypeMetadata(classType);
        typeStack.push(classType);
        String keyFieldName = "";
        if (isEntity && ObjectPOJO.class.isAssignableFrom(clazz)) {
            SimpleTypeFieldMetadata keyField = new SimpleTypeFieldMetadata(typeStack.peek(),
                    true,
                    false,
                    true,
                    "unique-id", //$NON-NLS-1$
                    STRING,
                    Collections.<String>emptyList(),
                    Collections.<String>emptyList());
            keyField.setData(LINK, "PK/unique-id"); //$NON-NLS-1$
            classType.addField(keyField);
        } else if (isEntity) {
            keyFieldName = "unique-id";
        }
        // Class is abstract / interface: load sub classes
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            Iterable<Class> subClasses = getSubclasses(clazz);
            for (Class subClass : subClasses) {
                TypeMetadata typeMetadata = loadClass(subClass);
                typeMetadata.addSuperType(typeStack.peek(), this);
            }
        }
        // Analyze methods
        Method[] declaredMethods = clazz.getMethods();
        for (Method declaredMethod : declaredMethods) {
            if (!Object.class.equals(declaredMethod.getDeclaringClass()) && !Modifier.isStatic(declaredMethod.getModifiers())) {
                if (declaredMethod.getName().startsWith(GETTER_PREFIX) && declaredMethod.getName().length() > GETTER_PREFIX.length()) {
                    String fieldName = getName(declaredMethod);
                    Class<?> returnType = declaredMethod.getReturnType();
                    FieldMetadata newField;
                    boolean isMany = false;
                    boolean isKey = keyFieldName.equals(fieldName);
                    if (Iterable.class.isAssignableFrom(returnType)) {
                        Type genericReturnType = declaredMethod.getGenericReturnType();
                        if (genericReturnType instanceof ParameterizedType) {
                            Type type = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
                            if (type instanceof Class<?>) {
                                returnType = ((Class) type);
                            } else {
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug("List returned by " + declaredMethod.getName() + " isn't using generic types.");
                                }
                                returnType = String.class;
                            }
                            isMany = true;
                        }
                    } else if (Map.class.isAssignableFrom(returnType)) {
                        isMany = true;
                    } else if (returnType.isArray()) {
                        isMany = true;
                        returnType = ((Class) returnType.getComponentType());
                    } else if (returnType.getName().startsWith("org.w3c.")) { //$NON-NLS-1$
                        // TODO Serialized XML to string column
                        continue;
                    } else if (Class.class.equals(returnType)) {
                        continue;
                    }
                    if (returnType.isPrimitive() || returnType.getName().startsWith(JAVA_LANG_PREFIX)) {
                        String fieldTypeName = returnType.getName().toLowerCase();
                        if (fieldTypeName.startsWith(JAVA_LANG_PREFIX)) {
                            fieldTypeName = StringUtils.substringAfter(fieldTypeName, JAVA_LANG_PREFIX);
                        }
                        TypeMetadata fieldType = new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, fieldTypeName);
                        newField = new SimpleTypeFieldMetadata(typeStack.peek(),
                                isKey,
                                isMany,
                                isKey,
                                fieldName,
                                fieldType,
                                Collections.<String>emptyList(),
                                Collections.<String>emptyList());
                        if ("string".equals(fieldTypeName)) {
                            fieldType.setData(MetadataRepository.DATA_MAX_LENGTH, String.valueOf(Integer.MAX_VALUE));
                        }
                    } else {
                        ComplexTypeMetadata fieldType;
                        if (Map.class.isAssignableFrom(returnType)) {
                            fieldType = MAP_TYPE;
                        } else {
                            fieldType = (ComplexTypeMetadata) loadClass(returnType);
                        }
                        if (!isEntity || !fieldType.isInstantiable()) {
                            newField = new ContainedTypeFieldMetadata(typeStack.peek(),
                                    isMany,
                                    false,
                                    fieldName,
                                    new ContainedComplexTypeRef(
                                            typeStack.peek(),
                                            StringUtils.EMPTY,
                                            fieldType.getName(),
                                            fieldType),
                                    Collections.<String>emptyList(),
                                    Collections.<String>emptyList());
                        } else {
                            newField = new ReferenceFieldMetadata(typeStack.peek(),
                                    false,
                                    isMany,
                                    false,
                                    fieldName,
                                    fieldType,
                                    fieldType.getField("unique-id"), //$NON-NLS-1$
                                    null,
                                    true,
                                    false,
                                    STRING,
                                    Collections.<String>emptyList(),
                                    Collections.<String>emptyList());
                        }
                    }
                    typeStack.peek().addField(newField);
                }
            }
        }
        return typeStack.pop();
    }

    private Iterable<Class> getSubclasses(Class clazz) {
        Iterable<Class> classes = registeredSubClasses.get(clazz);
        return classes != null ? classes : Collections.<Class>emptySet();
    }

    private String getTypeName(Class clazz) {
        return format(StringUtils.substringAfterLast(clazz.getName(), ".")); //$NON-NLS-1$
    }

    private static String getName(Method declaredMethod) {
        return format(declaredMethod.getName().substring(GETTER_PREFIX.length()));
    }

    // DataModelPOJO -> data-model-pOJO (serialization convention by castor xml).
    public static String format(String s) {
        if (s == null) {
            return null;
        }
        if (s.length() == 1) {
            return s.toLowerCase();
        }
        StringBuilder typeName = new StringBuilder();
        char[] chars = s.toCharArray();
        if (chars.length >= 2 && (isMaj(chars[0]) != isMaj(chars[1]))) {
            chars[0] = shift(chars[0]);
        }
        typeName.append(chars[0]);
        for (int i = 1; i < chars.length; i++) {
            char current = chars[i];
            if (isMaj(current) && !isMaj(chars[i - 1])) {
                typeName.append('-');
                typeName.append(shift(current));
            } else {
                typeName.append(current);
            }
        }
        return typeName.toString();
    }

    private static boolean isMaj(char c) {
        return c >= 'A' && c < 'a';
    }

    private static char shift(char c) {
        if (isMaj(c)) {
            return (char) (c - MAJ_DIFF);
        } else {
            return (char) (c + MAJ_DIFF);
        }
    }
}
