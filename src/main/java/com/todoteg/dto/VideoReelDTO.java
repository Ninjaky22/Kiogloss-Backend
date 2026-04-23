package com.todoteg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoReelDTO {
    private Long id;
    private String videoUrl;
    private String thumbnailUrl;
    private String username;
    private Long productId;
    private String productTitle;
    private String productPrice;
    private String productImage;
    private String productSlug;
    private String createdAt;
}
