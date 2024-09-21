function createProductCard(product) {
    return `
        <div class="col-md-4 product-card">
            <div class="card">
                <img src="${product.imageUrl}" class="card-img-top" alt="${product.name}">
                <div class="card-body">
                    <h5 class="product-title">${product.name}</h5>
                    <p class="product-price">
                        $${product.price.toFixed(2)} 
                        ${product.quantity < 1 ? '<span class="text-danger ms-2"><strong>Out of stock</strong></span>' : ''}
                    </p>
                </div>
                <button class="add-to-cart btn btn-primary" data-product-id="${product.id}">
                    <i class="fas fa-shopping-cart"></i> Add to cart
                </button>
                <button class="view-details btn btn-primary" data-product-id="${product.id}">
                                <i class="fas fa-info-circle"></i> View Details
                </button>
            </div>
        </div>
    `;
}


// Function for adding handlers to "Add to Cart" buttons
function addProductEventListeners() {
    const addToCartButtons = document.querySelectorAll('.add-to-cart');
    const viewDetailsButtons = document.querySelectorAll('.view-details');

    addToCartButtons.forEach(button => {
        button.addEventListener('click', () => {
            const productId = button.getAttribute('data-product-id');
            addToCart(productId);
        });
    });
    // Handlers for the "View Details" button
    viewDetailsButtons.forEach(button => {
        button.addEventListener('click', () => {
            const productId = button.getAttribute('data-product-id');
            // Redirects to a page with detailed product information
            window.location.href = `/product?id=${productId}`;
        });
    });

}


