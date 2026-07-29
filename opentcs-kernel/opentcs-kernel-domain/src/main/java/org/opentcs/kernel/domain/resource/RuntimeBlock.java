package org.opentcs.kernel.domain.resource;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 运行态 Block 定义（来自地图建模）。
 */
public final class RuntimeBlock {

    private final String blockId;
    private final String name;
    private final String type;
    private final Set<String> members;

    public RuntimeBlock(String blockId, String name, String type, Set<String> members) {
        this.blockId = Objects.requireNonNull(blockId, "blockId");
        this.name = name == null || name.isBlank() ? blockId : name;
        this.type = type == null || type.isBlank() ? "SINGLE_VEHICLE_ONLY" : type;
        this.members = Collections.unmodifiableSet(new LinkedHashSet<>(
                members == null ? Set.of() : members));
    }

    public String getBlockId() {
        return blockId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Set<String> getMembers() {
        return members;
    }

    public boolean containsMember(String member) {
        return member != null && members.contains(member);
    }

    public boolean isSingleVehicleOnly() {
        return !"SAME_DIRECTION_ONLY".equalsIgnoreCase(type);
    }
}
