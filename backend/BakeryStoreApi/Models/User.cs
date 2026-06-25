using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BakeryStoreApi.Models
{
    [Table("Users")]
    public class User
    {
        [Key]
        public int UserId { get; set; }

        [Required]
        [MaxLength(100)]
        public string FullName { get; set; } = string.Empty;

        [Required]
        [MaxLength(100)]
        public string Email { get; set; } = string.Empty;

        [Required]
        [MaxLength(255)]
        public string PasswordHash { get; set; } = string.Empty;

        [MaxLength(20)]
        public string? Phone { get; set; }

        [MaxLength(20)]
        public string? Role { get; set; } = "User";

        public DateTime CreatedAt { get; set; }

        public List<Order> Orders { get; set; } = new List<Order>();

        public List<CartItem> CartItems { get; set; } = new List<CartItem>();

        public List<Address> Addresses { get; set; } = new List<Address>();

        public List<Review> Reviews { get; set; } = new List<Review>();
    }
}