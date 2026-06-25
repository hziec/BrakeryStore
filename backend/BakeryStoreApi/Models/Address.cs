using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BakeryStoreApi.Models
{
    [Table("Addresses")]
    public class Address
    {
        [Key]
        public int AddressId { get; set; }

        public int UserId { get; set; }

        [MaxLength(255)]
        public string? Street { get; set; }

        [MaxLength(100)]
        public string? City { get; set; }

        [MaxLength(100)]
        public string? District { get; set; }

        [MaxLength(100)]
        public string? Ward { get; set; }

        [MaxLength(20)]
        public string? Phone { get; set; }

        [ForeignKey("UserId")]
        public User? User { get; set; }

        public List<Order> Orders { get; set; } = new List<Order>();
    }
}