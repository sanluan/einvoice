package einvoice;

import java.io.File;
import java.io.IOException;

import org.dom4j.DocumentException;

import com.sanluan.einvoice.service.OfdInvoiceExtractor;

public class Test {

    public static void main(String[] args) {
        try {
            System.out.println(
                    OfdInvoiceExtractor.extract(new File("D:\\012001900311_34063477.ofd")));
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }

}
