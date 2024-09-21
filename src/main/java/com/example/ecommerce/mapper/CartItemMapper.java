package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.CartItemDTO;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.repository.CartRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public abstract class CartItemMapper {

    @Autowired
    private CartRepository cartRepository;

    // Mapping CartItem entity to a DTO
    @Mapping(source = "cart.id", target = "cartId")
    public abstract CartItemDTO toDTO(CartItem cartItem);

    // Mapping a DTO to a CartItem entity
    @Mapping(source = "cartId", target = "cart", qualifiedByName = "mapCartIdToCart")
    public abstract CartItem toEntity(CartItemDTO cartItemDTO);

    // Method for mapping cartId to Cart object
    @Named("mapCartIdToCart")
    protected Cart mapCartIdToCart(Integer cartId) {
        if (cartId == null) {
            return null;
        }
        // Use repository to get Cart entity by ID
        return cartRepository.findById(cartId).orElse(null);
    }
}