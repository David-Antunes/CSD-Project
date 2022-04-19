package com.csd.bftsmart.exceptions;

public class Either<T> {
    private final ExceptionCode code;
    private final T result;

    public Either(ExceptionCode code, T t) {
        this.code = code;
        this.result = t;
    }

    public ExceptionCode left() {
        return code;
    }

    public T right() {
        return result;
    }
}
