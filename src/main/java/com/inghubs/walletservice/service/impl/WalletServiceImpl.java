package com.inghubs.walletservice.service.impl;

import com.inghubs.walletservice.mapper.WalletMapper;
import com.inghubs.walletservice.model.dto.CreateWalletRequest;
import com.inghubs.walletservice.model.dto.CreateWalletResponse;
import com.inghubs.walletservice.model.entity.Customer;
import com.inghubs.walletservice.model.entity.Wallet;
import com.inghubs.walletservice.repository.CustomerRepository;
import com.inghubs.walletservice.repository.WalletRepository;
import com.inghubs.walletservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final CustomerRepository customerRepository;
    private final WalletMapper walletMapper;

    @Override
    public CreateWalletResponse createWallet(CreateWalletRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Wallet wallet = walletMapper.toEntity(request);
        wallet.setCustomer(customer);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUsableBalance(BigDecimal.ZERO);

        Wallet saved = walletRepository.save(wallet);
        return walletMapper.toResponse(saved);
    }
}