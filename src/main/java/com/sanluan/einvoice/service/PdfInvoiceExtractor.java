package com.sanluan.einvoice.service;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

/**
 * 专用于处理电子发票识别的类
 * 
 * @author arthurlee
 *
 */

public class PdfInvoiceExtractor {

    public static Invoice extract(File file) throws IOException {
        Invoice invoice = new Invoice();
        PDDocument doc = PDDocument.load(file);
        PDPage firstPage = doc.getPage(0);
        int pageWidth = Math.round(firstPage.getCropBox().getWidth());
        PDFTextStripper textStripper = new PDFTextStripper();
        textStripper.setSortByPosition(true);
        String fullText = textStripper.getText(doc);
        if (firstPage.getRotation() != 0) {
            pageWidth = Math.round(firstPage.getCropBox().getHeight());
        }
        String allText = replace(fullText).replaceAll("（", "(").replaceAll("）", ")").replaceAll("￥", "¥");
        {
            String reg = "机器编号:(?<machineNumber>\\d{12})|发票代码:(?<code>\\d{12})|发票号码:(?<number>\\d{8})|:(?<date>\\d{4}年\\d{2}月\\d{2}日)"
                    + "|校验码:(?<checksum>\\d{20}|\\S{4,})";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(allText);
            while (matcher.find()) {
                if (matcher.group("machineNumber") != null) {
                    invoice.setMachineNumber(matcher.group("machineNumber"));
                } else if (matcher.group("code") != null) {
                    invoice.setCode(matcher.group("code"));
                } else if (matcher.group("number") != null) {
                    invoice.setNumber(matcher.group("number"));
                } else if (matcher.group("date") != null) {
                    invoice.setDate(matcher.group("date"));
                } else if (matcher.group("checksum") != null) {
                    invoice.setChecksum(matcher.group("checksum"));
                }
            }
        }
        {
            String reg = "合计¥?(?<amount>[^ \\f\\n\\r\\t\\v\\*]*)(?:¥?(?<taxAmount>\\S*)|\\*+)\\s";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(allText);
            if (matcher.find()) {
                try {
                    invoice.setAmount(new BigDecimal(matcher.group("amount")));
                } catch (Exception e) {
                }
                try {
                    invoice.setTaxAmount(new BigDecimal(matcher.group("taxAmount")));
                } catch (Exception e) {
                    invoice.setTaxAmount(new BigDecimal(0));
                }
            }
        }
        if (null == invoice.getAmount()) {
            String reg = "合\\u0020*计\\u0020*¥?(?<amount>[^ ]*)\\u0020+¥?(?:(?<taxAmount>\\S*)|\\*+)\\s";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(fullText);
            if (matcher.find()) {
                try {
                    invoice.setAmount(new BigDecimal(matcher.group("amount")));
                } catch (Exception e) {
                    invoice.setAmount(new BigDecimal(0));
                }
                try {
                    invoice.setTaxAmount(new BigDecimal(matcher.group("taxAmount")));
                } catch (Exception e) {
                    invoice.setTaxAmount(new BigDecimal(0));
                }
            }
        }
        {
            String reg = "价税合计\\u0028大写\\u0029(?<amountString>\\S*)\\u0028小写\\u0029¥?(?<amount>\\S*)\\s";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(allText);
            if (matcher.find()) {
                invoice.setTotalAmountString(matcher.group("amountString"));
                try {
                    invoice.setTotalAmount(new BigDecimal(matcher.group("amount")));
                } catch (Exception e) {
                    invoice.setTotalAmount(new BigDecimal(0));
                }
            }
        }
        {
            String reg = "收款人:(?<payee>\\S*)复核:(?<reviewer>\\S*)开票人:(?<drawer>\\S*)销售方";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(allText);
            if (matcher.find()) {
                invoice.setPayee(matcher.group("payee"));
                invoice.setReviewer(matcher.group("reviewer"));
                invoice.setDrawer(matcher.group("drawer"));
            }
            if (allText.indexOf("通行费") > 0 && allText.indexOf("车牌号") > 0) {
                invoice.setType("通行费");
            }
            Pattern type00Pattern = Pattern.compile("(?<p>\\S*)通发票");
            Matcher m00 = type00Pattern.matcher(allText);
            if (m00.find()) {
                invoice.setTitle(m00.group("p").replaceAll("(?:国|统|一|发|票|监|制)", "") + "通发票");
                if (null == invoice.getType()) {
                    invoice.setType("普通发票");
                }
            } else {
                Pattern type01Pattern = Pattern.compile("(?<p>\\S*)用发票");
                Matcher m01 = type01Pattern.matcher(allText);
                if (m01.find()) {
                    invoice.setTitle(m01.group("p").replaceAll("(?:国|统|一|发|票|监|制)", "") + "用发票");
                    if (null == invoice.getType()) {
                        invoice.setType("专用发票");
                    }
                }
            }
        }
        PDFKeyWordPosition kwp = new PDFKeyWordPosition();
        Map<String, List<Position>> positionListMap = kwp
                .getCoordinate(Arrays.asList("机器编号", "税率", "价税合计", "合计", "开票日期", "规格型号", "车牌号", "开户行及账号", "密", "码", "区"), doc);

        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        PDFTextStripperByArea detailStripper = new PDFTextStripperByArea();
        detailStripper.setSortByPosition(true);
        {
            Position machineNumber;
            if (positionListMap.get("机器编号").size() > 0) {
                machineNumber = positionListMap.get("机器编号").get(0);
            } else {
                machineNumber = positionListMap.get("开票日期").get(0);
                machineNumber.setY(machineNumber.getY() + 30);
            }
            Position taxRate = positionListMap.get("税率").get(0);
            Position totalAmount = positionListMap.get("价税合计").get(0);
            Position amount = positionListMap.get("合计").get(0);
            Position model = null;
            if (!positionListMap.get("规格型号").isEmpty()) {
                model = positionListMap.get("规格型号").get(0);
            } else {
                model = positionListMap.get("车牌号").get(0);
                model.setX(model.getX() - 15);
            }

            List<Position> account = positionListMap.get("开户行及账号");
            Position buyer;
            Position seller;
            if (account.size() < 2) {
                buyer = new Position(51, 122);
                seller = new Position(51, 341);
            } else {
                buyer = account.get(0);
                seller = account.get(1);
            }

            int maqX = 370;
            List<Position> mi = positionListMap.get("密");
            List<Position> ma = positionListMap.get("码");
            List<Position> qu = positionListMap.get("区");
            for (int i = 0; i < mi.size(); i++) {
                float x1 = mi.get(i).getX();
                for (int j = 0; j < ma.size(); j++) {
                    float x2 = ma.get(j).getX();
                    if (Math.abs(x1 - x2) < 5) {
                        for (int k = 0; k < qu.size(); k++) {
                            float x3 = qu.get(k).getX();
                            if (Math.abs(x2 - x3) < 5) {
                                maqX = Math.round((x1 + x2 + x3) / 3);
                            }
                        }
                    }
                }
            }
            {
                int x = Math.round(model.getX()) - 13;
                int y = Math.round(taxRate.getY()) + 5; // 用税率的y坐标作参考
                int h = Math.round(amount.getY()) - Math.round(taxRate.getY()) - 25; // 价税合计的y坐标减去税率的y坐标
                detailStripper.addRegion("detail", new Rectangle(0, y, pageWidth, h));
                stripper.addRegion("detailName", new Rectangle(0, y, x, h));
                stripper.addRegion("detailPrice", new Rectangle(x, y, pageWidth, h));
            }
            {
                int x = maqX + 10;
                int y = Math.round(machineNumber.getY()) + 10;
                int w = pageWidth - maqX - 10;
                int h = Math.round(taxRate.getY() - 5) - y;
                stripper.addRegion("password", new Rectangle(x, y, w, h));
            }
            {
                int x = Math.round(buyer.getX()) - 15; // 开户行及账号的x为参考
                int y = Math.round(machineNumber.getY()) + 10; // 机器编号的y坐标为参考
                int w = maqX - x - 5; // 密码区x坐标为参考
                int h = Math.round(buyer.getY()) - y + 20; // 开户行及账号的y坐标为参考
                stripper.addRegion("buyer", new Rectangle(x, y, w, h));
            }
            {
                int x = Math.round(seller.getX()) - 15; // 开户行及账号为x参考
                int y = Math.round(totalAmount.getY()) + 10; // 价税合计的y坐标为参考
                int w = maqX - x - 5; // 密码区的x为参考
                int h = Math.round(seller.getY()) - y + 20; // 开户行及账号的y为参考
                stripper.addRegion("seller", new Rectangle(x, y, w, h));
            }
        }
        stripper.extractRegions(firstPage);
        detailStripper.extractRegions(firstPage);
        doc.close();

        invoice.setPassword(StringUtils.trim(stripper.getTextForRegion("password")));

        String reg = "名称:(?<name>\\S*)|纳税人识别号:(?<code>\\S*)|地址、电话:(?<address>\\S*)|开户行及账号:(?<account>\\S*)|电子支付标识:(?<account2>\\\\S*)";
        {
            String buyer = replace(stripper.getTextForRegion("buyer"));
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(buyer);
            while (matcher.find()) {
                if (matcher.group("name") != null) {
                    invoice.setBuyerName(matcher.group("name"));
                } else if (matcher.group("code") != null) {
                    invoice.setBuyerCode(matcher.group("code"));
                } else if (matcher.group("address") != null) {
                    invoice.setBuyerAddress(matcher.group("address"));
                } else if (matcher.group("account") != null) {
                    invoice.setBuyerAccount(matcher.group("account"));
                } else if (matcher.group("account2") != null) {
                    invoice.setBuyerAccount(matcher.group("account2"));
                }
            }
        }
        {
            String seller = replace(stripper.getTextForRegion("seller"));
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(seller);
            while (matcher.find()) {
                if (matcher.group("name") != null) {
                    invoice.setSellerName(matcher.group("name"));
                } else if (matcher.group("code") != null) {
                    invoice.setSellerCode(matcher.group("code"));
                } else if (matcher.group("address") != null) {
                    invoice.setSellerAddress(matcher.group("address"));
                } else if (matcher.group("account") != null) {
                    invoice.setSellerAccount(matcher.group("account"));
                }
            }
        }
        {
            List<String> skipList = new ArrayList<>();
            List<Detail> detailList = new ArrayList<>();
            String[] detailPriceStringArray = stripper.getTextForRegion("detailPrice").replaceAll("　", " ").replaceAll(" ", " ")
                    .replaceAll("\r", "").split("\\n");
            for (String detailString : detailPriceStringArray) {
                Detail detail = new Detail();
                detail.setName("");
                String[] itemArray = StringUtils.split(detailString, " ");
                if (2 == itemArray.length) {
                    detail.setAmount(new BigDecimal(itemArray[0]));
                    detail.setTaxAmount(new BigDecimal(itemArray[1]));
                    detailList.add(detail);
                } else if (2 < itemArray.length) {
                    detail.setAmount(new BigDecimal(itemArray[itemArray.length - 3]));
                    String taxRate = itemArray[itemArray.length - 2];
                    if (taxRate.indexOf("免税") > 0 || taxRate.indexOf("不征税") > 0 || taxRate.indexOf("出口零税率") > 0
                            || taxRate.indexOf("普通零税率") > 0 || taxRate.indexOf("%") < 0) {
                        detail.setTaxRate(new BigDecimal(0));
                        detail.setTaxAmount(new BigDecimal(0));
                    } else {
                        BigDecimal rate = new BigDecimal(Integer.parseInt(taxRate.replaceAll("%", "")));
                        detail.setTaxRate(rate.divide(new BigDecimal(100)));
                        detail.setTaxAmount(new BigDecimal(itemArray[itemArray.length - 1]));
                    }
                    for (int j = 0; j < itemArray.length - 3; j++) {
                        if (itemArray[j].matches("^(-?\\d+)(\\.\\d+)?$")) {
                            if (null == detail.getCount()) {
                                detail.setCount(new BigDecimal(itemArray[j]));
                            } else {
                                detail.setPrice(new BigDecimal(itemArray[j]));
                            }
                        } else {
                            if (itemArray.length >= j + 1 && !itemArray[j + 1].matches("^(-?\\d+)(\\.\\d+)?$")) {
                                detail.setUnit(itemArray[j + 1]);
                                detail.setModel(itemArray[j]);
                                j++;
                            } else if (itemArray[j].length() > 2) {
                                detail.setModel(itemArray[j]);
                            } else {
                                detail.setUnit(itemArray[j]);
                            }
                        }
                    }
                    detailList.add(detail);
                } else {
                    skipList.add(detailString);
                }
            }

            String[] detailNameStringArray = stripper.getTextForRegion("detailName").replaceAll("　", " ").replaceAll(" ", " ")
                    .replaceAll("\r", "").split("\\n");
            String[] detailStringArray = replace(detailStripper.getTextForRegion("detail")).replaceAll("\r", "").split("\\n");
            int i = 0, j = 0, h = 0, m = 0;
            Detail lastDetail = null;
            for (String detailString : detailStringArray) {
                if (m < detailNameStringArray.length) {
                    if (detailString.matches("\\S+\\d*(%|免税|不征税|出口零税率|普通零税率)\\S*")
                            && !detailString.matches("^ *\\d*(%|免税|不征税|出口零税率|普通零税率)\\S*")
                            && detailString.matches("\\S+\\d+%[\\-\\d]+\\S*")
                            || detailStringArray.length > i + 1
                                    && detailStringArray[i + 1].matches("^ *\\d*(%|免税|不征税|出口零税率|普通零税率)\\S*")) {
                        if (j < detailList.size()) {
                            lastDetail = detailList.get(j);
                            lastDetail.setName(detailNameStringArray[m]);
                        }
                        j++;
                    } else if (null != lastDetail && StringUtils.isNotBlank(detailNameStringArray[m])) {
                        if (skipList.size() > h) {
                            String skip = skipList.get(h);
                            if (detailString.endsWith(skip)) {
                                if (detailString.equals(skip)) {
                                    m--;
                                } else {
                                    lastDetail.setName(lastDetail.getName() + detailNameStringArray[m]);
                                }
                                lastDetail.setModel(lastDetail.getModel() + skip);
                                h++;
                            } else {
                                lastDetail.setName(lastDetail.getName() + detailNameStringArray[m]);
                            }
                        } else {
                            lastDetail.setName(lastDetail.getName() + detailNameStringArray[m]);
                        }
                    }
                }
                i++;
                m++;
            }
            invoice.setDetailList(detailList);
        }
        return invoice;

    }

    public static String replace(String str) {
        return str.replaceAll(" ", "").replaceAll("　", "").replaceAll("：", ":").replaceAll(" ", "");
    }
}