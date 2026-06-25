using BakeryStoreApi.Data;
using BakeryStoreApi.DTOs;
using BakeryStoreApi.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace BakeryStoreApi.Controllers
{
    [Route("api/reviews")]
    [ApiController]
    public class ReviewsController : ControllerBase
    {
        private readonly AppDbContext _context;

        public ReviewsController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet("product/{productId}")]
        public async Task<ActionResult<List<ReviewResponse>>> GetReviewsByProduct(int productId)
        {
            var reviews = await _context.Reviews
                .Include(x => x.User)
                .Where(x => x.ProductId == productId)
                .OrderByDescending(x => x.ReviewId)
                .Select(x => new ReviewResponse
                {
                    ReviewId = x.ReviewId,
                    ProductId = x.ProductId,
                    UserId = x.UserId,
                    FullName = x.User != null ? x.User.FullName : "",
                    Rating = x.Rating,
                    Comment = x.Comment,
                    CreatedAt = x.CreatedAt
                })
                .ToListAsync();

            return Ok(reviews);
        }

        [HttpPost]
        public async Task<ActionResult<MessageResponse>> AddReview(ReviewRequest request)
        {
            if (request.UserId <= 0 || request.ProductId <= 0)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Dữ liệu đánh giá không hợp lệ"
                });
            }

            if (request.Rating < 1 || request.Rating > 5)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Số sao đánh giá phải từ 1 đến 5"
                });
            }

            var userExists = await _context.Users
                .AnyAsync(x => x.UserId == request.UserId);

            if (!userExists)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Người dùng không tồn tại"
                });
            }

            var productExists = await _context.Products
                .AnyAsync(x => x.ProductId == request.ProductId);

            if (!productExists)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Sản phẩm không tồn tại"
                });
            }

            var review = new Review
            {
                UserId = request.UserId,
                ProductId = request.ProductId,
                Rating = request.Rating,
                Comment = request.Comment,
                CreatedAt = DateTime.Now
            };

            _context.Reviews.Add(review);
            await _context.SaveChangesAsync();

            return Ok(new MessageResponse
            {
                Message = "Đánh giá sản phẩm thành công"
            });
        }
    }
}