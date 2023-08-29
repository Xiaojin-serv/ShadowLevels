package top.shadowpixel.shadowlevels.util;

import lombok.var;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import top.shadowpixel.shadowcore.api.config.Configuration;
import top.shadowpixel.shadowcore.util.object.ObjectUtils;
import top.shadowpixel.shadowcore.util.plugin.VersionUtils;
import top.shadowpixel.shadowcore.util.text.ColorUtils;
import top.shadowpixel.shadowcore.util.text.ReplaceUtils;
import top.shadowpixel.shadowlevels.object.hooks.ItemsAdderHook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ItemUtils {

    public static ItemBuilder builder() {
        return builder(Material.STONE);
    }

    public static ItemBuilder builder(Material material) {
        return new ItemBuilder(material);
    }

    public static ItemBuilder builder(ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    public static ItemBuilder builder(Configuration section) {
        return builder().fromConfigSection(section);
    }

    public static ItemBuilder builder(Player player, Configuration section) {
        return builder().fromConfigSection(player, section);
    }

    @SuppressWarnings({"UnusedReturnValue", "deprecation"})
    public static class ItemBuilder {

        private ItemStack itemStack;
        private ItemMeta  itemMeta;

        public ItemBuilder(Material material) {
            this.itemStack = new ItemStack(material);
            this.itemMeta = itemStack.getItemMeta();
        }

        public ItemBuilder(ItemStack itemStack) {
            this.itemStack = itemStack;
            this.itemMeta = itemStack.getItemMeta();
        }

        public ItemBuilder setDurability(Integer damage) {
            return setDamage(damage);
        }

        public ItemBuilder setDamage(Integer damage) {
            itemStack.setDurability(damage.shortValue());
            return this;
        }

        public ItemBuilder setType(Material material) {
            this.itemStack.setType(material);
            return this;
        }

        public ItemBuilder setMaterialData(MaterialData materialData) {
            this.itemStack.setData(materialData);
            return this;
        }

        public ItemBuilder setAmount(Integer amount) {
            this.itemStack.setAmount(amount);
            return this;
        }

        public ItemBuilder setDisplayName(String name) {
            itemMeta.setDisplayName(ColorUtils.colorize(name));
            return this;
        }

        public ItemBuilder setLore(String... lores) {
            itemMeta.setLore(ColorUtils.colorize(Arrays.asList(lores)));
            return this;
        }

        public ItemBuilder setLore(List<String> lores) {
            return this.setLore(lores.toArray(new String[0]));
        }

        public ItemBuilder addEnchantment(String name, Integer level) {
            return this.addEnchantment(Enchantment.getByName(name.toUpperCase()), level);
        }

        public ItemBuilder addEnchantment(Enchantment enchantment, Integer level) {
            this.itemMeta.addEnchant(enchantment, level, true);
            return this;
        }

        public ItemBuilder addItemFlag(ItemFlag... flags) {
            itemMeta.addItemFlags(flags);
            return this;
        }

        public ItemBuilder removeItemFlag(ItemFlag... flags) {
            itemMeta.removeItemFlags(flags);
            return this;
        }

        public ItemBuilder setItemFlag(ItemFlag... flags) {
            itemMeta.getItemFlags().clear();
            itemMeta.getItemFlags().addAll(Arrays.asList(flags));
            return this;
        }

        public ItemBuilder setUnbreakable(Boolean able) {
            itemMeta.setUnbreakable(able);
            return this;
        }

        public ItemBuilder addAttribute(Attribute attribute, AttributeModifier attributeModifier) {
            itemMeta.addAttributeModifier(attribute, attributeModifier);
            return this;
        }

        public ItemBuilder removeAttribute(Attribute attribute) {
            itemMeta.removeAttributeModifier(attribute);
            return this;
        }

        public ItemBuilder removeAttribute(Attribute attribute, AttributeModifier attributeModifier) {
            itemMeta.removeAttributeModifier(attribute, attributeModifier);
            return this;
        }

        public ItemBuilder removeAttribute(EquipmentSlot slot) {
            itemMeta.removeAttributeModifier(slot);
            return this;
        }

        public ItemStack build() {
            this.itemStack.setItemMeta(this.itemMeta);
            return this.itemStack;
        }

        public ItemBuilder fromConfigSection(Configuration section) {
            return this.fromConfigSection(null, section);
        }

        public ItemBuilder fromConfigSection(Player player, Configuration section) {
            if (section == null) {
                throw new NullPointerException("ConfigurationSection is null");
            }

            if (section.contains("Name")) {
                this.setDisplayName(ReplaceUtils.coloredReplace(section.getString("Name"), player));
            }

            if (section.contains("Lore")) {
                this.setLore(ReplaceUtils.coloredReplace(section.getStringList("Lore"), player));
            }

            if (section.contains("Data")) {
                this.setDurability(section.getInt("Data"));
            }

            if (section.contains("Amount")) {
                this.setAmount(section.getInt("Amount"));
            }

            if (section.contains("Material") || section.contains("Type")) {
                var mat = ObjectUtils.getOrElse(section.getString("Material"), section.getString("Type"));
                var stack = ItemsAdderHook.getItemStack(mat);

                if (stack == null) {
                    this.setType(ObjectUtils.getOrElse(Material.getMaterial(section.getString("Material", "Type").toUpperCase()), Material.STONE));
                } else {
                    this.itemStack = stack;
                }
            }

            if (section.contains("Unbreakable")) {
                this.setUnbreakable(section.getBoolean("Unbreakable"));
            }

            if (section.contains("Enchantments")) {
                if (section.isList("Enchantments")) {
                    section.getStringList("Enchantments").forEach(ench -> {
                        var enchantment = ench.split(" : ");
                        this.addEnchantment(enchantment[0], Integer.parseInt(enchantment[1]));
                    });
                } else if (section.isString("Enchantments")) {
                    var enchantment = section.getString("Enchantments").split(" : ");
                    this.addEnchantment(enchantment[0], Integer.parseInt(enchantment[1]));
                }
            }

            if (section.contains("Attributes")) {
                var attributes = section.getConfigurationSection("Attributes");
                attributes.getKeys().forEach(a -> this.addAttribute(Attribute.valueOf(a.toUpperCase()), (AttributeModifier) attributes.get(a)));
            }

            try {
                if (section.contains("ItemFlags")) {
                    try {
                        if (section.isList("ItemFlags")) {
                            var list = section.getStringList("ItemFlags");
                            list.forEach(flag -> {
                                try {
                                    this.addItemFlag(ItemFlag.valueOf(flag.toUpperCase()));
                                } catch (Throwable ignore) {

                                }
                            });
                        } else if (section.isString("ItemFlags")) {
                            try {
                                this.addItemFlag(ItemFlag.valueOf(section.getString("ItemFlags")));
                            } catch (Throwable ignore) {

                            }
                        }
                    } catch (Throwable ignore) {
                    }
                }
            } catch (Throwable ignore) {
            }

            return this;
        }

        public ItemBuilder saveToConfigSection(ConfigurationSection section) {
            if (itemMeta.hasDisplayName()) {
                section.set("Name", itemMeta.getDisplayName());
            }

            if (itemMeta.hasLore()) {
                section.set("Lore", itemMeta.getLore());
            }

            if (itemMeta.hasEnchants()) {
                if (itemMeta.getEnchants().size() == 1) {
                    itemMeta.getEnchants().forEach((key, value) -> section.set("Enchantments", key.getName() + " : " + value));
                } else {
                    var enc = new ArrayList<String>();
                    this.itemMeta.getEnchants().forEach((ench, level) -> enc.add(ench.getName() + " : " + level));
                    section.set("Enchantments", enc);
                }
            }

            try {
                var itemFlags = new ArrayList<>(itemMeta.getItemFlags());
                if (!itemFlags.isEmpty()) {
                    if (itemFlags.size() == 1) {
                        section.set("ItemFlags", itemFlags.get(0).name());
                    } else {
                        section.set("ItemFlags", itemFlags.stream().map(ItemFlag::name).collect(Collectors.toList()));
                    }
                }
            } catch (Throwable ignore) {
            }

            if (VersionUtils.compareNMSVersion("v1_13_R2") < 0 && itemMeta.hasAttributeModifiers()) {
                var map = new HashMap<String, AttributeModifier>();
                this.itemMeta.getAttributeModifiers().entries().forEach(entry -> map.put(entry.getKey().name(), entry.getValue()));

                section.set("Attributes", map);
            }

            if (VersionUtils.compareNMSVersion("v1_9") < 0) {
                section.set("Unbreakable", itemMeta.isUnbreakable());
            }

            section.set("Material", itemStack.getType().name());
            section.set("Amount", itemStack.getAmount());
            section.set("Data", itemStack.getDurability());

            return this;
        }
    }
}
