document.addEventListener('DOMContentLoaded', () => {
    const productList = document.getElementById('product-list');
    const sortOptions = document.getElementById('sort-options');
    const paginationContainer = document.getElementById('pagination');
    let selectedCategory =  null
    let selectedBrand = null;
    let currentPage = 0;
    let totalPages = 0;

    // Getting request parameters from URL
    const urlParams = new URLSearchParams(window.location.search);
    const searchQuery = urlParams.get('query'); // Get the "query" parameter

    // Function to update the header
    function updateProductsTitle() {
        const productsTitle = document.getElementById('products-title'); // Getting the header element
        let title = "All Products";

        if (searchQuery && selectedCategory === null && selectedBrand === null) {
            title = `Search: "${searchQuery}"`;
        } else if (selectedCategory && selectedBrand && selectedBrand !== "0" && selectedCategory !== "0") {
            title = `${document.querySelector('#brands-list a[data-brand-id="' + selectedBrand + '"]').textContent.trim()} ${document.querySelector('#categories-list a[data-category-id="' + selectedCategory + '"]').textContent.trim()}`;
        }
        else if(selectedCategory !== "0" && selectedBrand === "0"){
            title = `All ${document.querySelector('#categories-list a[data-category-id="' + selectedCategory + '"]').textContent.trim()}`;
        }
        else if(selectedCategory === "0" && selectedBrand !== "0"){
            title = `${document.querySelector('#brands-list a[data-brand-id="' + selectedBrand + '"]').textContent.trim()} products`;
        }
        productsTitle.textContent = title; // Updating the header text
    }




    // Function for loading products with filtering and pagination
    function loadProducts(categoryId = null, brandId = null, sortField = 'id', sortDirection = 'ASC', page = 0, query = '') {
        let url = `/home/getAllProducts?sortField=${sortField}&sortDirection=${sortDirection}&page=${page}`;

        if (categoryId) {
            url += `&categoryId=${categoryId}`;
        }
        if (brandId) {
            url += `&brandId=${brandId}`;
        }
        if (query) {
            url += `&query=${encodeURIComponent(query)}`; // Add a search parameter
        }
        //number - current page
        // totalPages -Total pages
        // content - list of elements
        fetch(url)
            .then(response => response.json())
            .then(data => {
                productList.innerHTML = ''; // Cleansing current products
                data.content.forEach(product => {
                    const productCard = createProductCard(product);
                    productList.innerHTML += productCard;
                });
                addProductEventListeners();
                // Updating pagination information
                currentPage = data.number;
                totalPages = data.totalPages;
                updatePagination();
                updateProductsTitle(); // Update header after selecting a category

            })
            .catch(error => console.error('Error when loading products:', error));
    }

    // Function for updating pagination
    function updatePagination() {
        paginationContainer.innerHTML = '';

        // Previous page button
        const prevButton = document.createElement('button');
        prevButton.classList.add('btn', 'btn-custom', 'me-2', 'px-4', 'py-2'); // Using custom styles
        prevButton.innerHTML = '<i class="fas fa-chevron-left"></i> Previous'; // Add an icon
        prevButton.disabled = currentPage === 0;
        prevButton.addEventListener('click', () => {
            if (currentPage > 0) {
                loadProducts(selectedCategory, selectedBrand, sortOptions.value.split('-')[0], sortOptions.value.split('-')[1], currentPage - 1, searchQuery);
                document.getElementById('products-title').scrollIntoView({ behavior: 'smooth' }); // Scroll to the beginning of the content

            }
        });
        paginationContainer.appendChild(prevButton);

        // Next page button
        const nextButton = document.createElement('button');
        nextButton.classList.add('btn', 'btn-custom', 'px-4', 'py-2');
        nextButton.innerHTML = 'Next <i class="fas fa-chevron-right"></i>';
        nextButton.disabled = currentPage >= totalPages - 1;
        nextButton.addEventListener('click', () => {
            if (currentPage < totalPages - 1) {
                loadProducts(selectedCategory, selectedBrand, sortOptions.value.split('-')[0], sortOptions.value.split('-')[1], currentPage + 1, searchQuery);
                document.getElementById('products-title').scrollIntoView({ behavior: 'smooth' }); // Scroll to the beginning of the content

            }
        });
        paginationContainer.appendChild(nextButton);
    }

    // Sorting change handler
    sortOptions.addEventListener('change', () => {
        const selectedOption = sortOptions.value.split('-');
        const sortField = selectedOption[0];
        const sortDirection = selectedOption[1];
        loadProducts(selectedCategory, selectedBrand, sortField, sortDirection, currentPage, searchQuery);
    });

    // Category click handler
    document.getElementById('categories-list').addEventListener('click', (event) => {
        if (event.target.tagName === 'A') {
            // First, remove the 'selected' class from all elements
            document.querySelectorAll('#categories-list a').forEach(link => link.classList.remove('selected'));

            // Add the 'selected' class to the current element
            event.target.classList.add('selected');

            // Get the selected category and load products
            selectedCategory = event.target.getAttribute('data-category-id');


            // I added this check to mark as selected the AllBrands area by default if it is not selected
            if(selectedBrand == null){
                selectedBrand = "0"
                const allBrandsElement = document.querySelector('#brands-list a[data-brand-id="0"]');
                allBrandsElement.classList.add('selected');
            }


            loadProducts(selectedCategory, selectedBrand, sortOptions.value.split('-')[0], sortOptions.value.split('-')[1], 0);
        }
    });

    // Brand click handler
    document.getElementById('brands-list').addEventListener('click', (event) => {
        if (event.target.tagName === 'A') {
            // First remove the 'selected' class from all elements
            document.querySelectorAll('#brands-list a').forEach(link => link.classList.remove('selected'));

            // Add the 'selected' class to the current element
            event.target.classList.add('selected');

            // Get the selected brand and load products
            selectedBrand = event.target.getAttribute('data-brand-id');


            // I added this check to mark as selected the AllCategories area by default if it is not selected
            if(selectedCategory == null){
                selectedCategory = "0"
                const allCategoriesElement = document.querySelector('#categories-list a[data-category-id="0"]');
                allCategoriesElement.classList.add('selected');
            }


            loadProducts(selectedCategory, selectedBrand, sortOptions.value.split('-')[0], sortOptions.value.split('-')[1], 0);
        }
    });
    // Load products on the first page load
    // Check if there is a search query when the page loads
    if (searchQuery) {
        loadProducts(null, null, 'id', 'ASC', 0, searchQuery); // Loading products with a search query
    } else {
        // Load products without a search query
        loadProducts();
    }
    updateProductsTitle(); // Update the header after selecting a category

    // Uploading categories
    fetch('/home/categories')
        .then(response => response.json())
        .then(categories => {
            const categoriesList = document.getElementById('categories-list');

            // Add the "All Categories" tab as the first category
            const allCategoriesItem = document.createElement('li');
            allCategoriesItem.className = '';
            if(searchQuery){
                allCategoriesItem.innerHTML = `<a class="nav-link" href="#" data-category-id="0">All Categories</a>`;
            }
            else{
                allCategoriesItem.innerHTML = `<a class="nav-link selected" href="#" data-category-id="0">All Categories</a>`;
            }
            categoriesList.appendChild(allCategoriesItem);

            categories.forEach(category => {
                const categoryItem = document.createElement('li');
                categoryItem.className = '';
                categoryItem.innerHTML = `<a class="nav-link" href="#" data-category-id="${category.id}">${category.name}</a>`;
                categoriesList.appendChild(categoryItem);
            });
        })
        .catch(error => console.error('Error when loading categories:', error));

    // Uploading brands
    fetch('/home/brands')
        .then(response => response.json())
        .then(brands => {
            const brandsList = document.getElementById('brands-list');


            // Adding the "All Brands" tab as the first brand
            const allBrandsItem = document.createElement('li');
            allBrandsItem.className = '';
            if(searchQuery){
                allBrandsItem.innerHTML = `<a class="nav-link" href="#" data-brand-id="0">All Brands</a>`;
            }
            else{
                allBrandsItem.innerHTML = `<a class="nav-link selected" href="#" data-brand-id="0">All Brands</a>`;

            }
            brandsList.appendChild(allBrandsItem);

            brands.forEach(brand => {
                const brandItem = document.createElement('li');
                brandItem.className = '';
                brandItem.innerHTML = `<a class="nav-link" href="#" data-brand-id="${brand.id}">${brand.name}</a>`;
                brandsList.appendChild(brandItem);
            });
        })
        .catch(error => console.error('Error when loading brands:', error));
});


