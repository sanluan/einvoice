package einvoice;

import java.io.File;
import java.io.IOException;

import com.sanluan.einvoice.service.PdfInvoiceExtractor;

public class Test {

    public static void main(String[] args) {
        try {
            System.out.println(
                    PdfInvoiceExtractor.extract(new File("D:\\Users\\kerneler\\OneDrive\\桌面\\18-01-490139.pdf")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
