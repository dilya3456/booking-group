package com.company.services;

import com.company.data.interfaces.IDB;
import com.company.models.PromoCode;
import com.company.repositories.interfaces.IPromoRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;

public class PromoService {
    private final IDB db;
    private final IPromoRepository repo;

    public PromoService(IDB db, IPromoRepository repo) {
        this.db = db;
        this.repo = repo;
    }

    public String applyPromo(int bookingId, String code) {
        if (code == null || code.trim().isEmpty()) return "Promo code is empty.";

        Connection con = null;
        try {
            con = db.getConnection();
            con.setAutoCommit(false);

            PromoCode promo = repo.findValidPromo(con, code);
            if (promo == null) { con.rollback(); return "Promo not found."; }
            if (!promo.isActive()) { con.rollback(); return "Promo is inactive."; }
            if (promo.isExpiredNow()) { con.rollback(); return "Promo is expired."; }
            if (promo.limitReached()) { con.rollback(); return "Promo usage limit reached."; }

            if (repo.wasPromoUsedForBooking(con, promo.getId(), bookingId)) {
                con.rollback();
                return "This promo is already used for booking " + bookingId + ".";
            }

            BigDecimal oldTotal = repo.getBookingTotal(con, bookingId);
            BigDecimal discount = calcDiscount(oldTotal, promo);
            BigDecimal newTotal = oldTotal.subtract(discount);
            if (newTotal.compareTo(BigDecimal.ZERO) < 0) newTotal = BigDecimal.ZERO;

            repo.updateBookingTotal(con, bookingId, newTotal);
            repo.registerUsage(con, promo.getId(), bookingId);

            repo.insertHistory(
                    con,
                    bookingId,
                    "PROMO_APPLIED",
                    "code=" + promo.getCode()
                            + ", oldTotal=" + oldTotal
                            + ", discount=" + discount
                            + ", newTotal=" + newTotal
            );

            con.commit();
            return "Promo applied ✅ code=" + promo.getCode()
                    + " | discount=" + discount
                    + " | newTotal=" + newTotal;

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (Exception ignore) {}
            return "Apply promo failed: " + e.getMessage();
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (Exception ignore) {}
        }
    }

    private BigDecimal calcDiscount(BigDecimal total, PromoCode promo) {
        BigDecimal percent = BigDecimal.valueOf(promo.getDiscountPercent());
        BigDecimal percentDiscount = BigDecimal.ZERO;
        if (percent.compareTo(BigDecimal.ZERO) > 0) {
            percentDiscount = total.multiply(percent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        BigDecimal fixedDiscount = promo.getDiscountAmount() == null ? BigDecimal.ZERO : promo.getDiscountAmount();

        BigDecimal discount = percentDiscount.add(fixedDiscount);
        if (discount.compareTo(total) > 0) discount = total;
        return discount.setScale(2, RoundingMode.HALF_UP);
    }
}
