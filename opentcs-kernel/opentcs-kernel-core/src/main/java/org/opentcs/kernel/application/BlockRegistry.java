package org.opentcs.kernel.application;

import org.opentcs.kernel.api.dto.BlockDTO;
import org.opentcs.kernel.domain.resource.RuntimeBlock;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 运行态 Block 注册表。
 */
public class BlockRegistry {

    private final ConcurrentHashMap<String, RuntimeBlock> blocksById = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> memberToBlockIds = new ConcurrentHashMap<>();

    public synchronized void replaceAll(List<BlockDTO> blocks) {
        blocksById.clear();
        memberToBlockIds.clear();
        if (blocks == null) {
            return;
        }
        for (BlockDTO dto : blocks) {
            if (dto == null || dto.getBlockId() == null || dto.getBlockId().isBlank()) {
                continue;
            }
            Set<String> members = dto.getMembers() == null
                    ? Set.of()
                    : dto.getMembers().stream()
                    .filter(m -> m != null && !m.isBlank())
                    .map(String::trim)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            RuntimeBlock block = new RuntimeBlock(dto.getBlockId(), dto.getName(), dto.getType(), members);
            blocksById.put(block.getBlockId(), block);
            for (String member : members) {
                memberToBlockIds.computeIfAbsent(member, k -> ConcurrentHashMap.newKeySet())
                        .add(block.getBlockId());
            }
        }
    }

    public synchronized void clear() {
        blocksById.clear();
        memberToBlockIds.clear();
    }

    public Optional<RuntimeBlock> get(String blockId) {
        return Optional.ofNullable(blocksById.get(blockId));
    }

    public Collection<RuntimeBlock> getAll() {
        return List.copyOf(blocksById.values());
    }

    public List<RuntimeBlock> findByMember(String member) {
        if (member == null || member.isBlank()) {
            return List.of();
        }
        Set<String> ids = memberToBlockIds.getOrDefault(member, Set.of());
        return ids.stream()
                .map(blocksById::get)
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}
