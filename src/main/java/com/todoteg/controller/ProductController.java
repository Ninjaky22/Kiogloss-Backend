package com.todoteg.controller;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todoteg.dto.ProductDetailDTO;
import com.todoteg.dto.ProductListDTO;
import com.todoteg.dto.TagDTO;
import com.todoteg.dto.VideoReelDTO;
import com.todoteg.service.ProductService;
import com.todoteg.service.VideoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    private final VideoService videoService;
    
    /**
     * GET /products/ - Listar productos publicados con paginación y búsqueda
     */
    @GetMapping("/products")
    public ResponseEntity<Page<ProductListDTO>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(name = "page_size", defaultValue = "8") int pageSize) {

        Sort sortOrder;
        if ("price-low".equals(sort)) {
            sortOrder = Sort.by("price").ascending();
        } else if ("price-high".equals(sort)) {
            sortOrder = Sort.by("price").descending();
        } else {
            // "latest" o por defecto
            sortOrder = Sort.by("published").descending();
        }

        Pageable pageable = PageRequest.of(page, pageSize, sortOrder);

        List<String> tagList = null;
        if (tags != null && !tags.isBlank()) {
            tagList = List.of(tags.split(","));
        }

        Page<ProductListDTO> products = productService.getPublishedProducts(search, tagList, pageable);
        return ResponseEntity.ok(products);
    }
    
    /**
     * GET /article/{slug}/ - Obtener detalle de producto por slug
     */
    @GetMapping("/article/{slug}")
    public ResponseEntity<ProductDetailDTO> getProductDetail(@PathVariable String slug) {
        ProductDetailDTO product = productService.getProductBySlug(slug);
        return ResponseEntity.ok(product);
    }
    
    /**
     * GET /tags/ - Listar todas las categorías/tags
     */
    @GetMapping("/tags")
    public ResponseEntity<List<TagDTO>> getTags() {
        List<TagDTO> tags = productService.getAllTags();
        return ResponseEntity.ok(tags);
    }
    
    @GetMapping("/videos")
    public ResponseEntity<List<VideoReelDTO>> getAllVideos() {
        return ResponseEntity.ok(videoService.getPublishedVideos());
    }
}