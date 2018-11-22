package jp.co.starsoft.servlet;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Enumeration;

@WebServlet(name = "ImageUpload", urlPatterns = "/upload_image")
public class ImageUploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


//        Enumeration<String> headerNames = req.getHeaderNames();
//
//        while (headerNames.hasMoreElements()) {
//            String header = headerNames.nextElement();
//
//            System.out.println(header + ": " + req.getHeader(header));
//        }
//
//        ServletInputStream inputStream = req.getInputStream();
//
//        byte[] data = new byte[2048];
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        int length;
//
//        while ((length = inputStream.read(data)) != -1) {
//            byteArrayOutputStream.write(data, 0, length);
//        }
//
//        System.out.println(new String(byteArrayOutputStream.toByteArray()));
//        byteArrayOutputStream.close();

        String image_data = req.getParameter("image_data");
        //System.out.println(image_data);
        byte[] imageData = Base64.getDecoder().decode(image_data);

        FileOutputStream fileOutputStream = new FileOutputStream("c:\\ICARD\\image.png");
        fileOutputStream.write(imageData);
        fileOutputStream.close();

        resp.getWriter().write(image_data);

    }
}
