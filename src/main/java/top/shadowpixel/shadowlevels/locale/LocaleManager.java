package top.shadowpixel.shadowlevels.locale;

import lombok.var;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowcore.api.config.ConfigurationProvider;
import top.shadowpixel.shadowcore.api.locale.AbstractLocaleManager;
import top.shadowpixel.shadowcore.api.locale.Locale;
import top.shadowpixel.shadowcore.util.collection.ListUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.util.Logger;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class LocaleManager extends AbstractLocaleManager<ShadowLevels> {

    public static final List<Locale.PresetLocaleInfo> PRESET_LOCALE_INFOS = ListUtils.immutableList(
            Locale.PresetLocaleInfo.of("zh_CN", "Locale/zh_CN"),
            Locale.PresetLocaleInfo.of("zh_TW", "Locale/zh_TW"),
            Locale.PresetLocaleInfo.of("en_US", "Locale/en_US")
    );

    public static final List<String> CONTENTS = ListUtils.immutableList(
            "Events.yml",
            "Messages.yml"
    );
    private static      Locale       internal;

    public LocaleManager(ShadowLevels plugin, File directory) {
        super(plugin, directory);
    }

    @NotNull
    public static Locale getInternal() {
        if (internal == null) {
            var plugin = ShadowLevels.getInstance();
            var locale = new Locale(plugin, null, CONTENTS);
            locale.setName("Internal");

            try {
                //noinspection DataFlowIssue
                locale.addConfig("Messages", ConfigurationProvider.getProvider("Yaml")
                        .load(plugin.getResource("Locale/zh_CN/Messages.yml")));
            } catch (Exception e) {
                Logger.warn("Failed to load internal locale!");
                Logger.error("It may not affect a lot, you can ignore it.");
            }

            internal = locale;
        }

        return internal;
    }

    public static LocaleManager getInstance() {
        return ShadowLevels.getInstance().getLocaleManager();
    }

    @Override
    public void initialize() {
        super.initialize();
        setDefaultLocale(plugin.getConfiguration().getString("Locale.Default"));
    }

    @Override
    public @NotNull Collection<Locale.PresetLocaleInfo> getPresetLocales() {
        return PRESET_LOCALE_INFOS;
    }

    @Override
    public @NotNull Collection<String> getContents() {
        return CONTENTS;
    }
}
