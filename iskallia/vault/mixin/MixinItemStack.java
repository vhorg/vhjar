package iskallia.vault.mixin;

import iskallia.vault.VaultMod;
import iskallia.vault.config.DurabilityConfig;
import iskallia.vault.core.random.ChunkRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.Modifiers;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSounds;
import iskallia.vault.item.IConditionalDamageable;
import iskallia.vault.util.calc.DurabilityWearReductionHelper;
import iskallia.vault.world.data.ServerVaults;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {ItemStack.class},
   priority = 1001
)
public abstract class MixinItemStack {
   @Shadow
   @Final
   @Deprecated
   private Item item;
   private float health;

   @Shadow
   public abstract int getMaxDamage();

   @Shadow
   public abstract ItemStack copy();

   @Shadow
   public abstract Item getItem();

   @Shadow
   public abstract boolean isDamageableItem();

   @Shadow
   public abstract int getDamageValue();

   @Shadow
   public abstract void setDamageValue(int var1);

   @Shadow
   public abstract void enchant(Enchantment var1, int var2);

   @Overwrite
   public boolean hurt(int damage, Random rand, @Nullable ServerPlayer damager) {
      if (!this.isDamageableItem()) {
         return false;
      } else if (this.item == Items.ELYTRA && new Random().nextInt(5) != 0) {
         return false;
      } else if (this.getItem() instanceof VaultGearItem gearItem && gearItem.isBroken((ItemStack)this)) {
         return false;
      } else {
         if (damage > 0) {
            if (this.getItem() instanceof IConditionalDamageable cd && cd.isImmuneToDamage((ItemStack)this, damager)) {
               return false;
            }

            int unbreaking = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, (ItemStack)this);
            int durabilityNegation = 0;
            boolean isArmor = ((ItemStack)this).getItem() instanceof ArmorItem;
            DurabilityConfig cfg = ModConfigs.DURABILITY;
            float chance = isArmor ? cfg.getArmorDurabilityIgnoreChance(unbreaking) : cfg.getDurabilityIgnoreChance(unbreaking);

            for (int k = 0; unbreaking > 0 && k < damage; k++) {
               if (rand.nextFloat() < chance) {
                  durabilityNegation++;
               }
            }

            int wearReduction = 0;
            if (damager != null) {
               float wearReductionChance = DurabilityWearReductionHelper.getDurabilityWearReduction(damager);

               for (int kx = 0; kx < damage; kx++) {
                  if (rand.nextFloat() < wearReductionChance) {
                     wearReduction++;
                  }
               }
            }

            damage -= durabilityNegation;
            damage -= wearReduction;
            if (damage <= 0) {
               return false;
            }
         }

         if (damager != null && damage != 0) {
            CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(damager, (ItemStack)this, this.getDamageValue() + damage);
         }

         int absDamage = this.getDamageValue() + damage;
         this.setDamageValue(absDamage);
         int newDamage = this.getDamageValue();
         if (damager != null && newDamage == -1) {
            damager.level.playSound(null, damager.getOnPos(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
         }

         return newDamage >= this.getMaxDamage();
      }
   }

   @Inject(
      method = {"getDisplayName"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void useGearRarity(CallbackInfoReturnable<Component> ci) {
      if (this.getItem() instanceof VaultGearItem) {
         ItemStack itemStack = this.copy();
         VaultGearData data = VaultGearData.read(itemStack);
         VaultGearState state = data.getState();
         if (state != VaultGearState.UNIDENTIFIED) {
            MutableComponent returnValue = (MutableComponent)ci.getReturnValue();
            Style style = returnValue.getStyle().withColor(data.getRarity().getColor());
            ci.setReturnValue(returnValue.setStyle(style));
         }
      }
   }

   @Redirect(
      method = {"getTooltipLines"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/ItemStack;isDamaged()Z"
      )
   )
   public boolean alwaysShowDamageTooltip(ItemStack stack) {
      if (stack.getItem() instanceof VaultGearItem) {
         VaultGearData data = VaultGearData.read(stack);
         if (data.getState() == VaultGearState.IDENTIFIED) {
            return true;
         }
      }

      return stack.isDamaged();
   }

   @Inject(
      method = {"getHoverName"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getGearHoverName(CallbackInfoReturnable<Component> cir) {
      ItemStack thisInstance = (ItemStack)this;
      if (thisInstance.getItem() instanceof VaultGearItem) {
         CompoundTag compoundtag = thisInstance.getTagElement("display");
         if (compoundtag != null && compoundtag.contains("Name", 8)) {
            try {
               MutableComponent component = Serializer.fromJson(compoundtag.getString("Name"));
               if (component != null) {
                  VaultGearData gearData = VaultGearData.read(thisInstance);
                  cir.setReturnValue(component.withStyle(Style.EMPTY.withColor(gearData.getRarity().getColor())));
                  return;
               }

               compoundtag.remove("Name");
            } catch (Exception var6) {
               compoundtag.remove("Name");
            }
         }
      }
   }

   @Inject(
      method = {"finishUsingItem"},
      at = {@At("HEAD")}
   )
   public void finishUsingItemHead(Level world, LivingEntity entity, CallbackInfoReturnable<ItemStack> ci) {
      this.health = entity.getHealth();
   }

   @Inject(
      method = {"finishUsingItem"},
      at = {@At("RETURN")}
   )
   public void finishUsingItemReturn(Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> ci) {
      if (level instanceof VirtualWorld world && entity instanceof Player player && !(player.getHealth() <= this.health) && this.getItem() != ModItems.BOTTLE) {
         ServerVaults.get(world)
            .ifPresent(
               vault -> vault.ifPresent(
                  Vault.MODIFIERS,
                  modifiers -> {
                     int present = (int)modifiers.getEntries()
                        .stream()
                        .map(Modifiers.Entry::getContext)
                        .map(ModifierContext::getGroup)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(id -> id.equals(VaultMod.id("healing_penalty")))
                        .count();
                     if (present < 4) {
                        ChunkRandom random = ChunkRandom.any();
                        random.setSeed(vault.get(Vault.SEED));
                        random.setRegionSeed(vault.get(Vault.SEED), present, -present, random.nextInt() * present);
                        addModifier(world, vault, player, VaultMod.id("healing_penalty"), random);
                     }
                  }
               )
            );
      }
   }

   private static void addModifier(VirtualWorld world, Vault vault, Player player, ResourceLocation pool, RandomSource random) {
      List<VaultModifier<?>> modifiers = new ArrayList<>(ModConfigs.VAULT_MODIFIER_POOLS.getRandom(pool, vault.get(Vault.LEVEL).get(), random));
      Object2IntMap<VaultModifier<?>> groups = new Object2IntOpenHashMap();
      modifiers.forEach(modifier -> groups.put(modifier, groups.getOrDefault(modifier, 0) + 1));
      ObjectIterator<Entry<VaultModifier<?>>> it = groups.object2IntEntrySet().iterator();
      TextComponent suffix = new TextComponent("");

      while (it.hasNext()) {
         Entry<VaultModifier<?>> entry = (Entry<VaultModifier<?>>)it.next();
         suffix.append(((VaultModifier)entry.getKey()).getChatDisplayNameComponent(entry.getIntValue()));
         if (it.hasNext()) {
            suffix.append(new TextComponent(", "));
         }
      }

      TextComponent text = new TextComponent("");
      if (modifiers.isEmpty()) {
         text.append(player.getDisplayName())
            .append(new TextComponent(" angered the ").withStyle(ChatFormatting.GRAY))
            .append(new TextComponent("gods").withStyle(ChatFormatting.RED))
            .append(new TextComponent(".").withStyle(ChatFormatting.GRAY));
      } else {
         text.append(player.getDisplayName())
            .append(new TextComponent(" angered the ").withStyle(ChatFormatting.GRAY))
            .append(new TextComponent("gods").withStyle(ChatFormatting.RED))
            .append(new TextComponent(" and added ").withStyle(ChatFormatting.GRAY))
            .append(suffix)
            .append(new TextComponent(".").withStyle(ChatFormatting.GRAY));
      }

      groups.forEach((modifier, count) -> vault.get(Vault.MODIFIERS).addModifier(modifier, count, true, random, context -> context.setGroup(pool)));

      for (Listener listener : vault.get(Vault.LISTENERS).getAll()) {
         listener.getPlayer().ifPresent(other -> {
            world.playSound(null, other.getX(), other.getY(), other.getZ(), ModSounds.MOB_TRAP, SoundSource.PLAYERS, 0.9F, 0.6F);
            other.displayClientMessage(text, false);
         });
      }
   }
}
