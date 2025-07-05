package com.homeease.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class PdfHeaderFooter extends PdfPageEventHelper {

    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        // Header without border
        PdfPTable header = new PdfPTable(1);
        try {
            header.setWidths(new int[]{24});
            header.setTotalWidth(527);
            header.setLockedWidth(true);
            header.getDefaultCell().setFixedHeight(40);
            header.getDefaultCell().setBorder(Rectangle.NO_BORDER); // ❌ Remove underline
            header.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            header.addCell(new Phrase("HomeEase - Smart Home Services", headerFont));

            header.writeSelectedRows(0, -1, 34, 830, writer.getDirectContent());
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }
}
