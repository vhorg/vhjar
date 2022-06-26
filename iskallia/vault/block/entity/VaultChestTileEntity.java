package iskallia.vault.block.entity;

import com.google.common.base.Enums;
import iskallia.vault.config.LootTablesConfig;
import iskallia.vault.config.VaultChestConfig;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.BasicScavengerItem;
import iskallia.vault.nbt.VMapNBT;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.PlayerFilter;
import iskallia.vault.util.VaultRarity;
import iskallia.vault.util.calc.ChestRarityHelper;
import iskallia.vault.util.data.RandomListAccess;
import iskallia.vault.util.data.WeightedDoubleList;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.chest.VaultChestEffect;
import iskallia.vault.world.vault.gen.piece.VaultPiece;
import iskallia.vault.world.vault.gen.piece.VaultTreasure;
import iskallia.vault.world.vault.logic.objective.ScavengerHuntObjective;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import iskallia.vault.world.vault.modifier.CatalystChanceModifier;
import iskallia.vault.world.vault.modifier.ChestTrapModifier;
import iskallia.vault.world.vault.player.VaultPlayer;
import iskallia.vault.world.vault.player.VaultRunner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

public class VaultChestTileEntity extends ChestTileEntity {
   private VaultRarity rarity;
   private boolean generated;
   private BlockState renderState;
   private final VMapNBT<VaultRarity, Integer> rarityPool = new VMapNBT<>(
      (nbt, rarity1) -> nbt.func_74768_a("Rarity", rarity1.ordinal()),
      (nbt, weight) -> nbt.func_74768_a("Weight", weight),
      nbt -> VaultRarity.values()[nbt.func_74762_e("Rarity")],
      nbt -> nbt.func_74762_e("Weight")
   );
   private int ticksSinceSync;

   protected VaultChestTileEntity(TileEntityType<?> typeIn) {
      super(typeIn);
   }

   public VaultChestTileEntity() {
      this(ModBlocks.VAULT_CHEST_TILE_ENTITY);
   }

   public Map<VaultRarity, Integer> getRarityPool() {
      return this.rarityPool;
   }

   @Nullable
   public VaultRarity getRarity() {
      return this.rarity;
   }

   @OnlyIn(Dist.CLIENT)
   public void setRenderState(BlockState renderState) {
      this.renderState = renderState;
   }

   public void func_73660_a() {
      int i = this.field_174879_c.func_177958_n();
      int j = this.field_174879_c.func_177956_o();
      int k = this.field_174879_c.func_177952_p();
      this.ticksSinceSync++;
      this.field_145987_o = func_213977_a(this.field_145850_b, this, this.ticksSinceSync, i, j, k, this.field_145987_o);
      this.field_145986_n = this.field_145989_m;
      float f = 0.1F;
      if (this.field_145987_o > 0 && this.field_145989_m == 0.0F) {
         this.playVaultChestSound(true);
      }

      if (this.field_145987_o == 0 && this.field_145989_m > 0.0F || this.field_145987_o > 0 && this.field_145989_m < 1.0F) {
         float f1 = this.field_145989_m;
         if (this.field_145987_o > 0) {
            this.field_145989_m += 0.1F;
         } else {
            this.field_145989_m -= 0.1F;
         }

         if (this.field_145989_m > 1.0F) {
            this.field_145989_m = 1.0F;
         }

         if (this.field_145989_m < 0.5F && f1 >= 0.5F) {
            this.playVaultChestSound(false);
         }

         if (this.field_145989_m < 0.0F) {
            this.field_145989_m = 0.0F;
         }
      }

      if (this.field_145850_b.field_72995_K) {
         this.addParticles();
      }
   }

   private void playVaultChestSound(boolean open) {
      if (this.field_145850_b != null) {
         double x = this.field_174879_c.func_177958_n() + 0.5;
         double y = this.field_174879_c.func_177956_o() + 0.5;
         double z = this.field_174879_c.func_177952_p() + 0.5;
         if (open) {
            this.field_145850_b
               .func_184148_a(
                  null, x, y, z, SoundEvents.field_187657_V, SoundCategory.BLOCKS, 0.5F, this.field_145850_b.field_73012_v.nextFloat() * 0.1F + 0.9F
               );
            if (this.rarity != null) {
               switch (this.rarity) {
                  case RARE:
                     this.field_145850_b
                        .func_184148_a(
                           null,
                           x,
                           y,
                           z,
                           ModSounds.VAULT_CHEST_RARE_OPEN,
                           SoundCategory.BLOCKS,
                           0.2F,
                           this.field_145850_b.field_73012_v.nextFloat() * 0.1F + 0.9F
                        );
                     break;
                  case EPIC:
                     this.field_145850_b
                        .func_184148_a(
                           null,
                           x,
                           y,
                           z,
                           ModSounds.VAULT_CHEST_EPIC_OPEN,
                           SoundCategory.BLOCKS,
                           0.2F,
                           this.field_145850_b.field_73012_v.nextFloat() * 0.1F + 0.9F
                        );
                     break;
                  case OMEGA:
                     this.field_145850_b
                        .func_184148_a(
                           null,
                           x,
                           y,
                           z,
                           ModSounds.VAULT_CHEST_OMEGA_OPEN,
                           SoundCategory.BLOCKS,
                           0.2F,
                           this.field_145850_b.field_73012_v.nextFloat() * 0.1F + 0.9F
                        );
               }
            }
         } else {
            this.field_145850_b
               .func_184148_a(
                  null, x, y, z, SoundEvents.field_187651_T, SoundCategory.BLOCKS, 0.5F, this.field_145850_b.field_73012_v.nextFloat() * 0.1F + 0.9F
               );
         }
      }
   }

   private void addParticles() {
      if (this.field_145850_b != null) {
         if (this.rarity != null && this.rarity != VaultRarity.COMMON && this.rarity != VaultRarity.RARE) {
            float xx = this.field_145850_b.field_73012_v.nextFloat() * 2.0F - 1.0F;
            float zz = this.field_145850_b.field_73012_v.nextFloat() * 2.0F - 1.0F;
            double x = this.field_174879_c.func_177958_n() + 0.5 + 0.7 * xx;
            double y = this.field_174879_c.func_177956_o() + this.field_145850_b.field_73012_v.nextFloat();
            double z = this.field_174879_c.func_177952_p() + 0.5 + 0.7 * zz;
            double xSpeed = this.field_145850_b.field_73012_v.nextFloat() * xx;
            double ySpeed = (this.field_145850_b.field_73012_v.nextFloat() - 0.5) * 0.25;
            double zSpeed = this.field_145850_b.field_73012_v.nextFloat() * zz;
            float red = this.rarity == VaultRarity.EPIC ? 1.0F : 0.0F;
            float green = this.rarity == VaultRarity.OMEGA ? 1.0F : 0.0F;
            float blue = this.rarity == VaultRarity.EPIC ? 1.0F : 0.0F;
            this.field_145850_b.func_195594_a(new RedstoneParticleData(red, green, blue, 1.0F), x, y, z, xSpeed, ySpeed, zSpeed);
         }
      }
   }

   public void func_184281_d(PlayerEntity player) {
      this.generateChestLoot(player, false);
   }

   public void generateChestLoot(PlayerEntity player, boolean compressLoot) {
      if (this.func_145831_w() != null && !this.func_145831_w().func_201670_d() && player instanceof ServerPlayerEntity && !this.generated) {
         ServerWorld world = (ServerWorld)this.func_145831_w();
         ServerPlayerEntity sPlayer = (ServerPlayerEntity)player;
         if (!MiscUtils.isPlayerFakeMP(sPlayer) && !player.func_175149_v()) {
            VaultRaid vault = VaultRaidData.get(world).getAt(world, this.func_174877_v());
            if (vault == null) {
               this.generated = true;
               this.field_184284_m = null;
               this.func_70296_d();
            } else {
               BlockState state = this.func_195044_w();
               if (!sPlayer.func_184812_l_() && this.shouldPreventCheatyAccess(vault, world, state)) {
                  this.generated = true;
                  this.field_184284_m = null;
                  this.func_70296_d();
               } else if (this.shouldDoChestTrapEffect(vault, world, sPlayer, state)) {
                  this.generated = true;
                  this.field_184284_m = null;
               } else {
                  if (this.field_184284_m == null) {
                     WeightedDoubleList<String> chestRarityList = new WeightedDoubleList<>();
                     float incChestRarity = ChestRarityHelper.getIncreasedChestRarity(sPlayer);
                     if (this.rarityPool.isEmpty()) {
                        ModConfigs.VAULT_CHEST.RARITY_POOL.forEach((rarity, weight) -> {
                           if (!rarity.equalsIgnoreCase(VaultRarity.COMMON.name())) {
                              chestRarityList.add(rarity, weight.floatValue() * (1.0F + incChestRarity));
                           } else {
                              chestRarityList.add(rarity, weight.floatValue());
                           }
                        });
                     } else {
                        this.rarityPool.forEach((rarity, weight) -> {
                           if (!rarity.equals(VaultRarity.COMMON)) {
                              chestRarityList.add(rarity.name(), weight.floatValue() * (1.0F + incChestRarity));
                           } else {
                              chestRarityList.add(rarity.name(), weight.floatValue());
                           }
                        });
                     }

                     this.rarity = vault.getPlayer(player)
                        .map(VaultPlayer::getProperties)
                        .flatMap(properties -> properties.getBase(VaultRaid.CHEST_PITY))
                        .map(pity -> pity.getRandomChestRarity(chestRarityList, player, world.func_201674_k()))
                        .flatMap(key -> Enums.getIfPresent(VaultRarity.class, key).toJavaUtil())
                        .orElse(VaultRarity.COMMON);
                     int level = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
                     LootTablesConfig.Level config = ModConfigs.LOOT_TABLES.getForLevel(level);
                     if (config != null) {
                        if (state.func_177230_c() == ModBlocks.VAULT_CHEST) {
                           this.field_184284_m = config.getChest(this.rarity);
                        } else if (state.func_177230_c() == ModBlocks.VAULT_TREASURE_CHEST) {
                           this.field_184284_m = config.getTreasureChest(this.rarity);
                        } else if (state.func_177230_c() == ModBlocks.VAULT_ALTAR_CHEST) {
                           this.field_184284_m = config.getAltarChest(this.rarity);
                        } else if (state.func_177230_c() == ModBlocks.VAULT_COOP_CHEST) {
                           this.field_184284_m = config.getCoopChest(this.rarity);
                        } else if (state.func_177230_c() == ModBlocks.VAULT_BONUS_CHEST) {
                           this.field_184284_m = config.getBonusChest(this.rarity);
                        }
                     }
                  }

                  List<ItemStack> loot = this.generateSpecialLoot(vault, world, sPlayer, state);
                  this.fillFromLootTable(player, loot, compressLoot);
                  this.func_70296_d();
                  this.func_145831_w().func_184138_a(this.func_174877_v(), this.func_195044_w(), this.func_195044_w(), 3);
                  this.generated = true;
               }
            }
         } else {
            this.generated = true;
            this.func_70296_d();
         }
      }
   }

   private List<ItemStack> generateSpecialLoot(VaultRaid vault, ServerWorld sWorld, ServerPlayerEntity player, BlockState thisState) {
      List<ItemStack> loot = new ArrayList<>();
      if (vault.getActiveObjectives().stream().noneMatch(VaultObjective::preventsCatalystFragments)) {
         vault.getProperties().getBase(VaultRaid.CRYSTAL_DATA).ifPresent(crystalData -> {
            if (crystalData.isChallenge() || !crystalData.preventsRandomModifiers()) {
               float chance = ModConfigs.VAULT_CHEST_META.getCatalystChance(thisState.func_177230_c().getRegistryName(), this.rarity);
               float incModifier = 0.0F;

               for (CatalystChanceModifier modifier : vault.getActiveModifiersFor(PlayerFilter.any(), CatalystChanceModifier.class)) {
                  incModifier += modifier.getCatalystChanceIncrease();
               }

               chance *= 1.0F + incModifier;
               if (sWorld.func_201674_k().nextFloat() < chance) {
                  loot.add(new ItemStack(ModItems.VAULT_CATALYST_FRAGMENT));
               }
            }

            if (crystalData.getGuaranteedRoomFilters().isEmpty()) {
               float chance = ModConfigs.VAULT_CHEST_META.getRuneChance(thisState.func_177230_c().getRegistryName(), this.rarity);
               if (sWorld.func_201674_k().nextFloat() < chance) {
                  Item rune = ModConfigs.VAULT_RUNE.getRandomRune();
                  int vaultLevel = vault.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
                  int minLevel = ModConfigs.VAULT_RUNE.getMinimumLevel(rune).orElse(0);
                  if (vaultLevel >= minLevel) {
                     loot.add(new ItemStack(rune));
                  }
               }
            }
         });
      }

      vault.getProperties().getBase(VaultRaid.LEVEL).ifPresent(level -> {
         int traders = ModConfigs.SCALING_CHEST_REWARDS.traderCount(thisState.func_177230_c().getRegistryName(), this.rarity, level);

         for (int i = 0; i < traders; i++) {
            int slot = MiscUtils.getRandomEmptySlot(this);
            if (slot != -1) {
               this.func_70299_a(slot, new ItemStack(ModItems.TRADER_CORE));
            }
         }

         int statues = ModConfigs.SCALING_CHEST_REWARDS.statueCount(thisState.func_177230_c().getRegistryName(), this.rarity, level);

         for (int ix = 0; ix < statues; ix++) {
            int slot = MiscUtils.getRandomEmptySlot(this);
            if (slot != -1) {
               ItemStack statue = new ItemStack(ModBlocks.GIFT_NORMAL_STATUE);
               if (ModConfigs.SCALING_CHEST_REWARDS.isMegaStatue()) {
                  statue = new ItemStack(ModBlocks.GIFT_MEGA_STATUE);
               }

               this.func_70299_a(slot, statue);
            }
         }
      });
      vault.getActiveObjective(ScavengerHuntObjective.class)
         .ifPresent(
            objective -> vault.getProperties()
               .getBase(VaultRaid.IDENTIFIER)
               .ifPresent(identifier -> ModConfigs.SCAVENGER_HUNT.generateChestLoot(objective.getGenerationDropFilter()).forEach(itemEntry -> {
                  ItemStack stack = itemEntry.createItemStack();
                  if (!stack.func_190926_b()) {
                     BasicScavengerItem.setVaultIdentifier(stack, identifier);
                     loot.add(stack);
                  }
               }))
         );
      return loot;
   }

   private boolean shouldDoChestTrapEffect(VaultRaid vault, ServerWorld sWorld, ServerPlayerEntity player, BlockState thisState) {
      return vault.getAllObjectives().stream().anyMatch(VaultObjective::preventsTrappedChests)
         ? false
         : vault.getPlayer(player.func_110124_au()).map(vPlayer -> {
            int level = vPlayer.getProperties().getBase(VaultRaid.LEVEL).orElse(0);
            boolean raffle = vault.getProperties().getBase(VaultRaid.IS_RAFFLE).orElse(false);
            VaultChestConfig config = null;
            if (thisState.func_177230_c() == ModBlocks.VAULT_CHEST) {
               config = ModConfigs.VAULT_CHEST;
            } else if (thisState.func_177230_c() == ModBlocks.VAULT_TREASURE_CHEST) {
               config = ModConfigs.VAULT_TREASURE_CHEST;
            } else if (thisState.func_177230_c() == ModBlocks.VAULT_ALTAR_CHEST) {
               config = ModConfigs.VAULT_ALTAR_CHEST;
            } else if (thisState.func_177230_c() == ModBlocks.VAULT_COOP_CHEST) {
               config = ModConfigs.VAULT_COOP_CHEST;
            } else if (thisState.func_177230_c() == ModBlocks.VAULT_BONUS_CHEST) {
               config = ModConfigs.VAULT_BONUS_CHEST;
            }

            if (config != null) {
               RandomListAccess<String> effectPool = config.getEffectPool(level, raffle);
               if (effectPool != null) {
                  for (ChestTrapModifier modifier : vault.getActiveModifiersFor(PlayerFilter.of(vPlayer), ChestTrapModifier.class)) {
                     effectPool = modifier.modifyWeightedList(config, effectPool);
                  }

                  VaultChestEffect effect = config.getEffectByName(effectPool.getRandom(this.field_145850_b.func_201674_k()));
                  if (effect != null) {
                     effect.apply(vault, vPlayer, sWorld);
                     this.field_145850_b.func_175656_a(this.func_174877_v(), ModBlocks.VAULT_BEDROCK.func_176223_P());
                     return true;
                  }
               }
            }

            return false;
         }).orElse(false);
   }

   private boolean shouldPreventCheatyAccess(VaultRaid vault, ServerWorld sWorld, BlockState thisState) {
      if (vault.getActiveObjective(ArchitectObjective.class).isPresent()) {
         return false;
      } else {
         if (thisState.func_177230_c() == ModBlocks.VAULT_TREASURE_CHEST) {
            boolean isValidPosition = false;

            for (VaultPiece piece : vault.getGenerator().getPiecesAt(this.func_174877_v())) {
               if (piece instanceof VaultTreasure) {
                  VaultTreasure treasurePiece = (VaultTreasure)piece;
                  if (treasurePiece.isDoorOpen(sWorld)) {
                     isValidPosition = true;
                  }
               }
            }

            if (!isValidPosition) {
               vault.getPlayers()
                  .stream()
                  .filter(vPlayer -> vPlayer instanceof VaultRunner)
                  .findAny()
                  .ifPresent(vRunner -> vRunner.runIfPresent(sWorld.func_73046_m(), sPlayer -> {
                     sPlayer.func_70097_a(DamageSource.field_76376_m, 1000000.0F);
                     sPlayer.func_70606_j(0.0F);
                  }));
               return true;
            }
         }

         return false;
      }
   }

   private void fillFromLootTable(@Nullable PlayerEntity player, List<ItemStack> customLoot, boolean compressLoot) {
      if (this.field_184284_m != null && this.field_145850_b.func_73046_m() != null) {
         LootTable loottable = this.field_145850_b.func_73046_m().func_200249_aQ().func_186521_a(this.field_184284_m);
         if (player instanceof ServerPlayerEntity) {
            CriteriaTriggers.field_232608_N_.func_235478_a_((ServerPlayerEntity)player, this.field_184284_m);
         }

         this.field_184284_m = null;
         Builder ctxBuilder = new Builder((ServerWorld)this.field_145850_b)
            .func_216015_a(LootParameters.field_237457_g_, Vector3d.func_237489_a_(this.field_174879_c))
            .func_216016_a(this.field_184285_n);
         if (player != null) {
            ctxBuilder.func_186469_a(player.func_184817_da()).func_216015_a(LootParameters.field_216281_a, player);
         }

         this.fillFromLootTable(loottable, ctxBuilder.func_216022_a(LootParameterSets.field_216261_b), customLoot, compressLoot);
      }
   }

   private void fillFromLootTable(LootTable lootTable, LootContext context, List<ItemStack> customLoot, boolean compressLoot) {
      if (!compressLoot) {
         customLoot.forEach(stack -> {
            int slot = MiscUtils.getRandomEmptySlot(this);
            if (slot != -1) {
               this.func_70299_a(slot, stack);
            }
         });
         lootTable.func_216118_a(this, context);
      } else {
         List<ItemStack> mergedLoot = MiscUtils.splitAndLimitStackSize(MiscUtils.mergeItemStacks(lootTable.func_216113_a(context)));
         mergedLoot.addAll(customLoot);
         mergedLoot.forEach(stack -> MiscUtils.addItemStack(this, stack));
      }
   }

   public void func_70299_a(int index, ItemStack stack) {
      super.func_70299_a(index, stack);
      this.func_145831_w().func_184138_a(this.func_174877_v(), this.func_195044_w(), this.func_195044_w(), 3);
   }

   public ItemStack func_70298_a(int index, int count) {
      ItemStack stack = super.func_70298_a(index, count);
      this.func_145831_w().func_184138_a(this.func_174877_v(), this.func_195044_w(), this.func_195044_w(), 3);
      return stack;
   }

   public ItemStack func_70304_b(int index) {
      ItemStack stack = super.func_70304_b(index);
      this.func_145831_w().func_184138_a(this.func_174877_v(), this.func_195044_w(), this.func_195044_w(), 3);
      return stack;
   }

   public BlockState func_195044_w() {
      return this.renderState != null ? this.renderState : super.func_195044_w();
   }

   public void func_230337_a_(BlockState state, CompoundNBT nbt) {
      super.func_230337_a_(state, nbt);
      if (nbt.func_150297_b("Rarity", 3)) {
         this.rarity = VaultRarity.values()[nbt.func_74762_e("Rarity")];
      }

      this.rarityPool.deserializeNBT(nbt.func_150295_c("RarityPool", 10));
      this.generated = nbt.func_74767_n("Generated");
   }

   public CompoundNBT func_189515_b(CompoundNBT compound) {
      CompoundNBT nbt = super.func_189515_b(compound);
      if (this.rarity != null) {
         nbt.func_74768_a("Rarity", this.rarity.ordinal());
      }

      nbt.func_218657_a("RarityPool", this.rarityPool.serializeNBT());
      nbt.func_74757_a("Generated", this.generated);
      return nbt;
   }

   public ITextComponent func_145748_c_() {
      if (this.rarity != null) {
         String rarity = StringUtils.capitalize(this.rarity.name().toLowerCase());
         BlockState state = this.func_195044_w();
         if (state.func_177230_c() == ModBlocks.VAULT_CHEST
            || state.func_177230_c() == ModBlocks.VAULT_COOP_CHEST
            || state.func_177230_c() == ModBlocks.VAULT_BONUS_CHEST) {
            return new StringTextComponent(rarity + " Chest");
         }

         if (state.func_177230_c() == ModBlocks.VAULT_TREASURE_CHEST) {
            return new StringTextComponent(rarity + " Treasure Chest");
         }

         if (state.func_177230_c() == ModBlocks.VAULT_ALTAR_CHEST) {
            return new StringTextComponent(rarity + " Altar Chest");
         }
      }

      return super.func_145748_c_();
   }

   public CompoundNBT func_189517_E_() {
      CompoundNBT nbt = new CompoundNBT();
      this.func_189515_b(nbt);
      return nbt;
   }

   public void handleUpdateTag(BlockState state, CompoundNBT tag) {
      this.func_230337_a_(state, tag);
   }

   @Nullable
   public SUpdateTileEntityPacket func_189518_D_() {
      return new SUpdateTileEntityPacket(this.field_174879_c, 1, this.func_189517_E_());
   }

   public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
      CompoundNBT tag = pkt.func_148857_g();
      this.handleUpdateTag(this.func_195044_w(), tag);
   }
}
