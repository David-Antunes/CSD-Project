package com.csd.blockneat.requests;

import java.io.Serializable;

public record Either<T>(ExceptionCode code, T result) implements Serializable {
    public ExceptionCode left() {
        return code;
    }

    public T right() {
        return result;
    }

    public static <T> Either<T> success(T t) {
        return new Either<>(ExceptionCode.SUCCESS, t);
    }

    public static <T> Either<T> success() {
        return new Either<>(ExceptionCode.SUCCESS, null);
    }

    public static <T> Either<T> failure(ExceptionCode t) {
        return new Either<>(t, null);
    }

}
