package com.kingalzo.l3exam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;

public class FactureResponse {

    @JsonProperty("walletCode")
    private String walletCode;

    @JsonProperty("factures")
    private List<Facture> factures;

    @JsonProperty("totalMontant")
    private double totalMontant;

    @JsonProperty("nombreFactures")
    private int nombreFactures;

    @JsonProperty("statut")
    private String statut;

    public FactureResponse() {}

    public FactureResponse(String walletCode, List<Facture> factures, double totalMontant, int nombreFactures, String statut) {
        this.walletCode = walletCode;
        this.factures = factures;
        this.totalMontant = totalMontant;
        this.nombreFactures = nombreFactures;
        this.statut = statut;
    }

    public String getWalletCode() { return walletCode; }
    public void setWalletCode(String walletCode) { this.walletCode = walletCode; }

    public List<Facture> getFactures() { return factures; }
    public void setFactures(List<Facture> factures) { this.factures = factures; }

    public double getTotalMontant() { return totalMontant; }
    public void setTotalMontant(double totalMontant) { this.totalMontant = totalMontant; }

    public int getNombreFactures() { return nombreFactures; }
    public void setNombreFactures(int nombreFactures) { this.nombreFactures = nombreFactures; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public static class Facture {
        @JsonProperty("id")
        private String id;

        @JsonProperty("reference")
        private String reference;

        @JsonProperty("montant")
        private double montant;

        @JsonProperty("dateEcheance")
        private LocalDate dateEcheance;

        @JsonProperty("unite")
        private String unite;

        @JsonProperty("statut")
        private String statut;

        public Facture() {}

        public Facture(String id, String reference, double montant, LocalDate dateEcheance, String unite, String statut) {
            this.id = id;
            this.reference = reference;
            this.montant = montant;
            this.dateEcheance = dateEcheance;
            this.unite = unite;
            this.statut = statut;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getReference() { return reference; }
        public void setReference(String reference) { this.reference = reference; }

        public double getMontant() { return montant; }
        public void setMontant(double montant) { this.montant = montant; }

        public LocalDate getDateEcheance() { return dateEcheance; }
        public void setDateEcheance(LocalDate dateEcheance) { this.dateEcheance = dateEcheance; }

        public String getUnite() { return unite; }
        public void setUnite(String unite) { this.unite = unite; }

        public String getStatut() { return statut; }
        public void setStatut(String statut) { this.statut = statut; }
    }
}
