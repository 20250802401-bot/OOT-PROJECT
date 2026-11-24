import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.time.LocalDate;// Interface

interface BookOperations {
  void addBook(Book b);

  void deleteBook(String id);

  Book searchBook(String id);
}// Abstract class

abstract class Person implements Serializable {
  String name;

  Person(String name) {
    this.name = name;
  }

  abstract void display();
}// Book class

class Book implements Serializable {
  String id, title, author;
  double price;
  int quantity;

  Book(String id, String title, String author, double price, int quantity) {
    this.id = id;
    this.title = title;
    this.author = author;
    this.price = price;
    this.quantity = quantity;
  }
}// Customer class

class Customer extends Person {
  String customerId;

  Customer(String customerId, String name) {
    super(name);
    this.customerId = customerId;
  }

  void display() {
    System.out.println(customerId + " " + name);
  }
}// Purchase history class

class Purchase implements Serializable {
  String customerId, customerName;
  String bookId, bookTitle, date;

  Purchase(String customerId, String customerName, String bookId, String bookTitle) {
    this.customerId = customerId;
    this.customerName = customerName;
    this.bookId = bookId;
    this.bookTitle = bookTitle;
    this.date = LocalDate.now().toString();
  }
}// Book request class

class BookRequest implements Serializable {
  String bookTitle, customerId, customerName;
  String date, status;

  BookRequest(String bookTitle, String customerId, String customerName) {
    this.bookTitle = bookTitle;
    this.customerId = customerId;
    this.customerName = customerName;
    this.date = LocalDate.now().toString();
    this.status = "Pending";
  }
}// User-defined exception

class BookNotFoundException extends Exception {
  BookNotFoundException(String message) {
    super(message);
  }
}// Bookstore class

class Bookstore implements BookOperations, Serializable {
  static String storeName = "My Bookstore";
  ArrayList<Book> books = new ArrayList<>();
  ArrayList<Purchase> purchases = new ArrayList<>();
  ArrayList<BookRequest> requests = new ArrayList<>();
  HashMap<String, Customer> customers = new HashMap<>();// Add book

  public void addBook(Book b) {
    books.add(b);
  }// Delete book using Iterator

  public void deleteBook(String id) {
    Iterator<Book> it = books.iterator();
    while (it.hasNext()) {
      Book b = it.next();
      if (b.id.equalsIgnoreCase(id)) {
        it.remove();
        return;
      }
    }
  }// Search book using ID

  public Book searchBook(String id) {
    for (Book b : books) {
      if (b.id.equalsIgnoreCase(id)) {
        return b;
      }
    }
    return null;
  }// Method overloading: Search book using title

  public Book searchBook(String title, boolean byTitle) {
    for (Book b : books) {
      if (byTitle && b.title.equalsIgnoreCase(title)) {
        return b;
      }
    }
    return null;
  }// Sell book

  public void sellBook(String id, String cid, String name) throws BookNotFoundException {
    Book b = searchBook(id);
    if (b == null) {
      throw new BookNotFoundException("Book not found!");
    }
    if (b.quantity <= 0) {
      throw new BookNotFoundException("Book is currently out of stock!");
    }
    if (cid.isEmpty() || name.isEmpty()) {
      throw new IllegalArgumentException("Enter customer ID and name!");
    }
    Customer c = customers.get(cid);
    if (c == null) {
      c = new Customer(cid, name);
      customers.put(cid, c);
    } else {
      c.name = name;
    }
    b.quantity--;
    purchases.add(new Purchase(cid, name, b.id, b.title));
  }// Place book request

  public void placeRequest(String title, String cid, String name) {
    if (title.isEmpty() || cid.isEmpty() || name.isEmpty()) {
      throw new IllegalArgumentException("Enter book title and customer details!");
    }
    Customer c = customers.get(cid);
    if (c == null) {
      customers.put(cid, new Customer(cid, name));
    } else {
      c.name = name;
    }
    requests.add(new BookRequest(title, cid, name));
  }// Save all records

  public void saveData() throws IOException {
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("bookstore.dat"));
    out.writeObject(this);
    out.close();
  }// Load saved records

  static Bookstore loadData() {
    try {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream("bookstore.dat"));
      Bookstore b = (Bookstore) in.readObject();
      in.close();
      return b;
    } catch (FileNotFoundException e) {
      return new Bookstore();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Error loading saved records!");
      return new Bookstore();
    }
  }
}// Multithreading: background thread that auto-saves and warns about low stock

class StockMonitorThread extends Thread {
  BookstoreManagement app;

  StockMonitorThread(BookstoreManagement app) {
    this.app = app;
    setDaemon(true); // stops automatically when the app closes
  }

  public void run() {
    while (true) {
      try {
        Thread.sleep(60000); // every 60 seconds
        // Swing must be updated on the GUI thread, so hand the work over
        SwingUtilities.invokeLater(() -> {
          app.saveAutomatically();
          app.updateLowStockLabel();
        });
      } catch (InterruptedException e) {
        return;
      }
    }
  }
}// Main GUI class

public class BookstoreManagement extends JFrame {
  Bookstore store;
  JTextField idField, titleField, authorField;
  JTextField priceField, quantityField;
  JTextField saleBookIdField;
  JTextField customerIdField, customerNameField;
  JTextField requestTitleField;
  JTextField requestCustomerIdField;
  JTextField requestCustomerNameField;
  DefaultTableModel bookModel;
  DefaultTableModel customerModel;
  DefaultTableModel purchaseModel;
  DefaultTableModel requestModel;
  JTable bookTable, customerTable;
  JTable purchaseTable, requestTable;
  JLabel lowStockLabel = new JLabel(" ");// Constructor

  BookstoreManagement() {
    store = Bookstore.loadData();
    setTitle(Bookstore.storeName);
    setSize(950, 650);
    setMinimumSize(new Dimension(850, 550));
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
    JLabel heading = new JLabel("MY BOOKSTORE MANAGEMENT SYSTEM", SwingConstants.CENTER);
    heading.setFont(new Font("Arial", Font.BOLD, 20));
    heading.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
    add(heading, BorderLayout.NORTH);// Create tabs
    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Inventory", createInventoryPanel());
    tabs.addTab("Sales", createSalesPanel());
    tabs.addTab("Customers", createCustomerPanel());
    tabs.addTab("Book Requests", createRequestPanel());
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
    mainPanel.add(tabs, BorderLayout.CENTER);
    add(mainPanel, BorderLayout.CENTER);
    lowStockLabel.setForeground(Color.RED);
    lowStockLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 8, 15));
    add(lowStockLabel, BorderLayout.SOUTH);
    refreshBooks();
    updateLowStockLabel();
    new StockMonitorThread(this).start();
    refreshCustomers();
    refreshPurchases();
    refreshRequests();
    setLocationRelativeTo(null);
    setVisible(true);
  }// INVENTORY PANEL

  JPanel createInventoryPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 12));
    panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));// Book details
    JPanel form = new JPanel(new GridLayout(3, 4, 12, 12));
    form.setBorder(BorderFactory.createTitledBorder("Book Details"));
    idField = new JTextField();
    titleField = new JTextField();
    authorField = new JTextField();
    priceField = new JTextField();
    quantityField = new JTextField();
    form.add(new JLabel("Book ID:"));
    form.add(idField);
    form.add(new JLabel("Title:"));
    form.add(titleField);
    form.add(new JLabel("Author:"));
    form.add(authorField);
    form.add(new JLabel("Price:"));
    form.add(priceField);
    form.add(new JLabel("Quantity:"));
    form.add(quantityField);
    form.add(new JLabel(""));// Book table
    String[] columns = { "Book ID", "Title", "Author", "Price", "Quantity" };
    bookModel = new DefaultTableModel(columns, 0);
    bookTable = new JTable(bookModel);
    bookTable.setRowHeight(25);
    JScrollPane scroll = new JScrollPane(bookTable);// Buttons
    JPanel buttons = new JPanel(new GridLayout(2, 3, 10, 10));
    JButton addBtn = new JButton("Add Book");
    JButton searchBtn = new JButton("Search Book");
    JButton updateBtn = new JButton("Update Book");
    JButton deleteBtn = new JButton("Delete Book");
    JButton viewBtn = new JButton("View All Books");
    JButton clearBtn = new JButton("Clear Fields");
    buttons.add(addBtn);
    buttons.add(searchBtn);
    buttons.add(updateBtn);
    buttons.add(deleteBtn);
    buttons.add(viewBtn);
    buttons.add(clearBtn);
    JPanel actionPanel = new JPanel(new BorderLayout());
    actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
    actionPanel.add(buttons, BorderLayout.CENTER);
    panel.add(form, BorderLayout.NORTH);
    panel.add(scroll, BorderLayout.CENTER);
    panel.add(actionPanel, BorderLayout.SOUTH);// Add book
    addBtn.addActionListener(e -> {
      try {
        String id = idField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        if (id.isEmpty() || title.isEmpty() || author.isEmpty() || priceField.getText().trim().isEmpty()
            || quantityField.getText().trim().isEmpty()) {
          throw new IllegalArgumentException("Enter all book details!");
        }
        double price = Double.parseDouble(priceField.getText());
        int quantity = Integer.parseInt(quantityField.getText());
        if (price < 0 || quantity < 0) {
          throw new IllegalArgumentException("Price and quantity cannot be negative!");
        }
        if (store.searchBook(id) != null) {
          throw new IllegalArgumentException("Book ID already exists!");
        }
        store.addBook(new Book(id, title, author, price, quantity));
        saveAutomatically();
        refreshBooks();
        JOptionPane.showMessageDialog(this, "Book added successfully!");
        clearBookFields();
      } catch (NumberFormatException ex) {
        showError("Enter valid price and quantity!");
      } catch (IllegalArgumentException ex) {
        showError(ex.getMessage());
      }
    });// Search book
    searchBtn.addActionListener(e -> {
      Book b;
      if (!idField.getText().trim().isEmpty()) {
        b = store.searchBook(idField.getText().trim());
      } else if (!titleField.getText().trim().isEmpty()) {
        b = store.searchBook(titleField.getText().trim(), true);
      } else {
        showError("Enter Book ID or Title!");
        return;
      }
      if (b != null) {
        showBookDetails(b);
      } else {
        showError("Book not found!");
      }
    });// Update book
    updateBtn.addActionListener(e -> {
      Book b = store.searchBook(idField.getText().trim());
      if (b == null) {
        showError("Book not found!");
        return;
      }
      try {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        double price = b.price;
        int quantity = b.quantity;
        if (!priceField.getText().trim().isEmpty()) {
          price = Double.parseDouble(priceField.getText());
        }
        if (!quantityField.getText().trim().isEmpty()) {
          quantity = Integer.parseInt(quantityField.getText());
        }
        if (price < 0 || quantity < 0) {
          throw new IllegalArgumentException("Values cannot be negative!");
        }
        if (!title.isEmpty()) {
          b.title = title;
        }
        if (!author.isEmpty()) {
          b.author = author;
        }
        b.price = price;
        b.quantity = quantity;
        saveAutomatically();
        refreshBooks();
        JOptionPane.showMessageDialog(this, "Book updated successfully!");
        clearBookFields();
      } catch (NumberFormatException ex) {
        showError("Enter valid price and quantity!");
      } catch (IllegalArgumentException ex) {
        showError(ex.getMessage());
      }
    });// Delete book
    deleteBtn.addActionListener(e -> {
      String id = idField.getText().trim();
      if (store.searchBook(id) == null) {
        showError("Book not found!");
        return;
      }
      int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?", "Confirm Deletion",
          JOptionPane.YES_NO_OPTION);
      if (choice == JOptionPane.YES_OPTION) {
        store.deleteBook(id);
        saveAutomatically();
        refreshBooks();
        clearBookFields();
        JOptionPane.showMessageDialog(this, "Book deleted successfully!");
      }
    });
    viewBtn.addActionListener(e -> refreshBooks());
    clearBtn.addActionListener(e -> clearBookFields());
    return panel;
  }// SALES PANEL

  JPanel createSalesPanel() {
    JPanel panel = new JPanel(new BorderLayout(15, 15));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
    JPanel form = new JPanel(new GridLayout(3, 2, 15, 15));
    form.setBorder(BorderFactory.createTitledBorder("Record a Sale"));
    saleBookIdField = new JTextField(20);
    customerIdField = new JTextField(20);
    customerNameField = new JTextField(20);
    form.add(new JLabel("Book ID:"));
    form.add(saleBookIdField);
    form.add(new JLabel("Customer ID:"));
    form.add(customerIdField);
    form.add(new JLabel("Customer Name:"));
    form.add(customerNameField);
    JLabel info = new JLabel("<html>Enter the book ID and customer details.<br>"
        + "The stock will be updated and the purchase " + "will be recorded automatically.</html>");
    JPanel center = new JPanel(new BorderLayout());
    center.add(info, BorderLayout.NORTH);
    JButton sellBtn = new JButton("Sell Book");
    JButton clearBtn = new JButton("Clear Fields");
    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
    buttons.add(sellBtn);
    buttons.add(clearBtn);
    panel.add(form, BorderLayout.NORTH);
    panel.add(center, BorderLayout.CENTER);
    panel.add(buttons, BorderLayout.SOUTH);// Sell book
    sellBtn.addActionListener(e -> {
      try {
        store.sellBook(saleBookIdField.getText().trim(), customerIdField.getText().trim(),
            customerNameField.getText().trim());
        saveAutomatically();
        refreshBooks();
        refreshCustomers();
        refreshPurchases();
        JOptionPane.showMessageDialog(this, "Sale recorded successfully!");
        saleBookIdField.setText("");
        customerIdField.setText("");
        customerNameField.setText("");
      } catch (BookNotFoundException ex) {
        showError(ex.getMessage());
      } catch (IllegalArgumentException ex) {
        showError(ex.getMessage());
      }
    });
    clearBtn.addActionListener(e -> {
      saleBookIdField.setText("");
      customerIdField.setText("");
      customerNameField.setText("");
    });
    return panel;
  }// CUSTOMER PANEL

  JPanel createCustomerPanel() {
    JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    customerModel = new DefaultTableModel(new String[] { "Customer ID", "Customer Name" }, 0);
    purchaseModel = new DefaultTableModel(
        new String[] { "Customer ID", "Customer Name", "Book ID", "Book Purchased", "Date" }, 0);
    customerTable = new JTable(customerModel);
    purchaseTable = new JTable(purchaseModel);
    customerTable.setRowHeight(25);
    purchaseTable.setRowHeight(25);
    JPanel top = new JPanel(new BorderLayout());
    top.setBorder(BorderFactory.createTitledBorder("Customer Records"));
    top.add(new JScrollPane(customerTable), BorderLayout.CENTER);
    JPanel bottom = new JPanel(new BorderLayout());
    bottom.setBorder(BorderFactory.createTitledBorder("Purchase History"));
    bottom.add(new JScrollPane(purchaseTable), BorderLayout.CENTER);
    panel.add(top);
    panel.add(bottom);
    return panel;
  }// BOOK REQUESTS PANEL

  JPanel createRequestPanel() {
    JPanel panel = new JPanel(new BorderLayout(12, 12));
    panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));// Request details
    JPanel form = new JPanel(new GridLayout(3, 2, 15, 12));
    form.setBorder(BorderFactory.createTitledBorder("Book Request Details"));
    requestTitleField = new JTextField(20);
    requestCustomerIdField = new JTextField(20);
    requestCustomerNameField = new JTextField(20);
    form.add(new JLabel("Book Title:"));
    form.add(requestTitleField);
    form.add(new JLabel("Customer ID:"));
    form.add(requestCustomerIdField);
    form.add(new JLabel("Customer Name:"));
    form.add(requestCustomerNameField);// Request table
    requestModel = new DefaultTableModel(
        new String[] { "Book Title", "Customer ID", "Customer Name", "Request Date", "Status" }, 0);
    requestTable = new JTable(requestModel);
    requestTable.setRowHeight(25);
    JScrollPane scroll = new JScrollPane(requestTable);// Buttons
    JButton addBtn = new JButton("Place Request");
    JButton fulfillBtn = new JButton("Mark as Fulfilled");
    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
    buttons.add(addBtn);
    buttons.add(fulfillBtn);
    panel.add(form, BorderLayout.NORTH);
    panel.add(scroll, BorderLayout.CENTER);
    panel.add(buttons, BorderLayout.SOUTH);// Place request
    addBtn.addActionListener(e -> {
      try {
        store.placeRequest(requestTitleField.getText().trim(), requestCustomerIdField.getText().trim(),
            requestCustomerNameField.getText().trim());
        saveAutomatically();
        refreshRequests();
        refreshCustomers();
        JOptionPane.showMessageDialog(this, "Book request recorded!");
        requestTitleField.setText("");
        requestCustomerIdField.setText("");
        requestCustomerNameField.setText("");
      } catch (IllegalArgumentException ex) {
        showError(ex.getMessage());
      }
    });// Mark request as fulfilled
    fulfillBtn.addActionListener(e -> {
      int selected = requestTable.getSelectedRow();
      if (selected == -1) {
        showError("Select a request first!");
        return;
      }
      int index = requestTable.convertRowIndexToModel(selected);
      BookRequest r = store.requests.get(index);
      if (r.status.equals("Fulfilled")) {
        showError("This request is already fulfilled!");
        return;
      }
      r.status = "Fulfilled";
      saveAutomatically();
      refreshRequests();
      JOptionPane.showMessageDialog(this, "Request marked as fulfilled!");
    });
    return panel;
  }// REFRESH BOOK TABLE

  void refreshBooks() {
    bookModel.setRowCount(0);
    for (Book b : store.books) {
      bookModel.addRow(new Object[] { b.id, b.title, b.author, b.price, b.quantity });
    }
  }// REFRESH CUSTOMER TABLE

  void refreshCustomers() {
    customerModel.setRowCount(0);
    for (Customer c : store.customers.values()) {
      customerModel.addRow(new Object[] { c.customerId, c.name });
    }
  }// REFRESH PURCHASE HISTORY

  void refreshPurchases() {
    purchaseModel.setRowCount(0);
    for (Purchase p : store.purchases) {
      purchaseModel.addRow(new Object[] { p.customerId, p.customerName, p.bookId, p.bookTitle, p.date });
    }
  }// REFRESH REQUEST TABLE

  void refreshRequests() {
    requestModel.setRowCount(0);
    for (BookRequest r : store.requests) {
      requestModel.addRow(new Object[] { r.bookTitle, r.customerId, r.customerName, r.date, r.status });
    }
  }// LOW STOCK WARNING (updated by the background thread)

  void updateLowStockLabel() {
    ArrayList<String> low = new ArrayList<>();
    for (Book b : store.books) {
      if (b.quantity < 3) {
        low.add(b.title + " (" + b.quantity + ")");
      }
    }
    lowStockLabel.setText(low.isEmpty() ? " " : "Low stock: " + String.join(", ", low));
  }// SHOW BOOK DETAILS

  void showBookDetails(Book b) {
    JOptionPane.showMessageDialog(this, "Book Found!\n\n" + "Book ID: " + b.id + "\nTitle: " + b.title + "\nAuthor: "
        + b.author + "\nPrice: " + b.price + "\nQuantity: " + b.quantity);
  }// AUTOMATIC SAVE

  void saveAutomatically() {
    try {
      store.saveData();
    } catch (IOException ex) {
      showError("Could not save records!");
    }
  }// CLEAR BOOK FIELDS

  void clearBookFields() {
    idField.setText("");
    titleField.setText("");
    authorField.setText("");
    priceField.setText("");
    quantityField.setText("");
  }// SHOW ERROR MESSAGE

  void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }// MAIN METHOD

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new BookstoreManagement());
  }
}
