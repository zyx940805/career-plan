package com.career.plan.controller;

import com.career.plan.common.Result;
import com.career.plan.entity.PlanTask;
import com.career.plan.entity.ShareRecord;
import com.career.plan.service.agent.ReportAgentService;
import com.career.plan.service.agent.report.MarkdownReportGenerator;
import com.career.plan.service.planTask.PlanTaskService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 报告导出与分享接口
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private PlanTaskService planTaskService;

    @Autowired
    private ReportAgentService reportAgentService;

    @Autowired
    private MarkdownReportGenerator markdownReportGenerator;

    /**
     * 生成 PDF 报告
     * POST /api/report/export/pdf
     */
    @PostMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(@RequestBody Map<String, Object> request) {
        try {
            String taskId = (String) request.get("taskId");

            // 获取任务信息
            PlanTask task = planTaskService.getOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PlanTask>()
                            .eq(PlanTask::getTaskId, taskId)
            );

            if (task == null) {
                throw new RuntimeException("未找到任务：" + taskId);
            }

            if (task.getFinalReport() == null) {
                throw new RuntimeException("任务尚未完成，无法生成 PDF");
            }

            // 生成 PDF
            byte[] pdfBytes = generatePlanningReportPdf(task);

            String fileName = "planning_report_" + taskId + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 生成分享卡片
     * POST /api/report/share
     */
    @PostMapping("/share")
    public Result<Map<String, Object>> shareReport(@RequestBody Map<String, Object> request) {
        try {
            String taskId = (String) request.get("taskId");
            String shareType = (String) request.get("shareType"); // card / link

            // 获取任务信息
            PlanTask task = planTaskService.getOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PlanTask>()
                            .eq(PlanTask::getTaskId, taskId)
            );

            if (task == null) {
                return Result.error(404, "未找到任务");
            }

            if (task.getFinalReport() == null) {
                return Result.error(400, "任务尚未完成，无法分享");
            }

            // 生成分享数据
            Map<String, Object> shareData = generateShareCardData(task);

            // 保存分享记录
            ShareRecord shareRecord = new ShareRecord();
            shareRecord.setTaskId(taskId);
            shareRecord.setShareType(shareType != null ? shareType : "card");
            shareRecord.setShareUrl("/share/" + taskId);
            shareRecord.setCreatedAt(LocalDateTime.now());

            // 这里应该保存到数据库，但 ShareRecordMapper 需要注入
            // shareRecordMapper.insert(shareRecord);

            Map<String, Object> result = new HashMap<>();
            result.put("shareUrl", "https://your-domain.com/share/" + taskId);
            result.put("shareType", shareType != null ? shareType : "card");
            result.put("shareData", shareData);
            result.put("expiresAt", LocalDateTime.now().plusDays(7)); // 7 天有效期

            return Result.success(result);

        } catch (Exception e) {
            return Result.error(500, "生成分享卡片失败：" + e.getMessage());
        }
    }

    /**
     * 生成职业规划报告 PDF
     */
    private byte[] generatePlanningReportPdf(PlanTask task) throws Exception {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        // 设置中文字体
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font titleFont = new Font(bfChinese, 18, Font.BOLD);
        Font headerFont = new Font(bfChinese, 14, Font.BOLD);
        Font contentFont = new Font(bfChinese, 12, Font.NORMAL);
        Font smallFont = new Font(bfChinese, 10, Font.NORMAL);

        // 标题
        Paragraph title = new Paragraph("职业规划分析报告", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        // 报告基本信息
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(10f);

        addCell(infoTable, "报告编号", task.getTaskId(), smallFont);
        addCell(infoTable, "生成时间", formatDateTime(task.getCreatedAt()), smallFont);
        if (task.getCompletedAt() != null) {
            addCell(infoTable, "完成时间", formatDateTime(task.getCompletedAt()), smallFont);
        }
        if (task.getMatchScore() != null) {
            addCell(infoTable, "匹配评分", task.getMatchScore() + "分", smallFont);
        }

        document.add(infoTable);
        document.add(new Paragraph("\n"));

        // 执行摘要
        if (task.getSummary() != null) {
            document.add(new Paragraph("📋 执行摘要", headerFont));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(task.getSummary(), contentFont));
            document.add(new Paragraph("\n"));
        }

        // 推荐岗位
        if (task.getTopRole() != null) {
            document.add(new Paragraph("🎯 推荐岗位", headerFont));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(task.getTopRole(), contentFont));
            document.add(new Paragraph("\n"));
        }

        // QA 评分
        if (task.getQaScore() != null) {
            document.add(new Paragraph("✅ 质量评分", headerFont));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("报告质量评分：" + task.getQaScore() + "分", contentFont));
            document.add(new Paragraph("\n"));
        }

        // 最终报告内容（如果是 Markdown 格式，需要转换）
        if (task.getFinalReport() != null) {
            document.add(new Paragraph("📖 详细报告", headerFont));
            document.add(new Paragraph("\n"));

            // 从 Map 中提取 Markdown 内容
            Object markdownObj = task.getFinalReport().get("markdown");
            if (markdownObj != null) {
                String markdown = markdownObj.toString();
                // 简单处理 Markdown，实际应该用专门的解析器
                String plainText = convertMarkdownToPlainText(markdown);
                document.add(new Paragraph(plainText, smallFont));
            }
        }

        // 页脚
        document.add(new Paragraph("\n\n"));
        document.add(new Paragraph("---", smallFont));
        document.add(new Paragraph("本报告由职业规划系统自动生成，仅供参考", smallFont));

        document.close();
        return baos.toByteArray();
    }

    /**
     * 生成分享卡片数据
     */
    private Map<String, Object> generateShareCardData(PlanTask task) {
        Map<String, Object> cardData = new HashMap<>();

        // 基本信息
        cardData.put("taskId", task.getTaskId());
        cardData.put("title", "职业规划分析报告");
        cardData.put("generatedAt", formatDateTime(task.getCreatedAt()));

        // 核心数据
        Map<String, Object> stats = new HashMap<>();
        if (task.getMatchScore() != null) {
            stats.put("matchScore", task.getMatchScore());
        }
        if (task.getQaScore() != null) {
            stats.put("qaScore", task.getQaScore());
        }
        stats.put("status", task.getStatus() == 1 ? "已完成" : "处理中");
        cardData.put("statistics", stats);

        // 推荐内容
        if (task.getTopRole() != null) {
            cardData.put("topRecommendation", task.getTopRole());
        }
        if (task.getSummary() != null) {
            cardData.put("summary", task.getSummary());
        }

        // 卡片样式
        cardData.put("cardType", "professional");
        cardData.put("themeColor", "#4A90E2");

        // 二维码数据（如果需要）
        cardData.put("qrCodeData", "https://your-domain.com/share/" + task.getTaskId());

        return cardData;
    }

    /**
     * 辅助方法：向 PDF 表格添加单元格
     */
    private void addCell(PdfPTable table, String key, String value, Font font) {
        PdfPCell cellKey = new PdfPCell(new Phrase(key, font));
        PdfPCell cellValue = new PdfPCell(new Phrase(value != null ? value : "", font));
        cellKey.setPadding(5);
        cellValue.setPadding(5);
        table.addCell(cellKey);
        table.addCell(cellValue);
    }

    /**
     * 辅助方法：格式化日期时间
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    /**
     * 辅助方法：将 Markdown 转换为纯文本（简化版）
     */
    private String convertMarkdownToPlainText(String markdown) {
        if (markdown == null) return "";

        // 移除 Markdown 标记
        String text = markdown.replaceAll("#+", ""); // 移除标题标记
        text = text.replaceAll("\\*\\*", ""); // 移除粗体标记
        text = text.replaceAll("\\*", ""); // 移除斜体标记
        text = text.replaceAll("`", ""); // 移除代码标记
        text = text.replaceAll("\\[([^\\]]+)\\]\\([^)]+\\)", "$1"); // 处理链接

        return text;
    }
}

