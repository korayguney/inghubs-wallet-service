package com.inghubs.walletservice.repository.criteria;

import com.inghubs.walletservice.model.entity.Wallet;
import com.inghubs.walletservice.model.dto.enums.Currency;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of the WalletRepositoryCriteria interface.
 * Provides methods for querying wallets based on various filters using JPA Criteria API.
 */
@Repository
public class WalletRepositoryCriteriaImpl implements WalletRepositoryCriteria {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Finds wallets based on the provided filters such as customer ID, currency, and balance range.
     *
     * @param customerId The ID of the customer whose wallets are to be retrieved (optional).
     * @param currency The currency type to filter wallets (optional).
     * @param minAmount The minimum balance to filter wallets (optional).
     * @param maxAmount The maximum balance to filter wallets (optional).
     * @return A list of Wallet entities matching the specified filters.
     */
    @Override
    public List<Wallet> findWalletsByFilters(Long customerId, Currency currency, BigDecimal minAmount, BigDecimal maxAmount) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Wallet> query = cb.createQuery(Wallet.class);
        Root<Wallet> wallet = query.from(Wallet.class);

        Predicate predicate = cb.conjunction();

        if (customerId != null) {
            predicate = cb.and(predicate, cb.equal(wallet.get("customer").get("id"), customerId));
        }
        if (currency != null) {
            predicate = cb.and(predicate, cb.equal(wallet.get("currency"), currency));
        }
        if (minAmount != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(wallet.get("balance"), minAmount));
        }
        if (maxAmount != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(wallet.get("balance"), maxAmount));
        }

        query.where(predicate);

        return entityManager.createQuery(query).getResultList();
    }
}