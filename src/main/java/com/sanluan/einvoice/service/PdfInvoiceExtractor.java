package com.sanluan.einvoice.service;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;

import static com.sanluan.einvoice.service.PdfFullElectronicInvoiceService.getFullElectronicInvoice;
import static com.sanluan.einvoice.service.PdfRegularInvoiceService.getRegularInvoice;
import static com.sanluan.einvoice.utils.StringUtils.replace;

/**
 * 专用于处理电子发票识别的类
 * 
 * @author arthurlee
 *
 */

public class PdfInvoiceExtractor {

    public static Invoice extract(File file) throws IOException {

//        PDDocument doc = PDDocument.load(file);
        PDDocument doc = Loader.loadPDF(file);
        PDPage firstPage = doc.getPage(0);
        int pageWidth = Math.round(firstPage.getCropBox().getWidth());
        PDFTextStripper textStripper = new PDFTextStripper();
        textStripper.setSortByPosition(true);
        String fullText = textStripper.getText(doc);
        if (firstPage.getRotation() != 0) {
            pageWidth = Math.round(firstPage.getCropBox().getHeight());
        }
        String allText = replace(fullText).replaceAll("（", "(").replaceAll("）", ")").replaceAll("￥", "¥");
        if(allText.contains("电子发票")){
            // 全票
          return getFullElectronicInvoice(fullText,allText,pageWidth,doc,firstPage);
        }else {
           return getRegularInvoice(fullText,allText,pageWidth,doc,firstPage);
        }
    }


}