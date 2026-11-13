# Frontier Paper 26.4 — Independent Minecraft patch applicability scan

> Scan date: November 13, 2026. Source baseline: 90 contiguous applied patches plus independently replayed compatible patches through 0154; patches 0091/0092/0098/0110-0112/0135/0150 and other earlier blockers were **not** integrated.

**Result:** 64 patches conditionally applied; 107 failed; 171 total scanned. This is **NOT** a compiled, feature-complete, or tested server build.

Among the failures, 20 directly target deleted Moonrise source paths. Failures on other paths still require manual API/conflict checks, even if they report missing Git blobs.

**Critical event features:** 10 of the final ten patches (0316–0325) did not auto-apply. These include legacy knockback, falling-block stacking, raytrace visibility and velocity handling. Retain and port intentionally before gameplay testing.

The scan used a **disposable worktree**, applying each patch with git am --3way and explicitly skipping failures before continuing. An APPLY entry only means the patch operation succeeded in the resulting **partially patched source graph**; it is not independent proof of compatibility, correctness, or safety.

No changes from this scan were moved into the experimental patch stack; all 325 original patch slots remain tracked.

| Patch | Result | Deleted Moonrise target | Patch title |
|---|---|---|---|
| 0155 | Needs review | — | C2ME Reduce Allocations |
| 0156 | Applied in scratch | — | Lithium fast util |
| 0157 | Applied in scratch | — | Lithium cached iterate outwards |
| 0158 | Applied in scratch | — | Use faster and thread safe ban list date format pars |
| 0159 | Needs review | Yes | Cache chunk key |
| 0160 | Applied in scratch | — | Configurable tripwire dupe |
| 0161 | Needs review | — | Paper PR Fix MC 117075 Block Entities Unload Lag Spi |
| 0162 | Applied in scratch | — | Sepals Rearrange the attackable conditions |
| 0163 | Needs review | — | SparklyPaper Skip dirty stats copy when requesting p |
| 0164 | Applied in scratch | — | SparklyPaper Reset dirty flag when loading maps from |
| 0165 | Needs review | — | Optimize checking nearby players for spawning |
| 0166 | Applied in scratch | — | Optimize baby villager sensor |
| 0167 | Needs review | — | Only player pushable |
| 0168 | Applied in scratch | — | Remove iterators from Inventory |
| 0169 | Needs review | — | optimize mob despawn |
| 0170 | Applied in scratch | — | Slightly optimise getNearestPlayer |
| 0171 | Applied in scratch | — | Bulk writes to writeLongArray during chunk loading |
| 0172 | Needs review | — | Improve sorting in SortedArraySet |
| 0173 | Applied in scratch | — | Rewrite ClientboundLightUpdatePacketData |
| 0174 | Needs review | — | Spawner Configurations |
| 0175 | Needs review | Yes | SparklyPaper Parallel world ticking |
| 0176 | Needs review | — | SparklyPaper Track each world MSPT |
| 0177 | Needs review | — | Paper PR Fix cancelled Projectile Events still consu |
| 0178 | Applied in scratch | — | Optimize SetLookAndInteract and NearestVisibleLiving |
| 0179 | Applied in scratch | — | rewrite InsideBrownianWalk |
| 0180 | Needs review | — | Use BFS on getSlopeDistance |
| 0181 | Needs review | — | Paper PR Throttle failed spawn attempts |
| 0182 | Applied in scratch | — | Improve BlockEntity ticking isRemoved check |
| 0183 | Needs review | — | Raytrace AntiXray SDK integration |
| 0184 | Applied in scratch | — | Optimize ContextMap.create |
| 0185 | Applied in scratch | — | Use direct iteration on Sensing.tick |
| 0186 | Needs review | — | Optimise non flush packet sending |
| 0187 | Applied in scratch | — | Null handling on MultifaceSpreader |
| 0188 | Needs review | Yes | Optimize ThreadedTicketLevelPropagator |
| 0189 | Applied in scratch | — | Optimise MobEffectUtil getDigSpeedAmplification |
| 0190 | Needs review | Yes | Optimise chunkUnloads |
| 0191 | Applied in scratch | — | Optimize BlockEntityType isValid |
| 0192 | Needs review | Yes | Paper PR Add ticket on player join to avoid chunk lo |
| 0193 | Applied in scratch | — | Sakura Optimise check inside blocks and traverse blo |
| 0194 | Needs review | Yes | Sakura copy EntityList implementation to BasicEntity |
| 0195 | Applied in scratch | — | Protocol Core |
| 0196 | Needs review | — | Async switch connection state |
| 0197 | Needs review | — | Optimize BlockEntities tickersInLevel |
| 0198 | Applied in scratch | — | Pluto Check if the cactus can even survive being pla |
| 0199 | Applied in scratch | — | Only tick items at hand |
| 0200 | Applied in scratch | — | Optimise player movement checks |
| 0201 | Applied in scratch | — | Add configurable death item drop knockback settings |
| 0202 | Applied in scratch | — | Optimize getScaledTrackingDistance |
| 0203 | Applied in scratch | — | Optimize SynchedEntityData packDirty |
| 0204 | Needs review | — | Cache block state tags |
| 0205 | Needs review | Yes | optimize getEntityStatus |
| 0206 | Applied in scratch | — | Rail Optimization optimized PoweredRailBlock logic |
| 0207 | Needs review | — | optimise ChunkGenerator getMobsAt |
| 0208 | Needs review | — | cache biome for mob spawning and advancements |
| 0209 | Needs review | — | optimize mob spawning |
| 0210 | Needs review | — | optimize structure map |
| 0211 | Needs review | — | throttle mob spawning |
| 0212 | Applied in scratch | — | Add BlockExplosionHitEvent |
| 0213 | Needs review | — | Old Blast Protection explosion knockback |
| 0214 | Applied in scratch | — | Use UUID for cure reputation |
| 0215 | Needs review | — | Fix crash during parsing unknown command message |
| 0216 | Needs review | — | optimize random tick |
| 0217 | Needs review | — | cache biome zoom seed |
| 0218 | Needs review | — | Replace EntitySelectorOptions map with optimized col |
| 0219 | Applied in scratch | — | optimize no action time |
| 0220 | Applied in scratch | — | optimize waypoint |
| 0221 | Needs review | — | Paw optimization |
| 0222 | Applied in scratch | — | Skip inactive entity for execute |
| 0223 | Needs review | Yes | Optimise getEntities |
| 0224 | Needs review | — | Re route SetClosestHomeAsWalkTarget s poi finding to |
| 0225 | Needs review | — | optimize checkInsideBlocks calls |
| 0226 | Applied in scratch | — | Do not create fire if explosion was cancelled |
| 0227 | Applied in scratch | — | Skip BlockPhysicsEvent if no listeners |
| 0228 | Needs review | Yes | Lithium equipment tracking |
| 0229 | Applied in scratch | — | Add io_uring support |
| 0230 | Needs review | — | Paper PR Optimise temptation lookups |
| 0231 | Needs review | — | Paper PR Optimise temptation lookups changes |
| 0232 | Needs review | — | Paper PR Fix cancelled rooted dirt drops being duped |
| 0233 | Needs review | — | Lithium combined heightmap update |
| 0234 | Needs review | — | Skip PreCreatureSpawnEvent if no listeners |
| 0235 | Needs review | — | optimize goal selector |
| 0236 | Needs review | Yes | thread unsafe chunk map |
| 0237 | Needs review | — | optimize SimpleBitStorage object layout |
| 0238 | Needs review | — | optimize get chunk |
| 0239 | Needs review | — | remove shouldTickBlocksAt check |
| 0240 | Needs review | — | optimize PalettedContainer get |
| 0241 | Applied in scratch | — | optimize LevelChunk getBlockStateFinal |
| 0242 | Needs review | — | Pluto don t load chunks to spread grass |
| 0243 | Needs review | — | optimize fluid state access |
| 0244 | Applied in scratch | — | optimize applyMovementEmissionAndPlaySound |
| 0245 | Applied in scratch | — | optimize isStateClimbable |
| 0246 | Applied in scratch | — | optimize getOnPos |
| 0247 | Needs review | — | optimize collision shape |
| 0248 | Needs review | — | cache collision list |
| 0249 | Needs review | — | fast bit radix sort |
| 0250 | Applied in scratch | — | optimize tickEffects |
| 0251 | Needs review | — | Pluto Expose Direction Plane s faces |
| 0252 | Needs review | — | Pluto reduce allocation |
| 0253 | Needs review | — | Only update frozen ticks if changed |
| 0254 | Needs review | — | Prevent executing commands if server stopped |
| 0255 | Needs review | — | optimize canHoldAnyFluid |
| 0256 | Needs review | Yes | Multithreaded Tracker |
| 0257 | Needs review | — | optimize collidedAlongVector |
| 0258 | Needs review | Yes | Rewrite entity despawn time |
| 0259 | Applied in scratch | — | Optimize Vec3i hashing |
| 0260 | Needs review | — | Cache world border |
| 0261 | Applied in scratch | — | Optimize PatchedDataComponentMap equals |
| 0262 | Needs review | — | optimize movement vector normalization |
| 0263 | Needs review | — | Reduce optimiseRandomTick new BlockPos instance crea |
| 0264 | Needs review | — | configurable goal update interval |
| 0265 | Needs review | — | Prevent entities from moving into weak loaded chunks |
| 0266 | Applied in scratch | — | Don t load POI for competitor scan |
| 0267 | Needs review | — | optimize PathNavigation followThePath |
| 0268 | Applied in scratch | — | reduce enchantment allocations |
| 0269 | Applied in scratch | — | Reduce AbstractContainerMenu allocations |
| 0270 | Needs review | — | Optimize map lookups with isEmpty check |
| 0271 | Needs review | — | Filter ClientboundSetEntityMotionPacket |
| 0272 | Applied in scratch | — | Disable Useless Particles |
| 0273 | Applied in scratch | — | Optimize pushable selector |
| 0274 | Applied in scratch | — | Optimize respawn anchor explosion |
| 0275 | Applied in scratch | — | Skip VehicleEntityCollisionEvent if no listeners |
| 0276 | Needs review | — | Cache identifier toString and hash |
| 0277 | Applied in scratch | — | Fix MC 301114 Combat Tracker memory leak |
| 0278 | Needs review | — | Improve Purpur JS expression evaluation |
| 0279 | Needs review | — | configurable ice and snow tick chance |
| 0280 | Applied in scratch | — | fix skeleton horse trap NPE |
| 0281 | Applied in scratch | — | Skip item merge checks for full stacks |
| 0282 | Needs review | Yes | disable world data saving |
| 0283 | Needs review | — | Replace entity equipment items to array |
| 0284 | Needs review | Yes | Leaves Lithium Sleeping Block Entity |
| 0285 | Needs review | — | fixup Leaves Lithium Sleeping Block Entity |
| 0286 | Needs review | — | Leaves Vanilla hopper |
| 0287 | Needs review | — | Reduce debug subscribers overhead |
| 0288 | Needs review | — | Pre authenticate event |
| 0289 | Applied in scratch | — | Optimize getLeashHolder |
| 0290 | Applied in scratch | — | reduce computeSpeed Vec3 allocations |
| 0291 | Applied in scratch | — | Cache XP orb merge parameters |
| 0292 | Applied in scratch | — | Short circuit isOnFire checks |
| 0293 | Applied in scratch | — | optimize ClientboundCommandsPacket enumerateNodes |
| 0294 | Needs review | — | Replace entity tracker by fluid map |
| 0295 | Applied in scratch | — | Short circuit InventoryChangeTrigger without listene |
| 0296 | Needs review | — | optimize PathNavigation shouldRecomputePath |
| 0297 | Needs review | Yes | Optimize PoiAccess visited section tracking |
| 0298 | Needs review | Yes | optimize entity activation |
| 0299 | Needs review | — | Leaves Fork cache item max stack size |
| 0300 | Applied in scratch | — | Replace ServerClockManager map with optimized collec |
| 0301 | Needs review | — | Avoid registry access lookup on every random tick |
| 0302 | Applied in scratch | — | Skip firing GenericGameEvent if no listeners |
| 0303 | Needs review | Yes | Limit pushable entity queries |
| 0304 | Applied in scratch | — | Lithium Cache retracting piston collision shapes |
| 0305 | Needs review | — | Lithium optimize non POI block searches |
| 0306 | Applied in scratch | — | Configurable locator bar max distance |
| 0307 | Needs review | Yes | reduce PalettedContainer lookup in collisions |
| 0308 | Needs review | — | Move ClientboundCommandsPacket creation off the main |
| 0309 | Needs review | — | Lithium Reduce Brain memories lookup |
| 0310 | Needs review | Yes | Optimize SpreadingSnowyBlock light retrieval |
| 0311 | Needs review | — | Use legacy tracker ticking |
| 0312 | Needs review | — | Optimize chunk packet construction |
| 0313 | Applied in scratch | — | GUI theme |
| 0314 | Needs review | — | propagate block destruction cancellation |
| 0315 | Needs review | Yes | Integrate Sakura 26.3 Minecraft changes |
| 0316 | Needs review | — | Restore Fuji maintenance command hurt camera toggle  |
| 0317 | Needs review | — | Restore opt in Fuji legacy knockback |
| 0318 | Needs review | — | Fix Sakura integration compile errors in Entity |
| 0319 | Needs review | — | Restore opt in Fuji instant falling block stacking |
| 0320 | Needs review | — | Avoid shadowing the command build context |
| 0321 | Needs review | — | Use the captured safe ender pearl destination for th |
| 0322 | Needs review | — | Honor Sakura visibility settings across Leaf entity  |
| 0323 | Needs review | — | Port Fuji raytrace culling hooks across all entity t |
| 0324 | Needs review | — | Send and optionally flush one melee knockback veloci |
| 0325 | Needs review | — | Allow configurable every tick player movement tracki |
