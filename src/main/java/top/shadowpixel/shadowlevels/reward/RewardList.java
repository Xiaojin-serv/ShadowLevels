package top.shadowpixel.shadowlevels.reward;

import lombok.Getter;
import lombok.ToString;
import lombok.var;
import top.shadowpixel.shadowcore.api.config.Configuration;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.level.Level;

import java.util.ArrayList;

@Getter
@ToString
public class RewardList {

    private final Level             level;
    @Getter
    private final Configuration     configuration;
    @Getter
    private final ArrayList<Reward> rewards = new ArrayList<>();

    @Getter
    private String  name;
    @Getter
    private String  title;
    @Getter
    private Integer size;

    public RewardList(Configuration section) {
        this(section, null);
    }

    public RewardList(Configuration section, String name) {
        this.name = name;
        this.configuration = section;
        this.level = ShadowLevels.getInstance().getLevel(section.getString("Level-System"));

        this.title = section.getString("Title");
        this.size = section.getInt("Size");

        if (section.getConfigurationSection("Rewards") != null) {
            var rewards = section.getConfigurationSection("Rewards");
            if (rewards == null) {
                return;
            }

            rewards.getKeys().forEach(index -> {
                var rewardSection = rewards.getConfigurationSection(index);
                assert rewardSection != null;

                this.rewards.add(new Reward(this, rewardSection));
            });
        }
    }

    public void setName(String name) {
        if (this.name == null) {
            this.name = name;
        }
    }

    @Deprecated
    public Configuration getConfigurationSection() {
        return configuration;
    }
}
