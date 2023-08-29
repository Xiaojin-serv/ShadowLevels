package top.shadowpixel.shadowlevels.object.enums;

import lombok.Getter;

@Getter
public enum ModificationType {
    ADD("Add"), REMOVE("Remove"), SET("Set");

    private final String name;

    ModificationType(String name) {
        this.name = name;
    }
}
