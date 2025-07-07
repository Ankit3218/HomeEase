package com.homeease.util;

import com.homeease.entity.Booking;
import com.homeease.entity.User;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PdfInvoiceGenerator {

    public static ByteArrayInputStream generateInvoice(Booking booking) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            PdfWriter writer = PdfWriter.getInstance(document, out);
           

           
              writer.setPageEvent(new PdfHeaderFooter());
        
            writer.setPageEvent(new PdfFooter()); 
            document.open();

           
            Image logo = Image.getInstance("src/main/resources/static/images/logo.png");
            logo.scaleToFit(100, 50);
            logo.setAlignment(Image.ALIGN_LEFT);
            document.add(logo);

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Booking Invoice", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(10);
            title.setSpacingAfter(20);
            document.add(title);

           
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(20f);
            table.setWidths(new int[]{1, 2});

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            BaseColor headerColor = new BaseColor(63, 81, 181); // 

            addTableRow(table, "Booking ID:", String.valueOf(booking.getId()), headerFont, cellFont, headerColor);
            addTableRow(table, "Customer Name:", booking.getCustomerName(), headerFont, cellFont, headerColor);
            addTableRow(table, "Service:", booking.getService().getTitle(), headerFont, cellFont, headerColor);
            addTableRow(table, "Date:", booking.getBookingDate().toString(), headerFont, cellFont, headerColor);
            addTableRow(table, "Amount:", "₹ " + booking.getTotalAmount(), headerFont, cellFont, headerColor);
            addTableRow(table, "Address:", booking.getAddress(), headerFont, cellFont, headerColor);
            addTableRow(table, "Phone:", booking.getContact(), headerFont, cellFont, headerColor);
           

            document.add(table);

           
            Paragraph thankYou = new Paragraph("Thank you for choosing HomeEase!", cellFont);
            thankYou.setAlignment(Element.ALIGN_CENTER);
            thankYou.setSpacingAfter(30f);
            document.add(thankYou);

         
            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(40f);
            footerTable.setWidths(new float[]{1, 1});

          
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);

     
            PdfPCell qrCell = new PdfPCell();
            qrCell.setBorder(Rectangle.NO_BORDER);

            Paragraph qrLabel = new Paragraph("Scan QR Code for Booking Info", smallFont);
            qrLabel.setSpacingAfter(5f);
            qrCell.addElement(qrLabel);

         
            String qrContent = String.format(
            	    "Booking Invoice\n" +
            	    "---------------\n" +
            	    "Booking ID   : %s\n" +
            	    ", Customer     : %s\n" +
            	    ", Service      : %s\n" +
            	    ", Date         : %s\n" +
            	    ", Amount       : INR %.2f",
            	    booking.getId(),
            	    booking.getCustomerName(),
            	    booking.getService().getTitle(),
            	    booking.getBookingDate().toString(),
            	    booking.getTotalAmount()
            	);


            

    byte[] qrCode = QrCodeGenerator.generateQRCodeImage(qrContent, 150, 150);

            Image qrImage = Image.getInstance(qrCode);
            qrImage.scaleToFit(100, 100);
            qrImage.setAlignment(Image.ALIGN_LEFT);

            qrCell.addElement(qrImage); 

            
            PdfPCell signatureCell = new PdfPCell();
            signatureCell.setBorder(Rectangle.NO_BORDER);
            signatureCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            Paragraph signatureLine = new Paragraph("_________________", boldFont);
            signatureLine.setAlignment(Element.ALIGN_RIGHT);
            Paragraph signatureText = new Paragraph("Authorized Signature\nHomeEase Pvt. Ltd.", boldFont);
            signatureText.setAlignment(Element.ALIGN_RIGHT);
            
            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 12);


            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            Paragraph date = new Paragraph("Date: " + currentDate.format(formatter), dateFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setSpacingBefore(5f);

            signatureCell.addElement(signatureLine);
            signatureCell.addElement(signatureText);
            signatureCell.addElement(date);


            footerTable.addCell(qrCell);
            footerTable.addCell(signatureCell);

            
            document.add(footerTable);
            
       
            Font termsHeadingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
            Font termsFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.DARK_GRAY);

            Paragraph termsHeading = new Paragraph("Terms and Conditions", termsHeadingFont);
            termsHeading.setSpacingBefore(20f);
            termsHeading.setSpacingAfter(5f);

            Paragraph termsBody = new Paragraph(
            	"1. This is a computer-generated invoice and does not require a physical signature.\n" +
                "2. A full refund will be provided if the booking is cancelled within 24 hours of confirmation.\n" +
                "3. No refund will be processed after 24 hours from the time of booking.\n" +
                "4. Booking details once confirmed cannot be altered. Please verify before confirming.\n" +
                "5. HomeEase Pvt. Ltd. is not responsible for delays caused by unforeseen circumstances.\n" +
                "6. For any service-related issues, kindly reach out to our support within 48 hours of service delivery.",
                termsFont
            );
            termsBody.setAlignment(Element.ALIGN_JUSTIFIED);

            document.add(termsHeading);
            document.add(termsBody);

            PdfContentByte canvas = writer.getDirectContentUnder();
            Font watermarkFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 52, new BaseColor(220, 220, 220)); // Lighter gray
            Phrase watermark = new Phrase("HomeEase", watermarkFont);

         
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark, 297.5f, 421, 45);



            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private static void addTableRow(PdfPTable table, String label, String value,
                                    Font headerFont, Font cellFont, BaseColor headerColor) {
        PdfPCell cell1 = new PdfPCell(new Phrase(label, headerFont));
        cell1.setBackgroundColor(headerColor);
        cell1.setPadding(8);
        cell1.setBorderWidth(0.5f);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell cell2 = new PdfPCell(new Phrase(value, cellFont));
        cell2.setPadding(8);
        cell2.setBorderWidth(0.5f);
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(cell1);
        table.addCell(cell2);
    }
}
