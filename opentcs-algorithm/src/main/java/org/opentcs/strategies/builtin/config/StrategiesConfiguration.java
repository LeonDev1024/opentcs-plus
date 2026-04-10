package org.opentcs.strategies.builtin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 策略模块配置（前缀 {@code opentcs.strategies}）。
 */
@ConfigurationProperties(prefix = "opentcs.strategies")
public class StrategiesConfiguration {

    private Routing routing = new Routing();
    private Dispatch dispatch = new Dispatch();
    private Scheduling scheduling = new Scheduling();

    public Routing getRouting() {
        return routing;
    }

    public void setRouting(Routing routing) {
        this.routing = routing;
    }

    public Dispatch getDispatch() {
        return dispatch;
    }

    public void setDispatch(Dispatch dispatch) {
        this.dispatch = dispatch;
    }

    public Scheduling getScheduling() {
        return scheduling;
    }

    public void setScheduling(Scheduling scheduling) {
        this.scheduling = scheduling;
    }

    public static class Routing {
        private String algorithm = "dijkstra";
        private boolean cacheEnabled = true;
        private int cacheTtlSeconds = 300;

        public String getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        public boolean isCacheEnabled() {
            return cacheEnabled;
        }

        public void setCacheEnabled(boolean cacheEnabled) {
            this.cacheEnabled = cacheEnabled;
        }

        public int getCacheTtlSeconds() {
            return cacheTtlSeconds;
        }

        public void setCacheTtlSeconds(int cacheTtlSeconds) {
            this.cacheTtlSeconds = cacheTtlSeconds;
        }
    }

    public static class Dispatch {
        private boolean parkingEnabled = true;
        private boolean rechargeEnabled = true;
        private int batchSize = 50;
        private int dispatchIntervalMs = 1000;

        public boolean isParkingEnabled() {
            return parkingEnabled;
        }

        public void setParkingEnabled(boolean parkingEnabled) {
            this.parkingEnabled = parkingEnabled;
        }

        public boolean isRechargeEnabled() {
            return rechargeEnabled;
        }

        public void setRechargeEnabled(boolean rechargeEnabled) {
            this.rechargeEnabled = rechargeEnabled;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public int getDispatchIntervalMs() {
            return dispatchIntervalMs;
        }

        public void setDispatchIntervalMs(int dispatchIntervalMs) {
            this.dispatchIntervalMs = dispatchIntervalMs;
        }
    }

    public static class Scheduling {
        private boolean deadlockPrevention = true;
        private int conflictCheckIntervalMs = 100;

        public boolean isDeadlockPrevention() {
            return deadlockPrevention;
        }

        public void setDeadlockPrevention(boolean deadlockPrevention) {
            this.deadlockPrevention = deadlockPrevention;
        }

        public int getConflictCheckIntervalMs() {
            return conflictCheckIntervalMs;
        }

        public void setConflictCheckIntervalMs(int conflictCheckIntervalMs) {
            this.conflictCheckIntervalMs = conflictCheckIntervalMs;
        }
    }
}
