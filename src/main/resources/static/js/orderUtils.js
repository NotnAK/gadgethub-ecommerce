function loadOrderHistory() {
    fetch('/customer/orders')
        .then(response => response.json())
        .then(orders => {
            let orderContent = '<h2>Your Order History</h2>';
            if (orders.length === 0) {
                orderContent += '<p>You have no orders yet.</p>';
            } else {
                orderContent += `
                    <div class="table-responsive">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>Order ID</th>
                                    <th>Total Price</th>
                                    <th>Status</th>
                                    <th>Created At</th>
                                    <th>Updated At</th>
                                    <th>Address</th>
                                    <th>Full Name</th>
                                    <th>Phone Number</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                `;

                orders.forEach(order => {
                    // Format creation and modification dates
                    const createdAt = new Date(order.createdAt).toLocaleString();
                    const updatedAt = new Date(order.updatedAt).toLocaleString();
                    const orderId = order.id;

                    orderContent += `
                        <tr>
                            <td>${order.id}</td>
                            <td>$${order.totalPrice.toFixed(2)}</td>
                            <td>${order.status}</td>
                            <td>${createdAt}</td>
                            <td>${updatedAt}</td>
                            <td>${order.deliveryAddress}</td>
                            <td>${order.deliveryFullName}</td>
                            <td>${order.deliveryPhoneNumber}</td>
                            <td>
                                <button class="btn btn-primary toggle-order-items" data-order-id="${orderId}">View Items</button>
                            </td>
                        </tr>
                        <tr class="d-none" id="order-items-${orderId}">
                            <td colspan="6">
                                <div class="order-items">${generateOrderItemsContent(order.orderItems)}</div>
                            </td>
                        </tr>
                    `;
                });

                orderContent += `</tbody></table></div>`;

            }
            document.getElementById('profile-content').innerHTML = orderContent;

            // Add handlers for buttons to open/close the list of products
            document.querySelectorAll('.toggle-order-items').forEach(button => {
                button.addEventListener('click', toggleOrderItems);
            });
        })
        .catch(error => console.error('Error loading order history:', error));
}

// Function for generating HTML content of the order goods list
function generateOrderItemsContent(orderItems) {
    let itemsContent = '';
    orderItems.forEach(item => {
        itemsContent += `
            <div class="list-item d-flex align-items-center mb-3">
                <!-- Product Image Block -->
                <div class="list-item-image">
                    <img src="${item.imageUrl}" class="img-fluid" alt="${item.productName}">
                </div>

                <!-- Block with product information -->
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
}

function createOrder() {
    const form = document.getElementById('delivery-form');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    const formData = new FormData();
    formData.append('deliveryFullName', document.getElementById('deliveryFullName').value);
    formData.append('deliveryAddress', document.getElementById('deliveryAddress').value);
    formData.append('deliveryPhoneNumber', document.getElementById('deliveryPhoneNumber').value);
    // Send shipping data and create an order
    fetch('/customer/carts/order', {
        method: 'POST',
        body: formData // Transmit data in FormData format
    })
        .then(response => {
            if (response.ok) {
                Swal.fire('Order Created!', 'Your order has been placed successfully!', 'success');
                loadCartItems();
            } else {
                return response.text().then(errorText => {
                    Swal.fire('Error', 'Failed to create order: ' + errorText, 'error');
                });
            }
        })
        .catch(error => Swal.fire('Error', 'Error creating order: ' + error.message, 'error'));
}

// Function for opening/closing the list of goods of the order
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