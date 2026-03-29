package com.career.plan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.career.plan.entity.*;
import com.career.plan.mapper.*;
import com.career.plan.utils.JwtUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

/* -------------------------------------------------------------------------- */
/* UserService 实现                              */
/* -------------------------------------------------------------------------- */
@Service
class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Map<String, Object> login(String username, String password) {
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getPassword, password));
        if (user == null) return null;

        String token = jwtUtils.createToken(user.getId(), user.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        return result;
    }

    @Override
    public Long register(User user) {
        user.setCreatedAt(LocalDateTime.now());
        this.save(user);
        return user.getId();
    }
}

/* -------------------------------------------------------------------------- */
/* ProfileService 实现                            */
/* -------------------------------------------------------------------------- */
@Service
public class ProfileServiceImpl extends ServiceImpl<StudentProfileMapper, StudentProfile> implements ProfileService {
    @Override
    public StudentProfile getByUserId(Long userId) {
        return this.getOne(new LambdaQueryWrapper<StudentProfile>().eq(StudentProfile::getUserId, userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateProfile(StudentProfile profile) {
        profile.setUpdatedAt(LocalDateTime.now());
        return this.saveOrUpdate(profile);
    }

    /**
     * 简历PDF生成服务
     * 负责从用户档案数据创建PDF文档
     */
    @Service
    public static class ResumePdfService {

        @Autowired
        private StudentProfileMapper profileMapper;

        @Autowired
        private UserMapper userMapper;

        /**
         * 重载方法：支持通过 userId 直接生成 PDF
         * 解决 PdfController 中的报错：generateResumePdf(userId)
         */
        public byte[] generateResumePdf(Long userId) throws Exception {
            // 1. 先根据 ID 查询用户信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new RuntimeException("未找到 ID 为 " + userId + " 的用户信息");
            }
            // 2. 调用核心方法生成 PDF
            return generateResumePdf(user);
        }

        /**
         * 核心方法：通过 User 实体生成 PDF
         * @param user 用户实体类
         * @return PDF 字节数组
         */
        public byte[] generateResumePdf(User user) throws Exception {
            if (user == null) {
                throw new IllegalArgumentException("User 对象不能为空");
            }

            // 查询该用户的补充档案信息
            StudentProfile profile = profileMapper.selectOne(
                    new LambdaQueryWrapper<StudentProfile>().eq(StudentProfile::getUserId, user.getId())
            );

            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);

            document.open();

            // 设置中文字体 (iTextAsian.jar 支持)
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(bfChinese, 18, Font.BOLD);
            Font headerFont = new Font(bfChinese, 14, Font.BOLD);
            Font contentFont = new Font(bfChinese, 12, Font.NORMAL);

            // 标题
            Paragraph title = new Paragraph("个人职业规划报告", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // 基本信息表格
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            addCell(table, "用户名", user.getUsername(), contentFont);
            addCell(table, "注册邮箱", user.getEmail() != null ? user.getEmail() : "未绑定", contentFont);

            if (profile != null) {
                addCell(table, "竞争力评分", String.valueOf(profile.getCompetitivenessScore()), contentFont);
                String skills = (profile.getHardSkills() != null) ? String.join(", ", profile.getHardSkills()) : "暂无数据";
                addCell(table, "核心技能", skills, contentFont);
            }

            document.add(table);

            // 实习经历展示
            if (profile != null && profile.getInternshipExperience() != null) {
                document.add(new Paragraph("\n实习/项目经历:", headerFont));
                document.add(new Paragraph(profile.getInternshipExperience(), contentFont));
            }

            document.close();
            return baos.toByteArray();
        }

        /**
         * 辅助方法：向表格添加单元格
         */
        private void addCell(PdfPTable table, String key, String value, Font font) {
            PdfPCell cellKey = new PdfPCell(new Phrase(key, font));
            PdfPCell cellValue = new PdfPCell(new Phrase(value != null ? value : "", font));
            cellKey.setPadding(5);
            cellValue.setPadding(5);
            table.addCell(cellKey);
            table.addCell(cellValue);
        }
    }
}

/* -------------------------------------------------------------------------- */
/* PlanTaskService 实现                            */
/* -------------------------------------------------------------------------- */
@Service
class PlanTaskServiceImpl extends ServiceImpl<PlanTaskMapper, PlanTask> implements PlanTaskService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String startNewTask(Long userId) {
        String taskId = "TASK_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        PlanTask task = new PlanTask();
        task.setTaskId(taskId);
        task.setUserId(userId);
        task.setStatus(0);
        task.setCreatedAt(LocalDateTime.now());
        this.save(task);
        return taskId;
    }

    @Override
    public List<PlanTask> getUserHistory(Long userId) {
        return this.list(new LambdaQueryWrapper<PlanTask>()
                .eq(PlanTask::getUserId, userId)
                .orderByDesc(PlanTask::getCreatedAt));
    }
}