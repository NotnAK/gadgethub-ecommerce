document.addEventListener('DOMContentLoaded', () => {
    const productContainer = document.getElementById('product-container');

    // Get product id from URL
    const productId = new URLSearchParams(window.location.search).get('id');

    // Function for loading product data from the backend
    function loadProduct(productId) {
        fetch(`/home/product/${productId}`)
            .then(response => response.json())
            .then(product => {
                const productCard = `
                <div class="col-md-6">
                    <img src="${product.imageUrl}" alt="${product.name}" class="img-fluid product-image">
                </div>
                <div class="col-md-6">
                    <h2>${product.name}</h2>
                    <p><strong>Brand:</strong> ${product.brandName}</p>
                    <p><strong>Category:</strong> ${product.categoryName}</p>
                    <p><strong>Description:</strong> ${product.description}</p>
                    <h4><strong>Price:</strong> $${product.price.toFixed(2)}</h4>
                    <p>Quantity: ${product.quantity}</p>
                    <button class="btn btn-primary" id="add-to-cart" data-product-id="${product.id}">
                        <i class="fas fa-shopping-cart"></i> Add to cart
                    </button>
                </div>
            `;
                productContainer.innerHTML = productCard;
                addCartEventListener();
            })
            .catch(error => {
                console.error('Error when loading the product:', error);
                productContainer.innerHTML = '<p>Error while downloading the product. Please try again later.</p>';
            });
    }

    // Function for adding an item to the cart
    function addCartEventListener() {
        const addToCartButton = document.getElementById('add-to-cart');
        addToCartButton.addEventListener('click', () => {
            const productId = addToCartButton.getAttribute('data-product-id');
            addToCart(productId); // Use the global function addToCart
        });
    }

    // Load the product on the first page load
    loadProduct(productId);
});
