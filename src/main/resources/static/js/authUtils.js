document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('registerForm');
    form.addEventListener('submit', function (event) {
        if (!form.checkValidity()) {
            event.preventDefault();
            event.stopPropagation();
        }
        form.classList.add('was-validated');
    });
});

document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('registerForm');

    form.addEventListener('submit', function (event) {
        event.preventDefault();
        const formData = new FormData(form);
        fetch('/register-customer', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (response.ok) {
                    window.location.href = '/login';
                } else {
                    return response.text().then(errorMessage => {
                        showErrorMessage(errorMessage);
                    });
                }
            })
            .catch(error => {
                showErrorMessage('An unexpected error occurred. Please try again.');
                console.error('Error:', error);
            });
    });
    function showErrorMessage(message) {
        const errorContainer = document.createElement('div');
        errorContainer.classList.add('alert', 'alert-danger', 'mt-3');
        errorContainer.textContent = message;
        const existingError = document.querySelector('.alert-danger');
        if (existingError) {
            existingError.remove();
        }
        form.parentElement.prepend(errorContainer);
    }
});
function logoutUser() {
    fetch('/logout', {
        method: 'POST',
    })
        .then(response => {
            if (response.ok) {
                window.location.href = '/login';
            } else {
                alert('Error when logging out of the system');
            }
        })
        .catch(error => console.error('Error when logging out of the system:', error));
}