package com.mamba.thrift.iface.desc.parser.enums;

//org.apache.thrift.protocol.TType
public enum TType {

    STOP(0),

    VOID(1),

    BOOL(2),

    BYTE(3),

    DOUBLE(4),

    I16(6),

    I32(8),

    I64(10),

    STRING(11),

    BINARY(11, true),

    STRUCT(12),

    MAP(13),

    SET(14),

    LIST(15),

    ENUM(16);

    private final int type;

    private final boolean binary;

    TType(int type) {
        this(type, false);
    }

    TType(int type, boolean binary) {
        this.type = type;
        this.binary = binary;
    }

    public int getValue() {
        return type;
    }

    public boolean isBinary() {
        return binary;
    }

    public static TType find(int type, boolean binary) {
        for (TType item : TType.values()) {
            if (item.getValue() == type && item.isBinary() == binary) {
                return item;
            }
        }
        throw new RuntimeException("Invalid type=" + type + ",binary=" + binary);
    }
}
