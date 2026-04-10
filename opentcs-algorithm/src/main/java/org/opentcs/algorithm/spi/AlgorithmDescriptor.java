package org.opentcs.algorithm.spi;

/**
 * 算法插件描述符（不可变值对象）。
 * 由 {@link AlgorithmPlugin#getDescriptor()} 返回，用于注册表展示与日志。
 */
public final class AlgorithmDescriptor {

    private final String name;
    private final String version;
    private final String description;
    private final String author;

    public AlgorithmDescriptor(String name, String version, String description, String author) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.author = author;
    }

    public static AlgorithmDescriptor unknown(String className) {
        return new AlgorithmDescriptor(className, "unknown", "No @AlgorithmMeta annotation found", "");
    }

    public String getName() { return name; }
    public String getVersion() { return version; }
    public String getDescription() { return description; }
    public String getAuthor() { return author; }

    @Override
    public String toString() {
        return String.format("AlgorithmDescriptor{name='%s', version='%s', description='%s'}",
                name, version, description);
    }
}
