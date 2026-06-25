using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BakeryStoreApi.Models
{
    [Table("Orders")]
    public class Order
    {
        [Key]
        public int OrderId { get; set; }

        public int UserId { get; set; }

        [Column(TypeName = "decimal(18,2)")]
        public decimal TotalAmount { get; set; }

        [MaxLength(50)]
        public string? Status { get; set; } = "Pending";

        public int? ShippingAddressId { get; set; }

        public int? PaymentId { get; set; }

        public DateTime? DeliveryDate { get; set; }

        public DateTime CreatedAt { get; set; }

        [ForeignKey("UserId")]
        public User? User { get; set; }

        [ForeignKey("ShippingAddressId")]
        public Address? ShippingAddress { get; set; }

        public List<OrderDetail> OrderDetails { get; set; } = new List<OrderDetail>();
    }
}