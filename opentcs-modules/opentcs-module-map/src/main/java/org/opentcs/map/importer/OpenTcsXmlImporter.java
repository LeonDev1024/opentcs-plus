package org.opentcs.map.importer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析 openTCS 官方 XML 模型（例如 Demo-01.xml），提取模型名称、点和路径等基础信息。
 * 这里只做结构性解析，不直接依赖业务实体，方便在 Service 中映射到数据库实体。
 */
public class OpenTcsXmlImporter {

    public static class OpenTcsPoint {
        public String name;
        public BigDecimal x;
        public BigDecimal y;
        public BigDecimal z;
        public String type;
    }

    public static class OpenTcsPath {
        public String name;
        public String sourcePointName;
        public String destPointName;
        public BigDecimal length;
        public BigDecimal maxVelocity;
        public BigDecimal maxReverseVelocity;
        /**
         * 路由模式（来自 path 的 routingMode 属性），与 openTCS Path 的 RoutingType 对应。
         */
        public String routingType;
        /**
         * 几何连接类型（来自 <pathLayout connectionType="...">）：
         * DIRECT / ELBOW / SLANTED / POLYPATH / BEZIER / BEZIER_3
         */
        public String connectionType;
        /**
         * 控制点列表（来自 <pathLayout><controlPoint x="..." y="..."/></pathLayout>），
         * 坐标单位与模型坐标相同（mm），相对于地图坐标系。
         */
        public final List<ControlPoint> controlPoints = new ArrayList<>();
    }

    public static class ControlPoint {
        public BigDecimal x;
        public BigDecimal y;
    }

    public static class OpenTcsLocationType {
        public String name;
    }

    public static class OpenTcsLocation {
        public String name;
        public BigDecimal x;
        public BigDecimal y;
        public BigDecimal z;
        public String typeName;
        /**
         * 与该业务位置相连的 Point 名称集合（用于前续生成 link/dashedLine）
         */
        public List<String> attachedPointNames = new java.util.ArrayList<>();
    }

    public static class OpenTcsBlock {
        public String name;
        public String type;
        /**
         * 成员 Path 名称集合
         */
        public List<String> memberPathNames = new java.util.ArrayList<>();
        /**
         * 成员 Point 名称集合
         */
        public List<String> memberPointNames = new java.util.ArrayList<>();
    }

    public static class OpenTcsImportResult {
        private String modelName;
        private final List<OpenTcsPoint> points = new ArrayList<>();
        private final List<OpenTcsPath> paths = new ArrayList<>();
        private final List<OpenTcsLocationType> locationTypes = new ArrayList<>();
        private final List<OpenTcsLocation> locations = new ArrayList<>();
        private final List<OpenTcsBlock> blocks = new ArrayList<>();

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        public List<OpenTcsPoint> getPoints() {
            return points;
        }

        public List<OpenTcsPath> getPaths() {
            return paths;
        }

        public List<OpenTcsLocationType> getLocationTypes() {
            return locationTypes;
        }

        public List<OpenTcsLocation> getLocations() {
            return locations;
        }

        public List<OpenTcsBlock> getBlocks() {
            return blocks;
        }
    }

    public OpenTcsImportResult parse(InputStream in) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setExpandEntityReferences(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(in);
        document.getDocumentElement().normalize();

        OpenTcsImportResult result = new OpenTcsImportResult();

        Element root = document.getDocumentElement();
        if (root == null || !"model".equals(root.getNodeName())) {
            throw new IllegalArgumentException("XML 根节点不是 <model>");
        }
        result.setModelName(root.getAttribute("name"));

        // 解析 point
        NodeList pointNodes = root.getElementsByTagName("point");
        for (int i = 0; i < pointNodes.getLength(); i++) {
            Node node = pointNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element el = (Element) node;
            OpenTcsPoint p = new OpenTcsPoint();
            p.name = el.getAttribute("name");
            p.x = toDecimal(el.getAttribute("positionX"));
            p.y = toDecimal(el.getAttribute("positionY"));
            p.z = toDecimal(el.getAttribute("positionZ"));
            p.type = el.getAttribute("type");
            result.getPoints().add(p);
        }

        // 解析 path
        NodeList pathNodes = root.getElementsByTagName("path");
        for (int i = 0; i < pathNodes.getLength(); i++) {
            Node node = pathNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element el = (Element) node;
            OpenTcsPath p = new OpenTcsPath();
            p.name = el.getAttribute("name");
            p.sourcePointName = el.getAttribute("sourcePoint");
            p.destPointName = el.getAttribute("destinationPoint");
            p.length = toDecimal(el.getAttribute("length"));
            p.maxVelocity = toDecimal(el.getAttribute("maxVelocity"));
            p.maxReverseVelocity = toDecimal(el.getAttribute("maxReverseVelocity"));
            p.routingType = el.getAttribute("routingMode");

            // 解析 pathLayout：connectionType + controlPoint 列表
            NodeList childNodes = el.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node child = childNodes.item(j);
                if (child.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                Element childEl = (Element) child;
                if ("pathLayout".equals(childEl.getNodeName())) {
                    String connType = childEl.getAttribute("connectionType");
                    if (connType != null && !connType.isEmpty()) {
                        p.connectionType = connType;
                    }
                    NodeList cpNodes = childEl.getChildNodes();
                    for (int k = 0; k < cpNodes.getLength(); k++) {
                        Node cpNode = cpNodes.item(k);
                        if (cpNode.getNodeType() != Node.ELEMENT_NODE) {
                            continue;
                        }
                        Element cpEl = (Element) cpNode;
                        if ("controlPoint".equals(cpEl.getNodeName())) {
                            ControlPoint cp = new ControlPoint();
                            cp.x = toDecimal(cpEl.getAttribute("x"));
                            cp.y = toDecimal(cpEl.getAttribute("y"));
                            if (cp.x != null || cp.y != null) {
                                p.controlPoints.add(cp);
                            }
                        }
                    }
                }
            }
            result.getPaths().add(p);
        }

        // 解析 locationType
        NodeList typeNodes = root.getElementsByTagName("locationType");
        for (int i = 0; i < typeNodes.getLength(); i++) {
            Node node = typeNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element el = (Element) node;
            OpenTcsLocationType t = new OpenTcsLocationType();
            t.name = el.getAttribute("name");
            result.getLocationTypes().add(t);
        }

        // 解析 location
        NodeList locationNodes = root.getElementsByTagName("location");
        for (int i = 0; i < locationNodes.getLength(); i++) {
            Node node = locationNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element el = (Element) node;
            OpenTcsLocation l = new OpenTcsLocation();
            l.name = el.getAttribute("name");
            l.x = toDecimal(el.getAttribute("positionX"));
            l.y = toDecimal(el.getAttribute("positionY"));
            l.z = toDecimal(el.getAttribute("positionZ"));
            // 不同 openTCS 版本中 type 属性名可能不同，这里做个兜底
            String typeAttr = el.getAttribute("type");
            if (typeAttr == null || typeAttr.isEmpty()) {
                typeAttr = el.getAttribute("locationType");
            }
            l.typeName = typeAttr;

            // 解析与 point 的链接（link）
            NodeList childNodes = el.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node child = childNodes.item(j);
                if (child.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                Element childEl = (Element) child;
                if ("link".equals(childEl.getNodeName())) {
                    String pointName = childEl.getAttribute("point");
                    if (pointName != null && !pointName.isEmpty()) {
                        l.attachedPointNames.add(pointName);
                    }
                }
            }
            result.getLocations().add(l);
        }

        // 解析 block（区域规则）
        NodeList blockNodes = root.getElementsByTagName("block");
        for (int i = 0; i < blockNodes.getLength(); i++) {
            Node node = blockNodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element el = (Element) node;
            OpenTcsBlock b = new OpenTcsBlock();
            b.name = el.getAttribute("name");
            b.type = el.getAttribute("type");

            NodeList memberNodes = el.getChildNodes();
            for (int j = 0; j < memberNodes.getLength(); j++) {
                Node child = memberNodes.item(j);
                if (child.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                Element childEl = (Element) child;
                if ("member".equals(childEl.getNodeName())) {
                    String pathName = childEl.getAttribute("path");
                    if (pathName != null && !pathName.isEmpty()) {
                        b.memberPathNames.add(pathName);
                    }
                    String pointName = childEl.getAttribute("point");
                    if (pointName != null && !pointName.isEmpty()) {
                        b.memberPointNames.add(pointName);
                    }
                }
            }
            result.getBlocks().add(b);
        }

        return result;
    }

    private static BigDecimal toDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

