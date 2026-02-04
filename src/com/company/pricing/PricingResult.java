package com.company.pricing;

public class PricingResult {
    private final double base;
    private final double total;
    private final String breakdown;

    public PricingResult(double base, double total, String breakdown) {
        this.base = base;
        this.total = total;
        this.breakdown = breakdown;
    }

    public double getBase() { return base; }
    public double getTotal() { return total; }
    public String getBreakdown() { return breakdown; }
}