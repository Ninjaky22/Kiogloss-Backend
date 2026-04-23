package com.todoteg.service;

import com.todoteg.dto.VideoCreateRequest;
import com.todoteg.dto.VideoReelDTO;
import com.todoteg.dto.VideoUpdateRequest;
import com.todoteg.exception.ResourceNotFoundException;
import com.todoteg.model.Images;
import com.todoteg.model.Product;
import com.todoteg.model.VideoReel;
import com.todoteg.repository.ProductRepository;
import com.todoteg.repository.VideoReelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoReelRepository videoRepository;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public List<VideoReelDTO> getAllVideos() {
        return videoRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VideoReelDTO> getPublishedVideos() {
        return videoRepository.findAllPublishedByOrderByCreatedAtDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public VideoReelDTO createVideo(VideoCreateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        VideoReel video = new VideoReel();
        video.setVideoUrl(request.getVideoUrl());
        video.setThumbnailUrl(request.getThumbnailUrl());
        video.setUsername(request.getUsername());
        video.setProduct(product);

        video = videoRepository.save(video);
        return toDTO(video);
    }

    @Transactional
    public VideoReelDTO updateVideo(Long id, VideoUpdateRequest request) {
        VideoReel video = findById(id);

        if (request.getVideoUrl() != null) video.setVideoUrl(request.getVideoUrl());
        if (request.getThumbnailUrl() != null) video.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getUsername() != null) video.setUsername(request.getUsername());

        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
            video.setProduct(product);
        }

        video = videoRepository.save(video);
        return toDTO(video);
    }

    @Transactional
    public void deleteVideo(Long id) {
        VideoReel video = findById(id);
        fileStorageService.deleteIfLocal(video.getThumbnailUrl());
        fileStorageService.deleteIfLocal(video.getVideoUrl());
        videoRepository.delete(video);
    }

    private VideoReel findById(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video no encontrado con ID: " + id));
    }

    private VideoReelDTO toDTO(VideoReel v) {
        Product p = v.getProduct();
        String firstImage = p.getImages().stream()
                .findFirst()
                .map(Images::getImage)
                .orElse(null);

        return new VideoReelDTO(
                v.getId(),
                v.getVideoUrl(),
                v.getThumbnailUrl(),
                v.getUsername(),
                p.getId(),
                p.getTitle(),
                p.getPrice() != null ? p.getPrice().toString() : null,
                firstImage,
                p.getSlug(),
                v.getCreatedAt() != null ? v.getCreatedAt().toString() : null
        );
    }
}
