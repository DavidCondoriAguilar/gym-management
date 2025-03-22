package com.gymmanagement.gym_app.utils;

import com.gymmanagement.gym_app.model.ReportModel;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

@Service
@Slf4j
public class PdfReportService {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");

    public ByteArrayInputStream generateRetentionReportPdf(ReportModel report) {
        Document document = new Document(PageSize.A4, 50, 50, 60, 60); // márgenes: izquierda, derecha, arriba, abajo
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Agregar logo centrado
            addLogo(document);

            // Agregar título con estilo minimalista
            addTitle(document, "Reporte de Retención del Gimnasio");

            // Agregar separación
            document.add(Chunk.NEWLINE);

            // Agregar tabla con el contenido del reporte
            addReportTable(document, report);

            document.close();
        } catch (Exception e) {
            log.error("Error al generar el PDF del reporte de retención", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addLogo(Document document) {
        try {
            Image logo = Image.getInstance(new ClassPathResource("static/laufit.jpg").getURL());
            logo.scaleToFit(80, 80);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
        } catch (IOException | DocumentException e) {
            log.warn("No se encontró la imagen del gimnasio.", e);
        }
    }

    private void addTitle(Document document, String titleText) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new BaseColor(45, 45, 45));
        Paragraph title = new Paragraph(titleText, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
    }

    private void addReportTable(Document document, ReportModel report) throws DocumentException {
        // Configuración de la tabla: 2 columnas para etiqueta y valor
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        table.setWidths(new float[]{3, 2});

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

        // Método auxiliar para agregar filas
        addTableRow(table, "Total Miembros:", String.valueOf(report.getTotalMembers()), labelFont, valueFont);
        addTableRow(table, "Miembros Renovados:", String.valueOf(report.getRenewedMembers()), labelFont, valueFont);
        addTableRow(table, "Nuevos Miembros:", String.valueOf(report.getNewMembers()), labelFont, valueFont);
        addTableRow(table, "Miembros que Cancelaron:", String.valueOf(report.getChurnedMembers()), labelFont, valueFont);
        addTableRow(table, "Tasa de Retención (%):", DECIMAL_FORMAT.format(report.getRetentionRate()), labelFont, valueFont);
        addTableRow(table, "Duración Promedio (meses):", DECIMAL_FORMAT.format(report.getAverageMembershipDurationMonths()), labelFont, valueFont);
        addTableRow(table, "Miembros Activos:", String.valueOf(report.getActiveMembers()), labelFont, valueFont);
        addTableRow(table, "Miembros Inactivos:", String.valueOf(report.getInactiveMembers()), labelFont, valueFont);
        addTableRow(table, "Ingresos Brutos del Mes:", "S/ " + DECIMAL_FORMAT.format(report.getMembershipRevenueThisMonth()), labelFont, valueFont);
        addTableRow(table, "Plan Más Popular:", report.getMostPopularMembershipPlan(), labelFont, valueFont);
        addTableRow(table, "Total Descuentos Aplicados:", "S/ " + DECIMAL_FORMAT.format(report.getTotalDiscountsApplied()), labelFont, valueFont);
        addTableRow(table, "Ingresos Netos del Mes:", "S/ " + DECIMAL_FORMAT.format(report.getNetMembershipRevenueThisMonth()), labelFont, valueFont);

        document.add(table);
    }

    private void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, labelFont));
        cellLabel.setBorder(Rectangle.NO_BORDER);
        cellLabel.setPadding(8);
        table.addCell(cellLabel);

        PdfPCell cellValue = new PdfPCell(new Phrase(value, valueFont));
        cellValue.setBorder(Rectangle.NO_BORDER);
        cellValue.setPadding(8);
        cellValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellValue);
    }
}
