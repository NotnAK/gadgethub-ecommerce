document.addEventListener('DOMContentLoaded', () => {
    let currentPage = 0;
    let totalPages = 0;
    const params = new URLSearchParams(window.location.search);
    const type = params.get('type');


    // Обработчики для CRUD операций
    document.getElementById('load-info').addEventListener('click', () => {
        updateUrlParam('type', 'customer');


        loadAdminInfo();
        hidePagination(); // Hide pagination when loading admin information
    });

    // Customers
    document.getElementById('create-customer').addEventListener('click', () => {
        showCreateCustomerForm();
    });
    document.getElementById('view-customers').addEventListener('click', () => {
        updateUrlParam('type', 'customer');

        loadCustomers(); // Show pagination when loading clients
        showPagination();
    });

    // Products
    document.getElementById('create-product').addEventListener('click', () => {
        showCreateProductForm();
    });
    document.getElementById('view-products').addEventListener('click', () => {
        updateUrlParam('type', 'product');

        loadProducts();
        showPagination(); // Show pagination when loading products
    });

    // Categories
    document.getElementById('create-category').addEventListener('click', () => {
        showCreateCategoryForm();
    });
    document.getElementById('view-categories').addEventListener('click', () => {
        updateUrlParam('type', 'category');
        loadCategories();
        hidePagination(); // Hide pagination when loading categories
    });

    // Brands
    document.getElementById('create-brand').addEventListener('click', () => {
        showCreateBrandForm();
    });
    document.getElementById('view-brands').addEventListener('click', () => {
        updateUrlParam('type', 'brand');
        loadBrands();
        hidePagination(); // Hide pagination when loading brands
    });

    // Orders
    document.getElementById('view-orders').addEventListener('click', () => {
        updateUrlParam('type', 'order');
        loadOrders();
        showPagination(); // Show pagination when loading orders
    });
    if (type !== null) {
        switch (type) {
            case 'customer':
                loadCustomers()
                break;
            case 'product':
                loadProducts()
                break;
            case 'category':
                loadCategories()
                break;
            case 'brand':
                loadBrands()
                break;
            case 'order':
                loadOrders()
                break;
            default:
                loadAdminInfo();
                return;
        }
    } else {
        loadAdminInfo()
    }

});

function hidePagination() {
    const paginationContainer = document.getElementById('pagination');
    paginationContainer.style.display = 'none'; // Hiding the container
}

// Function for displaying pagination
function showPagination() {
    const paginationContainer = document.getElementById('pagination');
    paginationContainer.style.display = 'block'; // Showing the container
}

// Go to the creation pages with the parameter
function showCreateProductForm() {
    window.location.href = '/admin/add-product?type=product';
}

function showCreateCustomerForm() {
    window.location.href = '/admin/add-product?type=user';
}

function showCreateCategoryForm() {
    window.location.href = '/admin/add-product?type=category';
}

function showCreateBrandForm() {
    window.location.href = '/admin/add-product?type=brand';
}

function updateUrlParam(key, value) {
    const url = new URL(window.location);
    url.searchParams.set(key, value);
    history.pushState({}, '', url);  // Update URL without reloading the page
}