package com.example.demo.domain.store.exception;

import com.example.demo.infrastructure.exception.object.general.GeneralException;
import com.example.demo.infrastructure.exception.payload.code.BaseCode;

public class StoreHandler extends GeneralException {
    public StoreHandler(BaseCode code) {
        super(code);
    }
}
