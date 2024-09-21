function loadAdminInfo() {
    fetch('/admin/info')
        .then(response => response.json())
        .then(data => {
            const adminContent = `
                <div class="card">
                    <div class="card-header text-center">
                        <h2>Admin Information</h2>
                    </div>
                    <div class="card-body">
                        <p><strong>Email:</strong> ${data.email}</p>
                        <p><strong>Full Name:</strong> ${data.fullName}</p>
                        <p><strong>Address:</strong> ${data.address || 'Not provided'}</p>
                        <p><strong>Phone Number:</strong> ${data.phoneNumber || 'Not provided'}</p>
                    </div>
                    <button id="logout" class="btn btn-danger">
                        <i class="fas fa-sign-out-alt"></i> Logout
                    </button>

                </div>
            `;
            document.getElementById('admin-content').innerHTML = adminContent;
            document.getElementById('logout').addEventListener('click', logoutUser);

        })
        .catch(error => {
            document.getElementById('admin-content').innerHTML = `<div class="alert alert-danger">Error loading admin info: ${error}</div>`;
            console.error('Error loading admin info:', error);
        });
}
function loadCustomers(page = 0) {
    let url = `/admin/customers?page=${page}`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            let content = `
                <div class="container">
                    <h2 class="text-center mb-4">Customers List</h2>
                    <div class="table-responsive"> 
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Id</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>Address</th>
                                <th>Role</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
            `;

            data.content.forEach(customer => {
                content += `
                    <tr><td>${customer.id}</td>
                        <td>${customer.fullName || 'Not provided'}</td>
                        <td>${customer.email}</td>
                        <td>${customer.phoneNumber || 'Not provided'}</td>
                        <td>${customer.address || 'Not provided'}</td>
                        <td>${customer.role}</td>
                        <td>
                            <button class="btn btn-primary manage-entity" data-customer-id="${customer.id}">
                                Manage
                            </button>
                        </td>
                    </tr>
                `;
            });

            content += `
                        </tbody>
                    </table>
                </div>
            `;

            document.getElementById('admin-content').innerHTML = content;

            document.querySelectorAll('.manage-entity').forEach(button => {
                button.addEventListener('click', (event) => {
                    const customerId = event.target.getAttribute('data-customer-id');
                    window.location.href = `/admin/manage?type=customer&id=${customerId}`;
                });
            });
            currentPage = data.number;
            totalPages = data.totalPages;
            updatePagination(loadCustomers, currentPage);
        })
        .catch(error => console.error('Error:', error));
}
function updatePagination(loadInfo, currentPage) {
    const paginationContainer = document.getElementById('pagination');
    paginationContainer.innerHTML = '';
    const prevButton = document.createElement('button');
    prevButton.classList.add('btn', 'btn-custom', 'me-2', 'px-4', 'py-2');
    prevButton.innerHTML = '<i class="fas fa-chevron-left"></i> Previous';
    prevButton.disabled = currentPage === 0;
    prevButton.addEventListener('click', () => {
        if (currentPage > 0) {
            loadInfo(currentPage - 1);
            document.getElementById('admin-content').scrollIntoView({ behavior: 'smooth' }); // Scroll to the beginning of the content

        }
    });
    paginationContainer.appendChild(prevButton);


    const nextButton = document.createElement('button');
    nextButton.classList.add('btn', 'btn-custom', 'px-4', 'py-2');
    nextButton.innerHTML = 'Next <i class="fas fa-chevron-right"></i>';
    nextButton.disabled = currentPage >= totalPages - 1;
    nextButton.addEventListener('click', () => {
        if (currentPage < totalPages - 1) {
            loadInfo(currentPage + 1);
            document.getElementById('admin-content').scrollIntoView({ behavior: 'smooth' }); // Scroll to the beginning of the content

        }
    });
    paginationContainer.appendChild(nextButton);
}
function loadProducts(page = 0) {
    let url = `/admin/products?page=${page}`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            let content = `
                <div class="container">
                    <h2 class="text-center mb-4">Products List</h2>
                    <div class="table-responsive">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Image</th>
                                <th>Id</th>
                                <th>Name</th>
                                <th>Description</th>
                                <th>Price</th>
                                <th>Quantity</th>
                                <th>Category</th>
                                <th>Brand</th>
                                <th>Status</th>
                                <th>Popularity</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
            `;

            data.content.forEach(product => {
                const isActive = product.isActive ? 'Active' : 'Inactive';
                const statusClass = product.isActive ? '' : 'text-danger'; // If the product is inactive, add a class for the red text
                content += `
                    <tr>
                        <td class="justify-content-center">
                            <img id="list-image" src="${product.imageUrl}" alt="${product.name}" class="img-fluid">
                        </td>
                        <td><strong>${product.id}</strong></td>
                        <td>${product.name}</td>
                        <td>Click edit to see more</td>
                        <td>$${product.price}</td>
                        <td>${product.quantity}</td>
                        <td>${product.categoryName || 'No category'}</td>
                        <td>${product.brandName || 'No brand'}</td>
                        <td class="${statusClass}"><strong>${isActive}</strong></td> <!-- Status is displayed in red if inactive -->
                        <td>${product.popularity}</td>
                        <td>
                           <button class="btn btn-primary manage-entity" data-product-id="${product.id}">
                                Manage
                            </button>
                        </td>
                    </tr>
                `;
            });

            content += `
                        </tbody>
                    </table>
                </div>
            `;

            document.getElementById('admin-content').innerHTML = content;


            document.querySelectorAll('.manage-entity').forEach(button => {
                button.addEventListener('click', (event) => {
                    const productId = event.target.getAttribute('data-product-id');
                    window.location.href = `/admin/manage?type=product&id=${productId}`;
                });
            });

            currentPage = data.number;
            totalPages = data.totalPages;
            updatePagination(loadProducts, currentPage);
        })
        .catch(error => console.error('Error:', error));
}
function loadCategories() {
    let url = `/admin/categories`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            let content = `
                <div class="container">
                    <h2 class="text-center mb-4">Categories List</h2>
                    <div class="table-responsive">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Id</th>
                                <th>Name</th>
                                <th>Description</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
            `;
            data.forEach(category => {
                content += `
                    <tr>
                        <td>${category.id}</td>
                        <td>${category.name}</td>
                        <td>${category.description || 'No description'}</td>
                        <td>
                            <button class="btn btn-primary manage-entity" data-category-id="${category.id}">
                                Manage
                            </button>
                        </td>
                    </tr>
                `;
            });

            content += `</tbody></table></div>
            `;

            document.getElementById('admin-content').innerHTML = content;

            document.querySelectorAll('.manage-entity').forEach(button => {
                button.addEventListener('click', (event) => {
                    const categoryId = event.target.getAttribute('data-category-id');
                    window.location.href = `/admin/manage?type=category&id=${categoryId}`;
                });
            });

        })
        .catch(error => console.error('Error:', error));
}
function loadBrands() {
    let url = `/admin/brands`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            let content = `
                <div class="container">
                    <h2 class="text-center mb-4">Brands List</h2>
                    <div class="table-responsive">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Id</th>
                                <th>Name</th>
                                <th>Description</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
            `;

            data.forEach(brand => {
                content += `
                    <tr>
                        <td>${brand.id}</td>
                        <td>${brand.name}</td>
                        <td>${brand.description || 'No description'}</td>
                        <td>
                            <button class="btn btn-primary manage-entity" data-brand-id="${brand.id}">
                                Manage
                            </button>
                        </td>
                    </tr>
                `;
            });

            content += `</tbody></table></div>
            `;

            document.getElementById('admin-content').innerHTML = content;

            document.querySelectorAll('.manage-entity').forEach(button => {
                button.addEventListener('click', (event) => {
                    const brandId = event.target.getAttribute('data-brand-id');
                    window.location.href = `/admin/manage?type=brand&id=${brandId}`;
                });
            });
        })
        .catch(error => console.error('Error:', error));
}
function loadOrders(page = 0) {
    let url = `/admin/orders?page=${page}`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            let content = `
                <div class="container">
                    <h2 class="text-center mb-4">Orders List</h2>
                    <div class="table-responsive">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Id</th>
                                <th>Customer Email</th>
                                <th>Status</th>
                                <th>Total Price</th>
                                <th>Created At</th>
                                <th>Updated At</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
            `;

            data.content.forEach(order => {
                const createdAt = new Date(order.createdAt).toLocaleString();
                const updatedAt = new Date(order.updatedAt).toLocaleString();
                const orderId = order.id;

                content += `
                    <tr>
                        <td>${order.id}</td>
                        <td>${order.customerEmail || 'No customer'}</td>
                        <td>${order.status}</td>
                        <td>$${order.totalPrice.toFixed(2)}</td>
                        <td>${createdAt}</td>
                        <td>${updatedAt}</td>
                        <td>
                            <button class="btn btn-primary manage-entity" data-order-id="${order.id}">
                                Manage
                            </button>
                            <button class="btn btn-secondary toggle-order-items" data-order-id="${order.id}">
                                View Items
                            </button>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="6">
                            <div class="order-items d-none" id="order-items-${orderId}">
                                ${generateOrderItemsContent(order.orderItems)}
                            </div>
                        </td>
                    </tr>
                `;
            });

            content += `
                        </tbody>
                    </table>
                </div>
            `;

            document.getElementById('admin-content').innerHTML = content;



            document.querySelectorAll('.toggle-order-items').forEach(button => {
                button.addEventListener('click', toggleOrderItems);
            });

            document.querySelectorAll('.manage-entity').forEach(button => {
                button.addEventListener('click', (event) => {
                    const orderId = event.target.getAttribute('data-order-id');
                    window.location.href = `/admin/manage?type=order&id=${orderId}`;
                });
            });
            currentPage = data.number;
            totalPages = data.totalPages;
            updatePagination(loadOrders, currentPage);
        })
        .catch(error => console.error('Error:', error));
}
function toggleOrderItems(event) {
    const orderId = event.target.getAttribute('data-order-id');
    const itemsContainer = document.getElementById(`order-items-${orderId}`);
    if (itemsContainer.classList.contains('d-none')) {
        itemsContainer.classList.remove('d-none');
        event.target.textContent = 'Hide Items';
    } else {
        itemsContainer.classList.add('d-none');
        event.target.textContent = 'View Items';
    }
}
// Function for displaying order items
function generateOrderItemsContent(orderItems) {
    let itemsContent = '<h5>Order Items</h5>';
    orderItems.forEach(item => {
        itemsContent += `
            <div class="list-item d-flex align-items-center mb-3">
                <div class="list-item-image">
                    <img src="${item.imageUrl}" class="img-fluid" alt="${item.productName}" >
                </div>
                <div class="list-item-info ms-3">
                    <p><strong>${item.productName}</strong></p>
                    <p>Price: $${item.productPrice.toFixed(2)}</p>
                    <p>Quantity: ${item.quantity}</p>
                </div>
            </div>
            <hr>
        `;
    });
    return itemsContent;
}function loadCustomerOrders(customerId) {
    const ordersContainer = document.getElementById('customer-orders');

    fetch(`/admin/customer-orders?customerId=${customerId}`)
        .then(response => response.json())
        .then(data => {
            let content = `
                <div class="container">
                    <div class="table-responsive">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Id</th>
                                <th>Customer Email</th>
                                <th>Status</th>
                                <th>Total Price</th>
                                <th>Created At</th>
                                <th>Updated At</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
            `;

            data.forEach(order => {
                const createdAt = new Date(order.createdAt).toLocaleString();
                const updatedAt = new Date(order.updatedAt).toLocaleString();
                const orderId = order.id;

                content += `
                    <tr>
                        <td>${order.id}</td>
                        <td>${order.customerEmail || 'No customer'}</td>
                        <td>${order.status}</td>
                        <td>$${order.totalPrice.toFixed(2)}</td>
                        <td>${createdAt}</td>
                        <td>${updatedAt}</td>
                        <td>
                            <button class="btn btn-primary manage-entity" data-order-id="${order.id}">
                                Manage
                            </button>
                            <button class="btn btn-secondary toggle-order-items" data-order-id="${order.id}">
                                View Items
                            </button>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="6">
                            <div class="order-items d-none" id="order-items-${orderId}">
                                ${generateOrderItemsContent(order.orderItems)}
                            </div>
                        </td>
                    </tr>
                `;
            });

            content += `
                        </tbody>
                    </table>
                </div>
            `;
            document.getElementById('customer-orders').innerHTML = content;

            // Handler for buttons to open/close the list of goods of an order
            document.querySelectorAll('.toggle-order-items').forEach(button => {
                button.addEventListener('click', toggleOrderItems);
            });
            document.querySelectorAll('.manage-entity').forEach(button => {
                button.addEventListener('click', (event) => {
                    const orderId = event.target.getAttribute('data-order-id');
                    window.location.href = `/admin/manage?type=order&id=${orderId}`;
                });
            });
        });
}