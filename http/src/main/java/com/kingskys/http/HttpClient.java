package com.kingskys.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {
    public HttpResult postData(String urlpath, String data, Map<String, String> headers) throws IOException {
        OutputStream outputStream = null;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(urlpath);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(6000);
            connection.setReadTimeout(10000);

            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            outputStream = connection.getOutputStream();

            if (data != null) {
                outputStream.write(data.getBytes());
            }

            // 获取服务器响应码
            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                return new HttpResult(true, code, readInputStream(connection.getInputStream()));
            }

            return new HttpResult(false, code, null);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ignored) {}

            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    public HttpResult getData(String urlpath) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("contentType", "utf-8");
        headers.put("Accept-Charset", "utf-8");
        return getData(urlpath, headers);
    }

    public HttpResult getData(String urlpath, Map<String, String> headers) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlpath);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000);
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            // 获取服务器响应码
            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                return new HttpResult(true, code, readInputStream(connection.getInputStream()));
            }

            return new HttpResult(false, code, null);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    private String readInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

        } catch (IOException e) {
            try {
                inputStream.close();;
            } catch (IOException ignored) {}
            throw e;
        }

        try {
            inputStream.close();;
        } catch (IOException ignored) {}

        return builder.toString();
    }


}
