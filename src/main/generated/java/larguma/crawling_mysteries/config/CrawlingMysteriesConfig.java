package larguma.crawling_mysteries.config;

import blue.endless.jankson.Jankson;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CrawlingMysteriesConfig extends ConfigWrapper<larguma.crawling_mysteries.config.CrawlingMysteriesConfigModel> {

    public final Keys keys = new Keys();

    private final Option<java.lang.Boolean> enableTrinketsRender = this.optionForKey(this.keys.enableTrinketsRender);
    private final Option<java.lang.Boolean> enableTombstone = this.optionForKey(this.keys.enableTombstone);

    private CrawlingMysteriesConfig() {
        super(larguma.crawling_mysteries.config.CrawlingMysteriesConfigModel.class);
    }

    private CrawlingMysteriesConfig(Consumer<Jankson.Builder> janksonBuilder) {
        super(larguma.crawling_mysteries.config.CrawlingMysteriesConfigModel.class, janksonBuilder);
    }

    public static CrawlingMysteriesConfig createAndLoad() {
        var wrapper = new CrawlingMysteriesConfig();
        wrapper.load();
        return wrapper;
    }

    public static CrawlingMysteriesConfig createAndLoad(Consumer<Jankson.Builder> janksonBuilder) {
        var wrapper = new CrawlingMysteriesConfig(janksonBuilder);
        wrapper.load();
        return wrapper;
    }

    public boolean enableTrinketsRender() {
        return enableTrinketsRender.value();
    }

    public void enableTrinketsRender(boolean value) {
        enableTrinketsRender.set(value);
    }

    public boolean enableTombstone() {
        return enableTombstone.value();
    }

    public void enableTombstone(boolean value) {
        enableTombstone.set(value);
    }


    public static class Keys {
        public final Option.Key enableTrinketsRender = new Option.Key("enableTrinketsRender");
        public final Option.Key enableTombstone = new Option.Key("enableTombstone");
    }
}

