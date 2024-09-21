// cartUtils.js

// Function for adding an item to the cart
function addToCart(productId) {
    fetch(`/customer/cart/items`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `productId=${productId}`,
    })
        .then(response => {
            if (response.ok) {
                Swal.fire({
                    icon: 'success',
                    title: 'Product added to cart',
                    showConfirmButton: false,
                    timer: 800,
                });
            } else if (response.status === 403) {
                // If access is denied (response 403)
                Swal.fire({
                    icon: 'error',
                    title: 'Access Denied',
                    text: 'You do not have permission to add items to the cart.',
                });
            } else if (response.status === 401) {
                // If the user is not authorized (response 401)
                Swal.fire({
                    icon: 'warning',
                    title: 'Not logged in',
                    text: 'You need to log in to add items to the cart.',
                    confirmButtonText: 'Log in',
                }).then(result => {
                    if (result.isConfirmed) {
                        window.location.href = '/login';
                    }
                });
            } else {
                response.text().then(errorText => {
                    Swal.fire({
                        icon: 'error',
                        title: 'Something went wrong',
                        text: errorText
                    });
                });
            }

        })
        .catch(error => {
            console.error('Error adding product to cart:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Something went wrong!',
            });
        });
}

// Loading cart items
function loadCartItems() {
    fetch('/customer/cart/items')
        .then(response => response.json())
        .then(cartItems => {
            let cartContent = '<h2>Your Cart</h2>';
            let totalPrice = 0;

            if (cartItems.length === 0) {
                cartContent += '<p>Your cart is empty.</p>';
            } else {
                cartItems.forEach(item => {
                    totalPrice += item.product.price * item.quantity;

                    cartContent += `
                         <div class="list-item d-flex align-items-center mb-3">
                <div class="list-item-image">
                    <img src="${item.product.imageUrl}" class="img-fluid" alt="${item.product.name}">
                </div>

                <!-- Block with product information -->
                <div class="list-item-info ms-3">
                    <p><strong>${item.product.name}</strong></p>
                    <p>Price: $${item.product.price.toFixed(2)}</p>
                    <p>Quantity: 
                        <input type="number" value="${item.quantity}" min="1" max="${item.product.quantity}"
                               data-cart-item-id="${item.id}" class="cart-item-quantity">
                        
                    </p>
                    <p class="text-muted">Max available: ${item.product.quantity}</p>
                    <button class="btn btn-danger btn-sm remove-item" data-cart-item-id="${item.id}">Remove</button>
                </div>
            </div>
            <hr>
                    `;
                });

                cartContent += `<h3>Total: $${totalPrice.toFixed(2)}</h3>`;
                cartContent += `
                    <button id="checkout-button" class="btn btn-success">Proceed to Checkout</button>
                    <button id="clear-cart-button" class="btn btn-warning">Clear cart</button>`;
            }

            document.getElementById('profile-content').innerHTML = cartContent;

            // Add handlers for changing the quantity of goods
            document.querySelectorAll('.cart-item-quantity').forEach(input => {
                input.addEventListener('change', updateCartItemQuantity);
            });

            // Add handlers for deleting goods
            document.querySelectorAll('.remove-item').forEach(button => {
                button.addEventListener('click', removeCartItem);
            });

            // Handler of the checkout button
            const checkoutButton = document.getElementById('checkout-button');
            if (checkoutButton) {
                checkoutButton.addEventListener('click', loadDeliveryForm);
            }
            // Handler of the "Clear cart" button
            const clearCartButton = document.getElementById('clear-cart-button');
            if (clearCartButton) {
                clearCartButton.addEventListener('click', clearCart);
            }
        })
        .catch(error => console.error('Error loading cart items:', error));
}

// Updating the quantity in the shopping cart
function updateCartItemQuantity(event) {
    const cartItemId = event.target.getAttribute('data-cart-item-id');
    const newQuantity = event.target.value;

    fetch(`/customer/carts/items/${cartItemId}?newQuantity=${newQuantity}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.ok) {
                loadCartItems();
            } else {
                loadCartItems();
                return response.text().then(errorText => {
                    alert('Failed to update information: ' + errorText);
                });
            }
        })
        .catch(error => console.error('Error updating cart item quantity:', error));
}

// Removing an item from the cart
function removeCartItem(event) {
    const cartItemId = event.target.getAttribute('data-cart-item-id');

    Swal.fire({
        title: 'Are you sure?',
        text: "Do you want to remove this item from the cart?",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, remove it!'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch(`/customer/carts/items/${cartItemId}`, {
                method: 'DELETE'
            })
                .then(response => {
                    if (response.ok) {
                        Swal.fire('Removed!', 'Item has been removed from the cart.', 'success');
                        loadCartItems(); // Reload the cart after deletion
                    } else {
                        Swal.fire('Error', 'Failed to remove item.', 'error');
                    }
                })
                .catch(error => Swal.fire('Error', 'Failed to remove item: ' + error.message, 'error'));
        }
    });
}

// Function for emptying the shopping cart
function clearCart() {
    Swal.fire({
        title: 'Are you sure?',
        text: "You won't be able to revert this!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, clear it!'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch('/customer/cart/clear', {
                method: 'DELETE'
            })
                .then(response => {
                    if (response.ok) {
                        Swal.fire('Cleared!', 'Your cart has been cleared.', 'success');
                        loadCartItems();
                    } else {
                        return response.text().then(errorText => {
                            Swal.fire('Error', 'Failed to clear the cart: ' + errorText, 'error');
                        });
                    }
                })
                .catch(error => Swal.fire('Error', 'Failed to clear the cart: ' + error.message, 'error'));
        }
    });
}
