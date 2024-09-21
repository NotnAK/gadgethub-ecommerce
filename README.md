# GadgetHub

**GadgetHub** is an e-commerce platform for electronics sales. The website allows users to browse products, add them to their cart, and place orders. Administrators have access to product management functionality.

## Technologies
This project is built using the following technologies:
- **Backend**: Spring Boot, Java, MySQL, Spring Security (OAuth2 for Google)
- **Frontend**: HTML, CSS, Bootstrap, JavaScript (Vanilla)
- **Database**: MySQL
- **Authentication**: Google OAuth2
- **File Storage**: Local images

## Key Features
### For Users:
- User registration and authentication (using Google OAuth2)
- View and search for products
- Add products to cart
- Place orders
- View order history

### For Admins:
- Manage products (add, edit, delete)
- Manage categories and brands
- View all orders

## Installation and Setup

### Clone the repository
To clone this repository to your local machine, use the following command:

```bash
git clone https://github.com/NotnAK/gadgethub-ecommerce.git
```

## Database Setup
1. Ensure **MySQL** is installed and running on your system.
2. Create a new MySQL database schema:
    ```sql
    CREATE DATABASE eshop;
    ```
3. Open the project and update the database credentials in the `application.properties` file:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/eshop?characterEncoding=utf8&serverTimezone=UTC
    spring.datasource.username=YOUR_DB_USERNAME
    spring.datasource.password=YOUR_DB_PASSWORD
    ```

## Google OAuth2 Setup
1. Register your app in the [Google Developer Console](https://console.developers.google.com/).
2. Set the **Google OAuth2** credentials in the `application.properties` file:
    ```properties
    spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
    spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
    ```

## Build and Run the Application
1. Ensure **MySQL** is running.
2. Navigate to the root of the project directory.
3. Build the project using **Maven**:
    ```bash
    ./mvnw clean install
    ```
4. Once built, run the Spring Boot application:
    ```bash
    ./mvnw spring-boot:run
    ```

## Access the Application
- The application will be available at [http://localhost:8888](http://localhost:8888).
- Default port: `8888`.

## Running Tests
You can run tests using the following command:
```bash
./mvnw test
```

## For access to the admin panel, use the following credentials:

Email: admin@example.com
Password: 123


## Database Structure

Below is the database structure and relationships diagram for the project:

![Database Diagram](https://github.com/user-attachments/assets/a6b7b0e5-1367-420f-8543-14d34c86a639)




