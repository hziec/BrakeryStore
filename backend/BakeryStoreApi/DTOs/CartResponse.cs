namespace BakeryStoreApi.DTOs
{
    public class CartResponse
    {
        public List<CartItemResponse> Items { get; set; } = new List<CartItemResponse>();

        public decimal TotalAmount { get; set; }
    }
}