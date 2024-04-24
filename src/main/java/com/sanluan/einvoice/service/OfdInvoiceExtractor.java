package com.sanluan.einvoice.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
 *
 */

public class OfdInvoiceExtractor {

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
        //invoice.setTaxAmount(new BigDecimal(root.elementTextTrim("TaxTotalAmount")));
        //System.out.println("错误Object: " + root.elementTextTrim("TaxTotalAmount"));

        String taxTotalAmountStr = root.elementTextTrim("TaxTotalAmount");
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?"); // 正则表达式匹配数字，包括可能的负号和小数部分
        Matcher matcher = pattern.matcher(taxTotalAmountStr);

        BigDecimal taxAmount = null;
        if (matcher.find()) {
            taxAmount = new BigDecimal(matcher.group());
            invoice.setTaxAmount(taxAmount);
        } else {
            // 处理错误情况，例如记录日志或抛出异常
            //throw new IllegalArgumentException("无法从字符串中提取有效的税金额: " + taxTotalAmountStr);
        }




        int ind = content.indexOf("圆整</ofd:TextCode>");
        invoice.setTotalAmountString(content.substring(content.lastIndexOf(">", ind) + 1, ind + 2));
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
        invoice.setPassword(root.elementText("TaxControlCode"));
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
                //detail.setTaxAmount(new BigDecimal(element.elementTextTrim("TaxAmount")));

                // 假设 element 是某个XML或JSON元素的引用
                String taxAmountStr = element.elementTextTrim("TaxAmount");
// 使用正则表达式匹配并提取数字部分
                Pattern patterndetail = Pattern.compile("-?\\d+(\\.\\d+)?"); // 匹配整数或小数
                Matcher matcherdetail = patterndetail.matcher(taxAmountStr);
                BigDecimal taxAmountdetail = null;
                if (matcherdetail.find()) {
                    // 提取匹配到的数字部分并转换为BigDecimal
                    taxAmountdetail = new BigDecimal(matcherdetail.group());
                    detail.setTaxAmount(taxAmountdetail);
                    detail.setTaxRate(
                            new BigDecimal(element.elementTextTrim("TaxScheme").replace("%", "")).divide(new BigDecimal(100)));
                } else {
                    // 如果找不到匹配项，处理错误情况
                    //throw new IllegalArgumentException("无法从字符串中提取有效的税额: " + taxAmountStr);
                }
// 设置税额



                detail.setCount(new BigDecimal(element.elementTextTrim("Quantity")));
                detail.setPrice(new BigDecimal(element.elementTextTrim("Price")));
                detail.setUnit(element.elementTextTrim("MeasurementDimension"));
                detail.setModel(element.elementTextTrim("Specification"));
                /*detail.setTaxRate(
                        new BigDecimal(element.elementTextTrim("TaxScheme").replace("%", "")).divide(new BigDecimal(100)));*/
                detailList.add(detail);
            }
            invoice.setDetailList(detailList);
        }
        return invoice;
    }
}