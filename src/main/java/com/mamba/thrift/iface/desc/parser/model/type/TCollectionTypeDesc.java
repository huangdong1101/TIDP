package com.mamba.thrift.iface.desc.parser.model.type;

import com.mamba.thrift.iface.desc.parser.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.ListMetaData;
import org.apache.thrift.meta_data.SetMetaData;

@Getter
@EqualsAndHashCode
public class TCollectionTypeDesc<T extends TDataTypeDesc> extends TDataTypeDesc {

    private final T element;

    public static TCollectionTypeDesc create(ListMetaData metaData, Function<FieldValueMetaData, TDataTypeDesc> function) throws Exception {
        return new TCollectionTypeDesc(metaData, function.apply(metaData.elemMetaData));
    }

    public static TCollectionTypeDesc create(SetMetaData metaData, Function<FieldValueMetaData, TDataTypeDesc> function) throws Exception {
        return new TCollectionTypeDesc(metaData, function.apply(metaData.elemMetaData));
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
