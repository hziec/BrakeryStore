using BakeryStoreApi.Data;
using BakeryStoreApi.DTOs;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace BakeryStoreApi.Controllers
{
    [Route("api/products")]
    [ApiController]
    public class ProductsController : ControllerBase
    {
        private readonly AppDbContext _context;

        public ProductsController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<List<ProductResponse>>> GetProducts(
            [FromQuery] int? categoryId,
            [FromQuery] string? keyword)
        {
            var query = _context.Products
                .Include(x => x.Category)
                .Include(x => x.Reviews)
                .AsQueryable();

            if (categoryId.HasValue && categoryId.Value > 0)
            {
                query = query.Where(x => x.CategoryId == categoryId.Value);
            }

            if (!string.IsNullOrWhiteSpace(keyword))
            {
                query = query.Where(x =>
                    x.ProductName.Contains(keyword) ||
                    (x.Description != null && x.Description.Contains(keyword)));
            }

            var products = await query
                .OrderByDescending(x => x.ProductId)
                .Select(x => new ProductResponse
                {
                    ProductId = x.ProductId,
                    ProductName = x.ProductName,
                    Description = x.Description,
                    Price = x.Price,
                    ImageUrl = x.ImageUrl,
                    Stock = x.Stock,
                    CategoryId = x.CategoryId,
                    CategoryName = x.Category != null ? x.Category.CategoryName : null,
                    CreatedAt = x.CreatedAt,
                    ReviewCount = x.Reviews.Count,
                    AverageRating = x.Reviews.Count > 0 ? x.Reviews.Average(r => r.Rating) : 0
                })
                .ToListAsync();

            return Ok(products);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<ProductResponse>> GetProductById(int id)
        {
            var product = await _context.Products
                .Include(x => x.Category)
                .Include(x => x.Reviews)
                .Where(x => x.ProductId == id)
                .Select(x => new ProductResponse
                {
                    ProductId = x.ProductId,
                    ProductName = x.ProductName,
                    Description = x.Description,
                    Price = x.Price,
                    ImageUrl = x.ImageUrl,
                    Stock = x.Stock,
                    CategoryId = x.CategoryId,
                    CategoryName = x.Category != null ? x.Category.CategoryName : null,
                    CreatedAt = x.CreatedAt,
                    ReviewCount = x.Reviews.Count,
                    AverageRating = x.Reviews.Count > 0 ? x.Reviews.Average(r => r.Rating) : 0
                })
                .FirstOrDefaultAsync();

            if (product == null)
            {
                return NotFound(new MessageResponse
                {
                    Message = "Không tìm thấy sản phẩm"
                });
            }

            return Ok(product);
        }
    }
}