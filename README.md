# E-Commerce Application

Welcome to my e-commerce project! This project includes various features and functionalities to create a robust e-commerce platform.

## Project Overview

The aim was to build a comprehensive e-commerce application with essential features. Here’s a summary of what this project does:

### Entities and Their Roles

- **Product**: Represents items available for purchase, including details like product name, price, and stock information.
- **OrderItem**: Represents items added to the cart, including quantity and total price.
- **Cart**: Manages the shopping cart functionality.
  - Each user has a cart.
  - Users create OrderItems and add them to their cart.
  - Users can add/remove items, update quantities in the cart.
  - On order creation, the cart is cleared and stock is updated.
  - Stock is updated on order creation, not on item addition to the cart.
  - Orders can be canceled, which updates the stock.
- **Order**: Represents a finalized purchase from the cart.
- **UserRole**: Manages security roles for authentication and authorization.
  - Role-based authentication and anonymous user access.
  - Anonymous users get a cart via HttpSession and can add/remove items, update quantities, and create/cancel orders.

### Security Layer

- **Role-based Authentication**: Users need to log in to access certain methods, but some methods are accessible to anonymous users.
- **User Management**:
  - Admins can create users.
  - Users can register themselves (role defaulted to Customer).
  - A built-in admin is created at startup (cannot be modified or deleted).

### Additional Features

- **Endpoints**: Includes various endpoints for extended functionalities.
- **Swagger**: Integrated for API documentation.
- **DTOs**: Used for requests and responses, employing mapper methods to avoid exposing database entities directly.
- **Contact Message System**: Integrated for user communication.

### How to Run

1. Clone the repository.
2. Build the project with Maven or your preferred tool.
3. Run the application on localhost (default port 8080).

### Postman Collection

A comprehensive Postman collection is available for testing the endpoints. It includes:

1. Logging in as the built-in admin.
2. Admin creating another admin and users.
3. Registering a new user and logging in.
4. CRUD operations on users and products.
5. OrderItem and Cart operations.
6. Order creation and cancellation.
7. Testing anonymous user functionalities.

### Testing

To test the application, import the provided Postman collection and execute the requests as per the sequence mentioned in the collection.

### Contributions and Feedback

If you have any questions, suggestions for improvements, or encounter any issues, feel free to reach out. The code is available on my GitHub, and I’m open to feedback and collaboration.

### Summary

This project covers essential e-commerce functionalities, including user authentication, product management, cart operations, and order processing. It provides a solid foundation for further development and enhancements.

Thank you for checking out my project! If you're interested in the details or want to discuss any aspect of it, please contact me. Your feedback and suggestions are highly appreciated!
