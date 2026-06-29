package com.kingalzo.l3exam.controller;

import com.kingalzo.l3exam.dto.FactureResponse;
import com.kingalzo.l3exam.proxy.FactureProxyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/external/factures")
public class ExternalFactureProxyController {

    private static final Logger logger = LoggerFactory.getLogger(ExternalFactureProxyController.class);
    private final FactureProxyService factureProxyService;

    public ExternalFactureProxyController(FactureProxyService factureProxyService) {
        this.factureProxyService = factureProxyService;
    }

    /**
     * GET /api/external/factures/{walletCode}/current
     * Consulter les factures impayées du mois en cours
     * Optionnel : ?unite=WOYAFAL pour filtrer par unité de paiement
     */
    @GetMapping("/{walletCode}/current")
    public ResponseEntity<FactureResponse> getFacturesForCurrentMonth(
            @PathVariable String walletCode,
            @RequestParam(required = false) String unite) {

        logger.info("GET /api/external/factures/{}/current requested with unite={}", walletCode, unite);

        try {
            FactureResponse response = factureProxyService.getFacturesForCurrentMonth(walletCode, unite);

            if ("NOT_FOUND".equals(response.getStatut())) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            logger.error("Error in getFacturesForCurrentMonth for wallet {}: {}", walletCode, ex.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/external/factures/{walletCode}/periode?debut=YYYY-MM-DD&fin=YYYY-MM-DD
     * Consulter les factures sur une période donnée
     */
    @GetMapping("/{walletCode}/periode")
    public ResponseEntity<FactureResponse> getFacturesForPeriod(
            @PathVariable String walletCode,
            @RequestParam String debut,
            @RequestParam String fin) {

        logger.info("GET /api/external/factures/{}/periode requested with debut={}, fin={}", walletCode, debut, fin);

        try {
            LocalDate dateDebut = LocalDate.parse(debut);
            LocalDate dateFin = LocalDate.parse(fin);

            FactureResponse response = factureProxyService.getFacturesForPeriod(walletCode, dateDebut, dateFin);

            if ("NOT_FOUND".equals(response.getStatut())) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(response);

        } catch (DateTimeParseException ex) {
            logger.error("Invalid date format. Expected YYYY-MM-DD. Got debut={}, fin={}", debut, fin);
            return ResponseEntity.badRequest().body(
                    new FactureResponse(walletCode, java.util.List.of(), 0.0, 0, 
                            "Invalid date format. Expected YYYY-MM-DD")
            );
        } catch (Exception ex) {
            logger.error("Error in getFacturesForPeriod for wallet {}: {}", walletCode, ex.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
