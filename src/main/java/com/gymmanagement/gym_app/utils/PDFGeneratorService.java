package com.gymmanagement.gym_app.utils;

import com.gymmanagement.gym_app.domain.GymMember;
import com.gymmanagement.gym_app.domain.MembershipPlan;
import com.gymmanagement.gym_app.domain.Promotion;
import com.gymmanagement.gym_app.domain.Payment;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class PDFGeneratorService {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ByteArrayInputStream generateMemberReport(GymMember member) {
        if (member == null) {
            log.error("El miembro es nulo, no se puede generar el PDF.");
            return new ByteArrayInputStream(new byte[0]);
        }

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            addLogo(document);
            addTitle(document, "Reporte de Miembro del Gimnasio");
            addMemberInfo(document, member);
            addMembershipInfo(document, member);
            addPromotions(document, member);
            addPaymentHistory(document, member);

            document.close();
        } catch (Exception ex) {
            log.error("Error al generar el PDF", ex);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addLogo(Document document) {
        try {
            Image logo = Image.getInstance(new ClassPathResource("static/laufit.jpg").getURL());
            logo.scaleToFit(100, 100);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
        } catch (IOException | DocumentException e) {
            log.warn("No se encontró la imagen del gimnasio.", e);
        }
    }

    private void addTitle(Document document, String titleText) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph(titleText, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
    }

    private void addMemberInfo(Document document, GymMember member) throws DocumentException {
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        document.add(new Paragraph("Nombre: " + member.getName(), normalFont));
        document.add(new Paragraph("Correo: " + member.getEmail(), normalFont));
        document.add(new Paragraph("Teléfono: " + member.getPhone(), normalFont));
        document.add(new Paragraph("Membresía activa: " + (Boolean.TRUE.equals(member.getActive()) ? "Sí" : "No"), normalFont));
        if (member.getRegistrationDate() != null) {
            document.add(new Paragraph("Fecha de Registro: " + member.getRegistrationDate().format(DATE_FORMATTER), normalFont));
        }
    }

    private void addMembershipInfo(Document document, GymMember member) throws DocumentException {
        MembershipPlan plan = member.getMembershipPlan();
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Plan de Membresía", boldFont));

        if (plan == null) {
            document.add(new Paragraph("No hay plan de membresía asociado.", normalFont));
            return;
        }

        document.add(new Paragraph("Nombre del Plan: " + plan.getName(), normalFont));
        document.add(new Paragraph("Descripción: " + (plan.getDescription() != null ? plan.getDescription() : "N/A"), normalFont));
        document.add(new Paragraph("Costo: S/ " + DECIMAL_FORMAT.format(plan.getCost()), normalFont));

        if (member.getMembershipEndDate() != null) {
            document.add(new Paragraph("Fecha de Vencimiento: " + member.getMembershipEndDate().format(DATE_FORMATTER), normalFont));
        }
    }

    private void addPromotions(Document document, GymMember member) throws DocumentException {
        List<Promotion> promotions = member.getPromotions();
        if (promotions != null && !promotions.isEmpty()) {
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Promociones Aplicadas", boldFont));

            PdfPTable promoTable = new PdfPTable(3);
            promoTable.setWidthPercentage(100);
            promoTable.setSpacingBefore(10f);

            addTableHeader(promoTable, "Nombre", "Descuento (%)", "Total con Descuento");

            Font tableFont = FontFactory.getFont(FontFactory.HELVETICA, 13);

            for (Promotion promo : promotions) {
                promoTable.addCell(new PdfPCell(new Phrase(promo.getName(), tableFont))).setHorizontalAlignment(Element.ALIGN_CENTER);
                promoTable.addCell(new PdfPCell(new Phrase(promo.getDiscountPercentage() + " %", tableFont))).setHorizontalAlignment(Element.ALIGN_CENTER);

                BigDecimal discount = member.getMembershipPlan().getCost()
                        .multiply(promo.getDiscountPercentage().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                BigDecimal totalWithDiscount = member.getMembershipPlan().getCost().subtract(discount);

                promoTable.addCell(new PdfPCell(new Phrase("S/ " + DECIMAL_FORMAT.format(totalWithDiscount), tableFont))).setHorizontalAlignment(Element.ALIGN_CENTER);
            }
            document.add(promoTable);
        }
    }

    private void addPaymentHistory(Document document, GymMember member) throws DocumentException {
        List<Payment> payments = member.getPayments();
        if (payments != null && !payments.isEmpty()) {
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Historial de Pagos", boldFont));

            PdfPTable paymentTable = new PdfPTable(3);
            paymentTable.setWidthPercentage(100);
            paymentTable.setSpacingBefore(10f);

            addTableHeader(paymentTable, "Fecha", "Monto", "Método");

            Font tableFont = FontFactory.getFont(FontFactory.HELVETICA, 13);

            for (Payment payment : payments) {
                paymentTable.addCell(new PdfPCell(new Phrase(payment.getPaymentDate().format(DATE_FORMATTER), tableFont))).setHorizontalAlignment(Element.ALIGN_CENTER);
                paymentTable.addCell(new PdfPCell(new Phrase("S/ " + DECIMAL_FORMAT.format(payment.getAmount()), tableFont))).setHorizontalAlignment(Element.ALIGN_CENTER);
                paymentTable.addCell(new PdfPCell(new Phrase(String.valueOf(payment.getPaymentMethod()), tableFont))).setHorizontalAlignment(Element.ALIGN_CENTER);
            }
            document.add(paymentTable);
        }
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
            headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(headerCell);
        }
    }
}
