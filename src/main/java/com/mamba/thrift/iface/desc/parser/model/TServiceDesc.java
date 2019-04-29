package com.mamba.thrift.iface.desc.parser.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@EqualsAndHashCode
public class TServiceDesc {

    private final String namespace;

    private final String service;

    private final List<TMethodDesc> methods;

    public TServiceDesc(String namespace, String service) {
        this(namespace, service, null);
    }

    public TServiceDesc(String namespace, String service, List<TMethodDesc> methods) {
        this.namespace = namespace;
        this.service = Objects.requireNonNull(service);
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
