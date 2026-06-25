namespace BakeryStoreApi.DTOs
{
    public class OrderResponse
    {
        public int OrderId { get; set; }

        public int UserId { get; set; }

        public decimal TotalAmount { get; set; }

        public string? Status { get; set; }

        public DateTime? DeliveryDate { get; set; }

        public DateTime CreatedAt { get; set; }

        public string? PaymentMethod { get; set; }

        public string? PaymentStatus { get; set; }

        public string? ShippingAddress { get; set; }
    }
}