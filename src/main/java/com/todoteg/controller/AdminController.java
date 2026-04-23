package com.todoteg.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.todoteg.dto.TagDTO;
import com.todoteg.dto.VideoCreateRequest;
import com.todoteg.dto.VideoReelDTO;
import com.todoteg.dto.VideoUpdateRequest;
import com.todoteg.dto.admin.ApiResponseDTO;
import com.todoteg.dto.admin.ColorCreateRequest;
import com.todoteg.dto.admin.ColorDTO;
import com.todoteg.dto.admin.ColorUpdateRequest;
import com.todoteg.dto.admin.DashboardStatsDTO;
import com.todoteg.dto.admin.OrderDetailDTO;
import com.todoteg.dto.admin.OrderSummaryDTO;
import com.todoteg.dto.admin.PageResponseDTO;
import com.todoteg.dto.admin.ProductAdminResponse;
import com.todoteg.dto.admin.ProductCreateRequest;
import com.todoteg.dto.admin.ProductImageUploadRequest;
import com.todoteg.dto.admin.ProductUpdateRequest;
import com.todoteg.dto.admin.SizeCreateRequest;
import com.todoteg.dto.admin.SizeDTO;
import com.todoteg.dto.admin.SizeUpdateRequest;
import com.todoteg.dto.admin.TagCreateRequest;
import com.todoteg.dto.admin.TagUpdateRequest;
import com.todoteg.dto.admin.UpdateUserRolesDTO;
import com.todoteg.dto.admin.UserDashboardDTO;
import com.todoteg.dto.admin.UserDetailDTO;
import com.todoteg.mapper.OrderMapper;
import com.todoteg.mapper.UserMapper;
import com.todoteg.service.AdminService;
import com.todoteg.service.FileStorageService;
import com.todoteg.service.OrderService;
import com.todoteg.service.UserService;
import com.todoteg.service.VideoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final AdminService adminService;
    private final UserService userService;
    private final OrderService orderService;
    private final VideoService videoService;
    private final FileStorageService fileStorageService;
    private final UserMapper userMapper;
    private final OrderMapper orderMapper;
    
    // ==================== FILE UPLOAD ====================
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        String filename = fileStorageService.store(file);
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(filename)
                .toUriString();
        return ResponseEntity.ok(Map.of("url", url));
    }
    
    // ==================== PRODUCT ENDPOINTS ====================
    
    @PostMapping("/products")
    public ResponseEntity<ProductAdminResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {
        ProductAdminResponse response = adminService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/products")
    public ResponseEntity<Page<ProductAdminResponse>> getAllProducts(
    		@RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(name = "page_size", defaultValue = "20") int pageSize) {
        
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("published").descending());
        Page<ProductAdminResponse> products = adminService.getAllProducts(search, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductAdminResponse> getProductById(@PathVariable Long id) {
        ProductAdminResponse product = adminService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductAdminResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        ProductAdminResponse response = adminService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    // ==================== IMAGE ENDPOINTS ====================
    

    @PostMapping("/products/images")
    public ResponseEntity<Void> uploadProductImage(
            @Valid @RequestBody ProductImageUploadRequest request) throws Exception {
        adminService.uploadProductImage(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    

    @DeleteMapping("/products/images/{id}")
    public ResponseEntity<Void> deleteProductImage(@PathVariable Long id) {
        adminService.deleteProductImage(id);
        return ResponseEntity.noContent().build();
    }
    
    // ==================== TAG ENDPOINTS ====================
    
    @PostMapping("/tags")
    public ResponseEntity<TagDTO> createTag(@Valid @RequestBody TagCreateRequest request) {
        TagDTO tag = adminService.createTag(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(tag);
    }
    
    @GetMapping("/tags")
    public ResponseEntity<List<TagDTO>> getAllTags() {
        List<TagDTO> tags = adminService.getAllTagsAdmin();
        return ResponseEntity.ok(tags);
    }
    
    @PutMapping("/tags/{id}")
    public ResponseEntity<TagDTO> updateTag(
            @PathVariable Long id,
            @Valid @RequestBody TagUpdateRequest request) {
        TagDTO tag = adminService.updateTag(id, request);
        return ResponseEntity.ok(tag);
    }
    
    @DeleteMapping("/tags/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        adminService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
    
    // ==================== VARIANT MANAGEMENT ====================
    
    @PostMapping("/variants/types")
    public ResponseEntity<com.todoteg.dto.VariantTypeDTO> createVariantType(@Valid @RequestBody com.todoteg.dto.admin.VariantTypeCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createVariantType(request));
    }
    
    @GetMapping("/variants/types")
    public ResponseEntity<List<com.todoteg.dto.VariantTypeDTO>> getAllVariantTypes() {
        return ResponseEntity.ok(adminService.getAllVariantTypes());
    }
    
    @PutMapping("/variants/types/{id}")
    public ResponseEntity<com.todoteg.dto.VariantTypeDTO> updateVariantType(@PathVariable Long id, @Valid @RequestBody com.todoteg.dto.admin.VariantTypeCreateRequest request) {
        return ResponseEntity.ok(adminService.updateVariantType(id, request));
    }
    
    @DeleteMapping("/variants/types/{id}")
    public ResponseEntity<Void> deleteVariantType(@PathVariable Long id) {
        adminService.deleteVariantType(id);
        return ResponseEntity.noContent().build();
    }
    
    
    
    
   @GetMapping("/users")
   public ResponseEntity<ApiResponseDTO<PageResponseDTO<UserDashboardDTO>>> getAllUsers(
           @RequestParam(required = false) String search,
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
           @RequestParam(defaultValue = "name") String sortBy,
           @RequestParam(defaultValue = "ASC") String sortDir
   ) {
       Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") 
           ? Sort.Direction.DESC 
           : Sort.Direction.ASC;
       
       Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));
       
       var usersPage = userService.findAllUsers(search, pageable);
       var dtoPage = userMapper.toUserDashboardPage(usersPage);
       
       return ResponseEntity.ok(ApiResponseDTO.success(dtoPage));
   }
   
   /**
    * Obtener detalles completos de un usuario
    * GET /api/admin/users/123
    */
   @GetMapping("/users/{id}")
   public ResponseEntity<ApiResponseDTO<UserDetailDTO>> getUserDetails(@PathVariable Long id) {
       var user = userService.findById(id);
       var dto = userMapper.toUserDetailDTO(user);
       return ResponseEntity.ok(ApiResponseDTO.success(dto));
   }
   
   /**
    * Actualizar roles de un usuario
    * PUT /api/admin/users/123/roles
    */
   @PutMapping("/users/{id}/roles")
   public ResponseEntity<ApiResponseDTO<UserDashboardDTO>> updateUserRoles(
           @PathVariable Long id,
           @RequestBody UpdateUserRolesDTO updateDTO
   ) {
       var updatedUser = userService.updateUserRoles(id, updateDTO);
       var dto = userMapper.toUserDashboardDTO(updatedUser);
       return ResponseEntity.ok(
           ApiResponseDTO.success("Roles actualizados correctamente", dto)
       );
   }
   
   /**
    * Activar/Desactivar usuario
    * PATCH /api/admin/users/123/toggle-status
    */
   @PatchMapping("/users/{id}/toggle-status")
   public ResponseEntity<ApiResponseDTO<UserDashboardDTO>> toggleUserStatus(@PathVariable Long id) {
       var user = userService.toggleUserStatus(id);
       var dto = userMapper.toUserDashboardDTO(user);
       return ResponseEntity.ok(
           ApiResponseDTO.success("Estado del usuario actualizado", dto)
       );
   }
   
   // ============= GESTIÓN DE ÓRDENES =============
   
   /**
    * Listar todas las órdenes (con filtros)
    * GET /api/admin/orders?status=pending&page=0&page_size=20
    */
   @GetMapping("/orders")
   public ResponseEntity<ApiResponseDTO<PageResponseDTO<OrderSummaryDTO>>> getAllOrders(
           @RequestParam(required = false) String status,
           @RequestParam(required = false) Long userId,
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(name = "page_size", defaultValue = "20") int pageSize
   ) {
       Pageable pageable = PageRequest.of(
           page, 
           pageSize, 
           Sort.by(Sort.Direction.DESC, "date")
       );
       
       var ordersPage = orderService.findAllOrders(status, userId, pageable);
       var dtoPage = orderMapper.toOrderSummaryPage(ordersPage);
       
       return ResponseEntity.ok(ApiResponseDTO.success(dtoPage));
   }
   
   /**
    * Obtener detalles completos de una orden
    * GET /api/admin/orders/456
    */
   @GetMapping("/orders/{id}")
   public ResponseEntity<ApiResponseDTO<OrderDetailDTO>> getOrderDetails(@PathVariable Long id) {
       var order = orderService.findById(id);
       var dto = userMapper.toOrderDetailDTO(order);
       return ResponseEntity.ok(ApiResponseDTO.success(dto));
   }
   
   /**
    * Obtener órdenes de un usuario específico
    * GET /api/admin/users/123/orders
    */
   @GetMapping("/users/{userId}/orders")
   public ResponseEntity<ApiResponseDTO<PageResponseDTO<OrderSummaryDTO>>> getUserOrders(
           @PathVariable Long userId,
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(name = "page_size", defaultValue = "10") int pageSize
   ) {
       Pageable pageable = PageRequest.of(
           page, 
           pageSize, 
           Sort.by(Sort.Direction.DESC, "date")
       );
       
       var ordersPage = orderService.findOrdersByUserId(userId, pageable);
       var dtoPage = orderMapper.toOrderSummaryPage(ordersPage);
       
       return ResponseEntity.ok(ApiResponseDTO.success(dtoPage));
   }
   
   /**
    * Actualizar estado de una orden
    * PATCH /api/admin/orders/456/status
    */
   @PatchMapping("/orders/{id}/status")
   public ResponseEntity<ApiResponseDTO<OrderSummaryDTO>> updateOrderStatus(
           @PathVariable Long id,
           @RequestParam String status
   ) {
       var order = orderService.updateOrderStatus(id, status);
       var dto = userMapper.toOrderSummaryDTO(order);
       return ResponseEntity.ok(
           ApiResponseDTO.success("Estado de orden actualizado", dto)
       );
   }
   
   // ============= ESTADÍSTICAS DEL DASHBOARD =============
   
   /**
    * Obtener estadísticas generales del dashboard
    * GET /api/admin/stats
    */
   @GetMapping("/stats")
   public ResponseEntity<ApiResponseDTO<DashboardStatsDTO>> getDashboardStats() {
       var stats = userService.getDashboardStats();
       return ResponseEntity.ok(ApiResponseDTO.success(stats));
   }
    
    // ==================== VIDEO ENDPOINTS ====================
    
    @GetMapping("/videos")
    public ResponseEntity<List<VideoReelDTO>> getAllVideos() {
        return ResponseEntity.ok(videoService.getAllVideos());
    }
    
    @PostMapping("/videos")
    public ResponseEntity<VideoReelDTO> createVideo(@Valid @RequestBody VideoCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(videoService.createVideo(request));
    }
    
    @PutMapping("/videos/{id}")
    public ResponseEntity<VideoReelDTO> updateVideo(
            @PathVariable Long id,
            @RequestBody VideoUpdateRequest request) {
        return ResponseEntity.ok(videoService.updateVideo(id, request));
    }
    
    @DeleteMapping("/videos/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return ResponseEntity.noContent().build();
    }
}