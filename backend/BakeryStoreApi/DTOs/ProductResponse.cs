namespace BakeryStoreApi.DTOs
{
    public class ProductResponse
    {
        public int ProductId { get; set; }

        public string ProductName { get; set; } = string.Empty;

        public string? Description { get; set; }

        public decimal Price { get; set; }

        public string? ImageUrl { get; set; }

        public int Stock { get; set; }

        public int? CategoryId { get; set; }

        public string? CategoryName { get; set; }

        public DateTime CreatedAt { get; set; }

        public int ReviewCount { get; set; }

        public double AverageRating { get; set; }
    }
}