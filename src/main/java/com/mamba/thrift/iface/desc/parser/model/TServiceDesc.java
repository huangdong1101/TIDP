package com.mamba.thrift.iface.desc.parser.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@EqualsAndHashCode
public class TServiceDesc {

    private final String namespace;

    private final String service;

    private final List<TMethodDesc> methods;

    public TServiceDesc(Class<?> structClass, List<TMethodDesc> methods) {
        Package pkg = structClass.getPackage();
        this.namespace = (pkg == null) ? null : pkg.getName();
        this.service = structClass.getSimpleName();
        this.methods = (methods == null || methods.isEmpty()) ? Collections.emptyList() : Collections.unmodifiableList(methods);
    }

    @Override
    public String toString() {
        if (this.getNamespace() == null || this.getNamespace().isEmpty()) {
            return String.format("{\"service\":\"%s\",\"methods\":%s}", this.getService(), this.getMethods());
        } else {
            return String.format("{\"namespace\":\"%s\",\"service\":\"%s\",\"methods\":%s}", this.getNamespace(), this.getService(), this.getMethods());
        }
    }
}
