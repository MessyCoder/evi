package jp.co.starsoft.util;

import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileIO {
    private static final Gson GSON = new Gson();
    private static final String CONTENT_TXT = "content.txt";

    public static List<String> loadTextFile(File targetFile) throws IOException {

        if (!targetFile.isFile()) {
            return Collections.emptyList();
        }

        List<String> content = new ArrayList<>();



        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(new FileInputStream(targetFile), StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {
                content.add(line);
            }
        }

        return content;
    }

    public static List<String> getDirectoryContent(File dir) throws IOException {

        if (!dir.isDirectory()) {
            throw new RuntimeException(dir.getPath() + " is not a directory.");
        }



        return loadTextFile(new File(dir, CONTENT_TXT));

    }

    public static String getDirectoryContentAsJson(File dir) throws IOException {

        return GSON.toJson(getDirectoryContent(dir));
    }

    public synchronized static boolean  createNewSheet(File dir, String sheetName) throws IOException{

        File newSheet = new File(dir, sheetName);

        if(newSheet.isDirectory()) {
            return false;
        }

        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream(new File(dir, CONTENT_TXT), true),
                StandardCharsets.UTF_8)){

            writer.write("1" + sheetName + "\r\n");
        }

        return newSheet.mkdirs();
    }

    public synchronized static boolean createNewSeparator(File dir, String separatorName) throws IOException{

        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream(new File(dir, CONTENT_TXT), true),
                StandardCharsets.UTF_8)){

            writer.write("0" + separatorName + "\r\n");
        }

        return true;
    }

    /**
     *
     * @param sheetDir
     * @param base64Data
     * @return image timestamp
     * @throws IOException
     */
    public synchronized static String addNewImage(File sheetDir, String base64Data) throws IOException{

        String timestamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream(new File(sheetDir, CONTENT_TXT), true),
                StandardCharsets.UTF_8)){

            writer.write(timestamp + "\r\n");
            writer.flush();
        }

        try (OutputStream outputStream = new FileOutputStream(
                new File(sheetDir, timestamp + ".png"), false)){

            byte[] imageData = Base64.getDecoder().decode(base64Data);
            outputStream.write(imageData);
            outputStream.flush();
        }

        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream(new File(sheetDir, timestamp + ".base64"), false),
                StandardCharsets.UTF_8)){

            writer.write(base64Data);
            writer.flush();
        }

        return timestamp;
    }


    public synchronized static TreeMap<String, String> getImageList(File sheetDir) throws IOException{

        TreeMap<String, String> map = new TreeMap<>();
        File file = new File(sheetDir, CONTENT_TXT);

        if (!file.exists()) {
            return map;
        }

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {
                File targetFile = new File(sheetDir, line + ".fabric.base64");
                if (!targetFile.isFile()) {
                    targetFile = new File(sheetDir, line + ".base64");
                }
                map.put(line, readAsString(targetFile));
            }
        }
        return map;
    }

    /**
     * 原始的な画像、およびその画像につながるFabricオブジェクトを読み取る。
     * @param sheetDir
     * @param imageDisplayName
     * @return
     */
    public static String loadImageInfo(File sheetDir, String imageDisplayName) throws IOException {

        Map<String, String> map = new HashMap<>();
        File originalImage = new File(sheetDir, imageDisplayName + ".base64");
        map.put("base64", readAsString(originalImage));

        File fabricObject = new File(sheetDir, imageDisplayName + ".fabric.obj");

        if (fabricObject.isFile()) {
            map.put("fabricObject", readAsString(originalImage));
        }

        return GSON.toJson(map);

    }


    public static String readAsString(File targetFile) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];
        int length;
        try (InputStream base64Reader = new FileInputStream(targetFile)) {

            while ((length = base64Reader.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }

        }

        return baos.toString();
    }
}
