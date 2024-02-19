package larguma.crawling_mysteries;

import blue.endless.jankson.Jankson;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CrawlingMysteriesConfig extends ConfigWrapper<larguma.crawling_mysteries.CrawlingMysteriesConfigModel> {

    public final Keys keys = new Keys();

    private final Option<java.lang.String> someLaterOption = this.optionForKey(this.keys.someLaterOption);
    private final Option<java.lang.Integer> anIntOption = this.optionForKey(this.keys.anIntOption);
    private final Option<java.lang.Boolean> aBooleanToggle = this.optionForKey(this.keys.aBooleanToggle);
    private final Option<larguma.crawling_mysteries.CrawlingMysteriesConfigModel.Choices> anEnumOption = this.optionForKey(this.keys.anEnumOption);

    private CrawlingMysteriesConfig() {
        super(larguma.crawling_mysteries.CrawlingMysteriesConfigModel.class);
    }

    private CrawlingMysteriesConfig(Consumer<Jankson.Builder> janksonBuilder) {
        super(larguma.crawling_mysteries.CrawlingMysteriesConfigModel.class, janksonBuilder);
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

    public java.lang.String someLaterOption() {
        return someLaterOption.value();
    }

    public void someLaterOption(java.lang.String value) {
        someLaterOption.set(value);
    }

    public int anIntOption() {
        return anIntOption.value();
    }

    public void anIntOption(int value) {
        anIntOption.set(value);
    }

    public boolean aBooleanToggle() {
        return aBooleanToggle.value();
    }

    public void aBooleanToggle(boolean value) {
        aBooleanToggle.set(value);
    }

    public larguma.crawling_mysteries.CrawlingMysteriesConfigModel.Choices anEnumOption() {
        return anEnumOption.value();
    }

    public void anEnumOption(larguma.crawling_mysteries.CrawlingMysteriesConfigModel.Choices value) {
        anEnumOption.set(value);
    }


    public static class Keys {
        public final Option.Key someLaterOption = new Option.Key("someLaterOption");
        public final Option.Key anIntOption = new Option.Key("anIntOption");
        public final Option.Key aBooleanToggle = new Option.Key("aBooleanToggle");
        public final Option.Key anEnumOption = new Option.Key("anEnumOption");
    }
}

