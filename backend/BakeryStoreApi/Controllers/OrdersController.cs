using BakeryStoreApi.Data;
using BakeryStoreApi.DTOs;
using BakeryStoreApi.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace BakeryStoreApi.Controllers
{
    [Route("api/orders")]
    [ApiController]
    public class OrdersController : ControllerBase
    {
        private readonly AppDbContext _context;

        public OrdersController(AppDbContext context)
        {
            _context = context;
        }

        [HttpPost("checkout")]
        public async Task<ActionResult<CheckoutResponse>> Checkout(CheckoutRequest request)
        {
            if (request.UserId <= 0)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "UserId không hợp lệ"
                });
            }

            if (string.IsNullOrWhiteSpace(request.ReceiverName) ||
                string.IsNullOrWhiteSpace(request.Phone) ||
                string.IsNullOrWhiteSpace(request.Street) ||
                string.IsNullOrWhiteSpace(request.City) ||
                string.IsNullOrWhiteSpace(request.District) ||
                string.IsNullOrWhiteSpace(request.Ward))
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Vui lòng nhập đầy đủ thông tin nhận hàng"
                });
            }

            var user = await _context.Users
                .FirstOrDefaultAsync(x => x.UserId == request.UserId);

            if (user == null)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Người dùng không tồn tại"
                });
            }

            var cartItems = await _context.CartItems
                .Include(x => x.Product)
                .Where(x => x.UserId == request.UserId)
                .ToListAsync();

            if (cartItems.Count == 0)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Giỏ hàng đang trống"
                });
            }

            foreach (var cartItem in cartItems)
            {
                if (cartItem.Product == null)
                {
                    return BadRequest(new MessageResponse
                    {
                        Message = "Có sản phẩm không hợp lệ trong giỏ hàng"
                    });
                }

                if (cartItem.Quantity > cartItem.Product.Stock)
                {
                    return BadRequest(new MessageResponse
                    {
                        Message = $"Sản phẩm {cartItem.Product.ProductName} không đủ tồn kho"
                    });
                }
            }

            var address = new Address
            {
                UserId = request.UserId,
                Street = request.Street,
                City = request.City,
                District = request.District,
                Ward = request.Ward,
                Phone = request.Phone
            };

            _context.Addresses.Add(address);
            await _context.SaveChangesAsync();

            decimal totalAmount = 0;

            var order = new Order
            {
                UserId = request.UserId,
                ShippingAddressId = address.AddressId,
                Status = "Pending",
                DeliveryDate = DateTime.Now.AddDays(3),
                CreatedAt = DateTime.Now
            };

            foreach (var cartItem in cartItems)
            {
                if (cartItem.Product == null)
                {
                    continue;
                }

                var orderDetail = new OrderDetail
                {
                    ProductId = cartItem.ProductId,
                    Quantity = cartItem.Quantity,
                    UnitPrice = cartItem.Product.Price
                };

                totalAmount += cartItem.Product.Price * cartItem.Quantity;
                order.OrderDetails.Add(orderDetail);

                cartItem.Product.Stock -= cartItem.Quantity;
            }

            order.TotalAmount = totalAmount;

            _context.Orders.Add(order);
            await _context.SaveChangesAsync();

            var payment = new Payment
            {
                OrderId = order.OrderId,
                Method = string.IsNullOrWhiteSpace(request.PaymentMethod) ? "Cash" : request.PaymentMethod,
                Status = "Pending",
                PaidAt = null
            };

            _context.Payments.Add(payment);
            await _context.SaveChangesAsync();

            order.PaymentId = payment.PaymentId;

            _context.CartItems.RemoveRange(cartItems);

            await _context.SaveChangesAsync();

            return Ok(new CheckoutResponse
            {
                Message = "Đặt hàng thành công",
                OrderId = order.OrderId,
                TotalAmount = order.TotalAmount,
                OrderStatus = order.Status,
                PaymentMethod = payment.Method,
                PaymentStatus = payment.Status
            });
        }

        [HttpGet("user/{userId}")]
        public async Task<ActionResult<List<OrderResponse>>> GetOrdersByUser(int userId)
        {
            var orders = await _context.Orders
                .Include(x => x.ShippingAddress)
                .Where(x => x.UserId == userId)
                .OrderByDescending(x => x.OrderId)
                .ToListAsync();

            var result = new List<OrderResponse>();

            foreach (var order in orders)
            {
                var payment = await _context.Payments
                    .FirstOrDefaultAsync(x => x.PaymentId == order.PaymentId);

                result.Add(new OrderResponse
                {
                    OrderId = order.OrderId,
                    UserId = order.UserId,
                    TotalAmount = order.TotalAmount,
                    Status = order.Status,
                    DeliveryDate = order.DeliveryDate,
                    CreatedAt = order.CreatedAt,
                    PaymentMethod = payment?.Method,
                    PaymentStatus = payment?.Status,
                    ShippingAddress = BuildAddress(order.ShippingAddress)
                });
            }

            return Ok(result);
        }

        [HttpGet("{orderId}")]
        public async Task<ActionResult<OrderDetailResponse>> GetOrderDetail(int orderId)
        {
            var order = await _context.Orders
                .Include(x => x.ShippingAddress)
                .Include(x => x.OrderDetails)
                .ThenInclude(x => x.Product)
                .FirstOrDefaultAsync(x => x.OrderId == orderId);

            if (order == null)
            {
                return NotFound(new MessageResponse
                {
                    Message = "Không tìm thấy đơn hàng"
                });
            }

            var payment = await _context.Payments
                .FirstOrDefaultAsync(x => x.PaymentId == order.PaymentId);

            return Ok(new OrderDetailResponse
            {
                OrderId = order.OrderId,
                UserId = order.UserId,
                TotalAmount = order.TotalAmount,
                Status = order.Status,
                DeliveryDate = order.DeliveryDate,
                CreatedAt = order.CreatedAt,
                PaymentMethod = payment?.Method,
                PaymentStatus = payment?.Status,
                ShippingAddress = BuildAddress(order.ShippingAddress),
                Items = order.OrderDetails.Select(d => new OrderDetailItemResponse
                {
                    ProductId = d.ProductId,
                    ProductName = d.Product != null ? d.Product.ProductName : "",
                    ImageUrl = d.Product != null ? d.Product.ImageUrl : null,
                    Quantity = d.Quantity,
                    UnitPrice = d.UnitPrice,
                    Total = d.Quantity * d.UnitPrice
                }).ToList()
            });
        }

        private static string? BuildAddress(Address? address)
        {
            if (address == null)
            {
                return null;
            }

            return $"{address.Street}, {address.Ward}, {address.District}, {address.City}";
        }
    }
}