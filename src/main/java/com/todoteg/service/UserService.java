package com.todoteg.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todoteg.dto.AccountInfoDTO;
import com.todoteg.dto.FavoriteProductDTO;
import com.todoteg.dto.UserCreateRequest;
import com.todoteg.dto.UserDetailResponse;
import com.todoteg.dto.UserUpdateRequest;
import com.todoteg.dto.admin.UpdateUserRolesDTO;
import com.todoteg.dto.admin.DashboardStatsDTO;
import com.todoteg.exception.ResourceNotFoundException;
import com.todoteg.model.Account;
import com.todoteg.model.Address;
import com.todoteg.model.DetailFavoritos;
import com.todoteg.model.Images;
import com.todoteg.model.Product;
import com.todoteg.model.UserProfile;
import com.todoteg.repository.AccountRepository;
import com.todoteg.repository.AddressRepository;
import com.todoteg.repository.DetailFavoritosRepository;
import com.todoteg.repository.ImagesRepository;
import com.todoteg.repository.UserProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserProfileRepository userRepository;
    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;
    private final DetailFavoritosRepository favoritosRepository;
    private final ImagesRepository imagesRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    
	public Page<UserProfile> findAllUsers(Pageable pageable) {
		return userRepository.findAll(pageable);
	}
    
    @Transactional
    public UserProfile createUser(UserCreateRequest request) throws Exception {
        // Validar email único
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Crear Address
        Address address = new Address();
        if (request.getAddress() != null) {
            address.setStreet(request.getAddress().getStreet());
            address.setStreetNumber(request.getAddress().getStreetNumber());
            address.setDistric(request.getAddress().getDistric());
            address = addressRepository.save(address);
        }
        
        // Crear UserProfile
        UserProfile user = new UserProfile();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setIsActive(true);
        user.setIsStaff(false);
        user.setIsSuperuser(request.getIsSuperuser());
        
        // Manejar imagen de perfil Base64
        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            String imageUrl = cloudinaryService.uploadBase64Image(request.getProfileImage());
            user.setProfileImage(imageUrl);
        }
        
        user = userRepository.save(user);
        
        // Crear Account
        Account account = new Account();
        account.setUser(user);
        account.setAddress(address);
        
        if (request.getAccount() != null) {
            account.setPointsPerPurchase(request.getAccount().getPointsPerPurchase() != null ? 
                    request.getAccount().getPointsPerPurchase() : 0);
            account.setIsActive(request.getAccount().getIsActive() != null ? 
                    request.getAccount().getIsActive() : true);
        } else {
            account.setPointsPerPurchase(0);
            account.setIsActive(true);
        }
        
        accountRepository.save(account);
        
        return user;
    }
    
    public UserDetailResponse getUserDetail(Long userId, String requestUrl) {
        UserProfile user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        // Obtener favoritos
        List<DetailFavoritos> favoritos = favoritosRepository.findByAccountId(account.getId());
        
        Object favoriteData;
        if (favoritos.isEmpty()) {
            favoriteData = "No tienes favoritos";
        } else {
            favoriteData = favoritos.stream().map(fav -> {
                Product product = fav.getProduct();
                List<Images> images = imagesRepository.findByProductId(product.getId());
                
                List<String> imageUrls = images.stream()
                        .map(img -> cloudinaryService.getImageUrl(img.getImage()))
                        .collect(Collectors.toList());
                
                FavoriteProductDTO dto = new FavoriteProductDTO();
                dto.setIdFa(fav.getId());
                dto.setId(product.getId());
                dto.setName(product.getTitle());
                dto.setPrice(product.getPrice().toString());
                dto.setSlug(product.getSlug());
                dto.setImages(imageUrls);
                return dto;
            }).collect(Collectors.toList());
        }
        
        // Construir respuesta
        AccountInfoDTO accountInfo = new AccountInfoDTO();
        accountInfo.setId(account.getId());
        accountInfo.setFavorite(favoriteData);
        accountInfo.setPointsPerPurchase(account.getPointsPerPurchase());
        accountInfo.setAddress(account.getAddress() != null ? account.getAddress().toString() : "");
        accountInfo.setIsActive(account.getIsActive());
        
        UserDetailResponse response = new UserDetailResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setProfileImage(cloudinaryService.getImageUrl(user.getProfileImage()));
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAccount(accountInfo);
        
        return response;
    }
    
    @Transactional
    public void updateUser(Long userId, UserUpdateRequest request) throws Exception {
        UserProfile user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        
        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            String imageUrl = cloudinaryService.uploadBase64Image(request.getProfileImage());
            user.setProfileImage(imageUrl);
        }
        
        // Actualizar dirección si viene en la petición
        if (request.getAddress() != null) {
            Account account = accountRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            
            Address address = account.getAddress();
            if (address == null) {
                address = new Address();
            }
            
            address.setStreet(request.getAddress().getStreet());
            address.setStreetNumber(request.getAddress().getStreetNumber());
            address.setDistric(request.getAddress().getDistric());
            addressRepository.save(address);
            
            account.setAddress(address);
            accountRepository.save(account);
        }
        
        userRepository.save(user);
    }
    
    /**
     * Buscar todos los usuarios con paginación y búsqueda opcional
     */
    @Transactional(readOnly = true)
    public Page<UserProfile> findAllUsers(String search, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            // Buscar por nombre o email
        	System.out.println(search);
            return userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                search, search, pageable
            );
        }
        return userRepository.findAll(pageable);
    }
    
    /**
     * Buscar usuario por ID
     */
    @Transactional(readOnly = true)
    public UserProfile findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }
    
    /**
     * Actualizar roles de un usuario
     */
    @Transactional
    public UserProfile updateUserRoles(Long id, UpdateUserRolesDTO updateDTO) {
        UserProfile user = findById(id);
        
        if (updateDTO.getIsStaff() != null) {
            user.setIsStaff(updateDTO.getIsStaff());
        }
        
        if (updateDTO.getIsSuperuser() != null) {
            user.setIsSuperuser(updateDTO.getIsSuperuser());
        }
        
        if (updateDTO.getIsActive() != null) {
            user.setIsActive(updateDTO.getIsActive());
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Activar/Desactivar usuario
     */
    @Transactional
    public UserProfile toggleUserStatus(Long id) {
        UserProfile user = findById(id);
        user.setIsActive(!user.getIsActive());
        return userRepository.save(user);
    }
    
    /**
     * Obtener estadísticas del dashboard
     */
    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
    	DashboardStatsDTO stats = new DashboardStatsDTO();
        
        stats.setTotalUsers(userRepository.count());
        stats.setActiveUsers(userRepository.countByIsActiveTrue());
        
        // Si tienes OrderRepository
        // stats.setTotalOrders(orderRepository.count());
        // stats.setTotalRevenue(orderRepository.sumTotalAmount());
        
        return stats;
    }
}