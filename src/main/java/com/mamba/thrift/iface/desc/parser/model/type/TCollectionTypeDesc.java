package com.mamba.thrift.iface.desc.parser.model.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.thrift.TBase;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.SetMetaData;

import java.util.function.BiFunction;

@Getter
@EqualsAndHashCode
public class TCollectionTypeDesc<T extends TDataTypeDesc> extends TDataTypeDesc {

    private final T element;

    public static TCollectionTypeDesc create(Class<? extends TBase> structClass, ListMetaData metaData, BiFunction<Class<? extends TBase>, FieldValueMetaData, TDataTypeDesc> function) {
        return new TCollectionTypeDesc(metaData, function.apply(structClass, metaData.elemMetaData));
    }

    public static TCollectionTypeDesc create(Class<? extends TBase> structClass, SetMetaData metaData, BiFunction<Class<? extends TBase>, FieldValueMetaData, TDataTypeDesc> function) {
        return new TCollectionTypeDesc(metaData, function.apply(structClass, metaData.elemMetaData));
    }

    private TCollectionTypeDesc(FieldValueMetaData metaData, T element) {
        super(metaData);
        this.element = element;
    }

    @Override
    public String toString() {
        return String.format("{\"type\":\"%s\",\"element\":%s}", this.getType(), this.getElement());
    }
}
