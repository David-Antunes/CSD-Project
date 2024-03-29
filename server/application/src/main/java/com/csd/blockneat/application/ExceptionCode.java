package com.csd.blockneat.application;

import java.io.Serializable;

public enum ExceptionCode implements Serializable {
    SUCCESS,
    USER_EXISTS,
    USER_DOES_NOT_EXIST,
    ACCOUNT_EXISTS,
    INVALID_VALUE,
    NOT_ENOUGH_BALANCE,
    SAME_ACCOUNT,
    INVALID_USER,
    INVALID_SIGNATURE,
    ACCOUNT_DOES_NOT_EXIST,
    ACCOUNT_DOES_NOT_BELONG_TO_USER,
    NOT_ENOUGH_TRANSACTIONS,
    INVALID_BLOCK,
}
