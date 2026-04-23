package com.todoteg.config;

import com.todoteg.model.*;
import com.todoteg.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final VariantTypeRepository variantTypeRepository;
    private final VariantOptionRepository variantOptionRepository;
    private final TagRepository tagRepository;
    private final ImagesRepository imagesRepository;

    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            System.out.println("Seeding database with professional dynamic e-commerce data...");

            // 1. Create Variant Types
            VariantType vtColor = new VariantType(); vtColor.setName("Color"); vtColor.setType("color"); variantTypeRepository.save(vtColor);
            VariantType vtSize = new VariantType(); vtSize.setName("Talla"); vtSize.setType("button"); variantTypeRepository.save(vtSize);
            VariantType vtModel = new VariantType(); vtModel.setName("Modelo"); vtModel.setType("button"); variantTypeRepository.save(vtModel);

            // 2. Create Options
            VariantOption optBlack = new VariantOption(null, vtColor, "Negro", "#000000"); variantOptionRepository.save(optBlack);
            VariantOption optWhite = new VariantOption(null, vtColor, "Blanco", "#ffffff"); variantOptionRepository.save(optWhite);
            
            VariantOption optSizeM = new VariantOption(null, vtSize, "M", null); variantOptionRepository.save(optSizeM);
            VariantOption optSizeL = new VariantOption(null, vtSize, "L", null); variantOptionRepository.save(optSizeL);
            
            VariantOption optGTX1060 = new VariantOption(null, vtModel, "GTX 1060", null); variantOptionRepository.save(optGTX1060);
            VariantOption optGTX1060S = new VariantOption(null, vtModel, "GTX 1060 Super", null); variantOptionRepository.save(optGTX1060S);

            // 3. Create Tags
            Tag tagTech = new Tag(); tagTech.setName("Tecnología"); tagRepository.save(tagTech);

            // 4. Create Product: GPU
            Product p1 = new Product();
            p1.setTitle("Tarjeta Gráfica Gamer");
            p1.setSlug("tarjeta-grafica-gamer");
            p1.setPrice(new BigDecimal("850000.00"));
            p1.setDescription("<h2>Potencia gráfica</h2><p>Ideal para gaming en 1080p.</p>");
            p1.setStatus("published");
            p1.setPublished(LocalDateTime.now());
            p1.getTags().add(tagTech);

            // Variants for GPU
            ProductVariant v1 = new ProductVariant();
            v1.setProduct(p1);
            v1.setStock(5);
            v1.setSku("GPU-1060-STD");
            v1.getOptions().addAll(List.of(optGTX1060, optBlack));
            
            ProductVariant v2 = new ProductVariant();
            v2.setProduct(p1);
            v2.setStock(2);
            v2.setSku("GPU-1060-SUP");
            v2.getOptions().addAll(List.of(optGTX1060S, optWhite));
            
            p1.getVariants().addAll(List.of(v1, v2));
            productRepository.save(p1);

            System.out.println("Database seeding completed!");
        }
    }
}
