# Fuji Triage — Sakura → Leaf patch buckets

Generated 2026-06-16. Method: set-intersect Sakura's modified MC files against Leaf's (parsed from feature-patch `+++ b/` headers).

## Summary

| Bucket | Meaning | Count |
|---|---|---|
| 1 additive | Sakura own `me/samsuik/**` classes (copy as-is) | 92 |
| 2 clean | MC files only Sakura modifies (apply cleanly) | 25 |
| 3 conflict | MC files BOTH Leaf and Sakura modify (3-way merge) | 72 |
| paper-patches | Sakura paper-server patches (triage separately) | 16 |
| api | Sakura Bukkit-API patches (triage separately) | 8 |

Sakura source-channel split: 55 `sources/` per-file patches + 33 `features/` commits = 97 distinct MC files.

## Bucket 2 — clean apply (Sakura-only MC files)

- `net/minecraft/network/protocol/game/ClientboundContainerSetSlotPacket.java`
- `net/minecraft/server/level/TicketType.java`
- `net/minecraft/world/CompoundContainer.java`
- `net/minecraft/world/entity/player/PlayerEquipment.java`
- `net/minecraft/world/entity/projectile/ProjectileUtil.java`
- `net/minecraft/world/item/EnderEyeItem.java`
- `net/minecraft/world/item/Item.java`
- `net/minecraft/world/item/SolidBucketItem.java`
- `net/minecraft/world/item/component/KineticWeapon.java`
- `net/minecraft/world/item/component/PiercingWeapon.java`
- `net/minecraft/world/level/block/BasePressurePlateBlock.java`
- `net/minecraft/world/level/block/FallingBlock.java`
- `net/minecraft/world/level/block/FenceGateBlock.java`
- `net/minecraft/world/level/block/HoneyBlock.java`
- `net/minecraft/world/level/block/LadderBlock.java`
- `net/minecraft/world/level/block/WaterlilyBlock.java`
- `net/minecraft/world/level/block/WebBlock.java`
- `net/minecraft/world/level/block/WeightedPressurePlateBlock.java`
- `net/minecraft/world/level/block/entity/TickingBlockEntity.java`
- `net/minecraft/world/level/block/piston/MovingPistonBlock.java`
- `net/minecraft/world/level/block/piston/PistonHeadBlock.java`
- `net/minecraft/world/level/block/piston/PistonMovingBlockEntity.java`
- `net/minecraft/world/level/material/Fluid.java`
- `net/minecraft/world/level/redstone/CollectingNeighborUpdater.java`
- `net/minecraft/world/level/redstone/ExperimentalRedstoneUtils.java`

## Bucket 3 — CONFLICT (both modify) — the real work

🔥 = predicted hot file (deep logic overlap, expect substantial merge).

- `net/minecraft/core/component/PatchedDataComponentMap.java`
- `net/minecraft/core/dispenser/DispenseItemBehavior.java`
- `net/minecraft/network/protocol/game/ClientboundLevelChunkPacketData.java`
- `net/minecraft/network/syncher/SynchedEntityData.java`
- `net/minecraft/server/MinecraftServer.java`
- `net/minecraft/server/dedicated/DedicatedServer.java`
- 🔥 `net/minecraft/server/level/ChunkMap.java`
- 🔥 `net/minecraft/server/level/ServerEntity.java`
- `net/minecraft/server/level/ServerLevel.java`
- `net/minecraft/server/level/ServerPlayer.java`
- `net/minecraft/server/network/ServerCommonPacketListenerImpl.java`
- `net/minecraft/server/network/ServerGamePacketListenerImpl.java`
- `net/minecraft/world/Container.java`
- 🔥 `net/minecraft/world/entity/Entity.java`
- `net/minecraft/world/entity/EntityEquipment.java`
- `net/minecraft/world/entity/InsideBlockEffectApplier.java`
- `net/minecraft/world/entity/LivingEntity.java`
- `net/minecraft/world/entity/Mob.java`
- `net/minecraft/world/entity/ai/attributes/AttributeInstance.java`
- `net/minecraft/world/entity/ai/attributes/AttributeMap.java`
- `net/minecraft/world/entity/animal/golem/IronGolem.java`
- 🔥 `net/minecraft/world/entity/item/FallingBlockEntity.java`
- `net/minecraft/world/entity/item/ItemEntity.java`
- 🔥 `net/minecraft/world/entity/item/PrimedTnt.java`
- `net/minecraft/world/entity/monster/Creeper.java`
- `net/minecraft/world/entity/monster/EnderMan.java`
- `net/minecraft/world/entity/npc/villager/Villager.java`
- `net/minecraft/world/entity/player/Inventory.java`
- `net/minecraft/world/entity/player/Player.java`
- `net/minecraft/world/entity/projectile/FishingHook.java`
- `net/minecraft/world/entity/projectile/Projectile.java`
- `net/minecraft/world/entity/projectile/ThrowableProjectile.java`
- `net/minecraft/world/entity/projectile/throwableitemprojectile/AbstractThrownPotion.java`
- `net/minecraft/world/entity/projectile/throwableitemprojectile/ThrownEnderpearl.java`
- `net/minecraft/world/food/FoodData.java`
- `net/minecraft/world/inventory/AbstractContainerMenu.java`
- `net/minecraft/world/item/BlockItem.java`
- `net/minecraft/world/item/BoatItem.java`
- `net/minecraft/world/item/BucketItem.java`
- `net/minecraft/world/item/EnderpearlItem.java`
- `net/minecraft/world/item/ItemStack.java`
- `net/minecraft/world/item/Items.java`
- `net/minecraft/world/item/SpawnEggItem.java`
- `net/minecraft/world/item/enchantment/EnchantmentHelper.java`
- `net/minecraft/world/level/BaseSpawner.java`
- `net/minecraft/world/level/BlockGetter.java`
- 🔥 `net/minecraft/world/level/Level.java`
- 🔥 `net/minecraft/world/level/ServerExplosion.java`
- `net/minecraft/world/level/block/BubbleColumnBlock.java`
- `net/minecraft/world/level/block/CactusBlock.java`
- `net/minecraft/world/level/block/ChestBlock.java`
- 🔥 `net/minecraft/world/level/block/HopperBlock.java`
- `net/minecraft/world/level/block/IceBlock.java`
- `net/minecraft/world/level/block/LiquidBlock.java`
- 🔥 `net/minecraft/world/level/block/RedStoneWireBlock.java`
- `net/minecraft/world/level/block/SugarCaneBlock.java`
- `net/minecraft/world/level/block/TripWireHookBlock.java`
- `net/minecraft/world/level/block/entity/BaseContainerBlockEntity.java`
- `net/minecraft/world/level/block/entity/BlockEntity.java`
- `net/minecraft/world/level/block/entity/DispenserBlockEntity.java`
- 🔥 `net/minecraft/world/level/block/entity/HopperBlockEntity.java`
- `net/minecraft/world/level/block/piston/PistonBaseBlock.java`
- `net/minecraft/world/level/block/state/BlockBehaviour.java`
- `net/minecraft/world/level/chunk/ChunkAccess.java`
- 🔥 `net/minecraft/world/level/chunk/LevelChunk.java`
- `net/minecraft/world/level/chunk/LevelChunkSection.java`
- `net/minecraft/world/level/material/FlowingFluid.java`
- `net/minecraft/world/level/material/LavaFluid.java`
- `net/minecraft/world/level/material/WaterFluid.java`
- 🔥 `net/minecraft/world/level/redstone/DefaultRedstoneWireEvaluator.java`
- 🔥 `net/minecraft/world/level/redstone/NeighborUpdater.java`
- `net/minecraft/world/phys/AABB.java`
