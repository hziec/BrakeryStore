using BakeryStoreApi.Data;
using BakeryStoreApi.DTOs;
using BakeryStoreApi.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace BakeryStoreApi.Controllers
{
    [Route("api/cart")]
    [ApiController]
    public class CartController : ControllerBase
    {
        private readonly AppDbContext _context;

        public CartController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet("user/{userId}")]
        public async Task<ActionResult<CartResponse>> GetCartByUser(int userId)
        {
            var userExists = await _context.Users.AnyAsync(x => x.UserId == userId);

            if (!userExists)
            {
                return NotFound(new MessageResponse
                {
                    Message = "Người dùng không tồn tại"
                });
            }

            var items = await _context.CartItems
                .Include(x => x.Product)
                .Where(x => x.UserId == userId)
                .OrderByDescending(x => x.CartItemId)
                .Select(x => new CartItemResponse
                {
                    CartItemId = x.CartItemId,
                    UserId = x.UserId,
                    ProductId = x.ProductId,
                    ProductName = x.Product != null ? x.Product.ProductName : "",
                    Description = x.Product != null ? x.Product.Description : null,
                    ImageUrl = x.Product != null ? x.Product.ImageUrl : null,
                    Price = x.Product != null ? x.Product.Price : 0,
                    Quantity = x.Quantity,
                    Total = x.Product != null ? x.Product.Price * x.Quantity : 0
                })
                .ToListAsync();

            return Ok(new CartResponse
            {
                Items = items,
                TotalAmount = items.Sum(x => x.Total)
            });
        }

        [HttpPost("add")]
        public async Task<ActionResult<MessageResponse>> AddToCart(AddToCartRequest request)
        {
            if (request.UserId <= 0 || request.ProductId <= 0)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Dữ liệu giỏ hàng không hợp lệ"
                });
            }

            if (request.Quantity <= 0)
            {
                request.Quantity = 1;
            }

            var userExists = await _context.Users.AnyAsync(x => x.UserId == request.UserId);

            if (!userExists)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Người dùng không tồn tại"
                });
            }

            var product = await _context.Products
                .FirstOrDefaultAsync(x => x.ProductId == request.ProductId);

            if (product == null)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Sản phẩm không tồn tại"
                });
            }

            if (product.Stock <= 0)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Sản phẩm đã hết hàng"
                });
            }

            if (request.Quantity > product.Stock)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Số lượng vượt quá tồn kho"
                });
            }

            var existingItem = await _context.CartItems
                .FirstOrDefaultAsync(x =>
                    x.UserId == request.UserId &&
                    x.ProductId == request.ProductId);

            if (existingItem != null)
            {
                var newQuantity = existingItem.Quantity + request.Quantity;

                if (newQuantity > product.Stock)
                {
                    return BadRequest(new MessageResponse
                    {
                        Message = "Tổng số lượng trong giỏ vượt quá tồn kho"
                    });
                }

                existingItem.Quantity = newQuantity;
            }
            else
            {
                var cartItem = new CartItem
                {
                    UserId = request.UserId,
                    ProductId = request.ProductId,
                    Quantity = request.Quantity,
                    CreatedAt = DateTime.Now
                };

                _context.CartItems.Add(cartItem);
            }

            await _context.SaveChangesAsync();

            return Ok(new MessageResponse
            {
                Message = "Đã thêm vào giỏ hàng"
            });
        }

        [HttpPut("update/{cartItemId}")]
        public async Task<ActionResult<MessageResponse>> UpdateCartItem(
            int cartItemId,
            UpdateCartItemRequest request)
        {
            var cartItem = await _context.CartItems
                .Include(x => x.Product)
                .FirstOrDefaultAsync(x => x.CartItemId == cartItemId);

            if (cartItem == null)
            {
                return NotFound(new MessageResponse
                {
                    Message = "Không tìm thấy sản phẩm trong giỏ"
                });
            }

            if (request.Quantity <= 0)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Số lượng phải lớn hơn 0"
                });
            }

            if (cartItem.Product != null && request.Quantity > cartItem.Product.Stock)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Số lượng vượt quá tồn kho"
                });
            }

            cartItem.Quantity = request.Quantity;

            await _context.SaveChangesAsync();

            return Ok(new MessageResponse
            {
                Message = "Cập nhật giỏ hàng thành công"
            });
        }

        [HttpDelete("remove/{cartItemId}")]
        public async Task<ActionResult<MessageResponse>> RemoveCartItem(int cartItemId)
        {
            var cartItem = await _context.CartItems
                .FirstOrDefaultAsync(x => x.CartItemId == cartItemId);

            if (cartItem == null)
            {
                return NotFound(new MessageResponse
                {
                    Message = "Không tìm thấy sản phẩm trong giỏ"
                });
            }

            _context.CartItems.Remove(cartItem);
            await _context.SaveChangesAsync();

            return Ok(new MessageResponse
            {
                Message = "Đã xóa sản phẩm khỏi giỏ"
            });
        }

        [HttpDelete("clear/{userId}")]
        public async Task<ActionResult<MessageResponse>> ClearCart(int userId)
        {
            var cartItems = await _context.CartItems
                .Where(x => x.UserId == userId)
                .ToListAsync();

            _context.CartItems.RemoveRange(cartItems);
            await _context.SaveChangesAsync();

            return Ok(new MessageResponse
            {
                Message = "Đã xóa toàn bộ giỏ hàng"
            });
        }
    }
}