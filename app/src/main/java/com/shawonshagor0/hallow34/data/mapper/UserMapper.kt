package com.shawonshagor0.hallow34.data.mapper

import com.shawonshagor0.hallow34.data.model.UserDto
import com.shawonshagor0.hallow34.domain.model.User

fun User.toDto(): UserDto {
    return UserDto(
        bpNumber = bpNumber,
        fullName = fullName,
        designation = designation,
        district = district,
        postingPlace = postingPlace,
        bloodGroup = bloodGroup,
        phone = phone,
        email = email,
        password = password,
        imageUrl = imageUrl,
        facebookProfileLink = facebookProfileLink
    )
}

fun UserDto.toDomain(): User {
    return User(
        bpNumber = bpNumber,
        fullName = fullName,
        designation = designation,
        district = district,
        postingPlace = postingPlace,
        bloodGroup = bloodGroup,
        phone = phone,
        email = email,
        password = password,
        imageUrl = imageUrl,
        facebookProfileLink = facebookProfileLink
    )
}
