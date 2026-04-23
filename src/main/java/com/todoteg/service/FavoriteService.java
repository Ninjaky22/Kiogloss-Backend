package com.todoteg.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.todoteg.dto.AccountFavoritesDTO;
import com.todoteg.dto.AccountFavoritesResponse;
import com.todoteg.dto.FavoriteCreateRequest;
import com.todoteg.dto.FavoriteIdDTO;
import com.todoteg.dto.FavoriteProductItemDTO;
import com.todoteg.model.Account;
import com.todoteg.model.DetailFavoritos;
import com.todoteg.model.Images;
import com.todoteg.model.Product;
import com.todoteg.repository.AccountRepository;
import com.todoteg.repository.DetailFavoritosRepository;
import com.todoteg.repository.ImagesRepository;
import com.todoteg.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    
    private final DetailFavoritosRepository favoritosRepository;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final ImagesRepository imagesRepository;
    private final CloudinaryService cloudinaryService;
    
    @Transactional
    public Long addFavorite(FavoriteCreateRequest request) {
        Account account = accountRepository.findById(request.getAccount())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        Product product = productRepository.findById(request.getProduct())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        DetailFavoritos favorite = new DetailFavoritos();
        favorite.setAccount(account);
        favorite.setProduct(product);
        favorite.setDate(LocalDate.now());
        favorite.setStatus(true);
        
        favorite = favoritosRepository.save(favorite);
        return favorite.getId();
    }
    
    public void removeFavorite(Long favoriteId) {
        DetailFavoritos favorite = favoritosRepository.findById(favoriteId)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));
        
        favoritosRepository.delete(favorite);
    }
    
    public AccountFavoritesResponse getAccountFavorites(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        List<DetailFavoritos> favoritos = favoritosRepository.findByAccountId(accountId);
        
        List<FavoriteIdDTO> favoriteIds = favoritos.stream()
                .map(fav -> new FavoriteIdDTO(fav.getId(), fav.getProduct().getId()))
                .collect(Collectors.toList());
        
        Object favoriteData;
        if (favoritos.isEmpty()) {
            favoriteData = "No tienes favoritos";
        } else {
            favoriteData = favoritos.stream().map(fav -> {
                Product product = fav.getProduct();
                List<Images> images = imagesRepository.findByProductId(product.getId());
                String imageUrl = images.isEmpty() ? "" : 
                        cloudinaryService.getImageUrl(images.get(0).getImage());
                
                FavoriteProductItemDTO item = new FavoriteProductItemDTO();
                item.setId(product.getId());
                item.setName(product.getTitle());
                item.setPrice(product.getPrice().toString());
                item.setSlug(product.getSlug());
                item.setImage(imageUrl);
                return item;
            }).collect(Collectors.toList());
        }
        
        AccountFavoritesDTO accountDTO = new AccountFavoritesDTO();
        accountDTO.setId(account.getId());
        accountDTO.setFavoriteID(favoriteIds);
        accountDTO.setFavorite(favoriteData);
        accountDTO.setAddress(account.getAddress() != null ? account.getAddress().toString() : "");
        
        return new AccountFavoritesResponse(accountDTO);
    }
}