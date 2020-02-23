package com.ecommerce.ecommApp.invoiceGenerator.pdfUtils;

import com.itextpdf.text.Font;

public class Utils {
    public final static String[] HEADER_ARRAY = {"Order Id", "Product name", "Brand", "Price", "Quantity", "Total Amount"};
    public final static Font SMALL_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 8,
            Font.ITALIC);
    public final static Font NORMAL_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    public final static Font NORMAL_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 10,
            Font.NORMAL);
    public final static float LEFT_INDENT = 25;
    public final static float RIGHT_INDENT = 10;

    public static final String PDF_EXTENSION = ".pdf";
    public static final String[] PDF_METADATA = {"Invoice", "Amit Verma", "Order Invoice"};

    public static final String INVOICE_FOLDER = "src/main/resources/invoice/";



}
