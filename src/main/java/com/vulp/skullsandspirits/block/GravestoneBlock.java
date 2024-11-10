package com.vulp.skullsandspirits.block;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.block.blockentity.GravestoneBlockEntity;
import com.vulp.skullsandspirits.util.DeathInfoHolder;
import com.vulp.skullsandspirits.util.SASUtils;
import com.vulp.skullsandspirits.world.DeathSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

public class GravestoneBlock extends Block implements EntityBlock {

    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty FLOATING = BooleanProperty.create("floating");

    private static final VoxelShape SHAPE = Stream.of(
            Block.box(2, 0, 6, 4, 14, 10),
            Block.box(4, 0, 6, 12, 16, 10),
            Block.box(12, 0, 6, 14, 14, 10)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape SHAPE_90 = Stream.of(
            Block.box(6, 0, 2, 10, 14, 4),
            Block.box(6, 0, 4, 10, 16, 12),
            Block.box(6, 0, 12, 10, 14, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public GravestoneBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(HORIZONTAL_FACING, Direction.NORTH).setValue(FLOATING, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HORIZONTAL_FACING).getAxis() == Direction.Axis.Z ? SHAPE : SHAPE_90;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GravestoneBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FLOATING, HORIZONTAL_FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return setRandomFacing(this.defaultBlockState());
    }

    public static BlockState setRandomFacing(BlockState state) {
        return state.setValue(HORIZONTAL_FACING, new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}[new Random().nextInt(4)]);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof GravestoneBlockEntity gravestoneBlockEntity) {
                if (level instanceof ServerLevel serverLevel) {
                    DeathInfoHolder deathInfoHolder = gravestoneBlockEntity.getDeathInfoHolder();
                    if (deathInfoHolder != null && deathInfoHolder.getGraveStage() != DeathInfoHolder.GraveStage.COMPLETE) {
                        NonNullList<ItemStack> items = deathInfoHolder.getCompiledInventories();
                        for (ItemStack item : items) {
                            Containers.dropItemStack(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, item);
                        }
                        ExperienceOrb.award(serverLevel, pos.getCenter(), deathInfoHolder.getExperience());
                        deathInfoHolder.setGraveStage(DeathInfoHolder.GraveStage.COMPLETE);
                    }
                    DeathSavedData.getOrCreate(level.getServer()).setDirty();
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (!level.isClientSide() && shouldBreak(level, pos, player)) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof GravestoneBlockEntity gravestoneBlockEntity) {
                DeathInfoHolder holder = gravestoneBlockEntity.getDeathInfoHolder();
                if (holder != null) {
                    autoGrantItemsOnGraveBreak(player, holder, holder.getDeathPos());
                }
                return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
            }
        }
        return false;
    }

    private boolean shouldBreak(Level level, BlockPos pos, Player player) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof GravestoneBlockEntity gravestoneBlockEntity) {
            if (level instanceof ServerLevel serverLevel) {
                DeathInfoHolder holder = gravestoneBlockEntity.getDeathInfoHolder();
                if (holder != null) {
                    UUID playerUUID = player.getUUID();
                    UUID graveUUID = holder.getUUID();

                    // Check if UUIDs match
                    if (playerUUID.equals(graveUUID)) {
                        return true;
                    } else {
                        String name = SASUtils.getPlayerNameFromUUID(serverLevel, graveUUID, player);
                        if (name != null) {
                            player.displayClientMessage(
                                    Component.translatable("message." + SkullsAndSpirits.MODID + ".wrongGrave", name)
                                            .withStyle(ChatFormatting.RED),
                                    false
                            );
                        }
                        // If name is null, any error message has probs already been handled by getPlayerNameFromUUID()
                        return false;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public static void autoGrantItemsOnGraveBreak(Player player, DeathInfoHolder holder, BlockPos dropsPos) {
        NonNullList<ItemStack>[] inventories = holder.getInventories();
        List<ItemStack> overflow = new ArrayList<>();

        overflow.addAll(transferInventoryWithOverflow(inventories[0], player.getInventory().items, false)); // Main Inventory
        overflow.addAll(transferInventoryWithOverflow(inventories[1], player.getInventory().armor, true)); // Armor Inventory
        overflow.addAll(transferInventoryWithOverflow(inventories[2], player.getInventory().offhand, false)); // Offhand Inventory
        overflow.addAll(inventories[3]);

        for (ItemStack item : new ArrayList<>(overflow)) {
            for (int i = 0; i < player.getInventory().items.size(); i++) {
                if (player.getInventory().items.get(i).isEmpty()) {
                    player.getInventory().items.set(i, item);
                    overflow.remove(item);
                    break;
                }
            }
        }

        ServerLevel level = (ServerLevel) player.level();
        for (ItemStack item : overflow) {
            Containers.dropItemStack(level, dropsPos.getX() + 0.5D, dropsPos.getY() + 0.5D, dropsPos.getZ() + 0.5D, item);
        }

        ExperienceOrb.award(level, dropsPos.getCenter(), holder.getExperience());

        holder.setGraveStage(DeathInfoHolder.GraveStage.COMPLETE);
        level.playSound(player, dropsPos, SoundEvents.PAINTING_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        DeathSavedData.getOrCreate(level.getServer()).setDirty();
    }

    private static List<ItemStack> transferInventoryWithOverflow(NonNullList<ItemStack> fromInventory, NonNullList<ItemStack> toInventory, boolean isArmor) {
        List<ItemStack> overflow = new ArrayList<>();
        for (int i = 0; i < fromInventory.size(); i++) {
            ItemStack stack = fromInventory.get(i).copy();
            if (stack.isEmpty()) {
                continue;
            }

            int index = i;

            // If the item is armor, assign to specific slots based on equipment type
            if (isArmor && stack.getItem() instanceof Equipable equipable) {
                switch (equipable.getEquipmentSlot()) {
                    case HEAD -> index = 3; // Helmet slot
                    case CHEST -> index = 2; // Chestplate slot
                    case LEGS -> index = 1; // Leggings slot
                    case FEET -> index = 0; // Boots slot
                    default -> {
                        overflow.add(stack);
                        continue;
                    }
                }
            }

            ItemStack existingStack = toInventory.get(index);

            // If the target slot is occupied, add the current stack to the overflow list
            if (!existingStack.isEmpty()) {
                overflow.add(stack);
                continue;
            }
            toInventory.set(index, stack);
        }
        return overflow;
    }

}
