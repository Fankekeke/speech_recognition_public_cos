package cc.mrbird.febs.cos.analysis;

import cc.mrbird.febs.common.config.MinioConfig;
import cc.mrbird.febs.cos.entity.VoiceFeature;
import cc.mrbird.febs.cos.service.IVoiceFeatureService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import okio.ByteString;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class IgrRecogntion {
    private static final String hostUrl = "https://ws-api.xfyun.cn/v2/igr"; //http url 不支持解析 ws/wss schema
    private static final String appid = "49952288";
    private static final String apiSecret = "NTQzZTdmYzRkYTRkM2JhODM0NWQ3Zjk3";
    private static final String apiKey = "7f5b179869b361d73448b9224e2a21d6";
    public static final int StatusFirstFrame = 0;
    public static final int StatusContinueFrame = 1;
    public static final int StatusLastFrame = 2;
    public static final Gson json = new Gson();

    @Resource
    private MinioConfig minioConfig;


    /**
     * 获取minio的文件识别年龄性别
     *
     * @param fileName
     */
    public void processByMinioFileName(String fileName, TaskCallBack taskCallBack) {
        try {
            String saveLocalPath = minioConfig.getObjectSaveTmpLocal(fileName);
            recogntion(saveLocalPath, taskCallBack);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 文件流形式识别年龄性别
     * @param fileInputStream
     * @param taskCallBack
     */
    public void processByFileInputStream(FileInputStream fileInputStream, TaskCallBack taskCallBack) {
        try {
            recogntion(fileInputStream, taskCallBack);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 文件流形式调用
     * @param fileInputStream
     * @param taskCallBack
     * @throws Exception
     */
    private static void recogntion(FileInputStream fileInputStream, TaskCallBack taskCallBack) throws Exception {
        String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
        OkHttpClient client = new OkHttpClient.Builder().build();
        String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();
        WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                System.out.println("receive=>" + text);
                taskCallBack.onComplete(text);
                ResponseData resp = json.fromJson(text, ResponseData.class);
                if (resp != null) {
                    if (resp.getCode() != 0) {
                        System.out.println("error=>" + resp.getMessage() + " sid=" + resp.getSid());
                        return;
                    }
                    if (resp.getData() != null) {
                        if (resp.getData().getStatus() == 2) {
                            // todo  resp.data.status ==2 说明数据全部返回完毕，可以关闭连接，释放资源
                            System.out.println("session end ");
                            webSocket.close(1000, "");
                        } else {
                            // todo 根据返回的数据处理
                        }
                    }
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                System.out.println("socket closing");
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                System.out.println("socket closed");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                System.out.println("connection failed");
            }
        });
        int frameSize = 1280; //每一帧音频的大小
        int intervel = 40;  //发送音频时间间隔
        int status = 0;  // 音频的状态
        //FileInputStream fs = new FileInputStream("0.pcm");
        try (FileInputStream fs = fileInputStream) {
            byte[] buffer = new byte[frameSize];
            // 发送音频
            end:
            while (true) {
                int len = fs.read(buffer);
                if (len == -1) {
                    status = StatusLastFrame;  //文件读完，改变status 为 2
                }
                switch (status) {
                    case StatusFirstFrame:   // 第一帧音频status = 0
                        JsonObject frame = new JsonObject();
                        JsonObject business = new JsonObject();  //第一帧必须发送
                        JsonObject common = new JsonObject();  //第一帧必须发送
                        JsonObject data = new JsonObject();  //每一帧都要发送
                        // 填充common
                        common.addProperty("app_id", appid);
                        //填充business
                        business.addProperty("aue", "raw");
                        business.addProperty("rate", "16000");
                        //填充data
                        data.addProperty("status", StatusFirstFrame);
                        data.addProperty("audio", Base64.getEncoder().encodeToString(Arrays.copyOf(buffer, len)));
                        //填充frame
                        frame.add("common", common);
                        frame.add("business", business);
                        frame.add("data", data);
                        webSocket.send(frame.toString());
                        status = StatusContinueFrame;  // 发送完第一帧改变status 为 1
                        break;
                    case StatusContinueFrame:  //中间帧status = 1
                        JsonObject frame1 = new JsonObject();
                        JsonObject data1 = new JsonObject();
                        data1.addProperty("status", StatusContinueFrame);
                        data1.addProperty("audio", Base64.getEncoder().encodeToString(Arrays.copyOf(buffer, len)));
                        frame1.add("data", data1);
                        webSocket.send(frame1.toString());
                        break;
                    case StatusLastFrame:    // 最后一帧音频status = 2 ，标志音频发送结束
                        JsonObject frame2 = new JsonObject();
                        JsonObject data2 = new JsonObject();
                        data2.addProperty("status", StatusLastFrame);
                        data2.addProperty("audio", "");
                        frame2.add("data", data2);
                        webSocket.send(frame2.toString());
                        break end;
                }
                Thread.sleep(intervel); //模拟音频采样延时
            }
            System.out.println("all data is send");
        }
    }

    /**
     * 本地路径形式调用
     * @param filePath
     * @param taskCallBack
     * @throws Exception
     */
    private static void recogntion(String filePath, TaskCallBack taskCallBack) throws Exception {
        String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
        OkHttpClient client = new OkHttpClient.Builder().build();
        String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();
        WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                System.out.println("receive=>" + text);
                taskCallBack.onComplete(text);
                ResponseData resp = json.fromJson(text, ResponseData.class);
                if (resp != null) {
                    if (resp.getCode() != 0) {
                        System.out.println("error=>" + resp.getMessage() + " sid=" + resp.getSid());
                        return;
                    }
                    if (resp.getData() != null) {
                        if (resp.getData().getStatus() == 2) {
                            // todo  resp.data.status ==2 说明数据全部返回完毕，可以关闭连接，释放资源
                            System.out.println("session end ");
                            webSocket.close(1000, "");
                        } else {
                            // todo 根据返回的数据处理
                        }
                    }
                }
            }


            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                System.out.println("socket closing");
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                System.out.println("socket closed");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                System.out.println("connection failed");
            }
        });
        int frameSize = 1280; //每一帧音频的大小
        int intervel = 40;  //发送音频时间间隔
        int status = 0;  // 音频的状态
        //FileInputStream fs = new FileInputStream("0.pcm");
        try (FileInputStream fs = new FileInputStream(filePath)) {
            byte[] buffer = new byte[frameSize];
            // 发送音频
            end:
            while (true) {
                int len = fs.read(buffer);
                if (len == -1) {
                    status = StatusLastFrame;  //文件读完，改变status 为 2
                }
                switch (status) {
                    case StatusFirstFrame:   // 第一帧音频status = 0
                        JsonObject frame = new JsonObject();
                        JsonObject business = new JsonObject();  //第一帧必须发送
                        JsonObject common = new JsonObject();  //第一帧必须发送
                        JsonObject data = new JsonObject();  //每一帧都要发送
                        // 填充common
                        common.addProperty("app_id", appid);
                        //填充business
                        business.addProperty("aue", "raw");
                        business.addProperty("rate", "16000");
                        //填充data
                        data.addProperty("status", StatusFirstFrame);
                        data.addProperty("audio", Base64.getEncoder().encodeToString(Arrays.copyOf(buffer, len)));
                        //填充frame
                        frame.add("common", common);
                        frame.add("business", business);
                        frame.add("data", data);
                        webSocket.send(frame.toString());
                        status = StatusContinueFrame;  // 发送完第一帧改变status 为 1
                        break;
                    case StatusContinueFrame:  //中间帧status = 1
                        JsonObject frame1 = new JsonObject();
                        JsonObject data1 = new JsonObject();
                        data1.addProperty("status", StatusContinueFrame);
                        data1.addProperty("audio", Base64.getEncoder().encodeToString(Arrays.copyOf(buffer, len)));
                        frame1.add("data", data1);
                        webSocket.send(frame1.toString());
                        break;
                    case StatusLastFrame:    // 最后一帧音频status = 2 ，标志音频发送结束
                        JsonObject frame2 = new JsonObject();
                        JsonObject data2 = new JsonObject();
                        data2.addProperty("status", StatusLastFrame);
                        data2.addProperty("audio", "");
                        frame2.add("data", data2);
                        webSocket.send(frame2.toString());
                        break end;
                }
                Thread.sleep(intervel); //模拟音频采样延时
            }
            System.out.println("all data is send");
        }
    }

    /**
     * 讯飞鉴权
     * @param hostUrl
     * @param apiKey
     * @param apiSecret
     * @return
     * @throws Exception
     */
    private static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        StringBuilder builder = new StringBuilder("host: ").append(url.getHost()).append("\n").//
                append("date: ").append(date).append("\n").//
                append("GET ").append(url.getPath()).append(" HTTP/1.1");
        Charset charset = Charset.forName("UTF-8");
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(charset), "hmacsha256");
        mac.init(spec);
        byte[] hexDigits = mac.doFinal(builder.toString().getBytes(charset));
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        HttpUrl httpUrl = HttpUrl.parse("https://" + url.getHost() + url.getPath()).newBuilder()
                .addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(charset))).//
                        addQueryParameter("date", date)
                .addQueryParameter("host", url.getHost())
                .build();
        return httpUrl.toString();
    }

    public static class ResponseData {
        private int code;
        private String message;
        private String sid;
        private Data data;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return this.message;
        }

        public String getSid() {
            return sid;
        }

        public Data getData() {
            return data;
        }
    }

    public static class Data {
        private int status;
        private Object result;

        public int getStatus() {
            return status;
        }

        public Object getResult() {
            return result;
        }
    }
}