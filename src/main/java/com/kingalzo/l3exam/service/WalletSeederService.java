package com.kingalzo.l3exam.service;

import com.kingalzo.l3exam.domain.Transaction;
import com.kingalzo.l3exam.domain.Wallet;
import com.kingalzo.l3exam.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class WalletSeederService {

    private static final Logger logger = LoggerFactory.getLogger(WalletSeederService.class);

    private final WalletRepository walletRepository;

    public WalletSeederService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Async("taskExecutor")
    @Transactional
    public void seedAsync(int count, long intervalMillis) {
        logger.info("Starting async wallet seeding: count={}, interval={}ms", count, intervalMillis);
        for (int i = 0; i < count; i++) {
            try {
                Wallet wallet = createRandomWallet();
                walletRepository.save(wallet);
                logger.debug("Seeded wallet {} with phone={}", wallet.getId(), wallet.getPhone());
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Seeder interrupted", e);
                break;
            } catch (Exception ex) {
                logger.error("Failed to seed wallet", ex);
            }
        }
        logger.info("Wallet seeding completed");
    }

    private Wallet createRandomWallet() {
        String phone = randomPhone();
        BigDecimal balance = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(0, 1000)).setScale(2, BigDecimal.ROUND_HALF_UP);
        Wallet wallet = new Wallet(phone, balance);

        // add one initial transaction
        BigDecimal amt = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 200)).setScale(2, BigDecimal.ROUND_HALF_UP);
        Transaction tx = new Transaction(amt, Transaction.TransactionType.CREDIT);
        wallet.addTransaction(tx);
        return wallet;
    }

    private String randomPhone() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder();
        sb.append("07");
        for (int i = 0; i < 8; i++) {
            sb.append(rnd.nextInt(0,10));
        }
        return sb.toString();
    }
}
