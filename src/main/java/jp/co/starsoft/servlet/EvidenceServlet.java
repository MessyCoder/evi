package jp.co.starsoft.servlet;

import com.google.gson.Gson;
import jp.co.starsoft.util.FileIO;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

@WebServlet(name = "EvidenceServlet", urlPatterns = "/evidence")
public class EvidenceServlet extends GenericEvidenceServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setAttribute("folderList", FileIO.getDirectoryContent(getEvidencePath()));

        req.getRequestDispatcher("evidence.jsp").forward(req, resp);
    }


    /**
     * 指定のエビデンスのシート内容を取得する。
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String requestId = req.getParameter("requestId");
        resp.setHeader("Content-Type", "application/json; charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        if ("1".equals(requestId)) {
            String evidence = req.getParameter("evidence");

            writer.write(FileIO.getDirectoryContentAsJson(new File(getEvidencePath(), evidence)));
        } else {

            String evidence = req.getParameter("evidence");
            String sheetName = req.getParameter("sheetName");

            Map<String, String> result = new HashMap<>();
            try {
                boolean success = false;
                if ("2".equals(requestId)) {
                    success = FileIO.createNewSheet(new File(getEvidencePath(), evidence), sheetName);
                } else if ("4".equals(requestId)) {
                    success = FileIO.createNewSeparator(new File(getEvidencePath(), evidence), sheetName);
                }


                if (success) {
                    result.put("message", "ok");
                } else {
                    result.put("message", "failed");
                }
            } catch (IOException ex) {
//                writer.write("{\"message\": \"failed\"}");
                result.put("message", ex.getMessage());
            }

            writer.write(GSON.toJson(result));

        }







    }


}
