using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BakeryStoreApi.Models
{
    [Table("Payments")]
    public class Payment
    {
        [Key]
        public int PaymentId { get; set; }

        public int OrderId { get; set; }

        [MaxLength(50)]
        public string? Method { get; set; }

        [MaxLength(50)]
        public string? Status { get; set; } = "Pending";

        public DateTime? PaidAt { get; set; }

        [ForeignKey("OrderId")]
        public Order? Order { get; set; }
    }
}