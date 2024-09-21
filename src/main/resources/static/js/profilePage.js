document.addEventListener('DOMContentLoaded', () => {
    loadCustomerInfo();
    document.getElementById('edit-info').addEventListener('click', loadEditInfoForm);
    document.getElementById('view-cart').addEventListener('click', loadCartItems);
    document.getElementById('order-history').addEventListener('click', loadOrderHistory);
    document.getElementById('logout').addEventListener('click', logoutUser);

});

// Loading information about the current client
function loadCustomerInfo() {
    fetch('/customer/info')
        .then(response => response.json())
        .then(data => {
            const customerInfo = `
                <p><strong>Email:</strong> ${data.email}</p>
                <p><strong>Full Name:</strong> ${data.fullName}</p>
                <p><strong>Address:</strong> ${data.address || 'Not provided'}</p>
                <p><strong>Phone Number:</strong> ${data.phoneNumber || 'Not provided'}</p>
            `;
            document.getElementById('customer-info').innerHTML = customerInfo;
            window.customerData = data;
        })
        .catch(error => console.error('Error loading customer info:', error));
}

// Loading the form for editing information with pre-filled values
function loadEditInfoForm() {
    const data = window.customerData;

    const editForm = `
        <h2>Edit Profile Information</h2>
        <form id="edit-info-form" novalidate>
            <div class="mb-3">
                <label for="fullName" class="form-label">Full Name</label>
                <input type="text" class="form-control" id="fullName" value="${data.fullName}" pattern="^[a-zA-Z\\s]+$" maxlength="50" required >
                <div class="invalid-feedback">Full name is required/Full name cannot contain numbers or special characters</div>
            </div>
            <div class="mb-3">
                <label for="address" class="form-label">Address</label>
                <input type="text" class="form-control" id="address" value="${data.address || ''}" maxlength="30" required>
                <div class="invalid-feedback">Address is required.</div>

            </div>
            <div class="mb-3">
                <label for="phoneNumber" class="form-label">Phone Number</label>
                <input type="tel" class="form-control" id="phoneNumber" value="${data.phoneNumber || ''}" pattern="^\\+?[0-9]{10,15}$" maxlength="50" required>
                <div class="invalid-feedback">Please enter a valid phone number (10-15 digits).</div>
            </div>
            <button type="submit" class="btn btn-primary">Save Changes</button>
        </form>
    `;
    document.getElementById('profile-content').innerHTML = editForm;

    document.getElementById('edit-info-form').addEventListener('submit', (event) => {
        event.preventDefault();
        const form = event.target;
        if (!form.checkValidity()) {
            event.preventDefault();
            event.stopPropagation();
        }
        else{
            saveCustomerInfo();
        }
        form.classList.add('was-validated');

    });
}

// Saving modified customer information
function saveCustomerInfo() {
    const formData = new FormData();
    formData.append('fullName', document.getElementById('fullName').value);
    formData.append('address', document.getElementById('address').value);
    formData.append('phoneNumber', document.getElementById('phoneNumber').value);


    fetch('/customer/info', {
        method: 'PUT',
        body: formData
    })
        .then(response => {
            if (response.ok) {
                Swal.fire('Success', 'Information updated successfully!', 'success');
                loadCustomerInfo();
            } else {
                return response.text().then(errorText => {
                    Swal.fire('Error', 'Failed to update information: ' + errorText, 'error');
                });
            }
        })
        .catch(error => Swal.fire('Error', 'Failed to update information: ' + error.message, 'error'));
}

document.addEventListener('DOMContentLoaded', () => {
    // Load client information on page load
    loadCustomerInfo();
// Checking query parameter to load dumpster
    const urlParams = new URLSearchParams(window.location.search);
    const view = urlParams.get('view');
    if (view === 'cart') {
        toggleProfileLayout();
        loadCartItems();
    }
    document.getElementById('edit-info').addEventListener('click', () => {
        toggleProfileLayout();
        loadEditInfoForm();
    });

    document.getElementById('view-cart').addEventListener('click', () => {
        toggleProfileLayout();
        loadCartItems();
    });

    document.getElementById('order-history').addEventListener('click', () => {
        toggleProfileLayout();
        loadOrderHistory();
    });

});

// Function for changing the alignment and moving the profile to the left
function toggleProfileLayout() {
    const profileContainer = document.getElementById('profile-container');
    const contentColumn = document.getElementById('content-column');

    // If the content is hidden, open it and move the profile to the left
    if (contentColumn.classList.contains('d-none')) {
        contentColumn.classList.remove('d-none');
        profileContainer.classList.add('left-aligned'); // Move the profile to the left
    }
}

/////////////////////////////////
// Loading the form for filling in the delivery data
function loadDeliveryForm() {
    const data = window.customerData;

    const deliveryForm = `
        <h2>Delivery Information</h2>
        <form id="delivery-form" novalidate>
            <div class="mb-3">
                <label for="deliveryFullName" class="form-label">Full Name</label>
                <input type="text" class="form-control" id="deliveryFullName" value="${data.fullName}" maxlength="50" required>
                <div class="invalid-feedback">Full name is required/Full name cannot contain numbers or special characters</div>
            </div>
            <div class="mb-3">
                <label for="deliveryAddress" class="form-label">Address</label>
                <input type="text" class="form-control" id="deliveryAddress" value="${data.address || ''}" maxlength="30" required>
                <div class="invalid-feedback">Address cannot be empty.</div>
            </div>
            <div class="mb-3">
                <label for="deliveryPhoneNumber" class="form-label">Phone Number</label>
                <input type="tel" class="form-control" id="deliveryPhoneNumber" value="${data.phoneNumber || ''}" maxlength="50" required pattern="^\\+?[0-9]{10,15}$">
                <div class="invalid-feedback">Please enter a valid phone number (10-15 digits).</div>
            </div>
            <button type="submit" class="btn btn-primary">Confirm Order</button>
            <button type="button" class="btn btn-secondary" id="cancel-order">Cancel</button>
        </form>
    `;
    document.getElementById('profile-content').innerHTML = deliveryForm;

    document.getElementById('delivery-form').addEventListener('submit', handleDeliveryFormSubmit);

    // Handler for canceling order placement
    document.getElementById('cancel-order').addEventListener('click', () => {
        loadCartItems();
    });
}
// Delivery form submission handler
function handleDeliveryFormSubmit(event) {
    event.preventDefault();
    const form = event.target;
    // Проверяем валидность формы
    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        return;
    }
    createOrder();
}



