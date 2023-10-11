package kr.co.shophub.shophub.user.service

import kr.co.shophub.shophub.follow.repository.FollowRepository
import kr.co.shophub.shophub.global.error.ResourceNotFoundException
import kr.co.shophub.shophub.shop.dto.ShopListResponse
import kr.co.shophub.shophub.shop.dto.ShopSimpleResponse
import kr.co.shophub.shophub.user.dto.*
import kr.co.shophub.shophub.user.model.User
import kr.co.shophub.shophub.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    fun getMyPage(userId: Long): MyPageResponse {
        val user = getUser(userId)
        val shops = followRepository.findByUser(user)
            .map { ShopSimpleResponse(it.shop) }
        val coupons = mutableListOf<String>()
        return MyPageResponse(
            email = user.email,
            followShop = ShopListResponse(shops),
            coupon = coupons
        )
    }

    @Transactional
    fun updateInfo(userId: Long, updateRequest: InfoUpdateRequest) {
        val user = getUser(userId)
        if (updateRequest.nickname != null) {
            user.updateNickname(updateRequest.nickname)
        }
        if (updateRequest.newPassword != null) {
            user.updatePassword(passwordEncoder, updateRequest.newPassword)
        }
    }

    fun checkPassword(request: PasswordRequest, userId: Long) {
        val user = getUser(userId)
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("비밀 번호가 일치하지 않습니다.")
        }
    }

    @Transactional
    fun updatePassword(updateRequest: PasswordUpdateRequest) {
        val user = userRepository.findByEmail(updateRequest.email)
            ?: throw ResourceNotFoundException("유저를 찾을 수 없습니다.")
        user.updatePassword(passwordEncoder, updateRequest.newPassword)
    }

    @Transactional
    fun deleteUser(userId: Long) {
        val user = getUser(userId)
        user.softDelete()
    }

    private fun getUser(userId: Long): User {
        return userRepository.findByIdAndDeletedIsFalse(userId)
            ?: throw ResourceNotFoundException("유저를 찾을 수 없습니다.")
    }
}