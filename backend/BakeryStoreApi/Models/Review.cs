using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BakeryStoreApi.Models
{
    [Table("Reviews")]
    public class Review
    {
        [Key]
        public int ReviewId { get; set; }

        public int ProductId { get; set; }

        public int UserId { get; set; }

        public int Rating { get; set; }

        public string? Comment { get; set; }

        public DateTime CreatedAt { get; set; }

        [ForeignKey("ProductId")]
        public Product? Product { get; set; }

        [ForeignKey("UserId")]
        public User? User { get; set; }
    }
}