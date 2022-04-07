package com.csd.bftsmart.infrastructure.mappers;

import com.csd.bftsmart.application.SOs.AccountSO;
import com.csd.bftsmart.infrastructure.entities.Account;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(source = "userId", target = "user.id")
    Account accountSOToAccount(AccountSO accountSO);

    @Mapping(source = "user.id", target = "userId")
    AccountSO accountToAccountSO(Account account);
}
