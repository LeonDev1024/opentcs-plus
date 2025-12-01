package org.opentcs.common.mybatis.handler;


import cn.hutool.json.JSONUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//JSONB对应JdbcType.OTHER
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.OTHER)
public class JsonbTypeHandler extends BaseTypeHandler<Object> {
    private static final String JSONB_STR = "jsonb";

    /**
     * 写数据库时，把Java对象转成JSONB类型
     */
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Object o, JdbcType jdbcType) throws SQLException {
        if (preparedStatement != null) {
            PGobject jsonObject = new PGobject();
            jsonObject.setType(JSONB_STR);
            jsonObject.setValue(JSONUtil.toJsonStr(o));
            preparedStatement.setObject(i, jsonObject);
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