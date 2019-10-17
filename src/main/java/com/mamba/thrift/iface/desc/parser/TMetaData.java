package com.mamba.thrift.iface.desc.parser;

import com.mamba.thrift.iface.desc.parser.model.TFieldDesc;
import com.mamba.thrift.iface.desc.parser.model.TMethodDesc;
import com.mamba.thrift.iface.desc.parser.model.TServiceDesc;
import com.mamba.thrift.iface.desc.parser.model.base.TEnumDesc;
import com.mamba.thrift.iface.desc.parser.model.base.TStructDesc;
import com.mamba.thrift.iface.desc.parser.model.type.TCollectionTypeDesc;
import com.mamba.thrift.iface.desc.parser.model.type.TDataTypeDesc;
import com.mamba.thrift.iface.desc.parser.model.type.TEnumTypeDesc;
import com.mamba.thrift.iface.desc.parser.model.type.TMapTypeDesc;
import com.mamba.thrift.iface.desc.parser.model.type.TStructTypeDesc;
import org.apache.thrift.TBase;
import org.apache.thrift.TEnum;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.meta_data.EnumMetaData;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.MapMetaData;
import org.apache.thrift.meta_data.SetMetaData;
import org.apache.thrift.meta_data.StructMetaData;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class TMetaData {

    private final Map<Class<? extends TBase>, TStructDesc> structs = new HashMap<>();

    private final Map<Class<? extends TEnum>, TEnumDesc> enums = new HashMap<>();

    private final Map<Class<?>, TServiceDesc> services = new HashMap<>();

    public TServiceDesc loadService(ClassLoader classLoader, String clientClassName) {
        Class<?> clazz;
        try {
            clazz = classLoader.loadClass(clientClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Failed to load client class: " + clientClassName);
        }
        if (!TServiceClient.class.isAssignableFrom(clazz)) {
            throw new IllegalStateException("'" + clientClassName + "' is not assignable from '" + TServiceClient.class.getName() + "'.");
        }
        return loadService(clazz.getDeclaringClass());
    }

    public TServiceDesc loadService(Class<?> clazz) {
        TServiceDesc metaData = this.services.get(clazz);
        if (metaData != null) {
            return metaData;
        }
        Class<?>[] classes = clazz.getClasses();
        Map<String, Class<?>> classMap = Arrays.stream(classes).collect(Collectors.toMap(Class::getSimpleName, Function.identity()));
        Class<?> ifaceClass = classMap.get("Iface");
        if (ifaceClass == null) {
            throw new IllegalStateException("Invalid service: " + clazz.getName());
        }

        Method[] methods = ifaceClass.getMethods();
        List<TMethodDesc> methodDescs = new ArrayList<>(methods.length);
        for (Method method : methods) {
            methodDescs.add(new TMethodDesc(method.getName(), this.parseMethodParameterTypes(method, classMap), this.parseMethodReturnType(method, classMap)));
        }
        metaData = new TServiceDesc(clazz, methodDescs);
        this.services.putIfAbsent(clazz, metaData);
        return metaData;
    }

    /**
     * 解析方法参数类型
     *
     * @param method
     * @param classMap
     * @return
     */
    private List<TFieldDesc> parseMethodParameterTypes(Method method, Map<String, Class<?>> classMap) {
        if (method.getParameterCount() == 0) {
            return Collections.emptyList();
        }
        String argsClassName = method.getName().concat("_args");
        Class<?> argsClass = classMap.get(argsClassName);
        if (argsClass == null) {
            throw new IllegalStateException("Invalid args class: " + argsClassName);
        }
        if (!TBase.class.isAssignableFrom(argsClass)) {
            throw new IllegalStateException("'" + argsClass.getName() + "' is not assignable from '" + TBase.class.getName() + "'.");
        }
        return this.parseFieldMetaData((Class<? extends TBase>) argsClass);
    }

    /**
     * 解析方法返回类型
     *
     * @param method
     * @param classMap
     * @return
     */
    private TDataTypeDesc parseMethodReturnType(Method method, Map<String, Class<?>> classMap) {
        Class<?> returnType = method.getReturnType();
        if (returnType == null || returnType == void.class) {
            return null;
        }
        String resultClassName = method.getName().concat("_result");
        Class<?> resultClass = classMap.get(resultClassName);
        if (resultClass == null) {
            throw new IllegalStateException("Invalid result class: " + method.toString());
        }
        if (!TBase.class.isAssignableFrom(resultClass)) {
            throw new IllegalStateException("'" + resultClass.getName() + "' is not assignable from '" + TBase.class.getName() + "'.");
        }

        List<TFieldDesc> resultDescs = this.parseFieldMetaData((Class<? extends TBase>) resultClass);
        if (resultDescs == null || resultDescs.isEmpty()) {
            throw new IllegalStateException("Result field is nil, method: " + method);
        }
        if (resultDescs.size() > 1) {
            throw new IllegalStateException("Result field is gt 1, classNamePrefix: " + method);
        }
        return resultDescs.get(0).getDataType();
    }

    private List<TFieldDesc> parseFieldMetaData(Class<? extends TBase> clazz) {
        Map<?, FieldMetaData> metaDataMap = getMetaDataMap(clazz);
        if (metaDataMap.isEmpty()) {
            return Collections.emptyList();
        }
        List<TFieldDesc> fields = new ArrayList<>(metaDataMap.size());
        transferFieldMetaData(clazz, metaDataMap.values(), fields);
        return Collections.unmodifiableList(fields);
    }

    private void transferFieldMetaData(Class<? extends TBase> clazz, Collection<FieldMetaData> metaDatas, List<TFieldDesc> fields) {
        for (FieldMetaData metaData : metaDatas) {
            fields.add(this.parseFieldMetaData(clazz, metaData));
        }
    }

    /**
     * 解析Field元数据
     *
     * @param metaData
     * @return
     * @throws Exception
     */
    private TFieldDesc parseFieldMetaData(Class<? extends TBase> clazz, FieldMetaData metaData) {
        return new TFieldDesc(metaData.fieldName, metaData.requirementType, this.parseFieldValueMetaData(clazz, metaData.valueMetaData));
    }

    /**
     * 解析Field数据类型元数据
     *
     * @param structClass
     * @param metaData
     * @return
     */
    private TDataTypeDesc parseFieldValueMetaData(Class<? extends TBase> structClass, FieldValueMetaData metaData) {
        if (metaData instanceof ListMetaData) {
            return this.parseFieldValueMetaData(structClass, (ListMetaData) metaData);
        } else if (metaData instanceof SetMetaData) {
            return this.parseFieldValueMetaData(structClass, (SetMetaData) metaData);
        } else if (metaData instanceof MapMetaData) {
            return this.parseFieldValueMetaData(structClass, (MapMetaData) metaData);
        } else if (metaData instanceof EnumMetaData) {
            return this.parseFieldValueMetaData((EnumMetaData) metaData);
        } else if (metaData instanceof StructMetaData) {
            return this.parseFieldValueMetaData((StructMetaData) metaData);
        } else {
            if (metaData.type == org.apache.thrift.protocol.TType.STRUCT) {
                return this.parseStructMetaData(structClass, metaData.getTypedefName());
            } else if (metaData.type == org.apache.thrift.protocol.TType.ENUM) {
                System.out.println(structClass.getPackage());
                return this.parseEnumMetaData(structClass, metaData.getTypedefName());
            } else {
                return new TDataTypeDesc(metaData);
            }
        }
    }

    /**
     * 解析List元数据
     *
     * @param structClass
     * @param metaData
     * @return
     */
    private TCollectionTypeDesc parseFieldValueMetaData(Class<? extends TBase> structClass, ListMetaData metaData) {
        return TCollectionTypeDesc.create(structClass, metaData, this::parseFieldValueMetaData);
    }

    /**
     * 解析Set元数据
     *
     * @param structClass
     * @param metaData
     * @return
     */
    private TCollectionTypeDesc parseFieldValueMetaData(Class<? extends TBase> structClass, SetMetaData metaData) {
        return TCollectionTypeDesc.create(structClass, metaData, this::parseFieldValueMetaData);
    }

    /**
     * 解析Map元数据
     *
     * @param structClass
     * @param metaData
     * @return
     */
    private TMapTypeDesc parseFieldValueMetaData(Class<? extends TBase> structClass, MapMetaData metaData) {
        return TMapTypeDesc.create(structClass, metaData, this::parseFieldValueMetaData);
    }

    /**
     * 解析Enum元数据
     *
     * @param metaData
     * @return
     * @throws Exception
     */
    private TEnumTypeDesc parseFieldValueMetaData(EnumMetaData metaData) {
        this.parseEnumClass(metaData.enumClass);
        return new TEnumTypeDesc(metaData);
    }

    /**
     * 解析Enum元数据
     *
     * @param structClass
     * @param fieldTypedefName
     * @return
     */
    private TEnumTypeDesc parseEnumMetaData(Class<? extends TBase> structClass, String fieldTypedefName) {
        Class<? extends TEnum> fieldClass = (Class<? extends TEnum>) loadClass(structClass, fieldTypedefName);
        this.parseEnumClass(fieldClass);
        return new TEnumTypeDesc(fieldClass);
    }

    /**
     * 解析Struct元数据
     *
     * @param metaData
     * @return
     * @throws Exception
     */
    private TStructTypeDesc parseFieldValueMetaData(StructMetaData metaData) {
        this.parseStructClass(metaData.structClass);
        return new TStructTypeDesc(metaData);
    }

    /**
     * 解析Struct元数据
     *
     * @param structClass
     * @param fieldTypedefName
     * @return
     */
    private TStructTypeDesc parseStructMetaData(Class<? extends TBase> structClass, String fieldTypedefName) {
        Class<? extends TBase> fieldClass = (Class<? extends TBase>) loadClass(structClass, fieldTypedefName);
        this.parseStructClass(fieldClass);
        return new TStructTypeDesc(fieldClass);
    }

    /**
     * 解析Enum
     *
     * @param enumClass
     * @throws Exception
     */
    public void parseEnumClass(Class<? extends TEnum> enumClass) {
        if (this.enums.containsKey(enumClass)) {
            return;
        }
        this.enums.put(enumClass, new TEnumDesc(enumClass));
    }

    /**
     * 解析Struct
     *
     * @param structClass
     */
    public void parseStructClass(Class<? extends TBase> structClass) {
        if (this.structs.containsKey(structClass)) {
            return;
        }
        Map<?, FieldMetaData> metaDataMap = getMetaDataMap(structClass);
        if (metaDataMap.isEmpty()) {
            this.structs.put(structClass, new TStructDesc<>(structClass, Collections.emptyList()));
            return;
        }
        List<TFieldDesc> fields = new ArrayList<>(metaDataMap.size());
        this.structs.put(structClass, new TStructDesc<>(structClass, fields));
        this.transferFieldMetaData(structClass, metaDataMap.values(), fields);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append('{');
        builder.append("\"services\":").append(this.services.values());
        if (!this.services.isEmpty()) {
            builder.append(",\"structs\":").append(this.structs.values());
        }
        if (!this.enums.isEmpty()) {
            builder.append(",\"enums\":").append(this.enums.values());
        }
        return builder.append('}').toString();
    }

    private static Map<? extends TFieldIdEnum, FieldMetaData> getMetaDataMap(Class<? extends TBase> clazz) {
        try {
            return (Map<? extends TFieldIdEnum, FieldMetaData>) clazz.getDeclaredField("metaDataMap").get(clazz);
        } catch (Exception e) {
            throw new IllegalStateException("Failed get field 'metaDataMap' from '" + clazz.getName() + "': " + e.getMessage(), e);
        }
    }

    private static Class<?> loadClass(Class<? extends TBase> structClass, String fieldTypedefName) {
        Package pkg = structClass.getPackage();
        if (pkg == null) {
            return loadClass(structClass.getClassLoader(), fieldTypedefName);
        } else {
            return loadClass(structClass.getClassLoader(), pkg.getName() + "." + fieldTypedefName);
        }
    }

    private static Class<?> loadClass(ClassLoader classLoader, String className) {
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Invalid class: " + className);
        }
    }
}
