package com.asktown.cupboard.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val user_id: String,
    val displayName: String,
    var email: String,
    var security_level: String,
    var profile_image: String,
    var phone: String
)
