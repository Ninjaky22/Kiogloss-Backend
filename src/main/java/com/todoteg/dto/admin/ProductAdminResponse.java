package com.todoteg.dto.admin;

import java.util.List;
import java.util.Map;

import com.todoteg.dto.ImageDTO;
import com.todoteg.dto.ProductVariantDTO;
import com.todoteg.dto.TagDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAdminResponse {
    private Long id;
    private String title;
    private String price;
    private String description;
    private String slug;
    private String status;
    private String published;
    private Integer stock; // Stock from default variant (when no real variants)
    private Map<String, String> attributes;
    private List<ProductVariantDTO> variants;
    private List<TagDTO> tags;
    private List<ImageDTO> images;
}