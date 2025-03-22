package fr.rapizz.model;

public enum OrderStatus {
    PENDING("En cours de traitement"),
    IN_PROGRESS("En cours de livraison"),
    DELIVERED("Commande livrée"),
    CANCELD("Commande annulée");

    private final String text;

    OrderStatus(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
