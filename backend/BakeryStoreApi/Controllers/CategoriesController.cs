using BakeryStoreApi.Data;
using BakeryStoreApi.DTOs;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace BakeryStoreApi.Controllers
{
    [Route("api/categories")]
    [ApiController]
    public class CategoriesController : ControllerBase
    {
        private readonly AppDbContext _context;

        public CategoriesController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<List<CategoryResponse>>> GetCategories()
        {
            var categories = await _context.Categories
                .OrderBy(x => x.CategoryId)
                .Select(x => new CategoryResponse
                {
                    CategoryId = x.CategoryId,
                    CategoryName = x.CategoryName,
                    Description = x.Description
                })
                .ToListAsync();

            return Ok(categories);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<CategoryResponse>> GetCategoryById(int id)
        {
            var category = await _context.Categories
                .Where(x => x.CategoryId == id)
                .Select(x => new CategoryResponse
                {
                    CategoryId = x.CategoryId,
                    CategoryName = x.CategoryName,
                    Description = x.Description
                })
                .FirstOrDefaultAsync();

            if (category == null)
            {
                return NotFound(new MessageResponse
                {
                    Message = "Không tìm thấy danh mục"
                });
            }

            return Ok(category);
        }
    }
}