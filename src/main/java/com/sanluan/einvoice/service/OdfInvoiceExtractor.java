package com.sanluan.einvoice.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.util.StreamUtils;

/**
 * 专用于处理电子发票识别的类
 * 
 * @author arthurlee
 *
 */

public class OdfInvoiceExtractor {

    public static Invoice extract(File file) throws IOException, DocumentException {
        ZipFile zipFile = new ZipFile(file);
        ZipEntry entry = zipFile.getEntry("Doc_0/Attachs/original_invoice.xml");
        ZipEntry entry1 = zipFile.getEntry("Doc_0/Pages/Page_0/Content.xml");
        InputStream input = zipFile.getInputStream(entry);
        InputStream input1 = zipFile.getInputStream(entry1);
        String body = StreamUtils.copyToString(input, Charset.forName("utf-8"));
        String content = StreamUtils.copyToString(input1, Charset.forName("utf-8"));
        zipFile.close();
        Document document = DocumentHelper.parseText(body);
        Element root = document.getRootElement();
        Invoice invoice = new Invoice();
        invoice.setMachineNumber(root.elementTextTrim("MachineNo"));
        invoice.setCode(root.elementTextTrim("InvoiceCode"));
        invoice.setNumber(root.elementTextTrim("InvoiceNo"));
        invoice.setDate(root.elementTextTrim("IssueDate"));
        invoice.setChecksum(root.elementTextTrim("InvoiceCheckCode"));
        invoice.setAmount(new BigDecimal(root.elementTextTrim("TaxExclusiveTotalAmount")));
        invoice.setTaxAmount(new BigDecimal(root.elementTextTrim("TaxTotalAmount")));
        invoice.setTotalAmountString(root.elementTextTrim(""));
        invoice.setTotalAmount(new BigDecimal(root.elementTextTrim("TaxInclusiveTotalAmount")));
        invoice.setPayee(root.elementTextTrim("Payee"));
        invoice.setReviewer(root.elementTextTrim("Checker"));
        invoice.setDrawer(root.elementTextTrim("InvoiceClerk"));
        int index = content.indexOf("</ofd:TextCode>");
        invoice.setTitle(content.substring(content.lastIndexOf(">", index) + 1, index));
        invoice.setType("普通发票");
        if (invoice.getTitle().contains("专用发票")) {
            invoice.setType("专用发票");
        } else if (invoice.getTitle().contains("通行费")) {
            invoice.setType("通行费");
        }
        invoice.setPassword(root.elementTextTrim("TaxControlCode"));
        Element buyer = root.element("Buyer");
        {
            invoice.setBuyerName(buyer.elementTextTrim("BuyerName"));
            invoice.setBuyerCode(buyer.elementTextTrim("BuyerTaxID"));
            invoice.setBuyerAddress(buyer.elementTextTrim("BuyerAddrTel"));
            invoice.setBuyerAccount(buyer.elementTextTrim("BuyerFinancialAccount"));
        }
        Element seller = root.element("Seller");
        {
            invoice.setSellerName(seller.elementTextTrim("SellerName"));
            invoice.setSellerCode(seller.elementTextTrim("SellerTaxID"));
            invoice.setSellerAddress(seller.elementTextTrim("SellerAddrTel"));
            invoice.setSellerAccount(seller.elementTextTrim("SellerFinancialAccount"));
        }
        Element details = root.element("GoodsInfos");
        {
            List<Detail> detailList = new ArrayList<>();
            List<Element> elements = details.elements();
            for (Element element : elements) {
                Detail detail = new Detail();
                detail.setName(element.elementTextTrim("Item"));
                detail.setAmount(new BigDecimal(element.elementTextTrim("Amount")));
                detail.setTaxAmount(new BigDecimal(element.elementTextTrim("TaxAmount")));
                detail.setCount(new BigDecimal(element.elementTextTrim("Quantity")));
                detail.setPrice(new BigDecimal(element.elementTextTrim("Price")));
                detail.setUnit(element.elementTextTrim("MeasurementDimension"));
                detail.setModel(element.elementTextTrim("Specification"));
                detail.setTaxRate(
                        new BigDecimal(element.elementTextTrim("TaxScheme").replace("%", "")).divide(new BigDecimal(100)));
                detailList.add(detail);
            }
            invoice.setDetailList(detailList);
        }
        return invoice;
    }
}