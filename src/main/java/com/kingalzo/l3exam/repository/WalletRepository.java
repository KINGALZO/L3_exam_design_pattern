package com.kingalzo.l3exam.repository;

import com.kingalzo.l3exam.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByPhone(String phone);
    Optional<Wallet> findByCode(String code);
    boolean existsByPhone(String phone);
    boolean existsByCode(String code);
}
