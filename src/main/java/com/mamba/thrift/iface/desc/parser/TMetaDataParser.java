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
import org.apache.thrift.TServiceClient;
import org.apache.thrift.meta_data.EnumMetaData;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.MapMetaData;
import org.apache.thrift.meta_data.SetMetaData;
import org.apache.thrift.meta_data.StructMetaData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class TMetaDataParser {

    private final ClassLoader classLoader;

    private final Map<Class<? extends TBase>, TStructDesc> structs = new HashMap<>();

    private final Map<Class<? extends TEnum>, TEnumDesc> enums = new HashMap<>();

    public TMetaDataParser(ClassLoader classLoader) {
        this.classLoader = Objects.requireNonNull(classLoader);
    }

    public TServiceDesc parseServiceMetaData(String className) throws Exception {
        Class<?> clazz = this.classLoader.loadClass(className);
        if (!TServiceClient.class.isAssignableFrom(clazz)) {
            throw new RuntimeException("'" + className + "' is not assignable from '" + TServiceClient.class.getName() + "'.");
        }
        return this.parseServiceMetaData((Class<? extends TServiceClient>) clazz);
    }

    public TServiceDesc parseServiceMetaData(Class<? extends TServiceClient> clazz) throws Exception {
        String className = clazz.getName();
        String serviceName = className.substring(className.lastIndexOf('.') + 1, className.length() - 7);
        Method[] methods = clazz.getInterfaces()[0].getMethods();
        if (methods == null || methods.length == 0) {
            return new TServiceDesc(clazz.getPackage().getName(), serviceName);
        }
        List<TMethodDesc> methodDescs = new ArrayList<>(methods.length);
        for (Method method : methods) {
            methodDescs.add(this.parseMethodMetaData(method));
        }
        return new TServiceDesc(clazz.getPackage().getName(), serviceName, methodDescs);
    }

    private TMethodDesc parseMethodMetaData(Method method) throws Exception {
        String ifaceClassName = method.getDeclaringClass().getName();
        String methodName = method.getName();
        String methodClassNamePrefix = ifaceClassName.substring(0, ifaceClassName.length() - 5).concat(methodName);
        List<TFieldDesc> paramsDescs;
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes == null || parameterTypes.length == 0) {
            paramsDescs = Collections.emptyList();
        } else {
            paramsDescs = this.parseFieldMetaData(methodClassNamePrefix.concat("_args"));
        }
        Class<?> returnType = method.getReturnType();
        if (returnType == null || returnType == void.class) {
            return new TMethodDesc(methodName, paramsDescs, null);
        }
        List<TFieldDesc> resultDescs = this.parseFieldMetaData(methodClassNamePrefix.concat("_result"));
        if (resultDescs == null || resultDescs.isEmpty()) {
            throw new RuntimeException("Result field is nil, classNamePrefix: " + methodClassNamePrefix);
        } else if (resultDescs.size() > 1) {
            throw new RuntimeException("Result field is gt 1, classNamePrefix: " + methodClassNamePrefix);
        } else {
            return new TMethodDesc(methodName, paramsDescs, resultDescs.get(0).getDataType());
        }
    }

    private List<TFieldDesc> parseFieldMetaData(String className) throws Exception {
        Class<?> clazz = this.classLoader.loadClass(className);
        if (!TBase.class.isAssignableFrom(clazz)) {
            throw new RuntimeException("'" + className + "' is not assignable from '" + TBase.class.getName() + "'.");
        } else {
            return this.parseFieldMetaData((Class<? extends TBase>) clazz);
        }
    }

    private List<TFieldDesc> parseFieldMetaData(Class<? extends TBase> clazz) throws Exception {
        Field field = clazz.getDeclaredField("metaDataMap");
        Map<?, FieldMetaData> metaDataMap = (Map<?, FieldMetaData>) field.get(clazz);
        if (metaDataMap.isEmpty()) {
            return Collections.emptyList();
        }
        List<TFieldDesc> fields = new ArrayList<>(metaDataMap.size());
        for (FieldMetaData metaData : metaDataMap.values()) {
            fields.add(this.parseFieldMetaData(metaData));
        }
        return Collections.unmodifiableList(fields);
    }

    /**
     * 解析Field元数据
     *
     * @param metaData
     * @return
     * @throws Exception
     */
    private TFieldDesc parseFieldMetaData(FieldMetaData metaData) throws Exception {
        return new TFieldDesc(metaData.fieldName, metaData.requirementType, this.parseFieldValueMetaData(metaData.valueMetaData));
    }

    /**
     * 解析Field数据类型元数据
     *
     * @param metaData
     * @return
     * @throws Exception
     */
    private TDataTypeDesc parseFieldValueMetaData(FieldValueMetaData metaData) throws Exception {
        if (metaData instanceof ListMetaData) {
            return this.parseFieldValueMetaData((ListMetaData) metaData);
        } else if (metaData instanceof SetMetaData) {
            return this.parseFieldValueMetaData((SetMetaData) metaData);
        } else if (metaData instanceof MapMetaData) {
            return this.parseFieldValueMetaData((MapMetaData) metaData);
        } else if (metaData instanceof EnumMetaData) {
            return this.parseFieldValueMetaData((EnumMetaData) metaData);
        } else if (metaData instanceof StructMetaData) {
            return this.parseFieldValueMetaData((StructMetaData) metaData);
        } else {
            return new TDataTypeDesc(metaData);
        }
    }

    /**
     * 解析List元数据
     *
     * @param metaData
     * @return
     * @throws Exception
     */
    private TCollectionTypeDesc parseFieldValueMetaData(ListMetaData metaData) throws Exception {
        return TCollectionTypeDesc.create(metaData, this::parseFieldValueMetaData);
    }

    /**
     * 解析Set元数据
     *
     * @param metaData
     * @return
     * @throws Exception
     */
    private TCollectionTypeDesc parseFieldValueMetaData(SetMetaData metaData) throws Exception {
        return TCollectionTypeDesc.create(metaData, this::parseFieldValueMetaData);
    }

    /**
     * 解析Map元数据
     *
     * @param metaData
     * @return
     * @throws Exception
     */
    private TMapTypeDesc parseFieldValueMetaData(MapMetaData metaData) throws Exception {
        return TMapTypeDesc.create(metaData, this::parseFieldValueMetaData);
    }

    /**
     * 解析Enum元数据
     *
     * @param metaData
     * @return
     * @throws Exception
     */
    private TEnumTypeDesc parseFieldValueMetaData(EnumMetaData metaData) throws Exception {
        this.parseEnumClass(metaData.enumClass);
        return new TEnumTypeDesc(metaData);
    }

    /**
     * 解析Struct元数据
     *
     * @param metaData
     * @return
     * @throws Exception
     */
    private TStructTypeDesc parseFieldValueMetaData(StructMetaData metaData) throws Exception {
        this.parseStructClass(metaData.structClass);
        return new TStructTypeDesc(metaData);
    }

    /**
     * 解析Enum
     *
     * @param enumClass
     * @throws Exception
     */
    public void parseEnumClass(Class<? extends TEnum> enumClass) throws Exception {
        if (this.enums.containsKey(enumClass)) {
            return;
        }
        this.enums.put(enumClass, new TEnumDesc(enumClass));
    }

    /**
     * 解析Struct
     *
     * @param structClass
     * @throws Exception
     */
    public void parseStructClass(Class<? extends TBase> structClass) throws Exception {
        if (this.structs.containsKey(structClass)) {
            return;
        }
        List<TFieldDesc> fields = this.parseFieldMetaData(structClass);
        this.structs.put(structClass, new TStructDesc<>(structClass, fields));
    }

    public Collection<TStructDesc> getStructs() {
        return Collections.unmodifiableCollection(this.structs.values());
    }

    public Collection<TEnumDesc> getEnums() {
        return Collections.unmodifiableCollection(this.enums.values());
    }
}
