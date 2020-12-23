package com.sanluan.einvoice.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sanluan.einvoice.service.Invoice;
import com.sanluan.einvoice.service.InvoiceExtractor;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    @Value("${backupPath}")
    private String backupPath;

    private static ThreadLocal<Map<String, DateFormat>> threadLocal = new ThreadLocal<>();
    private static final String FILE_NAME_FORMAT_STRING = "yyyy/MM-dd/HH-mm-ssSSSS";
    public static final RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000)
            .setConnectionRequestTimeout(5000).build();

    /**
     * @param pattern
     * @return date format
     */
    public static DateFormat getDateFormat(String pattern) {
        Map<String, DateFormat> map = threadLocal.get();
        DateFormat format = null;
        if (null == map) {
            map = new HashMap<>();
            format = new SimpleDateFormat(pattern);
            map.put(pattern, format);
            threadLocal.set(map);
        } else {
            format = map.computeIfAbsent(pattern, k -> new SimpleDateFormat(k));
        }
        return format;
    }

    @RequestMapping(value = "/extrat")
    public Invoice extrat(@RequestParam("file") MultipartFile file, String url) {
        String fileName = getDateFormat(FILE_NAME_FORMAT_STRING).format(new Date());
        File dest = new File(backupPath, fileName + ".pdf");
        dest.getParentFile().mkdirs();
        if (!file.isEmpty()) {
            try {
                FileUtils.copyInputStreamToFile(file.getInputStream(), dest);
            } catch (IOException e) {
            }
        } else if (null != url) {
            try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();) {
                HttpUriRequest request = new HttpGet(url);
                try (CloseableHttpResponse response = httpclient.execute(request)) {
                    HttpEntity entity = response.getEntity();
                    if (null != entity) {
                        BufferedInputStream inputStream = new BufferedInputStream(entity.getContent());
                        FileUtils.copyInputStreamToFile(inputStream, dest);
                        EntityUtils.consume(entity);
                    }
                }
            } catch (Exception e) {
            }
        }
        Invoice result = null;
        try {
            result = InvoiceExtractor.extract(dest);
            dest.delete();
        } catch (IOException e) {
            result = new Invoice();
            result.setTitle("error");
        }
        return result;
    }
}
