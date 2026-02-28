package org.opentcs.driver.protocol;

import lombok.Data;
import java.util.List;

/**
 * VDA5050 事实表消息
 */
@Data
public class FactsheetMessage {
    private int headerId;
    private String timestamp;
    private String version;
    private String manufacturer;
    private String serialNumber;
    private String agvType;
    private String agvId;
    private double maxSpeed;
    private double maxAcceleration;
    private double maxDeceleration;
    private double maxHeight;
    private double maxWeight;
    private double minHeight;
    private double wheelBase;
    private double trackWidth;
    private double length;
    private double width;
    private double height;
    private List<String> supportedActions;
    private List<String> supportedMaps;
    private List<String> supportedInterfaces;
    private String mqttTopicPrefix;
    private int maxOrderUpdateId;
    private int maxActionIdLength;
    private int maxNodeIdLength;
    private int maxEdgeIdLength;
    private int maxLoadIdLength;
    private int maxMapIdLength;
    private int maxZoneSetIdLength;
    private int maxActionDescriptionLength;
    private int maxNodeDescriptionLength;
    private int maxEdgeDescriptionLength;
    private int maxLoadDescriptionLength;
    private int maxMapDescriptionLength;
    private int maxZoneSetDescriptionLength;
    private int maxErrorDescriptionLength;
    private int maxErrorReferenceLength;
    private int maxActionParameterKeyLength;
    private int maxActionParameterValueLength;
    private boolean supportsTrajectories;
    private boolean supportsCorridors;
    private boolean supportsZoneSets;
    private boolean supportsMaps;
    private boolean supportsBatteryCharge;
    private boolean supportsBatteryVoltage;
    private boolean supportsSafetyStates;
    private boolean supportsLoads;
    private boolean supportsVisualization;
    private boolean supportsConnectionTopic;
}