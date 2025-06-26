package com.inghubs.walletservice.mapper;

import com.inghubs.walletservice.model.dto.CreateWalletRequest;
import com.inghubs.walletservice.model.dto.CreateWalletResponse;
import com.inghubs.walletservice.model.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balance", constant = "0.0")
    @Mapping(target = "usableBalance", constant = "0.0")
    Wallet toEntity(CreateWalletRequest request);

    @Mapping(target = "walletId", source = "id")
    @Mapping(target = "customerId", source = "customer.id")
    CreateWalletResponse toResponse(Wallet wallet);
}
