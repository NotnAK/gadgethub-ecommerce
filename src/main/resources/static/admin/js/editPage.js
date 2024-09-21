document.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(window.location.search);
    const type = params.get('type');
    const id = params.get('id'); // Get entity ID
    const formFields = document.getElementById('form-fields');
    const editHeader = document.getElementById('edit-header');
    const editForm = document.getElementById('edit-form');
    let url = '';

    switch (type) {
        case 'customer':
            url = `/admin/customers/${id}`;
            editHeader.textContent = 'Edit Customer';
            break;
        case 'product':
            url = `/admin/products/${id}`;
            editHeader.textContent = 'Edit Product';
            break;
        case 'category':
            url = `/admin/categories/${id}`;
            editHeader.textContent = 'Edit Category';
            break;
        case 'brand':
            url = `/admin/brands/${id}`;
            editHeader.textContent = 'Edit Brand';
            break;
        case 'order':
            url = `/admin/orders/${id}`;
            editHeader.textContent = 'Edit Order';
            break;
        default:
            editForm.innerHTML = `<p>Invalid type provided.</p>`;
            return;
    }

    // Download data from the server and fill out the form
    fetch(url)
        .then(response => response.json())
        .then(data => {
            loadItems(data, type)
        });


    function loadItems(data,type){
        switch (type) {
            case 'customer':
                populateCustomerForm(data);
                break;
            case 'product':
                populateProductForm(data);
                break;
            case 'category':
                populateCategoryForm(data);
                break;
            case 'brand':
                populateBrandForm(data);
                break;
            case 'order':
                populateOrderForm(data);
                break;
        }
    }

    function populateCustomerForm(data) {
        formFields.innerHTML = `
            <div class="mb-3">
                <label for="fullName" class="form-label">Full Name:</label>
                <input type="text" id="fullName" name="fullName" class="form-control" value="${data.fullName}" pattern="^[a-zA-Z\\s]+$" required>
                <div class="invalid-feedback">Full name is required and must contain only letters and spaces.</div>
            </div>
            <div class="mb-3">
                <label for="phoneNumber" class="form-label">Phone Number:</label>
                <input type="tel" id="phoneNumber" name="phoneNumber" class="form-control" value="${data.phoneNumber}" pattern="^\\+?[0-9]{10,15}$" required>
                <div class="invalid-feedback">Phone number must contain 10-15 digits.</div>
            </div>
            <div class="mb-3">
                <label for="address" class="form-label">Address:</label>
                <input type="text" id="address" name="address" class="form-control" value="${data.address}" maxlength="50" required>
                <div class="invalid-feedback">Address is required.</div>

            </div>
        `;
    }

    function populateProductForm(data) {
        formFields.innerHTML = `
        <div class="mb-3">
            <label for="name" class="form-label">Product Name:</label>
            <input type="text" id="name" name="name" class="form-control" value="${data.name}" minlength="2" maxlength="100" required>
            <div class="invalid-feedback">Product name must be between 2 and 100 characters.</div>
        </div>
        <div class="mb-3">
            <label for="description" class="form-label">Description:</label>
            <textarea id="description" name="description" class="form-control" maxlength="300">${data.description}</textarea>
            <div class="invalid-feedback">Description must be less than 300 characters.</div>
        </div>
        <div class="mb-3">
            <label for="price" class="form-label">Price:</label>
            <input type="number" id="price" name="price" class="form-control" min="0.01" step="0.01" value="${data.price}" required>
            <div class="invalid-feedback">Price must be a positive number.</div>
        </div>
        <div class="mb-3">
            <label for="quantity" class="form-label">Quantity:</label>
            <input type="number" id="quantity" name="quantity" class="form-control" min="0" value="${data.quantity}" required>
            <div class="invalid-feedback">Quantity must be zero or more.</div>
        </div>
        <div class="mb-3">
            <label for="categoryId" class="form-label">Category:</label>
            <select id="categoryId" name="categoryId" class="form-select" required>
                <option value="">Select a category</option>
            </select>
            <div class="invalid-feedback">Please select a category.</div>
        </div>
        <div class="mb-3">
            <label for="brandId" class="form-label">Brand:</label>
            <select id="brandId" name="brandId" class="form-select" required>
                <option value="">Select a brand</option>
            </select>
            <div class="invalid-feedback">Please select a brand.</div>
        </div>
        <div class="mb-3">
            <label for="isActive" class="form-label">Is Active:</label>
            <input type="checkbox" id="isActive" name="isActive" class="form-check-input" ${data.isActive ? 'checked' : ''}>
            <div class="invalid-feedback">Please select the product status.</div>
        </div>
    `;

        loadCategories(data.categoryName);
        loadBrands(data.brandName);
    }


    function populateCategoryForm(data) {
        formFields.innerHTML = `
            <div class="mb-3">
                <label for="name" class="form-label">Category Name:</label>
                <input type="text" id="name" name="name" class="form-control" value="${data.name}" minlength="2" maxlength="40" required>
                <div class="invalid-feedback">Category name must be between 2 and 40 characters.</div>
            </div>
            <div class="mb-3">
                <label for="description" class="form-label">Description:</label>
                <textarea id="description" name="description" class="form-control" maxlength="300">${data.description}</textarea>
                <div class="invalid-feedback">Description must be less than 300 characters.</div>
            </div>
        `;
    }

    function populateBrandForm(data) {
        formFields.innerHTML = `
            <div class="mb-3">
                <label for="name" class="form-label">Brand Name:</label>
                <input type="text" id="name" name="name" class="form-control" value="${data.name}" minlength="2" maxlength="40" required>
                <div class="invalid-feedback">Brand name must be between 2 and 40 characters.</div>
            </div>
            <div class="mb-3">
                <label for="description" class="form-label">Description:</label>
                <textarea id="description" name="description" class="form-control" maxlength="300">${data.description}</textarea>
                <div class="invalid-feedback">Description must be less than 300 characters.</div>
            </div>
        `;
    }

    function populateOrderForm(data) {
        formFields.innerHTML = `
        <div class="mb-3">
            <label for="status" class="form-label">Order Status:</label>
            <select id="status" name="status" class="form-control" required>
            </select>
        </div>
    `;

        // Load order statuses
        loadOrderStatuses(data.status);
    }
    function loadOrderStatuses(selectedStatus) {
        fetch('/admin/order-statuses')
            .then(response => response.json())
            .then(statuses => {
                const statusSelect = document.getElementById('status');
                statuses.forEach(status => {
                    const option = document.createElement('option');
                    option.value = status;
                    option.textContent = status;
                    if (status === selectedStatus) {
                        option.selected = true;
                    }
                    statusSelect.appendChild(option);
                });
            })
            .catch(error => console.error('Error loading order statuses:', error));
    }
    // Loading categories for the product form
    function loadCategories(selectedCategoryName) {
        fetch('/home/categories')
            .then(response => response.json())
            .then(categories => {
                const categorySelect = document.getElementById('categoryId');
                categories.forEach(category => {
                    const option = document.createElement('option');
                    option.value = category.id;
                    option.textContent = category.name;

                    // If this is the current product category, make it selected
                    if (category.name === selectedCategoryName) {
                        option.selected = true;
                    }

                    categorySelect.appendChild(option);
                });
            })
            .catch(error => console.error('Error loading categories:', error));
    }

// Uploading brands for the product form
    function loadBrands(selectedBrandName) {
        fetch('/home/brands')
            .then(response => response.json())
            .then(brands => {
                const brandSelect = document.getElementById('brandId');
                brands.forEach(brand => {
                    const option = document.createElement('option');
                    option.value = brand.id;
                    option.textContent = brand.name;

                    // If this is the current product brand, make it selected
                    if (brand.name === selectedBrandName) {
                        option.selected = true;
                    }

                    brandSelect.appendChild(option);
                });
            })
            .catch(error => console.error('Error loading brands:', error));
    }


    // Form validation and submission
    function handleSubmit(event) {

        event.preventDefault(); // Stop the standard form submission
        const form = event.target;
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }
        const formData = new FormData(editForm);
        if(type === 'product'){
            if (!document.getElementById('isActive').checked) {
                formData.append('isActive', 'false');
            } else {
                formData.append('isActive', 'true');
            }
        }
        if(type === 'order'){
            const status = formData.get('status');  // Get the status value from the form
            url = `${url}?orderStatus=${status}`;
        }
        fetch(url, {
            method: 'PUT',
            body: formData
        }).then(response => {
            if (response.ok) {
                alert('Updated successfully');
            } else {
                return response.text().then(errorText => {
                    alert(`Failed: ` + errorText);
                });
            }
        }).catch(error => {
            alert(`An error occurred: ${error.message}`);
        });
    }

    editForm.addEventListener('submit', handleSubmit);
});
