using BakeryStoreApi.Data;
using BakeryStoreApi.DTOs;
using BakeryStoreApi.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace BakeryStoreApi.Controllers
{
    [Route("api/auth")]
    [ApiController]
    public class AuthController : ControllerBase
    {
        private readonly AppDbContext _context;

        public AuthController(AppDbContext context)
        {
            _context = context;
        }

        [HttpPost("register")]
        public async Task<ActionResult<AuthResponse>> Register(RegisterRequest request)
        {
            if (string.IsNullOrWhiteSpace(request.FullName) ||
                string.IsNullOrWhiteSpace(request.Email) ||
                string.IsNullOrWhiteSpace(request.Password))
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Vui lòng nhập đầy đủ họ tên, email và mật khẩu"
                });
            }

            if (request.Password.Length < 6)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Mật khẩu phải có ít nhất 6 ký tự"
                });
            }

            var emailExists = await _context.Users
                .AnyAsync(x => x.Email == request.Email);

            if (emailExists)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Email đã tồn tại"
                });
            }

            var user = new User
            {
                FullName = request.FullName.Trim(),
                Email = request.Email.Trim(),
                PasswordHash = request.Password,
                Phone = request.Phone,
                Role = "User",
                CreatedAt = DateTime.Now
            };

            _context.Users.Add(user);
            await _context.SaveChangesAsync();

            return Ok(new AuthResponse
            {
                UserId = user.UserId,
                FullName = user.FullName,
                Email = user.Email,
                Phone = user.Phone,
                Role = user.Role
            });
        }

        [HttpPost("login")]
        public async Task<ActionResult<AuthResponse>> Login(LoginRequest request)
        {
            if (string.IsNullOrWhiteSpace(request.Email) ||
                string.IsNullOrWhiteSpace(request.Password))
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Vui lòng nhập email và mật khẩu"
                });
            }

            var user = await _context.Users
                .FirstOrDefaultAsync(x =>
                    x.Email == request.Email &&
                    x.PasswordHash == request.Password);

            if (user == null)
            {
                return Unauthorized(new MessageResponse
                {
                    Message = "Email hoặc mật khẩu không đúng"
                });
            }

            return Ok(new AuthResponse
            {
                UserId = user.UserId,
                FullName = user.FullName,
                Email = user.Email,
                Phone = user.Phone,
                Role = user.Role
            });
        }
    }
}