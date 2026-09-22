# Furrow — a simple full-stack grocery shopping app

A small full-stack Java grocery store: a Spring Boot + JPA + H2 REST backend,
and a vanilla HTML/CSS/JS storefront served from the same app. No build
tooling on the frontend, no login system — just products, a cart, and
checkout.

## Stack

- **Backend:** Java 17, Spring Boot 3.3 (Web, Data JPA, Validation)
- **Database:** H2 in-memory (seeded with ~26 grocery items on startup)
- **Frontend:** static HTML/CSS/JS served from `src/main/resources/static`,
  talking to the backend over `fetch()`

## Project layout

```
grocery-app/
├── pom.xml
└── src/main/
    ├── java/com/example/grocery/
    │   ├── GroceryApplication.java     # entry point
    │   ├── model/                      # Product, CartItem, Order, OrderItem
    │   ├── repository/                 # Spring Data JPA repositories
    │   ├── controller/                 # ProductController, CartController, OrderController
    │   └── config/DataLoader.java      # seeds sample products on startup
    └── resources/
        ├── application.properties
        └── static/
            ├── index.html
            ├── css/style.css
            └── js/app.js
```

## Running it

You'll need Java 17+ and Maven installed locally (the app has no external
runtime dependencies beyond the JVM — H2 runs in-memory).

```bash
cd grocery-app
mvn spring-boot:run
```

Then open **http://localhost:8080** — the storefront and the API are served
from the same origin, so no CORS configuration is needed in practice (a
permissive `@CrossOrigin` is included anyway for convenience if you split the
frontend out later).

The H2 console (handy for peeking at the data) is available at
**http://localhost:8080/h2-console** — JDBC URL `jdbc:h2:mem:grocerydb`, user
`sa`, empty password.

## What it does

- **Browse:** products are grouped by category (Fruits, Vegetables, Dairy,
  Bakery, Pantry, Beverages, Snacks), with a search box and category filter.
- **Cart:** adding an item creates/increments a `CartItem` row keyed by an
  anonymous cart-owner id the browser generates once and stores in
  `localStorage`, so the cart survives page reloads without any login.
- **Checkout:** submitting the delivery form converts the current cart into
  an `Order` with `OrderItem` line items, clears the cart, and returns an
  order confirmation with the order id and total.

## REST API

| Method | Endpoint                              | Description                          |
|--------|----------------------------------------|---------------------------------------|
| GET    | `/api/products`                        | List products (`?category=`, `?search=`) |
| GET    | `/api/products/categories`             | Distinct category names               |
| GET    | `/api/products/{id}`                   | Single product                        |
| POST   | `/api/products`                        | Create a product                      |
| PUT    | `/api/products/{id}`                   | Update a product                      |
| DELETE | `/api/products/{id}`                   | Delete a product                      |
| GET    | `/api/cart/{cartOwnerId}`              | View cart + running total             |
| POST   | `/api/cart/{cartOwnerId}/items`        | Add item `{ productId, quantity }`    |
| PUT    | `/api/cart/{cartOwnerId}/items/{id}`   | Update line quantity `{ quantity }`   |
| DELETE | `/api/cart/{cartOwnerId}/items/{id}`   | Remove a line                         |
| DELETE | `/api/cart/{cartOwnerId}`              | Clear the cart                        |
| POST   | `/api/orders/checkout`                 | Place an order from the current cart  |
| GET    | `/api/orders/{cartOwnerId}`            | Order history for that shopper        |

## Extending it

Natural next steps if you want to grow this: real user accounts (Spring
Security), product images instead of emoji, admin screens for managing
inventory, pagination for large catalogs, and a proper payment integration
at checkout.
