package net.fabricmc.fabric.impl.networking.player.tracking;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

public interface ThreadedAnvilChunkStorageTrackingExtensions {
	Collection<ServerPlayerEntity> fabric_getTrackingPlayers(Entity entity);
}
