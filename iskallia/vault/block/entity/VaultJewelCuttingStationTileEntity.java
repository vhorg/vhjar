package iskallia.vault.block.entity;

import iskallia.vault.config.VaultJewelCuttingConfig;
import iskallia.vault.container.VaultJewelCuttingStationContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.JewelCuttingParticleMessage;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.expertise.type.JewelExpertise;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.PlayerExpertisesData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

public class VaultJewelCuttingStationTileEntity extends BlockEntity implements MenuProvider {
   private final OverSizedInventory inventory = new OverSizedInventory(12, this) {
      public boolean canPlaceItem(int pIndex, ItemStack pStack) {
         return pIndex != 0 && pIndex != 1 ? false : super.canPlaceItem(pIndex, pStack);
      }
   };

   public boolean stillValid(Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this ? this.inventory.stillValid(player) : false;
   }

   public VaultJewelCuttingStationTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.VAULT_JEWEL_CUTTING_STATION_ENTITY, pos, state);
   }

   public OverSizedInventory getInventory() {
      return this.inventory;
   }

   public ItemStack getJewelInput() {
      return this.inventory.getItem(5);
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.inventory.load(tag);
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      this.inventory.save(tag);
   }

   public void cutJewel(VaultJewelCuttingStationContainer container, ServerPlayer player) {
      if (container.getJewelInputSlot() != null) {
         if (!this.canCraft()) {
            return;
         }

         ItemStack stack = container.getJewelInputSlot().getItem();
         if (!stack.isEmpty()) {
            VaultGearData data = VaultGearData.read(stack);
            Random random = new Random();
            boolean broken = false;
            boolean chipped = false;
            float probability = this.getJewelCuttingModifierRemovalChance();
            ExpertiseTree expertises = PlayerExpertisesData.get(player.getLevel()).getExpertises(player);

            for (JewelExpertise expertise : expertises.getAll(JewelExpertise.class, Skill::isUnlocked)) {
               probability -= expertise.getModifierChanceReduction();
            }

            int sizeToRemove = this.getJewelCuttingRange().getRandom();

            for (VaultGearAttributeInstance<Integer> sizeAttribute : data.getModifiers(ModGearAttributes.JEWEL_SIZE, VaultGearData.Type.ALL_MODIFIERS)) {
               sizeAttribute.setValue(Math.max(10, sizeAttribute.getValue() - sizeToRemove));
               data.write(stack);
            }

            if (random.nextFloat() < probability) {
               List<VaultGearModifier<?>> prefix = new ArrayList<>(data.getModifiers(VaultGearModifier.AffixType.PREFIX));
               List<VaultGearModifier<?>> suffix = new ArrayList<>(data.getModifiers(VaultGearModifier.AffixType.SUFFIX));
               int affixSize = prefix.size() + suffix.size();
               if (affixSize <= 1) {
                  this.breakJewel();
                  container.getJewelInputSlot().set(ItemStack.EMPTY);
                  broken = true;
               } else {
                  Collections.shuffle(prefix, random);
                  Collections.shuffle(suffix, random);
                  if (suffix.size() > 0 && prefix.size() > 0) {
                     boolean removedAffix = false;
                     if (random.nextBoolean()) {
                        for (VaultGearModifier<?> modifier : prefix) {
                           if (data.removeModifier(modifier)) {
                              data.updateAttribute(
                                 ModGearAttributes.PREFIXES, Integer.valueOf(Math.max(0, data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0) - 1))
                              );
                              data.setRarity(getNewRarity(affixSize - 1));
                              removedAffix = true;
                              break;
                           }
                        }
                     }

                     if (!removedAffix) {
                        for (VaultGearModifier<?> modifierx : suffix) {
                           if (data.removeModifier(modifierx)) {
                              data.updateAttribute(
                                 ModGearAttributes.SUFFIXES, Integer.valueOf(Math.max(0, data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0) - 1))
                              );
                              data.setRarity(getNewRarity(affixSize - 1));
                              break;
                           }
                        }
                     }
                  } else if (suffix.size() > 0) {
                     for (VaultGearModifier<?> modifierxx : suffix) {
                        if (data.removeModifier(modifierxx)) {
                           data.updateAttribute(
                              ModGearAttributes.SUFFIXES, Integer.valueOf(Math.max(0, data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0) - 1))
                           );
                           data.setRarity(getNewRarity(affixSize - 1));
                           break;
                        }
                     }
                  } else {
                     for (VaultGearModifier<?> modifierxxx : prefix) {
                        if (data.removeModifier(modifierxxx)) {
                           data.updateAttribute(
                              ModGearAttributes.PREFIXES, Integer.valueOf(Math.max(0, data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0) - 1))
                           );
                           data.setRarity(getNewRarity(affixSize - 1));
                           break;
                        }
                     }
                  }

                  chipped = true;
               }
            }

            ItemStack scrap = container.getScrapSlot().getItem();
            scrap.shrink(this.getRecipeInput().getMainInput().getCount());
            container.getScrapSlot().set(scrap);
            ItemStack bronze = container.getBronzeSlot().getItem();
            bronze.shrink(this.getRecipeInput().getSecondInput().getCount());
            container.getBronzeSlot().set(bronze);
            Level level = this.getLevel();
            if (level != null) {
               if (broken) {
                  level.playSound(null, container.getTilePos(), SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.8F, level.random.nextFloat() * 0.1F + 0.9F);
               } else {
                  if (chipped) {
                     level.playSound(null, container.getTilePos(), ModSounds.ARTISAN_SMITHING, SoundSource.BLOCKS, 0.3F, level.random.nextFloat() * 0.1F + 0.7F);
                  } else {
                     level.playSound(null, container.getTilePos(), ModSounds.ARTISAN_SMITHING, SoundSource.BLOCKS, 0.2F, level.random.nextFloat() * 0.1F + 0.9F);
                  }

                  data.write(stack);
                  container.getJewelInputSlot().set(stack);
               }
            }
         }
      }
   }

   private static VaultGearRarity getNewRarity(int size) {
      if (size == VaultGearRarity.SCRAPPY.getJewelModifierCount()) {
         return VaultGearRarity.SCRAPPY;
      } else if (size == VaultGearRarity.COMMON.getJewelModifierCount()) {
         return VaultGearRarity.COMMON;
      } else if (size == VaultGearRarity.EPIC.getJewelModifierCount()) {
         return VaultGearRarity.EPIC;
      } else if (size == VaultGearRarity.RARE.getJewelModifierCount()) {
         return VaultGearRarity.RARE;
      } else {
         return size == VaultGearRarity.OMEGA.getJewelModifierCount() ? VaultGearRarity.OMEGA : VaultGearRarity.SCRAPPY;
      }
   }

   private void breakJewel() {
      VaultJewelCuttingConfig.JewelCuttingOutput output = this.getRecipeOutput();
      if (output != null) {
         if (this.level != null) {
            this.level
               .playSound(
                  null,
                  this.getBlockPos(),
                  SoundEvents.GENERIC_EXTINGUISH_FIRE,
                  SoundSource.BLOCKS,
                  0.5F + new Random().nextFloat() * 0.25F,
                  0.75F + new Random().nextFloat() * 0.25F
               );
         }

         ItemStack input = this.inventory.getItem(0).copy();
         addStackToSlot(this.inventory, 2, this.getUseRelatedOutput(input, output.generateMainOutput()));
         addStackToSlot(this.inventory, 3, this.getUseRelatedOutput(input, output.generateExtraOutput1()));
         addStackToSlot(this.inventory, 4, this.getUseRelatedOutput(input, output.generateExtraOutput2()));
         ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new JewelCuttingParticleMessage(this.getBlockPos(), this.getJewelInput()));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void spawnBreakParticles(BlockPos pos, ItemStack stack) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         for (int i = 0; i < 4; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               new ItemParticleOption(ParticleTypes.ITEM, stack),
               true,
               pos.getX() + 0.5 + offset.x,
               pos.getY() + random.nextDouble() * 0.15F + 0.25,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 2.0,
               random.nextDouble() * 0.1,
               offset.z / 2.0
            );
         }

         for (int i = 0; i < 3; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               new ItemParticleOption(ParticleTypes.ITEM, stack),
               true,
               pos.getX() + 0.5 + offset.x,
               pos.above().getY() + random.nextDouble() * 0.15F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 20.0,
               random.nextDouble() * 0.2,
               offset.z / 20.0
            );
         }

         for (int i = 0; i < 3; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               new ItemParticleOption(ParticleTypes.ITEM, stack),
               true,
               pos.getX() + 0.5 + offset.x,
               pos.above().getY() + random.nextDouble() * 0.15F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 20.0,
               random.nextDouble() * 0.2,
               offset.z / 20.0
            );
         }

         for (int i = 0; i < 3; i++) {
            Random random = level.getRandom();
            Vec3 offset = new Vec3(
               random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1)
            );
            level.addParticle(
               new ItemParticleOption(ParticleTypes.ITEM, stack),
               true,
               pos.getX() + 0.5 + offset.x,
               pos.above().getY() + random.nextDouble() * 0.15F,
               pos.getZ() + 0.5 + offset.z,
               offset.x / 10.0,
               random.nextDouble() * 0.05,
               offset.z / 10.0
            );
         }
      }
   }

   public static void addStackToSlot(Container inventory, int slot, ItemStack toAdd) {
      if (!toAdd.isEmpty()) {
         ItemStack stack = inventory.getItem(slot);
         if (stack.isEmpty()) {
            inventory.setItem(slot, toAdd.copy());
         } else {
            if (canMerge(stack, toAdd)) {
               stack.grow(toAdd.getCount());
               inventory.setItem(slot, stack);
            }
         }
      }
   }

   public static boolean canMerge(ItemStack stack, ItemStack other) {
      return stack.getItem() == other.getItem() && ItemStack.tagMatches(stack, other);
   }

   public boolean canCraft() {
      VaultJewelCuttingConfig.JewelCuttingOutput output = this.getRecipeOutput();
      VaultJewelCuttingConfig.JewelCuttingInput input = this.getRecipeInput();
      if (input != null && output != null) {
         if (this.inventory.getItem(0).getCount() < input.getMainInput().getCount() || !canMerge(this.inventory.getItem(0), input.getMainInput())) {
            return false;
         } else if (this.inventory.getItem(1).getCount() < input.getSecondInput().getCount() || !canMerge(this.inventory.getItem(1), input.getSecondInput())) {
            return false;
         } else if (!MiscUtils.canFullyMergeIntoSlot(this.inventory, 2, output.getMainOutputMatching())) {
            return false;
         } else {
            return !MiscUtils.canFullyMergeIntoSlot(this.inventory, 3, output.getExtraOutput1Matching())
               ? false
               : MiscUtils.canFullyMergeIntoSlot(this.inventory, 4, output.getExtraOutput2Matching());
         }
      } else {
         return false;
      }
   }

   public VaultJewelCuttingConfig.JewelCuttingOutput getRecipeOutput() {
      return ModConfigs.VAULT_JEWEL_CUTTING_CONFIG.getJewelCuttingOutput();
   }

   public VaultJewelCuttingConfig.JewelCuttingInput getRecipeInput() {
      return ModConfigs.VAULT_JEWEL_CUTTING_CONFIG.getJewelCuttingInput();
   }

   public VaultJewelCuttingConfig.JewelCuttingRange getJewelCuttingRange() {
      return ModConfigs.VAULT_JEWEL_CUTTING_CONFIG.getJewelCuttingRange();
   }

   public float getJewelCuttingModifierRemovalChance() {
      return ModConfigs.VAULT_JEWEL_CUTTING_CONFIG.getJewelCuttingModifierRemovalChance();
   }

   private ItemStack getUseRelatedOutput(ItemStack input, ItemStack output) {
      float out = output.getCount();
      int resultCount = Mth.floor(out);
      if (resultCount < 1 && out > 0.0F && new Random().nextFloat() < out) {
         resultCount++;
      }

      ItemStack copyOut = output.copy();
      copyOut.setCount(resultCount);
      return copyOut;
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
      return this.getLevel() == null ? null : new VaultJewelCuttingStationContainer(containerId, this.getLevel(), this.getBlockPos(), inv);
   }
}
