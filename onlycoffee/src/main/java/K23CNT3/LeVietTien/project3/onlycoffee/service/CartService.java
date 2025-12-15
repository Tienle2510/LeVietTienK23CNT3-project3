package K23CNT3.LeVietTien.project3.onlycoffee.service;

import K23CNT3.LeVietTien.project3.onlycoffee.model.Cart;
import K23CNT3.LeVietTien.project3.onlycoffee.model.CartItem;  // THÊM IMPORT NÀY
import K23CNT3.LeVietTien.project3.onlycoffee.model.Product;
import K23CNT3.LeVietTien.project3.onlycoffee.model.User;
import K23CNT3.LeVietTien.project3.onlycoffee.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;

    public Optional<Cart> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    public Optional<Cart> getCartBySessionId(String sessionId) {
        return cartRepository.findBySessionId(sessionId);
    }

    @Transactional
    public Cart getOrCreateCart(User user, String sessionId) {
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElse(null);

        if (cart == null && sessionId != null) {
            cart = cartRepository.findBySessionId(sessionId)
                    .orElse(null);
        }

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setSessionId(sessionId);
            cart = cartRepository.save(cart);
        } else if (user != null && cart.getUser() == null) {
            // Nếu cart có sessionId nhưng chưa có user, gán user
            cart.setUser(user);
            cart = cartRepository.save(cart);
        }

        return cart;
    }

    @Transactional
    public Cart addToCart(Long cartId, Product product, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));

        // Kiểm tra số lượng tồn kho
        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        // Tìm cart item đã tồn tại
        CartItem existingItem = cart.findCartItemByProduct(product);

        if (existingItem != null) {
            // Cập nhật số lượng
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Thêm mới
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setPrice(product.getPrice());
            cart.addItem(newItem);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateCartItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));

        CartItem item = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in cart"));

        // Kiểm tra số lượng tồn kho
        Product product = item.getProduct();
        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        if (quantity <= 0) {
            cart.removeItem(item);
        } else {
            item.setQuantity(quantity);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));

        CartItem item = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in cart"));

        cart.removeItem(item);

        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));

        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    public BigDecimal calculateCartTotal(Cart cart) {
        return cart.getTotalPrice();
    }

    public int getCartItemCount(Cart cart) {
        return cart.getTotalItems();
    }
}