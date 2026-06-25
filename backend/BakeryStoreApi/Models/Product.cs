using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BakeryStoreApi.Models
{
    [Table("Products")]
    public class Product
    {
        [Key]
        public int ProductId { get; set; }

        [Required]
        [MaxLength(150)]
        public string ProductName { get; set; } = string.Empty;

        public string? Description { get; set; }

        [Column(TypeName = "decimal(18,2)")]
        public decimal Price { get; set; }

        [MaxLength(255)]
        public string? ImageUrl { get; set; }

        public int Stock { get; set; }

        public int? CategoryId { get; set; }

        public DateTime CreatedAt { get; set; }

        [ForeignKey("CategoryId")]
        public Category? Category { get; set; }

        public List<CartItem> CartItems { get; set; } = new List<CartItem>();

        public List<OrderDetail> OrderDetails { get; set; } = new List<OrderDetail>();

        public List<Review> Reviews { get; set; } = new List<Review>();
    }
}