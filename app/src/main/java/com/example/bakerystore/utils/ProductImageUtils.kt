package com.example.bakerystore.utils

object ProductImageUtils {

    private const val APP_PACKAGE = "com.example.bakerystore"

    fun getProductImageUrl(productName: String, apiImageUrl: String?): String {
        if (!apiImageUrl.isNullOrBlank()) {
            return convertImageNameToDrawableUri(apiImageUrl)
        }

        val imageFileName = getImageFileNameByProductName(productName)
        return convertImageNameToDrawableUri(imageFileName)
    }

    private fun convertImageNameToDrawableUri(imageName: String): String {
        val cleanName = imageName
            .trim()
            .lowercase()
            .replace(".jpg", "")
            .replace(".jpeg", "")
            .replace(".png", "")
            .replace("-", "_")
            .replace(" ", "_")

        return "android.resource://$APP_PACKAGE/drawable/$cleanName"
    }

    private fun getImageFileNameByProductName(productName: String): String {
        val name = productName.lowercase().trim()

        return when {
            // ProductId 1
            name.contains("bánh kem dâu") || name.contains("kem dâu") ->
                "banh_kem_dau.jpg"

            // ProductId 2
            name.contains("bánh kem socola") || name.contains("kem socola") ->
                "banh_kem_socola.jpg"

            // ProductId 3
            name.contains("tiramisu") ->
                "banh_tiramisu.jpg"

            // ProductId 4
            name.contains("bơ tỏi") ->
                "banh_mi_bo_toi.jpg"

            // ProductId 5
            name.contains("quy bơ") ->
                "banh_quy_bo.jpg"

            // ProductId 6
            name.contains("kem matcha") ->
                "banh_kem_matcha.jpg"

            // ProductId 7
            name.contains("kem phô mai") || name.contains("kem pho mai") ->
                "banh_kem_pho_mai.jpg"

            // ProductId 8
            name.contains("kem trái cây") || name.contains("kem trai cay") ->
                "banh_kem_trai_cay.jpg"

            // ProductId 9
            name.contains("bông lan cắt lát") || name.contains("bong lan cat lat") ->
                "banh_bong_lan_cat_lat.jpg"

            // ProductId 10
            name.contains("cuộn socola") || name.contains("cuon socola") ->
                "banh_cuon_socola.jpg"

            // ProductId 11
            name.contains("cuộn dâu") || name.contains("cuon dau") ->
                "banh_cuon_dau.jpg"

            // ProductId 12
            name.contains("cuộn matcha") || name.contains("cuon matcha") ->
                "banh_cuon_matcha.jpg"

            // ProductId 13
            name.contains("cuộn kem phô mai") || name.contains("cuon kem pho mai") ->
                "banh_cuon_kem_pho_mai.jpg"

            // ProductId 14
            name.contains("pudding caramel") || name.contains("caramel") ->
                "pudding_caramel.jpg"

            // ProductId 15
            name.contains("mousse socola") ->
                "mousse_socola.jpg"

            // ProductId 16
            name.contains("mousse dâu") || name.contains("mousse dau") ->
                "mousse_dau.jpg"

            // ProductId 17
            name.contains("cheesecake") ->
                "cheesecake_lanh.jpg"

            // ProductId 18
            name.contains("sandwich") ->
                "banh_mi_sandwich.jpg"

            // ProductId 19
            name.contains("chà bông") || name.contains("cha bong") ->
                "banh_mi_cha_bong.jpg"

            // ProductId 20
            name.contains("bánh mì phô mai") || name.contains("banh mi pho mai") ->
                "banh_mi_pho_mai.jpg"

            // ProductId 21
            name.contains("trứng muối") || name.contains("trung muoi") ->
                "banh_mi_trung_muoi.jpg"

            // ProductId 22
            name.contains("socola chip") ->
                "banh_quy_socola_chip.jpg"

            // ProductId 23
            name.contains("hạnh nhân") || name.contains("hanh nhan") ->
                "banh_quy_hanh_nhan.jpg"

            // ProductId 24
            name.contains("quy dừa") || name.contains("quy dua") ->
                "banh_quy_dua.jpg"

            // ProductId 25
            name.contains("trà xanh") || name.contains("tra xanh") ->
                "banh_quy_tra_xanh.jpg"

            else ->
                "banh_kem_dau.jpg"
        }
    }
}