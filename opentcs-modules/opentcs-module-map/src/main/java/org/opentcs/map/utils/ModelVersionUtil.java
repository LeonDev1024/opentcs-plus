package org.opentcs.map.utils;

public class ModelVersionUtil {

    /**
     * 版本号升级规则
     * @param modelVersion 当前版本号
     * @return 升级后的版本号
     */
    public static String getNextModelVersion(String modelVersion) {
        if (modelVersion == null) {
            return "1.0";
        }
        String[] versionParts = modelVersion.split("\\.");
        int majorVersion = Integer.parseInt(versionParts[0]);
        int minorVersion = Integer.parseInt(versionParts[1]);
        return String.format("%d.%d", majorVersion, minorVersion + 1);
    }
}
