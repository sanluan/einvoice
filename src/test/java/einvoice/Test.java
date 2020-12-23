package einvoice;

import java.io.File;
import java.io.IOException;

import com.sanluan.einvoice.service.InvoiceExtractor;

public class Test {

    public static void main(String[] args) {
        try {
            System.out.println(
                    InvoiceExtractor.extract(new File("D:\\Users\\kerneler\\OneDrive\\公司\\进项发票\\天津航天信息有限公司 -20200703-280.pdf")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
