package cc.mrbird.febs.common.utils;

/**
 * 路径管理工具类
 */
public class DirUtil {

    public static String projectRootDir = System.getProperty("user.dir");

    private final static String audioTmpPath = "/tmp";

    /**
     * 获取音频临时存储路径
     * @return
     */
    public static String getAudioTmpPath(){
        return projectRootDir + audioTmpPath;
    }


}
