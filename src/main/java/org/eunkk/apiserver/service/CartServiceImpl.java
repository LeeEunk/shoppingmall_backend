package org.eunkk.apiserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eunkk.apiserver.domain.Cart;
import org.eunkk.apiserver.domain.CartItem;
import org.eunkk.apiserver.domain.Member;
import org.eunkk.apiserver.domain.Product;
import org.eunkk.apiserver.dto.CartItemDTO;
import org.eunkk.apiserver.dto.CartItemListDTO;
import org.eunkk.apiserver.repository.CartItemRepository;
import org.eunkk.apiserver.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    @Override
    public List<CartItemListDTO> addOrModify(CartItemDTO cartItemDTO) {

        String email = cartItemDTO.getEmail();
        Long pno = cartItemDTO.getPno();
        int qty = cartItemDTO.getQty();
        Long cino = cartItemDTO.getCino(); //장바구니 아이템 번호

        //기존에 담겨있는 상품에 대한 처리
        if(cino != null) {
            Optional<CartItem> cartItemResult =  cartItemRepository.findById(cino);

            CartItem cartItem = cartItemResult.orElseThrow();

            cartItem.changeQty(qty);

            cartItemRepository.save(cartItem);

            return getCartItems(email);
        }


        Cart cart = getCart(email);

        // 카트 안에서 중복 제품 검사
        CartItem cartItem = null;
        cartItem = cartItemRepository.getItemOfPno(email, pno);

        if (cartItem == null) {
            Product product = Product.builder().pno(pno).build();
            cartItem = CartItem.builder().product(product).cart(cart).qty(qty).build();
        } else {
            cartItem.changeQty(qty);
        }

        cartItemRepository.save(cartItem);


        return getCartItems(email);
    }

    //카트를 만들어주는 기능
    private Cart getCart(String email) {
        //해당 email의 장바구니(Cart)가 있는지 확인 -> 반환

        // 없으면 Cart 객체 생성하고 추가 반환

        Cart cart = null;

        Optional<Cart> result = cartRepository.getCartOfMember(email);

        if(result.isEmpty()) {
            log.info("Cart of the member is not exist!!");

            Member member = Member.builder().email(email).build();

            Cart tempCart = Cart.builder().owner(member).build();

            cart = cartRepository.save(tempCart);
        }else {
            cart = result.get();
        }

        return cart;
    }

    @Override
    public List<CartItemListDTO> getCartItems(String email) {

        return cartItemRepository.getItemsOfCartDTOByEmail(email);
    }

    @Override
    public List<CartItemListDTO> remove(Long cino) {
        Long cno = cartItemRepository.getCartFromItem(cino);

        cartItemRepository.deleteById(cino);

        return cartItemRepository.getItemsOfCartDTOByCart(cno);
    }
}
