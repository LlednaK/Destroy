package petrolpark.mc.destroy.core.pollution;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import petrolpark.mc.destroy.DestroyAttachmentTypes;
import petrolpark.mc.destroy.DestroyDataMapTypes;
import petrolpark.mc.destroy.DestroyRegistries;

@EventBusSubscriber
public class LevelPollution extends Pollution<Level> {

    public static final Serializer SERIALIZER = new Serializer();

    public LevelPollution(Level level, Map<PollutionType<Level>, Integer> values) {
        super(level, values);
    };

    public static LevelPollution create(IAttachmentHolder holder) {
        if (holder instanceof Level level) return new LevelPollution(level, Collections.emptyMap());
        throw new IllegalArgumentException();
    };

    @Override
    public void syncInternal() {
        if (!holder.isClientSide()) CatnipServices.NETWORK.sendToAllClients(new LevelPollutionPacket(getValues()));
    };

    public void syncTo(ServerPlayer player) {
        CatnipServices.NETWORK.sendToClient(player, new LevelPollutionPacket(getValues()));
    };

    public static final void syncTo(Player entity) {
        if (entity instanceof ServerPlayer player) player.level().getData(DestroyAttachmentTypes.LEVEL_POLLUTION).syncTo(player);
    };

    @Override
    public PollutionType.Properties getProperties(PollutionType<Level> pollutionType) {
        return Optional.ofNullable(DestroyRegistries.LEVEL_POLLUTION_TYPES.getData(DestroyDataMapTypes.LEVEL_POLLUTION_PROPERTIES, DestroyRegistries.LEVEL_POLLUTION_TYPES.wrapAsHolder(pollutionType).getKey())).orElse(PollutionType.Properties.DEFAULT);
    };

    public static class Serializer extends Pollution.Serializer<Level, LevelPollution> {

        public Serializer() {
            super(DestroyRegistries.LEVEL_POLLUTION_TYPES);
        };

        @Override
        protected LevelPollution create(Level holder, Map<PollutionType<Level>, Integer> values) {
            return new LevelPollution(holder, values);
        };

        @Override
        public Level castHolder(IAttachmentHolder holder) {
            if (holder instanceof Level level) return level;
            throw new IllegalArgumentException();
        };

    };

    @SubscribeEvent
    public static final void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        syncTo(event.getEntity());
    };

    @SubscribeEvent
    public static final void onPlayerChangeDimensions(PlayerChangedDimensionEvent event) {
        syncTo(event.getEntity());
    };

    @SubscribeEvent
    public static final void onLevelTick(LevelTickEvent event) {
        event.getLevel().getData(DestroyAttachmentTypes.LEVEL_POLLUTION).tick(event.getLevel().getRandom());
    };
    
};
