package com.homeease.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class PdfFooter extends PdfPageEventHelper {
    Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfPTable footer = new PdfPTable(1);
         {
            footer.setTotalWidth(527);
            footer.setLockedWidth(true);
            footer.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            footer.addCell(new Phrase("© 2025 HomeEase Pvt. Ltd. | www.homeease.in |ankitk3218@gmail.com", footerFont));
            footer.writeSelectedRows(0, -1, 34, 30, writer.getDirectContent());
        } 
    }
}

