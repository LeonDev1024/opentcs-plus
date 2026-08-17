package org.opentcs.map.utils;

/**
 * 地图版本工具类
 * @author lyc
 */
public class MapVersionUtil {

    /**
     * minor 版本阈值，超过此值自动升级 major 版本
     */
    private static final int MINOR_THRESHOLD = 20;

    /**
     * 获取下一个版本号（自动递增 minor）
     * 规则：1.0 → 1.1 → ... → 1.20 → 2.0 → 2.1 → ...
     * @param currentVersion 当前版本号
     * @return 下一个版本号
     */
    public static String getNextVersion(String currentVersion) {
        if (currentVersion == null || currentVersion.isEmpty()) {
            return "1.0";
        }

        String[] parts = currentVersion.split("\\.");
        if (parts.length != 2) {
            return "1.0";
        }

        try {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);

            // minor 超过阈值则升级 major
            if (minor >= MINOR_THRESHOLD) {
                return String.format("%d.0", major + 1);
            } else {
                return String.format("%d.%d", major, minor + 1);
            }
        } catch (NumberFormatException e) {
            return "1.0";
        }
    }

    /**
     * 判断是否为首次版本
     * @param currentVersion 当前版本号
     * @return 是否为首次版本
     */
    public static boolean isFirstVersion(String currentVersion) {
        return currentVersion == null || currentVersion.isEmpty() || "1.0".equals(currentVersion);
    }
}
