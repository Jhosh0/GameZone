package com.gamezone.model;

public class DigitalVideoGame extends VideoGame implements Sellable, Displayable {

    private double sizeGB;
    private String downloadPlatform;

    public DigitalVideoGame(String title, double price, String platform, int stock, String genre, double sizeGB, String downloadPlatform) {
        super(title, price, platform, stock, genre);
        this.sizeGB = sizeGB;
        this.downloadPlatform = downloadPlatform;
    }

    public double getSizeGB() {
        return sizeGB;
    }

    public void setSizeGB(double sizeGB) {
        this.sizeGB = sizeGB;
    }

    public String getDownloadPlatform() {
        return downloadPlatform;
    }

    public void setDownloadPlatform(String downloadPlatform) {
        this.downloadPlatform = downloadPlatform;
    }

    @Override
    public double calculateFinalPrice() {
        double finalPrice = price;
        if (sizeGB > 50) {
            finalPrice += 5000;
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
        return title + " (Digital) - " + platform + " - Precio final: $" + calculateFinalPrice() +
                " - Tamaño: " + sizeGB + "GB - Stock: " + stock;
    }

    @Override
    public Object[] toTableRow() {
        return new Object[] { title, "Digital", platform, genre, calculateFinalPrice(), stock, downloadPlatform };
    }

    @Override
    public String toString() {
        return super.toString() +
                ", Tamaño: " + sizeGB + "GB" +
                ", Plataforma de descarga: " + downloadPlatform +
                ", Precio final: " + calculateFinalPrice();
    }
}
