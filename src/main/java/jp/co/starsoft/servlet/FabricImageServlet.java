package jp.co.starsoft.servlet;


import jp.co.starsoft.util.FileIO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "FabricImageServlet", urlPatterns = "/fabric_image")
public class FabricImageServlet extends GenericEvidenceServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setHeader("Content-Type", "application/json; charset=UTF-8");

        String evidence = req.getParameter("evidence");
        String sheetName = req.getParameter("sheetName");
        String displayName = req.getParameter("displayName");

        File evidenceFolder = new File(getEvidencePath(), evidence);
        File sheetFolder = new File(evidenceFolder, sheetName);
        String imageData = FileIO.loadImageInfo(sheetFolder, displayName);

        PrintWriter writer = resp.getWriter();
        writer.write(imageData);
    }
}
