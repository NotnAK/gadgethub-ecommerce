// Function for loading the header
function loadHeader() {
    fetch('/header.html')
        .then(response => response.text())
        .then(data => {
            document.getElementById('header').innerHTML = data;
            checkUserRole();
            // Once the header is loaded, add a handler for the search form
            const searchForm = document.getElementById('search-form');
            if (searchForm) {
                searchForm.addEventListener('submit', function (event) {
                    event.preventDefault();
                    const query = document.getElementById('search-query').value;
                    if (query.trim()){
                        // Redirect to the product page with the search parameter
                        window.location.href = `/products?query=${encodeURIComponent(query)}`;
                    }
                });
            }
            // Add a handler for clicking on the cart icon
            document.getElementById('cart-link').addEventListener('click', function (event) {
                event.preventDefault();
                window.location.href = '/profile?view=cart'; // Redirect with parameter
            });
        })
        .catch(error => console.error('Error when loading the header:', error));
}
function checkUserRole() {
    fetch('/api/user/info')
        .then(response => response.json())
        .then(data => {
            const profileLink = document.querySelector('#profile-link');
            const cartLink = document.querySelector('#cart-link')
            if (data.authenticated) {
                if (data.roles.includes('ROLE_ADMIN')) {
                    // If the user is an administrator, change the link to the administrator page
                    profileLink.href = '/admin';
                    cartLink.style.display = 'none';
                }
                else {
                    // If the user is a regular user, leave a link to the profile
                    profileLink.href = '/profile';
                }
            } else {
                // If the user is not authorized, redirect to the login page
                profileLink.href = '/login';
            }
        })
        .catch(error => {
            console.error('Error when retrieving user information:', error);
        });
}
document.addEventListener('DOMContentLoaded', loadHeader);
