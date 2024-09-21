document.addEventListener('DOMContentLoaded', () => {
    const formType = new URLSearchParams(window.location.search).get('type'); // Get form type from URL, e.g. ?type=product
    const formHeader = document.getElementById('form-header');
    const formFields = document.getElementById('form-fields');
    const dynamicForm = document.getElementById('dynamic-form');
    const formTitle = document.getElementById('form-title');

    // Function for loading form fields based on type
    function loadFormFields(type) {
        let fields = '';
        switch (type) {
            case 'product':
                formHeader.textContent = 'Add New Product';
                formTitle.textContent = 'Add Product';
                fields = `
                     <div class="mb-3">
                        <label for="name" class="form-label">Product Name:</label>
                        <input type="text" class="form-control" id="name" name="name" minlength="2" maxlength="100" required>
                        <div class="invalid-feedback">Product name must be between 2 and 100 characters.</div>
                    </div>
                    <div class="mb-3">
                        <label for="description" class="form-label">Description:</label>
                        <textarea class="form-control" id="description" name="description" maxlength="300"></textarea>
                        <div class="invalid-feedback">Description must be less than 300 characters.</div>
                    </div>
                    <div class="mb-3">
                        <label for="price" class="form-label">Price:</label>
                        <input type="number" step="0.01" class="form-control" id="price" name="price" min="0" required>
                        <div class="invalid-feedback">Price must be a positive number.</div>
                    </div>
                    <div class="mb-3">
                        <label for="quantity" class="form-label">Quantity:</label>
                        <input type="number" class="form-control" id="quantity" name="quantity" min="0" required>
                        <div class="invalid-feedback">Quantity must be zero or more.</div>
                    </div>
                    <div class="mb-3">
                        <label for="categoryId" class="form-label">Category:</label>
                        <select class="form-select" id="categoryId" name="categoryId" required>
                            <option value="">Select a category</option>
                        </select>
                        <div class="invalid-feedback">Please select a category.</div>
                    </div>
                    <div class="mb-3">
                        <label for="brandId" class="form-label">Brand:</label>
                        <select class="form-select" id="brandId" name="brandId" required>
                            <option value="">Select a brand</option>
                        </select>
                        <div class="invalid-feedback">Please select a brand.</div>
                    </div>
                    <div class="mb-3">
                        <label for="image" class="form-label">Product Image:</label>
                        <input type="file" class="form-control" id="image" name="image" required>
                        <div class="invalid-feedback">Please upload a product image.</div>
                    </div>
                `;
                break;

            case 'user':
                formHeader.textContent = 'Add New User';
                formTitle.textContent = 'Add User';
                fields = `
                   <div class="mb-3">
                        <label for="email" class="form-label">Email:</label>
                        <input type="email" class="form-control" id="email" name="email" maxlength="50" required>
                        <div class="invalid-feedback">Please provide a valid email.</div>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Password:</label>
                        <input type="password" class="form-control" id="password" name="password" minlength="6" maxlength="50" required>
                        <div class="invalid-feedback">Password must be at least 6 characters long.</div>
                    </div>
                    <div class="mb-3">
                        <label for="fullName" class="form-label">Full Name:</label>
                        <input type="text" class="form-control" id="fullName" name="fullName" pattern="^[a-zA-Z\\s]+$" maxlength="50" required>
                        <div class="invalid-feedback">Full name is required and cannot contain numbers or special characters.</div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="role" class="form-label">Role:</label>
                        <select class="form-select" id="role" name="role" required>
                            <option value="">Select a role</option>
                            <option value="USER">User</option>
                            <option value="ADMIN">Admin</option>
                        </select>
                        <div class="invalid-feedback">Please select a role.</div>
                    </div>
                    <div class="mb-3">
                        <label for="address" class="form-label">Address:</label>
                        <input type="text" class="form-control" id="address" name="address" maxlength="50">
                        <div class="invalid-feedback">Address is required.</div>
                    </div>
                    <div class="mb-3">
                        <label for="phoneNumber" class="form-label">Phone Number:</label>
                        <input type="tel" class="form-control" id="phoneNumber" name="phoneNumber" pattern="^\\+?[0-9]{10,15}$"  required>
                        <div class="invalid-feedback">Please enter a valid phone number (10-15 digits).</div>
                    </div>
                `;
                break;

            case 'category':
                formHeader.textContent = 'Add New Category';
                formTitle.textContent = 'Add Category';
                fields = `
                    <div class="mb-3">
                        <label for="name" class="form-label">Category Name:</label>
                        <input type="text" class="form-control" id="name" name="name" minlength="2" maxlength="40" required>
                        <div class="invalid-feedback">Category name must be between 2 and 40 characters.</div>
                    </div>
                    <div class="mb-3">
                        <label for="description" class="form-label">Description:</label>
                        <textarea class="form-control" id="description" name="description" maxlength="300" rows="3"></textarea>
                        <div class="invalid-feedback">Description must be less than 300 characters.</div>
                    </div>
                `;
                break;

            case 'brand':
                formHeader.textContent = 'Add New Brand';
                formTitle.textContent = 'Add Brand';
                fields = `
                    <div class="mb-3">
                        <label for="name" class="form-label">Brand Name:</label>
                        <input type="text" class="form-control" id="name" name="name" minlength="2" maxlength="40" required>
                        <div class="invalid-feedback">Brand name must be between 2 and 40 characters.</div>
                    </div>
                    <div class="mb-3">
                        <label for="description" class="form-label">Description:</label>
                        <textarea class="form-control" id="description" name="description" maxlength="300" rows="3"></textarea>
                        <div class="invalid-feedback">Description must be less than 300 characters.</div>
                    </div>
                `;
                break;

            default:
                formHeader.textContent = 'Add New Entity';
                fields = `<p>Invalid form type.</p>`;
        }

        // Load form fields into the container
        formFields.innerHTML = fields;
        if (type === 'product') {
            loadCategories();
            loadBrands();
        }
    }

    // Function for sending form data
    function handleSubmit(event) {
        event.preventDefault();

        const formData = new FormData(dynamicForm);
        if (!dynamicForm.checkValidity()) {
            dynamicForm.classList.add('was-validated');
            return;
        }
        let url;
        switch (formType) {
            case 'product':
                url = '/admin/products';
                break;
            case 'user':
                url = '/admin/customers';
                break;
            case 'category':
                url = '/admin/categories';
                break;
            case 'brand':
                url = '/admin/brands';
                break;
            default:
                alert('Invalid form type.');
                return;
        }
        fetch(url, {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (response.ok) {
                    alert(`${formType.charAt(0).toUpperCase() + formType.slice(1)} added successfully!`);
                    window.location.href = '/admin';
                } else {
                    return response.text().then(errorText => {
                        alert(`Failed to add ${formType}:` + errorText);
                    });
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert(`An error occurred while adding the ${formType}:.`);
            });
    }
    loadFormFields(formType);
    dynamicForm.addEventListener('submit', handleSubmit);
});
// Function to load categories
function loadCategories() {
    fetch('/home/categories')
        .then(response => response.json())
        .then(categories => {
            const categorySelect = document.getElementById('categoryId');
            categories.forEach(category => {
                const option = document.createElement('option');
                option.value = category.id;
                option.textContent = category.name;
                categorySelect.appendChild(option);
            });
        })
        .catch(error => console.error('Error loading categories:', error));
}

// Function to load brands
function loadBrands() {
    fetch('/home/brands')
        .then(response => response.json())
        .then(brands => {
            const brandSelect = document.getElementById('brandId');
            brands.forEach(brand => {
                const option = document.createElement('option');
                option.value = brand.id;
                option.textContent = brand.name;
                brandSelect.appendChild(option);
            });
        })
        .catch(error => console.error('Error loading brands:', error));
}