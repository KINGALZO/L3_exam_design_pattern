package com.kingalzo.l3exam.proxy;

import com.kingalzo.l3exam.dto.FactureResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;

@Service
public class FactureProxyService {

    private static final Logger logger = LoggerFactory.getLogger(FactureProxyService.class);
    private static final String EXTERNAL_API_BASE_URL = "http://localhost:8081/api/factures";

    private final WebClient webClient;

    public FactureProxyService(WebClient webClient) {
        this.webClient = webClient;
    }

    public FactureResponse getFacturesForCurrentMonth(String walletCode, String unite) {
        logger.info("Fetching current month factures for wallet: {} with unite: {}", walletCode, unite);

        try {
            var request = webClient.get()
                    .uri(uriBuilder -> {
                        var uri = uriBuilder.path(EXTERNAL_API_BASE_URL + "/{walletCode}/current");
                        if (unite != null && !unite.isEmpty()) {
                            uri.queryParam("unite", unite);
                        }
                        return uri.build(walletCode);
                    });

            FactureResponse response = request.retrieve()
                    .bodyToMono(FactureResponse.class)
                    .block();

            logger.info("Successfully retrieved factures for wallet: {}", walletCode);
            return response;

        } catch (WebClientResponseException.NotFound ex) {
            logger.warn("Wallet {} not found on external service", walletCode);
            return new FactureResponse(walletCode, java.util.List.of(), 0.0, 0, "NOT_FOUND");
        } catch (WebClientResponseException ex) {
            logger.error("Error calling external API for wallet {}: {}", walletCode, ex.getMessage());
            throw new RuntimeException("Failed to fetch factures from external service: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("Unexpected error while proxying factures request for wallet {}", walletCode, ex);
            throw new RuntimeException("Proxy error: " + ex.getMessage());
        }
    }

    public FactureResponse getFacturesForPeriod(String walletCode, LocalDate dateDebut, LocalDate dateFin) {
        logger.info("Fetching factures for wallet: {} from {} to {}", walletCode, dateDebut, dateFin);

        try {
            FactureResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(EXTERNAL_API_BASE_URL + "/{walletCode}/periode")
                            .queryParam("debut", dateDebut)
                            .queryParam("fin", dateFin)
                            .build(walletCode))
                    .retrieve()
                    .bodyToMono(FactureResponse.class)
                    .block();

            logger.info("Successfully retrieved factures for wallet: {} in period", walletCode);
            return response;

        } catch (WebClientResponseException.NotFound ex) {
            logger.warn("Wallet {} not found on external service", walletCode);
            return new FactureResponse(walletCode, java.util.List.of(), 0.0, 0, "NOT_FOUND");
        } catch (WebClientResponseException ex) {
            logger.error("Error calling external API for wallet {}: {}", walletCode, ex.getMessage());
            throw new RuntimeException("Failed to fetch factures from external service: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("Unexpected error while proxying factures request for wallet {}", walletCode, ex);
            throw new RuntimeException("Proxy error: " + ex.getMessage());
        }
    }
}
