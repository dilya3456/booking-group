package com.company.pricing;

import java.util.ArrayList;
import java.util.List;

public class PriceRuleFactory {

    public static List<PriceRule> defaultRules() {
        List<PriceRule> rules = new ArrayList<>();
        rules.add(new ClassMultiplierRule());
        rules.add(new BaggageRule());
        rules.add(new InsuranceRule());
        return rules;
    }
}