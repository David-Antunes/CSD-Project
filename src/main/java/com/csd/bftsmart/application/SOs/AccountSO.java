package com.csd.bftsmart.application.SOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountSO implements Serializable {
    private String id;
    private int balance;
    private String userId;
}
