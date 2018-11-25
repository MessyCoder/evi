package jp.co.starsoft.servlet;

import jp.co.starsoft.util.FileIO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@WebServlet(name = "ImageUpload", urlPatterns = "/upload_image")
public class ImageUploadServlet extends GenericEvidenceServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setHeader("Content-Type", "application/json; charset=UTF-8");
        String evidence = req.getParameter("evidence");
        String sheetName = req.getParameter("sheetName");

        File evidenceFolder = new File(getEvidencePath(), evidence);
        File sheetFolder = new File(evidenceFolder, sheetName);
        TreeMap<String, String> imageList = FileIO.getImageList(sheetFolder);


        resp.getWriter().write(GSON.toJson(imageList));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setHeader("Content-Type", "application/json; charset=UTF-8");
        String evidence = req.getParameter("evidence");
        String sheetName = req.getParameter("sheetName");


        String image_data = req.getParameter("image_data");
        //System.out.println(image_data);
//
//        FileOutputStream fileOutputStream = new FileOutputStream("c:\\ICARD\\image.png");
//        fileOutputStream.write(imageData);
//        fileOutputStream.close();
        Map<String, String> result = new HashMap<>();
        File evidenceFolder = new File(getEvidencePath(), evidence);

        if (!evidenceFolder.exists()) {
            if (!evidenceFolder.mkdirs()) {
                result.put("message", "failed");
            }
        }

        File sheetFolder = new File(evidenceFolder, sheetName);
        if (!sheetFolder.exists()) {
            if (!sheetFolder.mkdir()) {
                result.put("message", "failed");
            }
        }

        String displayName = FileIO.addNewImage(sheetFolder, image_data);

        result.put("message", "ok");
        result.put("displayName", displayName);

        resp.getWriter().write(GSON.toJson(result));

    }
}
