package com.career.plan.service.agent.match;

public enum MatchDimension {
    SKILL_MATCH("技能匹配", 0.40f),
    EXPERIENCE_MATCH("经验匹配", 0.25f),
    EDUCATION_MATCH("教育匹配", 0.15f),
    SOFT_SKILL_MATCH("软技能匹配", 0.10f),
    CULTURE_FIT("文化匹配", 0.10f);

    private final String name;
    private final float defaultWeight;

    MatchDimension(String name, float defaultWeight) {
        this.name = name;
        this.defaultWeight = defaultWeight;
    }

    public String getName() {
        return name;
    }

    public float getDefaultWeight() {
        return defaultWeight;
    }
}
