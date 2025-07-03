import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Data model classes
class User {
    private int userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;

    // Constructors
    public User() {}

    public User(int userId, String username, String email, String firstName, String lastName, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}

class Product {
    private int productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private int stockQuantity;
    private int categoryId;
    private String categoryName;

    // Constructors
    public Product() {}

    public Product(int productId, String productName, String description, BigDecimal price, 
                  int stockQuantity, int categoryId, String categoryName) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    // Getters and Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}

class Order {
    private int orderId;
    private int userId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;

    // Constructors
    public Order() {}

    public Order(int orderId, int userId, LocalDateTime orderDate, BigDecimal totalAmount, 
                String status, String shippingAddress) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.shippingAddress = shippingAddress;
    }

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                '}';
    }
}

// Database service class
class EcommerceDbService {
    private final String connectionUrl;
    private final String username;
    private final String password;

    public EcommerceDbService(String connectionUrl, String username, String password) {
        this.connectionUrl = connectionUrl;
        this.username = username;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionUrl, username, password);
    }

    // Get all products with their categories
    public List<Product> getProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        
        String query = """
            SELECT p.ProductID, p.ProductName, p.Description, p.Price, 
                   p.StockQuantity, p.CategoryID, c.CategoryName
            FROM Products p
            JOIN Categories c ON p.CategoryID = c.CategoryID
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("ProductID"),
                    rs.getString("ProductName"),
                    rs.getString("Description"),
                    rs.getBigDecimal("Price"),
                    rs.getInt("StockQuantity"),
                    rs.getInt("CategoryID"),
                    rs.getString("CategoryName")
                );
                products.add(product);
            }
        }

        return products;
    }

    // Get user by ID
    public User getUserById(int userId) throws SQLException {
        String query = "SELECT * FROM Users WHERE UserID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("UserID"),
                        rs.getString("Username"),
                        rs.getString("Email"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getTimestamp("CreatedAt").toLocalDateTime()
                    );
                }
            }
        }

        return null;
    }

    // Create a new order
    public int createOrder(int userId, BigDecimal totalAmount, String shippingAddress) throws SQLException {
        String query = """
            INSERT INTO Orders (UserID, TotalAmount, ShippingAddress)
            VALUES (?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userId);
            stmt.setBigDecimal(2, totalAmount);
            stmt.setString(3, shippingAddress);

            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }

        throw new SQLException("Failed to create order, no ID obtained.");
    }

    // Add order item
    public void addOrderItem(int orderId, int productId, int quantity, BigDecimal unitPrice) throws SQLException {
        String query = """
            INSERT INTO OrderItems (OrderID, ProductID, Quantity, UnitPrice)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, orderId);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);
            stmt.setBigDecimal(4, unitPrice);

            stmt.executeUpdate();
        }
    }

    // Update product stock
    public void updateProductStock(int productId, int newStock) throws SQLException {
        String query = "UPDATE Products SET StockQuantity = ? WHERE ProductID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, newStock);
            stmt.setInt(2, productId);

            stmt.executeUpdate();
        }
    }

    // Get orders by user
    public List<Order> getOrdersByUser(int userId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        
        String query = "SELECT * FROM Orders WHERE UserID = ? ORDER BY OrderDate DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                        rs.getInt("OrderID"),
                        rs.getInt("UserID"),
                        rs.getTimestamp("OrderDate").toLocalDateTime(),
                        rs.getBigDecimal("TotalAmount"),
                        rs.getString("Status"),
                        rs.getString("ShippingAddress")
                    );
                    orders.add(order);
                }
            }
        }

        return orders;
    }

    // Get products by category
    public List<Product> getProductsByCategory(int categoryId) throws SQLException {
        List<Product> products = new ArrayList<>();
        
        String query = """
            SELECT p.ProductID, p.ProductName, p.Description, p.Price, 
                   p.StockQuantity, p.CategoryID, c.CategoryName
            FROM Products p
            JOIN Categories c ON p.CategoryID = c.CategoryID
            WHERE p.CategoryID = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, categoryId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product(
                        rs.getInt("ProductID"),
                        rs.getString("ProductName"),
                        rs.getString("Description"),
                        rs.getBigDecimal("Price"),
                        rs.getInt("StockQuantity"),
                        rs.getInt("CategoryID"),
                        rs.getString("CategoryName")
                    );
                    products.add(product);
                }
            }
        }

        return products;
    }
}

// Main application class
public class EcommerceApplication {
    public static void main(String[] args) {
        // Database connection details
        String connectionUrl = "jdbc:mysql://localhost:3306/EcommerceDB";
        String dbUsername = "root";
        String dbPassword = "password";

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            EcommerceDbService service = new EcommerceDbService(connectionUrl, dbUsername, dbPassword);

            // Get all products
            System.out.println("=== Products ===");
            List<Product> products = service.getProducts();
            products.forEach(product -> 
                System.out.println(product.getProductName() + " - $" + product.getPrice() + 
                                 " (" + product.getCategoryName() + ")")
            );

            // Get user by ID
            System.out.println("\n=== User Info ===");
            User user = service.getUserById(1);
            if (user != null) {
                System.out.println("User: " + user.getFirstName() + " " + user.getLastName() + 
                                 " (" + user.getEmail() + ")");
            }

            // Create a new order
            System.out.println("\n=== Creating Order ===");
            int orderId = service.createOrder(1, new BigDecimal("1049.98"), "123 Main St, City, State");
            System.out.println("Created order with ID: " + orderId);

            // Add order items
            service.addOrderItem(orderId, 1, 1, new BigDecimal("999.99")); // Laptop
            service.addOrderItem(orderId, 2, 1, new BigDecimal("49.99"));  // Book
            System.out.println("Added items to order");

            // Get user orders
            System.out.println("\n=== User Orders ===");
            List<Order> userOrders = service.getOrdersByUser(1);
            userOrders.forEach(order -> 
                System.out.println("Order #" + order.getOrderId() + ": $" + order.getTotalAmount() + 
                                 " - " + order.getStatus())
            );

            // Get products by category
            System.out.println("\n=== Electronics Products ===");
            List<Product> electronics = service.getProductsByCategory(1);
            electronics.forEach(product -> 
                System.out.println(product.getProductName() + " - $" + product.getPrice() + 
                                 " (Stock: " + product.getStockQuantity() + ")")
            );

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
