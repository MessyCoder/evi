package jp.co.starsoft.servlet;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import jp.co.starsoft.module.sqltester.SQLFileTestProcess;

@WebServlet(name = "FileUpload", urlPatterns = "/upload")
public class FileUploadServlet extends HttpServlet {
    static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean isMultipart = ServletFileUpload.isMultipartContent(req);

        if (!isMultipart) {
            return;
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Configure a repository (to ensure a secure temp location is used)
        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        // Parse the request
        try {
            List<FileItem> items = upload.parseRequest(req);
            if (items.size() == 0) {
                return;
            }

            SQLFileTestProcess sqlFileTestProcess = new SQLFileTestProcess();
            sqlFileTestProcess.load(items.get(0).getInputStream());

            sqlFileTestProcess.runTest();

            req.setAttribute("sql_entries", sqlFileTestProcess.getReport());
            req.setAttribute("disorderedSQLID", sqlFileTestProcess.getDisorderedSQLID());
            req.setAttribute("non9bitsSQLID", sqlFileTestProcess.getNon9bitsSQLID());

            req.getRequestDispatcher("WEB-INF/jsp/test_result.jsp").forward(req, resp);

        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }

        
    }
}