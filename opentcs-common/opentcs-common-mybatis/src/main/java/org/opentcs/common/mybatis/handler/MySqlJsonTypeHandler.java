package org.opentcs.common.mybatis.handler;


import cn.hutool.json.JSONUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//MySQL JSON类型处理器
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class MySqlJsonTypeHandler extends BaseTypeHandler<Object> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Object o, JdbcType jdbcType) throws SQLException {
        if (preparedStatement != null) {
            // 直接设置JSON字符串
            String jsonStr = JSONUtil.toJsonStr(o);
            preparedStatement.setString(i, jsonStr);
        }
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String value = resultSet.getString(s);
        if (value == null) {
            return null;
        }
        return JSONUtil.parse(value);
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String value = resultSet.getString(i);
        if (value == null) {
            return null;
        }
        return JSONUtil.parse(value);
    }

    @Override
    public Object getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String value = callableStatement.getString(i);
        if (value == null) {
            return null;
        }
        return JSONUtil.parse(value);
    }
}
