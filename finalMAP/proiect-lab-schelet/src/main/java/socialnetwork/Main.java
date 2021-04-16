package socialnetwork;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendRequestDB;
import socialnetwork.repository.database.MessageDB;
import socialnetwork.repository.database.PrietenieDB;
import socialnetwork.repository.database.UserDB;
import socialnetwork.repository.file.FriendshipFile;
import socialnetwork.repository.file.UtilizatorFile;
import socialnetwork.service.MessageService;
import socialnetwork.service.RequestService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.ui.MainFX;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
//import socialnetwork.ui.UI;

public class Main {
    public static void main(String[] args) {

        //UI ui = new UI(srv, msrv, rsrv);
        MainFX gui = new MainFX();
        //gui.setServices(srv, msrv, rsrv);
        gui.main(args);
        /*
        try {
            com.itextpdf.text.Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream("pdf1.pdf"));

            document.open();
            Paragraph paragraph = new Paragraph("-------NEW FRIENDS FOR USER --");
            document.add(paragraph);
            PdfPTable table = new PdfPTable(2);
            table.addCell("id");
            table.addCell("data");
            document.close();
        } catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
        }
         */
    }
}


