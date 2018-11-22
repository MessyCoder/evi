package jp.co.starsoft.servlet;

import com.google.gson.Gson;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@WebServlet(name = "EvidenceServlet", urlPatterns = "/evidence")
public class EvidenceServlet extends HttpServlet {

    private static final Gson GSON = new Gson();

    private String propertiesFilePath;

    @Override
    public void init() throws ServletException {
        super.init();
        propertiesFilePath = getServletContext().getRealPath("WEB-INF/config.properties");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String[] folderList = new File(getEvidencePath()).list((dir, name) -> !dir.isFile());

        req.setAttribute("folderList", folderList);

        req.getRequestDispatcher("evidence.jsp").forward(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String evidence = req.getParameter("evidence");

        if (evidence == null || evidence.trim().length() == 0) {
            return;
        }

        File evidenceFolder = new File(getEvidencePath(), evidence);

        List<String> content = new ArrayList<>();
        try (BufferedReader reader =
        new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(
                                new File(evidenceFolder, "content.txt")), StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {
//                char flg = line.charAt(0);
//                String name = line.substring(1);

                content.add(line);
            }

        }

//        new FileReader(new File(evidenceFolder, "content.txt"), "UTF-8")

        String[] folderList = new File(getEvidencePath(), evidence).list((dir, name) -> !dir.isFile());

        resp.setHeader("Content-Type", "application/json; charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        writer.write(GSON.toJson(content));

    }


    private String getEvidencePath() throws IOException {

        Properties properties = new Properties();

        try (Reader reader = new InputStreamReader(new FileInputStream(propertiesFilePath), StandardCharsets.UTF_8)) {
            properties.load(reader);
        }

        return properties.getProperty("evidence_path");
    }
}
