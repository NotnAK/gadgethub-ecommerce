document.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(window.location.search);
    const type = params.get('type');
    const id = params.get('id');

    const detailsHeader = document.getElementById('details-header');
    const detailsBody = document.getElementById('details-body');
    const editButton = document.getElementById('edit-button');
    const deleteButton = document.getElementById('delete-button');
    function loadDetails(type, id) {
        let url = '';
        let title = '';

        switch (type) {
            case 'customer':
                url = `/admin/customers/${id}`;
                title = 'User Details';
                break;
            case 'product':
                url = `/admin/products/${id}`;
                title = 'Product Details';
                break;
            case 'category':
                url = `/admin/categories/${id}`;
                title = 'Category Details';
                break;
            case 'brand':
                url = `/admin/brands/${id}`;
                title = 'Brand Details';
                break;
            case 'order':
                url = `/admin/orders/${id}`;
                title = 'Order Details';
                break;
            default:
                detailsBody.innerHTML = `<p>Invalid type provided.</p>`;
                return;
        }

        detailsHeader.textContent = title;
        fetch(url)
            .then(response => response.json())
            .then(data => {
                let content = '';
                switch (type) {
                    case 'customer':
                        content = `
                            <div class="row">
                                <div class="col-md-3">
                                    <p><strong>Id:</strong> ${data.id}</p>
                                    
                                    <p><strong>Full Name:</strong> ${data.fullName}</p>
                                    <p><strong>Email:</strong> ${data.email}</p>
                                    <p><strong>Phone:</strong> ${data.phoneNumber || 'Not provided'}</p>
                                    <p><strong>Address:</strong> ${data.address || 'Not provided'}</p>
                                    <p><strong>Role:</strong> ${data.role}</p>
                                    <p><strong>Created At:</strong> ${new Date(data.createdAt).toLocaleString()}</p>
                                    <p><strong>Updated At:</strong> ${new Date(data.updatedAt).toLocaleString()}</p>
                                </div>
                                <div class="col-md-9">
                                <h3>Customer's Orders</h3>
                                    <div id="customer-orders" class="mb-4"></div>
                                </div>
                            </div>
                        `;
                        loadCustomerOrders(data.id);
                        break;
                    case 'product':
                        const statusClass = data.isActive ? '' : 'text-danger'; // If the product is inactive, add a class for red text
                        content = `
                        <div class="row">
                            <div class="col-md-6">
                                <img src="${data.imageUrl}" alt="${data.name}" class="img-fluid product-image">
                            </div>
                            <div class="col-md-6">
                                <p><strong>Id:</strong> ${data.id}</p>
                                <p><strong>Name:</strong> ${data.name}</p>
                                <p><strong>Description:</strong> ${data.description}</p>
                                <p><strong>Price:</strong> $${data.price}</p>
                                <p><strong>Quantity:</strong> ${data.quantity}</p>
                                <p><strong>Category:</strong> ${data.categoryName || 'No category'}</p>
                                <p><strong>Brand:</strong> ${data.brandName || 'No brand'}</p>
                                <p><strong>Popularity</strong>(count of successful orders): ${data.popularity}</p>
                                <p><strong>Is Active</strong>(users can see): <strong  class="${statusClass}">${data.isActive}</strong></p>
                            </div>
                        </div>
                        `;
                        break;
                    case 'category':
                        content = `
                            <p><strong>Name:</strong> ${data.name}</p>
                            <p><strong>Description:</strong> ${data.description || 'No description'}</p>
                        `;
                        break;
                    case 'brand':
                        content = `
                            <p><strong>Name:</strong> ${data.name}</p>
                            <p><strong>Description:</strong> ${data.description || 'No description'}</p>
                        `;
                        break;
                    case 'order':
                        content = `
                            <p><strong>Order ID:</strong> ${data.id}</p>
                            <p><strong>Customer Email:</strong> ${data.customerEmail}</p>
                            <p><strong>Status:</strong> ${data.status}</p>
                            <p><strong>Total Price:</strong> $${data.totalPrice}</p>
                            <p><strong>Created At:</strong> ${new Date(data.createdAt).toLocaleString()}</p>
                            <p><strong>Updated At:</strong> ${new Date(data.updatedAt).toLocaleString()}</p>
                            <p><strong>Delivery Address:</strong> ${data.deliveryAddress}</p>
                            <p><strong>Full Name:</strong> ${data.deliveryFullName}</p>
                            <p><strong>Phone number:</strong> ${data.deliveryPhoneNumber}</p>
                        `;
                        break;
                }
                detailsBody.innerHTML = content;
            })
            .catch(error => {
                detailsBody.innerHTML = `<p>Error loading details: ${error}</p>`;
                console.error('Error loading details:', error);
            });
    }

    function handleEdit() {
        window.location.href = `/admin/edit?type=${type}&id=${id}`;
    }


    function handleDelete() {
        if (confirm(`Are you sure you want to delete this ${type}?`)) {
            let url = `/admin/${type}s/${id}`;
            if(type === 'category'){
                url = `/admin/categories/${id}`
            }

            fetch(url, {
                method: 'DELETE'
            })
                .then(response => {
                    if (response.ok) {
                        alert(`${type.charAt(0).toUpperCase() + type.slice(1)} deleted successfully!`);
                        window.location.href = '/admin'; // Return to admin page after deletion
                    } else {
                        return response.text().then(errorText => {
                            alert(`Failed to delete ${type}: ` + errorText);
                        });
                    }
                })
                .catch(error => {
                    console.error('Error deleting:', error);
                    alert(`An error occurred while deleting the ${type}.`);
                });
        }
    }

    // Handlers for buttons
    editButton.addEventListener('click', handleEdit);
    deleteButton.addEventListener('click', handleDelete);

    // Uploading data
    loadDetails(type, id);
});

