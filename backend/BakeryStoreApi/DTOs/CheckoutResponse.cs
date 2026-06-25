namespace BakeryStoreApi.DTOs
{
    public class CheckoutResponse
    {
        public string Message { get; set; } = string.Empty;

        public int OrderId { get; set; }

        public decimal TotalAmount { get; set; }

        public string? OrderStatus { get; set; }

        public string? PaymentMethod { get; set; }

        public string? PaymentStatus { get; set; }
    }
}