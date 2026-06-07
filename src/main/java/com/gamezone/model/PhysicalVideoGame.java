package com.gamezone.model;

public class PhysicalVideoGame extends VideoGame implements Sellable, Displayable {

    private String condition;
    private String distributor;

    public PhysicalVideoGame(String title, double price, String platform, int stock, String genre,
                             String condition, String distributor) {
        super(title, price, platform, stock, genre);
        this.condition = condition;
        this.distributor = distributor;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    @Override
    public double calculateFinalPrice() {
        double finalPrice = price;
        if (condition != null && condition.equalsIgnoreCase("usado")) {
            finalPrice -= finalPrice * 0.25;
        }
        return finalPrice;
    }

    @Override
    public double sell(int qty) {
        double total = calculateFinalPrice() * qty;
        stock -= qty;
        return total;
    }

    @Override
    public String getDisplayInfo() {
        return title + " (Fisico) - " + platform + " - Precio final: $" + calculateFinalPrice() +
                " - Condicion: " + condition + " - Stock: " + stock;
    }

    @Override
    public Object[] toTableRow() {
        return new Object[] { title, "Fisico", platform, genre, calculateFinalPrice(), stock, condition };
    }

    @Override
    public String toString() {
        return super.toString() +
                ", Condicion: " + condition +
                ", Distribuidor: " + distributor +
                ", Precio final: " + calculateFinalPrice();
    }
}
