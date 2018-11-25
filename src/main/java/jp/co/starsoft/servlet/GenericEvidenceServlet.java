package jp.co.starsoft.servlet;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class GenericEvidenceServlet extends HttpServlet {
    static final Gson GSON = new Gson();
    private File evidencePath;

    @Override
    public void init() throws ServletException {
        super.init();

        String propertiesFilePath = getServletContext().getRealPath("WEB-INF/config.properties");
        Properties properties = new Properties();

        try (Reader reader = new InputStreamReader(new FileInputStream(propertiesFilePath), StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        evidencePath = new File(properties.getProperty("evidence_path"));
    }

    File getEvidencePath() {
        return evidencePath;
    }
}
