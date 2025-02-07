package cc.mrbird.febs.cos.recognition.iflytek;

import cc.mrbird.febs.cos.analysis.VoiceprintRecognition;

/**
 * 1.声纹识别接口,请填写在讯飞开放平台-控制台-对应能力页面获取的APPID、APIKey、APISecret。
 * 2.groupId要先创建,然后再在createFeature里使用,不然会报错23005,修改时需要注意保持统一。
 * 3.音频base64编码后数据(不超过4M),音频格式需要16K、16BIT的MP3音频。
 * 4.主函数只提供调用示例,其他参数请到对应类去更改,以适应实际的应用场景。
 * 5.使用1:1或1:N功能请注意更换音频。
 */
public class VoiceprintRecognitionMain {
    private static String requestUrl = "https://api.xf-yun.com/v1/private/s782b4996";

    //控制台获取以下信息
    private static String APPID = "75143dad";
    private static String apiSecret = "NmFlMzQ4NmFjZDdhZjkxOWRhNDMzODRl";
    private static String apiKey = "019d848b43cfeacdb876b2d4fe430834";

    //音频存放位置(比对功能请注意更换音频)
//    private static String AUDIO_PATH = "G:\\录音\\20231220\\20231220144414-18548923250-1-0001Admin.Mp3";
    private static String AUDIO_PATH = "F:\\work\\Complaint-Platform\\temp_speaker2_seg21.mp3";

    public static void main(String[] args) throws Exception {
        VoiceprintRecognition.getVoiceFeaturesLength();
        /**1.创建声纹特征库*/
//        CreateGroup.doCreateGroup(requestUrl, APPID, apiSecret, apiKey);
        /**2.添加音频特征*/
//        CreateFeature.doCreateFeature(requestUrl, APPID, apiSecret, apiKey, AUDIO_PATH, "user001");
        /**3.查询特征列表*/
//        Map<String, String> params = new HashMap<String, String>() {
//            {
//                put("groupId", "voiceprint_complaint_group");
//            }
//        };
//        QueryFeatureList.doQueryFeatureList(requestUrl, APPID, apiSecret, apiKey, params);
        /**4.特征比对1:1*/
//        SearchOneFeature.doSearchOneFeature(requestUrl, APPID, apiSecret, apiKey, AUDIO_PATH);
        /**5.特征比对1:N*/
//        SearchFeature.doSearchFeature(requestUrl, APPID, apiSecret, apiKey, AUDIO_PATH);
        /**6.更新音频特征*/
        //UpdateFeature.doUpdateFeature(requestUrl,APPID,apiSecret,apiKey,AUDIO_PATH);
        /**7.删除指定特征*/
//        DeleteFeature.doDeleteFeature(requestUrl,APPID,apiSecret,apiKey);
        /**8.删除声纹特征库*/
//        DeleteGroup.doDeleteGroup(requestUrl,APPID,apiSecret,apiKey);
    }
}