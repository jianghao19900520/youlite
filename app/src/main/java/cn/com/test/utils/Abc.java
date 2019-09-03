package cn.com.test.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import org.litepal.util.LogUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

public class Abc {



    /**
     * 可传入多张图片和参数
     * @throws IOException
     */
    public static String mulpost(String actionUrl,
                                 File file) throws IOException {
        String result = "";
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--";
        String CRLF = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";

        StringBuffer headBuffer = new StringBuffer(); //构建文件头部信息
        headBuffer.append(PREFIX);
        headBuffer.append(BOUNDARY);
        headBuffer.append(CRLF);
        headBuffer.append("Content-Disposition: form-data; name=\"" + "upload_file" + "\"; filename=\"" + file.getName() + "\"" + CRLF);//模仿web上传文件提交一个form表单给服务器，表单名随意起
        headBuffer.append("Content-Type: application/octet-stream" + CRLF);//若服务器端有文件类型的校验，必须明确指定Content-Type类型
        headBuffer.append(CRLF);
        Log.i("", headBuffer.toString());
        byte[] headBytes = headBuffer.toString().getBytes();

        StringBuffer endBuffer = new StringBuffer();//构建文件结束行
        endBuffer.append(CRLF);
        endBuffer.append(PREFIX);
        endBuffer.append(BOUNDARY);
        endBuffer.append(PREFIX);
        endBuffer.append(CRLF);
        byte[] endBytes = endBuffer.toString().getBytes();

        URL uri = new URL(actionUrl);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(5 * 1000);
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false);
        conn.setRequestMethod("POST"); // Post方式
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                + ";boundary=" + BOUNDARY);

//        // 首先组拼文本类型的参数
//        StringBuilder sb = new StringBuilder();
//        for (Map.Entry<String, String> entry : params.entrySet()) {
//            sb.append(PREFIX);
//            sb.append(BOUNDARY);
//            sb.append(LINEND);
//            sb.append("Content-Disposition: form-data; name=\""
//                    + entry.getKey() + "\"" + LINEND);
//            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
//            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
//            sb.append(LINEND);
//            sb.append(entry.getValue());
//            sb.append(LINEND);
//        }

        DataOutputStream outStream = new DataOutputStream(
                conn.getOutputStream());
//        outStream.write(sb.toString().getBytes());

        // 发送文件数据

        outStream.write(headBytes);//输出文件头部

        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fileInputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, length);//输出文件内容
        }

        fileInputStream.close();

        outStream.write(endBytes);//输出结束行
        outStream.close();

        InputStream is = conn.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "utf-8");
        BufferedReader br = new BufferedReader(isr);
        result = br.readLine();
        outStream.close();
        conn.disconnect();
        return result;
    }


}
