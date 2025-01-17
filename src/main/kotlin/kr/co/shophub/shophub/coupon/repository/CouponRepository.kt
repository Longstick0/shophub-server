package kr.co.shophub.shophub.coupon.repository

import kr.co.shophub.shophub.coupon.model.Coupon
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CouponRepository : JpaRepository<Coupon, Long>, CouponRepositoryCustom{

    @Query("""
        SELECT c
        FROM Coupon c
        JOIN FETCH c.shop
        WHERE c.id = :couponId
    """)
    fun findByCouponId(couponId: Long): Coupon?
}