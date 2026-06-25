using BakeryStoreApi.Data;
using BakeryStoreApi.DTOs;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace BakeryStoreApi.Controllers
{
    [Route("api/users")]
    [ApiController]
    public class UsersController : ControllerBase
    {
        private readonly AppDbContext _context;

        public UsersController(AppDbContext context)
        {
            _context = context;
        }

        [HttpGet("{userId}")]
        public async Task<ActionResult<AuthResponse>> GetProfile(int userId)
        {
            var user = await _context.Users
                .FirstOrDefaultAsync(x => x.UserId == userId);

            if (user == null)
            {
                return NotFound(new MessageResponse
                {
                    Message = "Không tìm thấy người dùng"
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

        [HttpPut("{userId}")]
        public async Task<ActionResult<MessageResponse>> UpdateProfile(
            int userId,
            UpdateProfileRequest request)
        {
            var user = await _context.Users
                .FirstOrDefaultAsync(x => x.UserId == userId);

            if (user == null)
            {
                return NotFound(new MessageResponse
                {
                    Message = "Không tìm thấy người dùng"
                });
            }

            if (string.IsNullOrWhiteSpace(request.FullName))
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Họ tên không được để trống"
                });
            }

            user.FullName = request.FullName.Trim();

            // Android sẽ khóa SĐT, nhưng backend vẫn giữ logic này để nếu cần có thể cập nhật.
            if (!string.IsNullOrWhiteSpace(request.Phone))
            {
                user.Phone = request.Phone.Trim();
            }

            await _context.SaveChangesAsync();

            return Ok(new MessageResponse
            {
                Message = "Cập nhật hồ sơ thành công"
            });
        }

        [HttpPut("change-password")]
        public async Task<ActionResult<MessageResponse>> ChangePassword(UpdatePasswordRequest request)
        {
            if (request.UserId <= 0 ||
                string.IsNullOrWhiteSpace(request.OldPassword) ||
                string.IsNullOrWhiteSpace(request.NewPassword) ||
                string.IsNullOrWhiteSpace(request.ConfirmPassword))
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Vui lòng nhập đầy đủ thông tin đổi mật khẩu"
                });
            }

            if (request.NewPassword != request.ConfirmPassword)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Mật khẩu mới và xác nhận mật khẩu không khớp"
                });
            }

            if (request.NewPassword.Length < 6)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Mật khẩu mới phải có ít nhất 6 ký tự"
                });
            }

            var user = await _context.Users
                .FirstOrDefaultAsync(x => x.UserId == request.UserId);

            if (user == null)
            {
                return NotFound(new MessageResponse
                {
                    Message = "Không tìm thấy người dùng"
                });
            }

            if (user.PasswordHash != request.OldPassword)
            {
                return BadRequest(new MessageResponse
                {
                    Message = "Mật khẩu cũ không đúng"
                });
            }

            user.PasswordHash = request.NewPassword;

            await _context.SaveChangesAsync();

            return Ok(new MessageResponse
            {
                Message = "Đổi mật khẩu thành công"
            });
        }
    }
}