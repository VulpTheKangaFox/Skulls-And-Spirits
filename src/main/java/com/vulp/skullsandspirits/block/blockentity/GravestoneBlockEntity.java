package com.vulp.skullsandspirits.block.blockentity;

import com.vulp.skullsandspirits.util.DeathInfoHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GravestoneBlockEntity extends BlockEntity {

    private DeathInfoHolder deathInfo;

    public GravestoneBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.GRAVESTONE.get(), pos, blockState);
    }

    // I haven't tested this math, there's a chance it's a little off.
    public static BlockPos calculatePlacement(ServerLevel level, BlockPos deathPos) {
        int maxRadius = 16;
        int maxVerticalRadius = 16;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int radius = 0; radius <= maxRadius; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) == radius || Math.abs(dz) == radius) {
                        mutablePos.set(deathPos.getX() + dx, deathPos.getY(), deathPos.getZ() + dz);
                        if (level.getBlockState(mutablePos).canBeReplaced()) {
                            return mutablePos.immutable();
                        }
                    }
                }
            }
            for (int dy = 1; dy <= maxVerticalRadius; dy++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        if (Math.abs(dx) == radius || Math.abs(dz) == radius) {
                            mutablePos.set(deathPos.getX() + dx, deathPos.getY() + dy, deathPos.getZ() + dz);
                            if (level.getBlockState(mutablePos).canBeReplaced()) {
                                return mutablePos.immutable();
                            }

                            mutablePos.set(deathPos.getX() + dx, deathPos.getY() - dy, deathPos.getZ() + dz);
                            if (level.getBlockState(mutablePos).canBeReplaced()) {
                                return mutablePos.immutable();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (this.deathInfo != null) {
            tag.put("deathInfo", this.deathInfo.toNBT(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("deathInfo")) {
            this.deathInfo = DeathInfoHolder.fromNBT(tag.getCompound("deathInfo"), registries);
        }
    }

    public void setDeathInfoHolder(DeathInfoHolder awaitingDeath) {
        this.deathInfo = awaitingDeath;
    }

    @Nullable
    public DeathInfoHolder getDeathInfoHolder() {
        return this.deathInfo;
    }
}
