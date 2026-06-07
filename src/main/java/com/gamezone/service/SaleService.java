package com.gamezone.service;

import com.gamezone.exception.InsufficientStockException;
import com.gamezone.exception.VideoGameNotFoundException;
import com.gamezone.model.Sale;
import com.gamezone.model.Sellable;
import com.gamezone.model.VideoGame;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.persistence.VideoGameRepository;

import java.util.List;
import java.util.UUID;

public class SaleService {

    private final VideoGameRepository videoGameRepository;
    private final SaleRepository saleRepository;

    public SaleService(VideoGameRepository videoGameRepository, SaleRepository saleRepository) {
        this.videoGameRepository = videoGameRepository;
        this.saleRepository = saleRepository;
    }

    public Sale venderVideojuego(String titulo, int cantidad) {
        VideoGame game = videoGameRepository.findByTitle(titulo)
                .orElseThrow(() -> new VideoGameNotFoundException("El videojuego no existe en el catálogo"));

        if (game.getStock() < cantidad) {
            throw new InsufficientStockException("No hay stock suficiente para realizar la venta");
        }

        double unitPrice = game.calculateFinalPrice();
        if (game instanceof Sellable sellable) {
            sellable.sell(cantidad);
        } else {
            game.setStock(game.getStock() - cantidad);
        }

        videoGameRepository.update(titulo, game);

        Sale sale = new Sale(generarId(), game, cantidad, unitPrice);
        saleRepository.save(sale);
        return sale;
    }

    public List<Sale> listarVentas() {
        return saleRepository.findAll();
    }

    private String generarId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
