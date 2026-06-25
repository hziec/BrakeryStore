namespace BakeryStoreApi.DTOs
{
    public class CartItemResponse
    {
        public int CartItemId { get; set; }

        public int UserId { get; set; }

        public int ProductId { get; set; }

        public string ProductName { get; set; } = string.Empty;

        public string? Description { get; set; }

        public string? ImageUrl { get; set; }

        public decimal Price { get; set; }

        public int Quantity { get; set; }

        public decimal Total { get; set; }
    }
}