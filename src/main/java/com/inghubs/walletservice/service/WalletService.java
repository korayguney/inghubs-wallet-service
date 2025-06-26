package com.inghubs.walletservice.service;

import com.inghubs.walletservice.model.dto.CreateWalletRequest;
import com.inghubs.walletservice.model.dto.CreateWalletResponse;

public interface WalletService {
    CreateWalletResponse createWallet(CreateWalletRequest request);
}
