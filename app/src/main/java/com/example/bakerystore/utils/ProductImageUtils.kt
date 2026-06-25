package com.example.bakerystore.utils

object ProductImageUtils {
    /**
     * Trả về URL hình ảnh chất lượng cao thực tế từ internet (Unsplash) 
     * dựa trên danh mục và tên sản phẩm mà người dùng yêu cầu.
     */
    fun getProductImageUrl(productName: String, apiImageUrl: String?): String {
        if (!apiImageUrl.isNullOrEmpty()) {
            return apiImageUrl
        }

        val name = productName.lowercase()
        return when {
            // --- BÁNH KEM (Cake) ---
            name.contains("kem dâu") -> "https://images.unsplash.com/photo-1565958011703-44f9829ba187?auto=format&fit=crop&w=800"
            name.contains("kem socola") -> "https://images.unsplash.com/photo-1578985545062-69928b1d9587?auto=format&fit=crop&w=800"
            name.contains("kem matcha") || (name.contains("kem") && name.contains("trà xanh")) -> "https://images.unsplash.com/photo-1582716401301-b2407dc7563d?auto=format&fit=crop&w=800"
            name.contains("kem phô mai") -> "https://images.unsplash.com/photo-1533134242443-d4fd215305ad?auto=format&fit=crop&w=800"
            name.contains("kem trái cây") -> "https://images.unsplash.com/photo-1464349095431-e9a21285b5f3?auto=format&fit=crop&w=800"
            
            // --- BÁNH CẮT LÁT (Slices & Rolls) ---
            name.contains("bông lan") && name.contains("lát") -> "https://images.unsplash.com/photo-1606312619070-d48b4c652a52?auto=format&fit=crop&w=800"
            name.contains("cuộn socola") -> "https://images.unsplash.com/photo-1621236322928-85476635293d?auto=format&fit=crop&w=800"
            name.contains("cuộn dâu") -> "https://images.unsplash.com/photo-1516054144838-f7bc40402206?auto=format&fit=crop&w=800"
            name.contains("cuộn matcha") -> "https://images.unsplash.com/photo-1550617931-e17a7b70dce2?auto=format&fit=crop&w=800"
            name.contains("cuộn phô mai") -> "https://images.unsplash.com/photo-1551404660-67468162870e?auto=format&fit=crop&w=800"

            // --- BÁNH LẠNH & TRÁNG MIỆNG (Cold & Desserts) ---
            name.contains("tiramisu") -> "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?auto=format&fit=crop&w=800"
            name.contains("caramel") || name.contains("pudding") -> "https://images.unsplash.com/photo-1519915028121-7d3463d20b13?auto=format&fit=crop&w=800"
            name.contains("mousse socola") -> "https://images.unsplash.com/photo-1541783245831-57d6fb0926d3?auto=format&fit=crop&w=800"
            name.contains("mousse dâu") -> "https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?auto=format&fit=crop&w=800"
            name.contains("cheesecake") || (name.contains("phô mai") && name.contains("lạnh")) -> "https://images.unsplash.com/photo-1524351199679-46cddf33273a?auto=format&fit=crop&w=800"

            // --- BÁNH MÌ & BÁNH MẶN (Bread & Savory) ---
            name.contains("bơ tỏi") -> "https://images.unsplash.com/photo-1586444248902-2f64eddf13cf?auto=format&fit=crop&w=800"
            name.contains("sandwich") -> "https://images.unsplash.com/photo-1539252554452-da098e21f435?auto=format&fit=crop&w=800"
            name.contains("chà bông") -> "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=800"
            name.contains("bánh mì phô mai") -> "https://images.unsplash.com/photo-1555507036-ab1f4038808a?auto=format&fit=crop&w=800"
            name.contains("trứng muối") -> "https://images.unsplash.com/photo-1512152272829-e3139592d56f?auto=format&fit=crop&w=800"

            // --- BÁNH KHÔ (Cookies) ---
            name.contains("quy bơ") -> "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?auto=format&fit=crop&w=800"
            name.contains("socola chip") -> "https://images.unsplash.com/photo-1558961359-1d99283f085c?auto=format&fit=crop&w=800"
            name.contains("hạnh nhân") -> "https://images.unsplash.com/photo-1511081692775-05d0f180a065?auto=format&fit=crop&w=800"
            name.contains("quy dừa") -> "https://images.unsplash.com/photo-1590080875515-8a03ca09733d?auto=format&fit=crop&w=800"
            name.contains("quy") || name.contains("cookie") || name.contains("matcha") || name.contains("trà xanh") -> "https://images.unsplash.com/photo-1619860860774-1e2e17343432?auto=format&fit=crop&w=800"

            // --- MẶC ĐỊNH (Cho các sản phẩm khác) ---
            name.contains("kem") || name.contains("cake") -> "https://images.unsplash.com/photo-1578985545062-69928b1d9587?auto=format&fit=crop&w=800"
            name.contains("bánh mì") || name.contains("mì") -> "https://images.unsplash.com/photo-1555507036-ab1f4038808a?auto=format&fit=crop&w=800"
            else -> "https://images.unsplash.com/photo-1555507036-ab1f4038808a?auto=format&fit=crop&w=800"
        }
    }
}