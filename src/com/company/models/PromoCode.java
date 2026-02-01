package com.company.models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class PromoCode {
    private final int id;
    private final String code;
    private final int discountPercent;
    private final BigDecimal discountAmount;
    private final boolean active;
    private final Timestamp expiresAt;
    private final Integer usageLimit;
    private final int usedCount;

    public PromoCode(int id, String code, int discountPercent, BigDecimal discountAmount,
                     boolean active, Timestamp expiresAt, Integer usageLimit, int usedCount) {
        this.id = id;
        this.code = code;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.active = active;
        this.expiresAt = expiresAt;
        this.usageLimit = usageLimit;
        this.usedCount = usedCount;
    }

    public int getId() { return id; }
    public String getCode() { return code; }
    public int getDiscountPercent() { return discountPercent; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public boolean isActive() { return active; }
    public Timestamp getExpiresAt() { return expiresAt; }
    public Integer getUsageLimit() { return usageLimit; }
    public int getUsedCount() { return usedCount; }

    public boolean isExpiredNow() {
        return expiresAt != null && expiresAt.before(new Timestamp(System.currentTimeMillis()));
    }

    public boolean limitReached() {
        return usageLimit != null && usedCount >= usageLimit;
    }
}