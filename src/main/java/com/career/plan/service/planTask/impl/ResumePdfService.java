package com.career.plan.service.planTask.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.career.plan.entity.StudentProfile;
import com.career.plan.entity.User;
import com.career.plan.mapper.StudentProfileMapper;
import com.career.plan.mapper.UserMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

/**
 * 简历 PDF 生成服务
 * 负责从用户档案数据创建 PDF 文档
 */
@Service
public class ResumePdfService {

    @Autowired
    private StudentProfileMapper profileMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 通过 userId 生成 PDF
     */
    public byte[] generateResumePdf(Long userId) throws Exception {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("未找到 ID 为 " + userId + " 的用户信息");
        }
        return generateResumePdf(user);
    }

    /**
     * 通过 User 实体生成 PDF
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
