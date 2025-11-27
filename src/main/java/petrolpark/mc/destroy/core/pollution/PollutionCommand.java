package petrolpark.mc.destroy.core.pollution;

import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import petrolpark.mc.destroy.DestroyAttachmentTypes;
import petrolpark.mc.destroy.DestroyRegistries;

@EventBusSubscriber
public class PollutionCommand {
    
    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static final void onRegisterCommands(RegisterCommandsEvent event) {
        final CommandBuildContext context = event.getBuildContext();
        event.getDispatcher().register(Commands.literal("destroy")
            .then(Commands.literal("pollution")
                .requires(cs -> cs.hasPermission(2))    
                .then(Commands.literal("level")
                    .then(Commands.argument("type", ResourceArgument.resource(context, DestroyRegistries.Keys.LEVEL_POLLUTION_TYPE))
                        .then(Commands.literal("query")
                            .executes(ctx -> {
                                return queryLevelPollution(ctx.getSource(), ctx.getArgument("type", PollutionType.class));
                            })
                        ).then(Commands.literal("set")
                            .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                .executes(ctx -> {
                                    return setLevelPollution(ctx.getSource(), ctx.getArgument("type", PollutionType.class), IntegerArgumentType.getInteger(ctx, "value"));
                                })
                            )
                        ).then(Commands.literal("add")
                            .then(Commands.argument("change", IntegerArgumentType.integer())
                                .executes(ctx -> {
                                    return addLevelPollution(ctx.getSource(), ctx.getArgument("type", PollutionType.class), IntegerArgumentType.getInteger(ctx, "change"));
                                })
                            )
                        )
                    )
                ).then(Commands.literal("chunk")
                    .then(Commands.argument("position", BlockPosArgument.blockPos())
                        .then(Commands.argument("type", ResourceArgument.resource(context, DestroyRegistries.Keys.CHUNK_POLLUTION_TYPE))
                            .then(Commands.literal("query")
                                .executes(ctx -> {
                                    return queryChunkPollution(ctx.getSource(), BlockPosArgument.getLoadedBlockPos(ctx, "position"), ctx.getArgument("type", PollutionType.class));
                                })
                            ).then(Commands.literal("set")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                    .executes(ctx -> {
                                        return setChunkPollution(ctx.getSource(), BlockPosArgument.getLoadedBlockPos(ctx, "position"), ctx.getArgument("type", PollutionType.class), IntegerArgumentType.getInteger(ctx, "value"));
                                    })
                                )
                            ).then(Commands.literal("add")
                                .then(Commands.argument("change", IntegerArgumentType.integer())
                                    .executes(ctx -> {
                                        return addChunkPollution(ctx.getSource(), BlockPosArgument.getLoadedBlockPos(ctx, "position"), ctx.getArgument("type", PollutionType.class), IntegerArgumentType.getInteger(ctx, "change"));
                                    })
                                )
                            )
                        )
                    )
                )
            )
        );
    };

    private static final int queryLevelPollution(CommandSourceStack source, PollutionType<Level> pollutionType) {
        final int pollutionLevel = source.getLevel().getData(DestroyAttachmentTypes.LEVEL_POLLUTION).getPollution(pollutionType);
        source.sendSuccess(() ->  Component.translatable("commands.destroy.pollution.query", pollutionType.getName(), pollutionLevel), true);
        return pollutionLevel;
    };

    private static final int setLevelPollution(CommandSourceStack source, PollutionType<Level> pollutionType, int value) {
        final int pollutionLevel = source.getLevel().getData(DestroyAttachmentTypes.LEVEL_POLLUTION).setPollution( pollutionType, value);
        source.sendSuccess(() ->  Component.translatable("commands.destroy.pollution.set", pollutionType.getName(), pollutionLevel), true);
        return pollutionLevel;
    };

    private static final int addLevelPollution(CommandSourceStack source, PollutionType<Level> pollutionType, int change) {
        final int pollutionLevel = source.getLevel().getData(DestroyAttachmentTypes.LEVEL_POLLUTION).changePollution( pollutionType, change);
        source.sendSuccess(() ->  Component.translatable("commands.destroy.pollution.set", pollutionType.getName(), pollutionLevel), true);
        return pollutionLevel;
    };

    private static final int queryChunkPollution(CommandSourceStack source, BlockPos pos, PollutionType<ChunkAccess> pollutionType) {
        final int pollutionLevel = source.getLevel().getChunk(pos).getData(DestroyAttachmentTypes.CHUNK_POLLUTION).getPollution(pollutionType);
        source.sendSuccess(() ->  Component.translatable("commands.destroy.pollution.query", pollutionType.getName(), pollutionLevel), true);
        return pollutionLevel;
    };

    private static final int setChunkPollution(CommandSourceStack source, BlockPos pos, PollutionType<ChunkAccess> pollutionType, int value) {
        final int pollutionLevel = source.getLevel().getChunk(pos).getData(DestroyAttachmentTypes.CHUNK_POLLUTION).setPollution(pollutionType, value);
        source.sendSuccess(() ->  Component.translatable("commands.destroy.pollution.set", pollutionType.getName(), pollutionLevel), true);
        return pollutionLevel;
    };

    private static final int addChunkPollution(CommandSourceStack source, BlockPos pos, PollutionType<ChunkAccess> pollutionType, int change) {
        final int pollutionLevel = source.getLevel().getChunk(pos).getData(DestroyAttachmentTypes.CHUNK_POLLUTION).changePollution( pollutionType, change);
        source.sendSuccess(() ->  Component.translatable("commands.destroy.pollution.set", pollutionType.getName(), pollutionLevel), true);
        return pollutionLevel;
    };
};
