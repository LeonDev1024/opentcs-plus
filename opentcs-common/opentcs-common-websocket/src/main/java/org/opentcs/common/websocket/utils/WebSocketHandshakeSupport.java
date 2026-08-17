package org.opentcs.common.websocket.utils;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.opentcs.common.core.utils.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;

/**
 * WebSocket 握手参数解析。浏览器无法给 WS 设置 Header，token 只能走 query。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebSocketHandshakeSupport {

    public static String firstValue(ServerHttpRequest request, String name) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest req = servletRequest.getServletRequest();
            String fromParam = req.getParameter(name);
            if (StringUtils.isNotBlank(fromParam)) {
                return fromParam;
            }
            String fromHeader = req.getHeader(name);
            if (StringUtils.isNotBlank(fromHeader)) {
                return fromHeader;
            }
        }
        String fromHeader = request.getHeaders().getFirst(name);
        if (StringUtils.isNotBlank(fromHeader)) {
            return fromHeader;
        }
        return queryParam(request.getURI(), name);
    }

    static String queryParam(URI uri, String name) {
        if (uri == null) {
            return null;
        }
        String query = uri.getRawQuery();
        if (StringUtils.isBlank(query)) {
            query = uri.getQuery();
        }
        if (StringUtils.isBlank(query)) {
            return null;
        }
        for (String pair : query.split("&")) {
            int eq = pair.indexOf('=');
            if (eq <= 0) {
                continue;
            }
            String key = decode(pair.substring(0, eq));
            if (name.equals(key)) {
                return decode(pair.substring(eq + 1));
            }
        }
        return null;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
