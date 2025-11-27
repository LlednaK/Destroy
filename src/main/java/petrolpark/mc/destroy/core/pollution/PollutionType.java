package petrolpark.mc.destroy.core.pollution;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.gui.AllIcons;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

public class PollutionType<P extends IAttachmentHolder> {

    protected final AllIcons icon;
    protected final String translationKey;
    protected Component name = null;

    public PollutionType(AllIcons icon, String translationKey) {
        this.icon = icon;
        this.translationKey = translationKey;
    };

    public Component getName() {
        if (name == null) name = Component.translatable(translationKey);
        return name;
    };
    
    public static final record Properties(int max, int syncThreshold, float ambientDecayChance) {

        public static final PollutionType.Properties DEFAULT = new PollutionType.Properties(65565, 1000, 0.002f);

        public static final Codec<PollutionType.Properties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max", 65565).forGetter(PollutionType.Properties::max),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("sync_threshold", 1000).forGetter(PollutionType.Properties::syncThreshold),
            Codec.floatRange(0f, Float.MAX_VALUE).optionalFieldOf("ambient_decay", 0.002f).forGetter(PollutionType.Properties::ambientDecayChance)
        ).apply(instance, PollutionType.Properties::new));
    };

    public static final record SpreadingProperties(float chance, float rate) {

        public static final PollutionType.SpreadingProperties DEFAULT = new PollutionType.SpreadingProperties(0.002f, 0.005f);

        public static final Codec<PollutionType.SpreadingProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.floatRange(0f, 1f).optionalFieldOf("chance", 0.002f).forGetter(PollutionType.SpreadingProperties::chance),
            Codec.floatRange(0f, 1f).optionalFieldOf("rate", 0.005f).forGetter(PollutionType.SpreadingProperties::rate)
        ).apply(instance, PollutionType.SpreadingProperties::new));
    };
};
