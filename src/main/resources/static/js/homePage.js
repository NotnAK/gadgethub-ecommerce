
// Function for loading products from REST API
function loadProducts() {

    fetch('/home/get')
        .then(response => response.json())
        .then(products => {
            const productList = document.getElementById('product-list');
            productList.innerHTML = ''; // Clear the list before loading new products

            products.forEach(product => {
                const productCard = createProductCard(product);
                productList.innerHTML += productCard;
            });
            addProductEventListeners();
        })
        .catch(error => console.error('Error when loading products:', error));
}


// Function for adding an item to the cart

// Calling the product loading function after the page has loaded
document.addEventListener('DOMContentLoaded', loadProducts);
