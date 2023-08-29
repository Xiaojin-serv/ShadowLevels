package top.shadowpixel.shadowlevels.object.enums;

import lombok.Getter;

@Getter
public enum RewardStatus {
    UNLOCKED("Reward-Unlocked"),
    LOCKED("Reward-Locked"),
    RECEIVED("Reward-Received"),
    NO_PERMISSIONS("Reward-NoPermissions");

    private final String name;

    RewardStatus(String s) {
        name = s;
    }

}
