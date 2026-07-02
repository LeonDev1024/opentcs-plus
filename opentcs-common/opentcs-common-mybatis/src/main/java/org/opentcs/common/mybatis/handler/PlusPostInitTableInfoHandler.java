package org.opentcs.common.mybatis.handler;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.handlers.PostInitTableInfoHandler;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.session.Configuration;
import org.opentcs.common.core.utils.SpringUtils;
import org.opentcs.common.core.utils.reflect.ReflectUtils;

/**
 * 修改表信息初始化方式
 * 目前用于：逻辑删除开关、业务表 tcs_ 前缀
 */
public class PlusPostInitTableInfoHandler implements PostInitTableInfoHandler {

    private static final String SYS_TABLE_PREFIX = "sys_";
    private static final String TCS_TABLE_PREFIX = "tcs_";

    @Override
    public void postTableInfo(TableInfo tableInfo, Configuration configuration) {
        String flag = SpringUtils.getProperty("mybatis-plus.enableLogicDelete", "true");
        if (!Convert.toBool(flag)) {
            ReflectUtils.setFieldValue(tableInfo, "withLogicDelete", false);
        }
        applyTcsTablePrefix(tableInfo);
    }

    /**
     * 非 RuoYi 系统表（sys_*）统一映射为 tcs_*，与 Flyway schema 保持一致。
     */
    private void applyTcsTablePrefix(TableInfo tableInfo) {
        String tableName = tableInfo.getTableName();
        if (tableName == null || tableName.isBlank()) {
            return;
        }
        if (tableName.startsWith(SYS_TABLE_PREFIX)
            || tableName.startsWith(TCS_TABLE_PREFIX)
            || tableName.startsWith("flyway_")) {
            return;
        }
        ReflectUtils.setFieldValue(tableInfo, "tableName", TCS_TABLE_PREFIX + tableName);
    }

}
