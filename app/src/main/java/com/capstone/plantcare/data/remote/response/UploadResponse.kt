package com.capstone.plantcare.data.remote.response

import com.google.gson.annotations.SerializedName

data class UploadResponse(

	@field:SerializedName("treatment")
	val treatment: String? = null,

	@field:SerializedName("disease")
	val disease: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("probability")
	val probability: Float? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("scanned_data")
	val scannedData: String? = null,

	@field:SerializedName("plant_type")
	val plantType: String? = null
)
