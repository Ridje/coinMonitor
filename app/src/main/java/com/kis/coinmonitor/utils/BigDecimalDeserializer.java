package com.kis.coinmonitor.utils;


import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;


import java.math.BigDecimal;

public class BigDecimalDeserializer extends NumberDeserializers.BigDecimalDeserializer {

    @Override
    public BigDecimal getNullValue(DeserializationContext ctxt) {
        return new BigDecimal(0);
    }
}
