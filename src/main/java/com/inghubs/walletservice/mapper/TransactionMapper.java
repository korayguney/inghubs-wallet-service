package com.inghubs.walletservice.mapper;

import com.inghubs.walletservice.model.dto.TransactionResponse;
import com.inghubs.walletservice.model.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "wallet.id", target = "walletId")
    TransactionResponse toTransactionResponse(Transaction transaction);
}