using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BakeryStoreApi.Models
{
    [Table("CartItems")]
    public class CartItem
    {
        [Key]
        public int CartItemId { get; set; }

        public int UserId { get; set; }

        public int ProductId { get; set; }

        public int Quantity { get; set; } = 1;

        public DateTime CreatedAt { get; set; }

        [ForeignKey("UserId")]
        public User? User { get; set; }

        [ForeignKey("ProductId")]
        public Product? Product { get; set; }
    }
}