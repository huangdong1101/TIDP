package com.mamba.thrift.iface.desc.parser.enums;

//org.apache.thrift.TFieldRequirementType
public enum TRequirement {

    REQUIRED(1),

    OPTIONAL(2),

    DEFAULT(3);

    private final int value;

    TRequirement(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TRequirement findByValue(int value) {
        for (TRequirement item : TRequirement.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        throw new IllegalArgumentException("Invalid type: " + value);
    }
}
