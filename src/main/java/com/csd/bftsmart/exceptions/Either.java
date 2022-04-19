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

    public static <T> Either<T> success(T t) {
        return new Either<>(ExceptionCode.SUCCESS, t);
    }
    public static <T> Either<T> failure(ExceptionCode t) {
        return new Either<>(t, null);
    }

}
