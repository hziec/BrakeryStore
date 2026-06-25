namespace BakeryStoreApi.DTOs
{
    public class CheckoutRequest
    {
        public int UserId { get; set; }

        public string ReceiverName { get; set; } = string.Empty;

        public string Phone { get; set; } = string.Empty;

        public string Street { get; set; } = string.Empty;

        public string City { get; set; } = string.Empty;

        public string District { get; set; } = string.Empty;

        public string Ward { get; set; } = string.Empty;

        public string PaymentMethod { get; set; } = "Cash";
    }
}