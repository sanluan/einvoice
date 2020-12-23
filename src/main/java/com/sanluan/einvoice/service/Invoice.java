package com.sanluan.einvoice.service;

import java.math.BigDecimal;
import java.util.List;

public class Invoice {
    private String title;
    private String machineNumber;
    private String code;
    private String number;
    private String date;
    private String checksum;
    private String buyerName;
    private String buyerCode;
    private String buyerAddress;
    private String buyerAccount;
    private String password;
    private BigDecimal amount;
    private BigDecimal taxAmount;
    private String totalAmountString;
    private BigDecimal totalAmount;
    private String sellerName;
    private String sellerCode;
    private String sellerAddress;
    private String sellerAccount;
    private String payee;
    private String reviewer;
    private String drawer;
    private String type;
    private List<Detail> detailList;

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the machineNumber
     */
    public String getMachineNumber() {
        return machineNumber;
    }

    /**
     * @param machineNumber
     *            the machineNumber to set
     */
    public void setMachineNumber(String machineNumber) {
        this.machineNumber = machineNumber;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code
     *            the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the number
     */
    public String getNumber() {
        return number;
    }

    /**
     * @param number
     *            the number to set
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date
     *            the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the checksum
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * @param checksum
     *            the checksum to set
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    /**
     * @return the buyerName
     */
    public String getBuyerName() {
        return buyerName;
    }

    /**
     * @param buyerName
     *            the buyerName to set
     */
    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    /**
     * @return the buyerInvoiceCode
     */
    public String getBuyerCode() {
        return buyerCode;
    }

    /**
     * @param buyerCode
     *            the buyerCode to set
     */
    public void setBuyerCode(String buyerCode) {
        this.buyerCode = buyerCode;
    }

    /**
     * @return the buyerAddress
     */
    public String getBuyerAddress() {
        return buyerAddress;
    }

    /**
     * @param buyerAddress
     *            the buyerAddress to set
     */
    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    /**
     * @return the buyerAccount
     */
    public String getBuyerAccount() {
        return buyerAccount;
    }

    /**
     * @param buyerAccount
     *            the buyerAccount to set
     */
    public void setBuyerAccount(String buyerAccount) {
        this.buyerAccount = buyerAccount;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @param amount
     *            the amount to set
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return the taxAmount
     */
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    /**
     * @param taxAmount
     *            the taxAmount to set
     */
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    /**
     * @return the totalAmountString
     */
    public String getTotalAmountString() {
        return totalAmountString;
    }

    /**
     * @param totalAmountString
     *            the totalAmountString to set
     */
    public void setTotalAmountString(String totalAmountString) {
        this.totalAmountString = totalAmountString;
    }

    /**
     * @return the totalAmount
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * @param totalAmount
     *            the totalAmount to set
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * @return the sellerName
     */
    public String getSellerName() {
        return sellerName;
    }

    /**
     * @param sellerName
     *            the sellerName to set
     */
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    /**
     * @return the sellerCode
     */
    public String getSellerCode() {
        return sellerCode;
    }

    /**
     * @param sellerCode
     *            the sellerCode to set
     */
    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    /**
     * @return the sellerAddress
     */
    public String getSellerAddress() {
        return sellerAddress;
    }

    /**
     * @param sellerAddress
     *            the sellerAddress to set
     */
    public void setSellerAddress(String sellerAddress) {
        this.sellerAddress = sellerAddress;
    }

    /**
     * @return the sellerAccount
     */
    public String getSellerAccount() {
        return sellerAccount;
    }

    /**
     * @param sellerAccount
     *            the sellerAccount to set
     */
    public void setSellerAccount(String sellerAccount) {
        this.sellerAccount = sellerAccount;
    }

    /**
     * @return the payee
     */
    public String getPayee() {
        return payee;
    }

    /**
     * @param payee
     *            the payee to set
     */
    public void setPayee(String payee) {
        this.payee = payee;
    }

    /**
     * @return the reviewer
     */
    public String getReviewer() {
        return reviewer;
    }

    /**
     * @param reviewer
     *            the reviewer to set
     */
    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    /**
     * @return the drawer
     */
    public String getDrawer() {
        return drawer;
    }

    /**
     * @param drawer
     *            the drawer to set
     */
    public void setDrawer(String drawer) {
        this.drawer = drawer;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the detailList
     */
    public List<Detail> getDetailList() {
        return detailList;
    }

    /**
     * @param detailList
     *            the detailList to set
     */
    public void setDetailList(List<Detail> detailList) {
        this.detailList = detailList;
    }

    @Override
    public String toString() {
        return "Invoice [title=" + title + ", machineNumber=" + machineNumber + ", code=" + code + ", number=" + number
                + ", date=" + date + ", checksum=" + checksum + ", buyerName=" + buyerName + ", buyerCode=" + buyerCode
                + ", buyerAddress=" + buyerAddress + ", buyerAccount=" + buyerAccount + ", password=" + password + ", amount="
                + amount + ", taxAmount=" + taxAmount + ", totalAmountString=" + totalAmountString + ", totalAmount="
                + totalAmount + ", sellerName=" + sellerName + ", sellerCode=" + sellerCode + ", sellerAddress=" + sellerAddress
                + ", sellerAccount=" + sellerAccount + ", payee=" + payee + ", reviewer=" + reviewer + ", drawer=" + drawer
                + ", type=" + type + ", detailList=" + detailList + "]";
    }
}

class Detail {
    private String name;
    private String model;
    private String unit;
    private BigDecimal count;
    private BigDecimal price;
    private BigDecimal amount;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * @param model
     *            the model to set
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit
     *            the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @return the count
     */
    public BigDecimal getCount() {
        return count;
    }

    /**
     * @param count
     *            the count to set
     */
    public void setCount(BigDecimal count) {
        this.count = count;
    }

    /**
     * @return the price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * @param price
     *            the price to set
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @param amount
     *            the amount to set
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return the taxRate
     */
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    /**
     * @param taxRate
     *            the taxRate to set
     */
    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    /**
     * @return the taxAmount
     */
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    /**
     * @param taxAmount
     *            the taxAmount to set
     */
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    @Override
    public String toString() {
        return "Detail [name=" + name + ", model=" + model + ", unit=" + unit + ", count=" + count + ", price=" + price
                + ", amount=" + amount + ", taxRate=" + taxRate + ", taxAmount=" + taxAmount + "]";
    }
}
