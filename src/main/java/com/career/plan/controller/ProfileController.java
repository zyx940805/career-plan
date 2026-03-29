package com.career.plan.controller;

import com.career.plan.common.Result;
import com.career.plan.entity.StudentProfile;
import com.career.plan.service.planTask.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 简历管理接口
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    /**
     * 保存/更新学生简历信息
     * POST /api/profile/update
     */
    @PostMapping("/update")
    public Result<Long> updateProfile(@RequestBody StudentProfile profile) {
        try {
            boolean success = profileService.saveOrUpdateProfile(profile);
            if (success) {
                return Result.success(profile.getId());
            } else {
                return Result.error(500, "保存失败");
            }
        } catch (Exception e) {
            return Result.error(500, "保存失败：" + e.getMessage());
        }
    }

    /**
     * 获取学生画像
     * GET /api/profile/get?userId=xxx
     */
    @GetMapping("/get")
    public Result<Map<String, Object>> getProfile(@RequestParam Long userId) {
        try {
            StudentProfile profile = profileService.getByUserId(userId);

            Map<String, Object> result = new HashMap<>();
            if (profile != null) {
                result.put("id", profile.getId());
                result.put("userId", profile.getUserId());
                result.put("basicInfo", profile.getBasicInfo());
                result.put("hardSkills", profile.getHardSkills());
                result.put("softSkills", profile.getSoftSkills());
                result.put("internshipExperience", profile.getInternshipExperience());
                result.put("employmentIntent", profile.getEmploymentIntent());
                result.put("completenessScore", profile.getCompletenessScore());
                result.put("competitivenessScore", profile.getCompetitivenessScore());
                result.put("missingFields", profile.getMissingFields());
                result.put("updatedAt", profile.getUpdatedAt());
            } else {
                result.put("message", "未找到该用户的简历信息");
            }

            return Result.success(result);
        } catch (Exception e) {
            return Result.error(500, "获取失败：" + e.getMessage());
        }
    }
}
