package com.mamba.thrift.iface.desc.parser.util.function;

public interface Function<T, R> {

    R apply(T t) throws Exception;
}
