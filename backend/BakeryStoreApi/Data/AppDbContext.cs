using BakeryStoreApi.Models;
using Microsoft.EntityFrameworkCore;

namespace BakeryStoreApi.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
        {
        }

        public DbSet<User> Users => Set<User>();
        public DbSet<Category> Categories => Set<Category>();
        public DbSet<Product> Products => Set<Product>();
        public DbSet<CartItem> CartItems => Set<CartItem>();
        public DbSet<Address> Addresses => Set<Address>();
        public DbSet<Payment> Payments => Set<Payment>();
        public DbSet<Review> Reviews => Set<Review>();
        public DbSet<Order> Orders => Set<Order>();
        public DbSet<OrderDetail> OrderDetails => Set<OrderDetail>();

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // =========================
            // USERS
            // =========================
            modelBuilder.Entity<User>(entity =>
            {
                entity.ToTable("Users");

                entity.HasKey(x => x.UserId);

                entity.Property(x => x.FullName)
                    .HasMaxLength(100)
                    .IsRequired();

                entity.Property(x => x.Email)
                    .HasMaxLength(100)
                    .IsRequired();

                entity.HasIndex(x => x.Email)
                    .IsUnique();

                entity.Property(x => x.PasswordHash)
                    .HasMaxLength(255)
                    .IsRequired();

                entity.Property(x => x.Phone)
                    .HasMaxLength(20);

                entity.Property(x => x.Role)
                    .HasMaxLength(20)
                    .HasDefaultValue("User");

                entity.Property(x => x.CreatedAt)
                    .HasDefaultValueSql("GETDATE()");
            });

            // =========================
            // CATEGORIES
            // =========================
            modelBuilder.Entity<Category>(entity =>
            {
                entity.ToTable("Categories");

                entity.HasKey(x => x.CategoryId);

                entity.Property(x => x.CategoryName)
                    .HasMaxLength(100)
                    .IsRequired();

                entity.Property(x => x.Description);
            });

            // =========================
            // PRODUCTS
            // =========================
            modelBuilder.Entity<Product>(entity =>
            {
                entity.ToTable("Products");

                entity.HasKey(x => x.ProductId);

                entity.Property(x => x.ProductName)
                    .HasMaxLength(150)
                    .IsRequired();

                entity.Property(x => x.Description);

                entity.Property(x => x.Price)
                    .HasColumnType("decimal(18,2)")
                    .IsRequired();

                entity.Property(x => x.ImageUrl)
                    .HasMaxLength(255);

                entity.Property(x => x.Stock)
                    .HasDefaultValue(0);

                entity.Property(x => x.CreatedAt)
                    .HasDefaultValueSql("GETDATE()");

                entity.HasOne(x => x.Category)
                    .WithMany(x => x.Products)
                    .HasForeignKey(x => x.CategoryId)
                    .OnDelete(DeleteBehavior.SetNull);
            });

            // =========================
            // CART ITEMS
            // =========================
            modelBuilder.Entity<CartItem>(entity =>
            {
                entity.ToTable("CartItems");

                entity.HasKey(x => x.CartItemId);

                entity.Property(x => x.Quantity)
                    .HasDefaultValue(1)
                    .IsRequired();

                entity.Property(x => x.CreatedAt)
                    .HasDefaultValueSql("GETDATE()");

                entity.HasOne(x => x.User)
                    .WithMany(x => x.CartItems)
                    .HasForeignKey(x => x.UserId)
                    .OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(x => x.Product)
                    .WithMany(x => x.CartItems)
                    .HasForeignKey(x => x.ProductId)
                    .OnDelete(DeleteBehavior.Cascade);
            });

            // =========================
            // ADDRESSES
            // =========================
            modelBuilder.Entity<Address>(entity =>
            {
                entity.ToTable("Addresses");

                entity.HasKey(x => x.AddressId);

                entity.Property(x => x.Street)
                    .HasMaxLength(255);

                entity.Property(x => x.City)
                    .HasMaxLength(100);

                entity.Property(x => x.District)
                    .HasMaxLength(100);

                entity.Property(x => x.Ward)
                    .HasMaxLength(100);

                entity.Property(x => x.Phone)
                    .HasMaxLength(20);

                entity.HasOne(x => x.User)
                    .WithMany(x => x.Addresses)
                    .HasForeignKey(x => x.UserId)
                    .OnDelete(DeleteBehavior.Cascade);
            });

            // =========================
            // ORDERS
            // =========================
            modelBuilder.Entity<Order>(entity =>
            {
                entity.ToTable("Orders");

                entity.HasKey(x => x.OrderId);

                entity.Property(x => x.TotalAmount)
                    .HasColumnType("decimal(18,2)")
                    .IsRequired();

                entity.Property(x => x.Status)
                    .HasMaxLength(50)
                    .HasDefaultValue("Pending");

                entity.Property(x => x.DeliveryDate);

                entity.Property(x => x.CreatedAt)
                    .HasDefaultValueSql("GETDATE()");

                entity.HasOne(x => x.User)
                    .WithMany(x => x.Orders)
                    .HasForeignKey(x => x.UserId)
                    .OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(x => x.ShippingAddress)
                    .WithMany(x => x.Orders)
                    .HasForeignKey(x => x.ShippingAddressId)
                    .OnDelete(DeleteBehavior.Restrict);

                // Orders.PaymentId đang tồn tại trong DB,
                // nhưng không map navigation Payment ở đây để tránh vòng quan hệ
                // vì Payments cũng có OrderId.
                entity.Property(x => x.PaymentId);
            });

            // =========================
            // ORDER DETAILS
            // =========================
            modelBuilder.Entity<OrderDetail>(entity =>
            {
                entity.ToTable("OrderDetails");

                entity.HasKey(x => x.OrderDetailId);

                entity.Property(x => x.Quantity)
                    .IsRequired();

                entity.Property(x => x.UnitPrice)
                    .HasColumnType("decimal(18,2)")
                    .IsRequired();

                entity.HasOne(x => x.Order)
                    .WithMany(x => x.OrderDetails)
                    .HasForeignKey(x => x.OrderId)
                    .OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(x => x.Product)
                    .WithMany(x => x.OrderDetails)
                    .HasForeignKey(x => x.ProductId)
                    .OnDelete(DeleteBehavior.Restrict);
            });

            // =========================
            // PAYMENTS
            // =========================
            modelBuilder.Entity<Payment>(entity =>
            {
                entity.ToTable("Payments");

                entity.HasKey(x => x.PaymentId);

                entity.Property(x => x.Method)
                    .HasMaxLength(50);

                entity.Property(x => x.Status)
                    .HasMaxLength(50)
                    .HasDefaultValue("Pending");

                entity.Property(x => x.PaidAt);

                entity.HasOne(x => x.Order)
                    .WithMany()
                    .HasForeignKey(x => x.OrderId)
                    .OnDelete(DeleteBehavior.Cascade);
            });

            // =========================
            // REVIEWS
            // =========================
            modelBuilder.Entity<Review>(entity =>
            {
                entity.ToTable("Reviews");

                entity.HasKey(x => x.ReviewId);

                entity.Property(x => x.Rating)
                    .IsRequired();

                entity.Property(x => x.Comment);

                entity.Property(x => x.CreatedAt)
                    .HasDefaultValueSql("GETDATE()");

                entity.HasCheckConstraint("CK_Reviews_Rating", "[Rating] BETWEEN 1 AND 5");

                entity.HasOne(x => x.Product)
                    .WithMany(x => x.Reviews)
                    .HasForeignKey(x => x.ProductId)
                    .OnDelete(DeleteBehavior.Cascade);

                entity.HasOne(x => x.User)
                    .WithMany(x => x.Reviews)
                    .HasForeignKey(x => x.UserId)
                    .OnDelete(DeleteBehavior.Cascade);
            });
        }
    }
}