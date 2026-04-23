package com.todoteg.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.todoteg.dto.ProductDetailDTO;
import com.todoteg.dto.ProductListDTO;
import com.todoteg.dto.ProductVariantDTO;
import com.todoteg.dto.TagDTO;
import com.todoteg.dto.VariantOptionDTO;
import com.todoteg.model.Images;
import com.todoteg.model.Product;
import com.todoteg.model.Tag;
import com.todoteg.repository.ImagesRepository;
import com.todoteg.repository.ProductRepository;
import com.todoteg.repository.TagRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ImagesRepository imagesRepository;
    private final TagRepository tagRepository;
    private final CloudinaryService cloudinaryService;
    private final EntityManager entityManager;
    
    public Page<ProductListDTO> getPublishedProducts(String search, List<String> tags, Pageable pageable) {
        Page<Product> products;
        
        if (tags != null && !tags.isEmpty()) {
            List<String> lowerTags = tags.stream().map(String::toLowerCase).collect(Collectors.toList());
            
            String countJpql = "SELECT COUNT(DISTINCT p) FROM Product p JOIN p.tags t WHERE p.status = 'published' AND LOWER(t.name) IN :tagNames";
            Long total = entityManager.createQuery(countJpql, Long.class)
                    .setParameter("tagNames", lowerTags)
                    .getSingleResult();
            
            String jpql = "SELECT DISTINCT p FROM Product p JOIN p.tags t WHERE p.status = 'published' AND LOWER(t.name) IN :tagNames ORDER BY p.published DESC";
            TypedQuery<Product> query = entityManager.createQuery(jpql, Product.class)
                    .setParameter("tagNames", lowerTags)
                    .setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize());
            
            products = new PageImpl<>(query.getResultList(), pageable, total);
        } else if (search != null && !search.isEmpty()) {
            products = productRepository.searchByStatus("published",search, pageable);
        } else {
            products = productRepository.findAllPublished(pageable);
        }
        
        return products.map(this::convertToListDTO);
    }
    
    public ProductDetailDTO getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        return convertToDetailDTO(product);
    }
    
    public List<TagDTO> getAllTags() {
        return tagRepository.findAll().stream()
                .map(tag -> new TagDTO(tag.getId(), tag.getName(), tag.getImageURL()))
                .collect(Collectors.toList());
    }
    
    private ProductListDTO convertToListDTO(Product product) {
        List<Images> images = imagesRepository.findByProductId(product.getId());
        String imageUrl = images.isEmpty() ? "" : 
                cloudinaryService.getImageUrl(images.get(0).getImage());
        
        ProductListDTO dto = new ProductListDTO();
        dto.setId(product.getId());
        dto.setImage(imageUrl);
        dto.setTitle(product.getTitle());
        dto.setSlug(product.getSlug());
        dto.setPrice(product.getPrice().toString());
        dto.setStock(product.getVariants().stream().mapToInt(com.todoteg.model.ProductVariant::getStock).sum());
        boolean hasRealVariants = product.getVariants().stream().anyMatch(v -> !v.getOptions().isEmpty());
        dto.setHasVariants(hasRealVariants);
        if (!hasRealVariants && !product.getVariants().isEmpty()) {
            dto.setDefaultVariantId(product.getVariants().iterator().next().getId());
        }
        
        return dto;
    }
    
    private ProductDetailDTO convertToDetailDTO(Product product) {
        List<Images> images = imagesRepository.findByProductId(product.getId());
        List<String> imageUrls = images.stream()
                .map(img -> cloudinaryService.getImageUrl(img.getImage()))
                .collect(Collectors.toList());
        
        List<String> tags = product.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
                
        List<com.todoteg.dto.ProductVariantDTO> variants = product.getVariants().stream()
                .map(v -> {
                    List<VariantOptionDTO> options = v.getOptions().stream()
                        .map(o -> new VariantOptionDTO(o.getId(), o.getVariantType().getName(), o.getValue(), o.getMetaValue()))
                        .collect(Collectors.toList());
                    return new com.todoteg.dto.ProductVariantDTO(v.getId(), options, v.getStock(), v.getSku(), v.getImageUrl());
                })
                .collect(Collectors.toList());
        
        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setId(product.getId());
        dto.setImages(imageUrls);
        dto.setTitle(product.getTitle());
        dto.setDescription(product.getDescription());
        dto.setTags(tags);
        dto.setPrice(product.getPrice().toString());
        dto.setStock(product.getVariants().stream().mapToInt(com.todoteg.model.ProductVariant::getStock).sum());
        dto.setAttributes(product.getAttributes());
        dto.setVariants(variants);
        
        return dto;
    }
}