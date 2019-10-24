package com.mamba.thrift.iface.desc.parser.model;

import com.mamba.thrift.iface.desc.parser.model.type.TDataTypeDesc;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@EqualsAndHashCode
public class TMethodDesc {

    private final String method;

    private final List<TFieldDesc> params;

    private final TDataTypeDesc result;

    private final TDataTypeDesc exception;

    public TMethodDesc(String method, List<TFieldDesc> params, List<TFieldDesc> results) {
        this.method = method;
        this.params = (params == null || params.isEmpty()) ? Collections.emptyList() : Collections.unmodifiableList(params);
        TDataTypeDesc success = null;
        TDataTypeDesc ex = null;
        if (results != null && !results.isEmpty()) {
            for (TFieldDesc desc : results) {
                switch (desc.getName()) {
                    case "success":
                        success = desc.getDataType();
                        break;
                    case "ex":
                        ex = desc.getDataType();
                        break;
                    default:
                        break;
                }
            }
        }
        this.result = success;
        this.exception = ex;
    }

    @Override
    public String toString() {
        if (this.getResult() == null) {
            if (this.getException() == null) {
                return String.format("{\"method\":\"%s\",\"params\":%s}", this.getMethod(), this.getParams());
            } else {
                return String.format("{\"method\":\"%s\",\"params\":%s,\"exception\":%s}", this.getMethod(), this.getParams(), this.getException());
            }
        } else {
            if (this.getException() == null) {
                return String.format("{\"method\":\"%s\",\"params\":%s,\"result\":%s}", this.getMethod(), this.getParams(), this.getResult());
            } else {
                return String.format("{\"method\":\"%s\",\"params\":%s,\"result\":%s,\"exception\":%s}", this.getMethod(), this.getParams(), this.getResult(), this.getException());
            }
        }
    }
}
