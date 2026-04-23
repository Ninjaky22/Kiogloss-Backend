package com.todoteg.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {
    
    private final Cloudinary cloudinary;
    
    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }
    
    /**
     * Sube una imagen en Base64 a Cloudinary
     * @param base64Image String en formato base64 (puede incluir o no el prefijo data:image...)
     * @return URL de la imagen subida
     */
    public String uploadBase64Image(String base64Image) throws IOException {
        // Remover el prefijo "data:image/...;base64," si existe
        String imageData = base64Image;
        if (base64Image.contains(",")) {
            imageData = base64Image.split(",")[1];
        }
        
        // Decodificar Base64
        byte[] imageBytes = Base64.getDecoder().decode(imageData);
        
        // Generar nombre único para la imagen
        String publicId = "uploads/images/" + UUID.randomUUID().toString();
        
        // Subir a Cloudinary
        Map uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.asMap(
                "public_id", publicId,
                "folder", "uploads/images"
        ));
        
        return (String) uploadResult.get("secure_url");
    }
    
    /**
     * Devuelve la URL completa de una imagen almacenada
     * Si ya es una URL completa, la devuelve tal cual
     */
    public String getImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        
        // Si ya es una URL completa, retornarla
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath;
        }
        
        // Si es una ruta relativa de Cloudinary
        return cloudinary.url().generate(imagePath);
    }
}