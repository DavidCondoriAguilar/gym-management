package com.gymmanagement.gym_app.utils;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.MembershipPlan;
import com.gymmanagement.gym_app.domain.Promotion;
import com.gymmanagement.gym_app.domain.Payment;
import com.gymmanagement.gym_app.domain.enums.PaymentStatus;
import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

@Service
@Slf4j
public class PDFGeneratorService {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final BaseColor PRIMARY_COLOR = new BaseColor(41, 128, 185);
    private static final BaseColor SECONDARY_COLOR = new BaseColor(52, 152, 219);
    private static final BaseColor LIGHT_GRAY = new BaseColor(245, 246, 247);
    private static final BaseColor DARK_GRAY = new BaseColor(52, 73, 94);
    private static final String WATERMARK_TEXT = "GYM MANAGEMENT SYSTEM";

    public ByteArrayInputStream generateMemberReport(GymMember member) {
        if (member == null) {
            log.error("El miembro es nulo, no se puede generar el PDF.");
            return new ByteArrayInputStream(new byte[0]);
        }

        Document document = new Document(PageSize.A4, 36, 36, 60, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            
            // Add watermark and page events
            writer.setPageEvent(new WatermarkPageEvent());
            
            document.open();
            
            // Add header with logo and title
            addHeader(document, member);
            
            // Add member information section
            addMemberInfoSection(document, member);
            
            // Add membership details section
            addMembershipInfoSection(document, member);
            
            // Add QR code with member ID
            addQrCode(document, member.getId().toString());
            
            // Add payment history if available
            if (member.getPayments() != null && !member.getPayments().isEmpty()) {
                addPaymentHistorySection(document, member);
            }
            
            // Add promotions if available
            if (member.getPromotions() != null && !member.getPromotions().isEmpty()) {
                addPromotionsSection(document, member);
            }
            
            // Add footer with signature
            addFooter(document);
            
            document.close();
        } catch (Exception ex) {
            log.error("Error al generar el PDF", ex);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addHeader(Document document, GymMember member) throws DocumentException, IOException {
        // Create a table for the header
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 3});
        
        try {
            // Add logo
            Image logo = Image.getInstance(new ClassPathResource("static/laufit.jpg").getURL());
            logo.scaleToFit(80, 80);
            PdfPCell logoCell = new PdfPCell(logo, false);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(logoCell);
        } catch (Exception e) {
            log.warn("No se pudo cargar el logo.", e);
            headerTable.addCell("");
        }
        
        // Add title and member ID
        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setPaddingLeft(10);
        
        Font titleFont = new Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 20, com.itextpdf.text.Font.BOLD, PRIMARY_COLOR);
        Font subtitleFont = new Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.NORMAL, DARK_GRAY);
        
        Paragraph title = new Paragraph("REPORTE DE MIEMBRO", titleFont);
        title.setSpacingAfter(5);
        
        Paragraph subtitle = new Paragraph("ID: " + member.getId().toString().substring(0, 8).toUpperCase(), subtitleFont);
        subtitle.setSpacingAfter(10);
        
        titleCell.addElement(title);
        titleCell.addElement(subtitle);
        
        // Add current date
        Paragraph date = new Paragraph("Fecha: " + LocalDate.now().format(DATE_FORMATTER), subtitleFont);
        titleCell.addElement(date);
        
        headerTable.addCell(titleCell);
        
        // Add a line separator
        LineSeparator line = new LineSeparator();
        line.setLineWidth(1f);
        line.setLineColor(PRIMARY_COLOR);
        
        // Add header to document
        document.add(headerTable);
        document.add(new Chunk(line));
        document.add(Chunk.NEWLINE);
    }

    private void addSectionTitle(Document document, String title) throws DocumentException {
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, PRIMARY_COLOR);
        Paragraph sectionTitle = new Paragraph(title, titleFont);
        sectionTitle.setSpacingBefore(20);
        sectionTitle.setSpacingAfter(10);
        
        // Add a small colored line under the title
        LineSeparator line = new LineSeparator();
        line.setLineWidth(2f);
        line.setLineColor(PRIMARY_COLOR);
        line.setPercentage(10);
        
        document.add(sectionTitle);
        document.add(line);
    }

    private void addMemberInfoSection(Document document, GymMember member) throws DocumentException {
        addSectionTitle(document, "INFORMACIÓN PERSONAL");
        
        // Create a table with 2 columns
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);
        table.setWidths(new float[]{1, 3});
        
        // Define fonts
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, DARK_GRAY);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, DARK_GRAY);
        
        // Add member information
        addInfoRow(table, "Nombre Completo:", member.getName(), labelFont, valueFont);
        addInfoRow(table, "Correo Electrónico:", member.getEmail(), labelFont, valueFont);
        addInfoRow(table, "Teléfono:", member.getPhone() != null ? member.getPhone() : "N/A", labelFont, valueFont);
        addInfoRow(table, "Estado:", Boolean.TRUE.equals(member.getActive()) ? "Activo" : "Inactivo", 
                 labelFont, valueFont);
        
        if (member.getRegistrationDate() != null) {
            addInfoRow(table, "Fecha de Registro:", 
                      member.getRegistrationDate().format(DATE_FORMATTER), labelFont, valueFont);
        }
        
        document.add(table);
    }

    private void addMembershipInfoSection(Document document, GymMember member) throws DocumentException {
        MembershipPlan plan = member.getMembershipPlan();
        
        addSectionTitle(document, "DETALLES DE LA MEMBRESÍA");
        
        if (plan == null) {
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, DARK_GRAY);
            document.add(new Paragraph("No hay plan de membresía asociado.", normalFont));
            return;
        }
        
        // Create a table for membership details
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);
        table.setWidths(new float[]{1, 3});
        
        // Define fonts
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, DARK_GRAY);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, DARK_GRAY);
        
        // Add membership information
        addInfoRow(table, "Plan:", plan.getName(), labelFont, valueFont);
        addInfoRow(table, "Descripción:", 
                  plan.getDescription() != null ? plan.getDescription() : "N/A", 
                  labelFont, valueFont);
        addInfoRow(table, "Costo Mensual:", 
                  "S/ " + DECIMAL_FORMAT.format(plan.getCost()), 
                  labelFont, valueFont);
        
        if (member.getMembershipStartDate() != null) {
            addInfoRow(table, "Fecha de Inicio:", 
                      member.getMembershipStartDate().format(DATE_FORMATTER), 
                      labelFont, valueFont);
        }
        
        if (member.getMembershipEndDate() != null) {
            // Calculate days remaining
            long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(), 
                member.getMembershipEndDate()
            );
            
            String statusText = member.getMembershipEndDate().isBefore(LocalDate.now()) 
                ? "Vencida" 
                : "Activa (" + daysRemaining + " días restantes)";
            
            addInfoRow(table, "Estado:", statusText, labelFont, valueFont);
            addInfoRow(table, "Fecha de Vencimiento:", 
                      member.getMembershipEndDate().format(DATE_FORMATTER), 
                      labelFont, valueFont);
        }
        
        document.add(table);
    }

    private void addPromotionsSection(Document document, GymMember member) throws DocumentException {
        List<Promotion> promotions = member.getPromotions();
        if (promotions == null || promotions.isEmpty()) {
            return;
        }
        
        addSectionTitle(document, "PROMOCIONES APLICADAS");
        
        // Create a table for promotions
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);
        table.setWidths(new float[]{3, 2, 2, 2});
        
        // Define fonts
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, DARK_GRAY);
        
        // Add table header
        addTableHeader(table, new String[]{"Promoción", "Descuento", "Ahorro", "Precio Final"}, headerFont, PRIMARY_COLOR);
        
        // Add promotion rows
        for (Promotion promo : promotions) {
            if (member.getMembershipPlan() != null) {
                BigDecimal originalPrice = member.getMembershipPlan().getCost();
                BigDecimal discountAmount = originalPrice.multiply(
                    promo.getDiscountPercentage().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                );
                BigDecimal finalPrice = originalPrice.subtract(discountAmount);
                
                addTableRow(table, 
                    new String[]{
                        promo.getName(),
                        promo.getDiscountPercentage() + "%",
                        "S/ " + DECIMAL_FORMAT.format(discountAmount),
                        "S/ " + DECIMAL_FORMAT.format(finalPrice)
                    }, 
                    cellFont,
                    null
                );
            }
        }
        
        document.add(table);
    }

    private void addPaymentHistorySection(Document document, GymMember member) throws DocumentException {
        List<Payment> payments = member.getPayments();
        if (payments == null || payments.isEmpty()) {
            return;
        }
        
        addSectionTitle(document, "HISTORIAL DE PAGOS");
        
        // Create a table for payment history
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);
        table.setWidths(new float[]{2, 2, 2, 2});
        
        // Define fonts
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, DARK_GRAY);
        
        // Add table header
        addTableHeader(table, new String[]{"Fecha", "Concepto", "Método", "Monto"}, headerFont, PRIMARY_COLOR);
        
        // Sort payments by date (newest first)
        payments.sort(Comparator.comparing(Payment::getPaymentDate).reversed());
        
        // Add payment rows
        for (Payment payment : payments) {
            addTableRow(table, 
                new String[]{
                    payment.getPaymentDate().format(DATE_FORMATTER),
                    "Pago de Membresía",
                    String.valueOf(payment.getPaymentMethod()),
                    "S/ " + DECIMAL_FORMAT.format(payment.getAmount())
                }, 
                cellFont,
                payment.getStatus() == PaymentStatus.PENDIENTE ? BaseColor.ORANGE : null
            );
        }
        
        // Calculate total paid
        BigDecimal totalPaid = payments.stream()
            .filter(p -> p.getStatus() == PaymentStatus.COMPLETADO)
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Add total row
        Font totalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, DARK_GRAY);
        addTableRow(table, 
            new String[]{"", "", "Total Pagado:", "S/ " + DECIMAL_FORMAT.format(totalPaid)},
            totalFont,
            LIGHT_GRAY
        );
        
        document.add(table);
    }

    // Helper method to add a table header
    private void addTableHeader(PdfPTable table, String[] headers, Font font, BaseColor bgColor) {
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, font));
            headerCell.setBackgroundColor(bgColor);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerCell.setPadding(5);
            table.addCell(headerCell);
        }
    }
    
    // Helper method to add a table row
    private void addTableRow(PdfPTable table, String[] values, Font font, BaseColor bgColor) {
        for (String value : values) {
            PdfPCell cell = new PdfPCell(new Phrase(value, font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(5);
            if (bgColor != null) {
                cell.setBackgroundColor(bgColor);
            }
            table.addCell(cell);
        }
    }
    
    // Helper method to add an info row with label and value
    private void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }
    
    private void addQrCode(Document document, String memberId) throws DocumentException, BadElementException, IOException {
        // Generate QR code with member ID
        String qrText = "Member ID: " + memberId + "\n" +
                       "Gym: " + "LAUFIT GYM" + "\n" +
                       "Generated: " + LocalDate.now().format(DATE_FORMATTER);
        
        // Create QR code
        BarcodeQRCode qrCode = new BarcodeQRCode(qrText, 100, 100, null);
        Image qrCodeImage = qrCode.getImage();
        qrCodeImage.scaleToFit(80, 80);
        
        // Position QR code at bottom right
        qrCodeImage.setAbsolutePosition(
            document.right() - 100,
            document.bottom() + 50
        );
        
        // Add QR code to document
        document.add(qrCodeImage);
    }
    
    private void addFooter(Document document) throws DocumentException {
        // Add a line separator
        LineSeparator line = new LineSeparator();
        line.setLineWidth(1f);
        line.setLineColor(PRIMARY_COLOR);
        document.add(line);
        
        // Add signature area
        Font signatureFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, DARK_GRAY);
        Paragraph signature = new Paragraph("\n\nFirma del Representante\n\n", signatureFont);
        signature.setAlignment(Element.ALIGN_RIGHT);
        document.add(signature);
        
        // Add footer text
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, DARK_GRAY);
        Paragraph footer = new Paragraph(
            "Documento generado electrónicamente por el Sistema de Gestión de Gimnasios\n" +
            "Fecha de generación: " + LocalDate.now().format(DATE_FORMATTER) + "\n" +
            "© " + LocalDate.now().getYear() + " LAUFIT GYM - Todos los derechos reservados", 
            footerFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }
    
    // Inner class for page events (watermark, etc.)
    private static class WatermarkPageEvent extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                PdfContentByte canvas = writer.getDirectContentUnder();
                
                // Add watermark text
                BaseFont baseFont = BaseFont.createFont();
                canvas.beginText();
                canvas.setColorFill(new BaseColor(230, 230, 230));
                canvas.setFontAndSize(baseFont, 60);
                
                // Add watermark text at 45 degrees
                PdfGState gs = new PdfGState();
                gs.setFillOpacity(0.1f);
                canvas.setGState(gs);
                
                // Calculate center of the page
                float x = document.getPageSize().getWidth() / 2;
                float y = document.getPageSize().getHeight() / 2;
                
                // Add watermark text at the center of the page
                canvas.showTextAligned(Element.ALIGN_CENTER, WATERMARK_TEXT, x, y, 45);
                canvas.endText();
                
                // Add page number
                canvas.beginText();
                canvas.setFontAndSize(baseFont, 10);
                canvas.setColorFill(DARK_GRAY);
                canvas.showTextAligned(Element.ALIGN_CENTER, 
                    "Página " + writer.getPageNumber(), 
                    document.getPageSize().getWidth() / 2, 
                    30, 0);
                canvas.endText();
                
            } catch (Exception e) {
                log.error("Error al agregar marca de agua", e);
            }
        }
    }
}
