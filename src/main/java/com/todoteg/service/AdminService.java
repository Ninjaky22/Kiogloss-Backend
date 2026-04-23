package com.todoteg.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todoteg.dto.ImageDTO;
import com.todoteg.dto.TagDTO;
import com.todoteg.dto.VariantOptionDTO;
import com.todoteg.dto.VariantTypeDTO;
import com.todoteg.dto.admin.ProductAdminResponse;
import com.todoteg.dto.admin.ProductCreateRequest;
import com.todoteg.dto.admin.ProductImageUploadRequest;
import com.todoteg.dto.admin.ProductUpdateRequest;
import com.todoteg.dto.admin.TagCreateRequest;
import com.todoteg.dto.admin.TagUpdateRequest;
import com.todoteg.dto.admin.VariantOptionCreateRequest;
import com.todoteg.dto.admin.VariantTypeCreateRequest;
import com.todoteg.model.Images;
import com.todoteg.model.Product;
import com.todoteg.model.ProductVariant;
import com.todoteg.model.Tag;
import com.todoteg.model.VariantOption;
import com.todoteg.model.VariantType;
import com.todoteg.repository.ImagesRepository;
import com.todoteg.repository.ProductRepository;
import com.todoteg.repository.TagRepository;
import com.todoteg.repository.VariantOptionRepository;
import com.todoteg.repository.VariantTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final ProductRepository productRepository;
    private final TagRepository tagRepository;
    private final VariantTypeRepository variantTypeRepository;
    private final VariantOptionRepository variantOptionRepository;
    private final com.todoteg.repository.ProductVariantRepository productVariantRepository;
    private final ImagesRepository imagesRepository;
    private final CloudinaryService cloudinaryService;
    
    // ==================== PRODUCT MANAGEMENT ====================
    
    @Transactional
    public ProductAdminResponse createProduct(ProductCreateRequest request) {
        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setSlug(request.getSlug());
        product.setStatus(request.getStatus() != null ? request.getStatus() : "draft");
        product.setPublished(LocalDateTime.now());
        
        if (request.getAttributes() != null) {
            product.setAttributes(request.getAttributes());
        }
        
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            for (var vReq : request.getVariants()) {
                ProductVariant variant = new ProductVariant();
                variant.setProduct(product);
                if (vReq.getOptionIds() != null && !vReq.getOptionIds().isEmpty()) {
                    variant.getOptions().addAll(variantOptionRepository.findAllById(vReq.getOptionIds()));
                }
                variant.setStock(vReq.getStock() != null ? vReq.getStock() : 0);
                variant.setSku(vReq.getSku());
                variant.setImageUrl(vReq.getImageUrl());
                product.getVariants().add(variant);
            }
        } else {
            // Auto-create default variant (no options) so stock/cart always works via variantId
            ProductVariant defaultVariant = new ProductVariant();
            defaultVariant.setProduct(product);
            defaultVariant.setStock(request.getStock() != null ? request.getStock() : 0);
            product.getVariants().add(defaultVariant);
        }
        
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            product.setTags(tags);
        }
        
        product = productRepository.save(product);
        return convertToAdminResponse(product);
    }
    
    @Transactional
    public ProductAdminResponse updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (request.getTitle() != null) {
            product.setTitle(request.getTitle());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getSlug() != null) {
            product.setSlug(request.getSlug());
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        if (request.getAttributes() != null) {
            product.getAttributes().clear();
            product.getAttributes().putAll(request.getAttributes());
        }
        
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            Set<Long> updatedVariantIds = request.getVariants().stream()
                    .filter(v -> v.getId() != null)
                    .map(com.todoteg.dto.admin.ProductVariantRequestDTO::getId)
                    .collect(Collectors.toSet());
            
            product.getVariants().removeIf(v -> !updatedVariantIds.contains(v.getId()));
            
            for (var vReq : request.getVariants()) {
                ProductVariant variant;
                if (vReq.getId() != null) {
                    variant = product.getVariants().stream()
                            .filter(v -> vReq.getId().equals(v.getId()))
                            .findFirst().orElse(null);
                    if (variant == null) continue;
                } else {
                    variant = new ProductVariant();
                    variant.setProduct(product);
                    product.getVariants().add(variant);
                }
                
                if (vReq.getOptionIds() != null) {
                    variant.getOptions().clear();
                    variant.getOptions().addAll(variantOptionRepository.findAllById(vReq.getOptionIds()));
                }
                variant.setStock(vReq.getStock() != null ? vReq.getStock() : 0);
                variant.setSku(vReq.getSku());
                variant.setImageUrl(vReq.getImageUrl());
            }
        } else if (request.getVariants() != null) {
            // Variants list is explicitly empty → ensure a default variant exists
            // Remove all real variants (those with options), keep or create default
            product.getVariants().removeIf(v -> !v.getOptions().isEmpty());
            
            if (product.getVariants().isEmpty()) {
                ProductVariant defaultVariant = new ProductVariant();
                defaultVariant.setProduct(product);
                defaultVariant.setStock(request.getStock() != null ? request.getStock() : 0);
                product.getVariants().add(defaultVariant);
            } else {
                // Update the existing default variant's stock
                ProductVariant existing = product.getVariants().iterator().next();
                existing.setStock(request.getStock() != null ? request.getStock() : existing.getStock());
            }
        }
        
        if (request.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            product.setTags(tags);
        }
        
        product = productRepository.save(product);
        return convertToAdminResponse(product);
    }
    
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
    }
    
    public Page<ProductAdminResponse> getAllProducts(String search, Pageable pageable) {
        return search != null
        		?  productRepository.searchByStatus(null, search, pageable).map(this::convertToAdminResponse)
        		:  productRepository.findAll(pageable).map(this::convertToAdminResponse);
    }
    
    public ProductAdminResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return convertToAdminResponse(product);
    }
    
    // ==================== IMAGE MANAGEMENT ====================
    
    @Transactional
    public void uploadProductImage(ProductImageUploadRequest request) throws Exception {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        String imageUrl = cloudinaryService.uploadBase64Image(request.getImageBase64());
        
        Images image = new Images();
        image.setProduct(product);
        image.setImage(imageUrl);
        
        imagesRepository.save(image);
    }
    
    public void deleteProductImage(Long imageId) {
        Images image = imagesRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        imagesRepository.delete(image);
    }
    
    // ==================== TAG MANAGEMENT ====================
    
    @Transactional
    public TagDTO createTag(TagCreateRequest request) {
        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setImageURL(request.getImageURL());
        tag = tagRepository.save(tag);
        return new TagDTO(tag.getId(), tag.getName(), tag.getImageURL());
    }
    
    @Transactional
    public TagDTO updateTag(Long tagId, TagUpdateRequest request) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        
        if (request.getName() != null) {
            tag.setName(request.getName());
        }
        if (request.getImageURL() != null) {
            tag.setImageURL(request.getImageURL());
        }
        
        tag = tagRepository.save(tag);
        return new TagDTO(tag.getId(), tag.getName(), tag.getImageURL());
    }
    
    public void deleteTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        tagRepository.delete(tag);
    }
    
    public List<TagDTO> getAllTagsAdmin() {
        return tagRepository.findAll().stream()
                .map(tag -> new TagDTO(tag.getId(), tag.getName(), tag.getImageURL()))
                .collect(Collectors.toList());
    }
    
    // ==================== VARIANT MANAGEMENT ====================
    
    @Transactional
    public VariantTypeDTO createVariantType(VariantTypeCreateRequest request) {
        VariantType type = new VariantType();
        type.setName(request.getName());
        type.setType(request.getType());
        
        if (request.getOptions() != null) {
            for (VariantOptionCreateRequest optReq : request.getOptions()) {
                VariantOption opt = new VariantOption();
                opt.setVariantType(type);
                opt.setValue(optReq.getValue());
                opt.setMetaValue(optReq.getMetaValue());
                type.getOptions().add(opt);
            }
        }
        
        type = variantTypeRepository.save(type);
        return convertToVariantTypeDTO(type);
    }
    
    @Transactional
    public VariantTypeDTO updateVariantType(Long id, VariantTypeCreateRequest request) {
        VariantType type = variantTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("VariantType not found"));
        type.setName(request.getName());
        type.setType(request.getType());
        
        // Smart merge: keep existing options that are in use by products
        Set<Long> existingOptionIds = type.getOptions().stream()
                .map(VariantOption::getId)
                .collect(Collectors.toSet());
        
        Set<Long> inUseIds = existingOptionIds.isEmpty() 
                ? new HashSet<>() 
                : productVariantRepository.findOptionIdsInUse(existingOptionIds);
        
        // Build a map of requested options by id (for matching existing ones)
        java.util.Map<Long, VariantOptionCreateRequest> requestById = new java.util.HashMap<>();
        List<VariantOptionCreateRequest> newOptions = new java.util.ArrayList<>();
        if (request.getOptions() != null) {
            for (VariantOptionCreateRequest optReq : request.getOptions()) {
                if (optReq.getId() != null && existingOptionIds.contains(optReq.getId())) {
                    requestById.put(optReq.getId(), optReq);
                } else {
                    newOptions.add(optReq);
                }
            }
        }
        
        // Remove options not in the request AND not in use
        Set<Long> requestedIds = requestById.keySet();
        type.getOptions().removeIf(opt -> {
            if (requestedIds.contains(opt.getId())) return false; // keep: still in request
            if (inUseIds.contains(opt.getId())) return false; // keep: in use by products
            return true; // safe to remove
        });
        
        // Update existing options that are still in the request
        for (VariantOption opt : type.getOptions()) {
            VariantOptionCreateRequest req = requestById.get(opt.getId());
            if (req != null) {
                opt.setValue(req.getValue());
                opt.setMetaValue(req.getMetaValue());
            }
        }
        
        // Add new options
        for (VariantOptionCreateRequest optReq : newOptions) {
            VariantOption opt = new VariantOption();
            opt.setVariantType(type);
            opt.setValue(optReq.getValue());
            opt.setMetaValue(optReq.getMetaValue());
            type.getOptions().add(opt);
        }
        
        type = variantTypeRepository.save(type);
        return convertToVariantTypeDTO(type);
    }
    
    public List<VariantTypeDTO> getAllVariantTypes() {
        return variantTypeRepository.findAll().stream()
                .map(this::convertToVariantTypeDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteVariantType(Long id) {
        VariantType type = variantTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de variante no encontrado"));
        
        Set<Long> optionIds = type.getOptions().stream()
                .map(VariantOption::getId)
                .collect(Collectors.toSet());
        
        if (!optionIds.isEmpty()) {
            Set<Long> inUseIds = productVariantRepository.findOptionIdsInUse(optionIds);
            if (!inUseIds.isEmpty()) {
                throw new RuntimeException(
                    "No se puede eliminar este tipo de variante porque algunas opciones están asociadas a productos existentes. " +
                    "Elimine primero las variantes de los productos que usan estas opciones."
                );
            }
        }
        
        variantTypeRepository.deleteById(id);
    }
    
    private VariantTypeDTO convertToVariantTypeDTO(VariantType type) {
        List<VariantOptionDTO> options = type.getOptions().stream()
            .map(o -> new VariantOptionDTO(o.getId(), o.getVariantType().getName(), o.getValue(), o.getMetaValue()))
            .collect(Collectors.toList());
        return new VariantTypeDTO(type.getId(), type.getName(), type.getType(), options);
    }
    
    // ==================== HELPER METHODS ====================
    
    private ProductAdminResponse convertToAdminResponse(Product product) {
        List<Images> images = imagesRepository.findByProductId(product.getId());
        List<ImageDTO> imageUrls = images.stream()
                .map(img -> new ImageDTO(img.getId(), cloudinaryService.getImageUrl(img.getImage())))
                .collect(Collectors.toList());
        
        List<com.todoteg.dto.ProductVariantDTO> variantDTOs = product.getVariants().stream()
                .filter(v -> !v.getOptions().isEmpty()) // Exclude default variants (no options)
                .map(v -> {
                    List<VariantOptionDTO> options = v.getOptions().stream()
                        .map(o -> new VariantOptionDTO(o.getId(), o.getVariantType().getName(), o.getValue(), o.getMetaValue()))
                        .collect(Collectors.toList());
                    return new com.todoteg.dto.ProductVariantDTO(v.getId(), options, v.getStock(), v.getSku(), v.getImageUrl());
                })
                .collect(Collectors.toList());
        
        // Calculate stock from default variant (variant with no options)
        Integer defaultStock = product.getVariants().stream()
                .filter(v -> v.getOptions().isEmpty())
                .findFirst()
                .map(ProductVariant::getStock)
                .orElse(null);
        
        List<TagDTO> tagDTOs = product.getTags().stream()
                .map(tag -> new TagDTO(tag.getId(), tag.getName(), tag.getImageURL()))
                .collect(Collectors.toList());
        
        ProductAdminResponse response = new ProductAdminResponse();
        response.setId(product.getId());
        response.setTitle(product.getTitle());
        response.setPrice(product.getPrice().toString());
        response.setDescription(product.getDescription());
        response.setSlug(product.getSlug());
        response.setStatus(product.getStatus());
        response.setPublished(product.getPublished().toString());
        response.setAttributes(product.getAttributes());
        response.setStock(defaultStock);
        response.setVariants(variantDTOs);
        response.setTags(tagDTOs);
        response.setImages(imageUrls);
        
        return response;
    }
}
