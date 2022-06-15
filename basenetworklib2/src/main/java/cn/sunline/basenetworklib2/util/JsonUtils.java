package cn.sunline.basenetworklib2.util;

/**
 * <p>文件描述：<p>
 * <p>作者: zengll<p>
 * <p>创建时间：2018/8/22<p>
 */
public class JsonUtils {

    /**
     * 去掉包裹JsonArray的双引号
     */
    public static String removeJsonArrayQuote(String srcJson){
        String r1 = "\"\\[";
        String r2 = "\\]\"";


        String result =  srcJson.replaceAll(r1, "\\[").replaceAll(r2, "\\]");

        return result;
    }

    public static String removeJsonObjectQuote(String srcJson){
        String r1 = "\"\\{";
        String r2 = "\\}\"";


        String result =  srcJson.replaceAll(r1, "\\{").replaceAll(r2, "\\}");

        return result;
    }

    public static String formatJson(String srcJson){
        return removeJsonObjectQuote(removeJsonArrayQuote(srcJson)).replaceAll("\\\\","");
    }

}
