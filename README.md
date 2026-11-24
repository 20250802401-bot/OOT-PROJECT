# Bookstore Management System

## Project Overview
The **Bookstore Management System** is a Java desktop application designed to help manage bookstore operations. It uses a graphical user interface (GUI) built with Java Swing and stores records locally so they can be accessed again when the application is reopened.

The system allows the user to manage books, record sales, keep customer details and purchase history, and track requests for books that are not currently available.

## Features

### 1. Inventory Management
- Add new books using their ID, title, author, price, and quantity.
- Search for books by ID or title.
- Update book details.
- Delete books from the inventory.
- View the complete inventory.
- Validate book details and prevent duplicate book IDs.

### 2. Sales Management
- Record a sale by entering the book ID and customer details.
- Automatically reduce the book's available quantity after a sale.
- Prevent sales when the book is unavailable or out of stock.
- Record the sale in the purchase history.

### 3. Customer Records
- Store customer IDs and names.
- Display registered customers.
- Update a customer's name when the same customer ID is entered again.

### 4. Purchase History
- Keep a record of the customer, book ID, book title, and purchase date for each sale.
- View recorded purchases in the Customer section.

### 5. Book Requests
- Record requests for books that are not currently in stock.
- Store the request date, customer details, and request status.
- Mark requests as fulfilled.

### 6. Low-Stock Monitoring
- Display a warning for books with fewer than three copies in stock.
- Check and refresh the low-stock warning automatically.

### 7. Automatic Saving
- Save bookstore data to a local file named `bookstore.dat`.
- Load previously saved records when the application starts.
- Automatically save changes and periodically save records in the background.

## Technologies Used
- **Java** – programming language.
- **Java Swing** – desktop GUI components.
- **ArrayList** – stores books, purchases, and book requests.
- **HashMap** – stores customer records using customer IDs.
- **Iterator** – used to safely remove books from the inventory.
- **Serialization** – saves and loads bookstore data using `bookstore.dat`.
- **Exception Handling** – handles invalid inputs and missing or unavailable books.
- **Multithreading** – periodically saves data and updates the low-stock warning.
- **LocalDate** – records purchase and request dates.

## Object-Oriented Programming Concepts
- **Interface:** `BookOperations` defines book-related operations.
- **Abstract class:** `Person` provides a common structure for people and declares the `display()` method.
- **Inheritance:** `Customer` inherits from `Person`.
- **Method overloading:** Books can be searched by ID or title using overloaded `searchBook()` methods.
- **Encapsulation through classes:** The application organizes book, customer, purchase, request, and bookstore data into classes.
- **Exception handling:** `BookNotFoundException` handles book-related errors, while other exceptions handle invalid inputs and saving/loading issues.
- **Serialization:** Serializable classes allow records to be stored as objects in a file.
- **Multithreading:** `StockMonitorThread` performs periodic background saving and stock-warning updates.

## Application Tabs
The GUI contains four main tabs:

| Tab | Purpose |
|---|---|
| **Inventory** | Add, search, update, delete, and view books. |
| **Sales** | Record book sales and update inventory. |
| **Customers** | View customer records and purchase history. |
| **Book Requests** | Record book requests and mark them as fulfilled. |

## Requirements
- Java JDK installed (Java 8 or later recommended).
- A Java-compatible environment such as VS Code, IntelliJ IDEA, Eclipse, or a terminal.

## How to Run

1. Save the source code as `BookstoreManagement.java`.
2. Open a terminal or command prompt in the folder containing the file.
3. Compile the program:

   ```bash
   javac BookstoreManagement.java
   ```

4. Run the application:

   ```bash
   java BookstoreManagement
   ```

5. The graphical bookstore management application will open.

> **Note:** The source file must be named `BookstoreManagement.java` because `BookstoreManagement` is the public class in the program.

## Data Storage
The application saves its data in `bookstore.dat` in the program's working directory. The file is created automatically when the application saves its records. When the application is launched again, saved records are loaded automatically.

## Project Structure
This project is implemented in a single Java source file:

- `BookstoreManagement.java` – contains the GUI and supporting classes.
- `bookstore.dat` – generated automatically to store serialized bookstore data.

## Conclusion
The Bookstore Management System provides a simple GUI-based way to manage bookstore inventory, sales, customers, purchase history, and book requests. It combines Java Swing with object-oriented programming, collections, exception handling, file serialization, and multithreading to create a practical desktop application.
