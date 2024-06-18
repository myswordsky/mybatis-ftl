package cn.xzb.mybatis.gen.utils;


import cn.hutool.core.util.RandomUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileBootUtil {

    public static void writeFile(String urlName, String content) throws IOException {
        File file = new File(urlName);
        if (!file.exists()) {
            file.createNewFile();
        } else {
            file.delete();
        }
        BufferedWriter out = new BufferedWriter(new FileWriter(urlName));
        out.write(content); // \r\n即为换行
        out.flush(); // 把缓存区内容压入文件
        out.close(); // 最后记得关闭文件
    }

    public static void createTempFile(String url, String fileName) {
        ClassPathResource classPathResource = new ClassPathResource(url);//url == mybatis/xxx.xxx
        File somethingFile = null;
        InputStream inputStream = null;
        try {
            inputStream = classPathResource.getInputStream();
            try {
                somethingFile = File.createTempFile(fileName.split("\\.")[0], fileName.split("\\.").length == 1 ? "" : fileName.split("\\.")[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert somethingFile != null;
            FileUtils.copyInputStreamToFile(inputStream, somethingFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static String getStringByInputStream(String url) throws FileNotFoundException {
        return getStringByInputStream(new FileInputStream(url));
    }

    public static String getStringByInputStream(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining(System.lineSeparator()));
    }

    public static String getFileString(String url) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(url));
            String str;
            StringBuilder sb = new StringBuilder();
            while ((str = in.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        } catch (IOException e) {
        }
        return null;
    }

    public static String getFileStringLine(String url) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(url));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            String ls = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            reader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
        }
        return null;
    }

    public static File asFile(InputStream inputStream) throws IOException {
        File tmp = File.createTempFile(System.currentTimeMillis() + RandomUtil.randomString(6), ".png", new File("D:\\temp"));
        OutputStream os = new FileOutputStream(tmp);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        return tmp;
    }

    public static byte[] toByteArray(InputStream input) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 4];
            int n;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            return output.toByteArray();
        } catch (Exception e) {
            System.out.println();
        }
        return null;
    }

    public static List<String> listFileUrl(String dir, String suffix) {
        File fileDir = new File(dir);
        File[] files = fileDir.listFiles();
        assert files != null;
        List<String> collect = Arrays.stream(files).map(File::getAbsolutePath).collect(Collectors.toList());
        if (suffix == null) {
            return collect;
        }
        return collect.stream().filter(e -> e.endsWith(suffix)).collect(Collectors.toList());
    }

    /**
     * xx\\xxx\\aaa.java  -> aaa
     *
     * @param fileSrc /
     * @return /
     */
    public static String getFileName(String fileSrc) {
        int i = fileSrc.lastIndexOf(File.separator);
        int i1 = fileSrc.lastIndexOf(".");
        return fileSrc.substring(i + 1, i1);
    }
}
