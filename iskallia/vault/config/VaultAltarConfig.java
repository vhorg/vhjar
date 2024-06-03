package iskallia.vault.config;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.world.data.item.ItemPredicate;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.SoulFlameItem;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import iskallia.vault.item.crystal.modifiers.DefaultCrystalModifiers;
import iskallia.vault.item.crystal.objective.AscensionCrystalObjective;
import iskallia.vault.item.crystal.objective.CompoundCrystalObjective;
import iskallia.vault.item.crystal.objective.PoolCrystalObjective;
import iskallia.vault.item.crystal.properties.CapacityCrystalProperties;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.ServerLifecycleHooks;

public class VaultAltarConfig extends Config {
   @Expose
   public float PULL_SPEED;
   @Expose
   public double PLAYER_RANGE_CHECK;
   @Expose
   public double ITEM_RANGE_CHECK;
   @Expose
   public int INFUSION_TIME;
   @Expose
   public int GROUP_DISPLAY_TICKS;
   @Expose
   public List<VaultAltarConfig.Interface> INTERFACES;

   @Override
   public String getName() {
      return "vault_altar";
   }

   public Optional<ItemStack> getOutput(ItemStack input, UUID uuid) {
      for (VaultAltarConfig.Interface element : this.INTERFACES) {
         if (element.matchesInput(input)) {
            return Optional.of(element.getOutput());
         }
      }

      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      ServerPlayer player = server.getPlayerList().getPlayer(uuid);
      int level = PlayerVaultStatsData.get(server).getVaultStats(uuid).getVaultLevel();
      if (input.getItem() != ModItems.SOUL_FLAME) {
         return Optional.empty();
      } else if (level < 65) {
         player.sendMessage(new TextComponent("You need to be level 65.").withStyle(ChatFormatting.RED), ChatType.GAME_INFO, uuid);
         return Optional.empty();
      } else if (!uuid.equals(SoulFlameItem.getOwnerUUID(input).orElse(null))) {
         player.sendMessage(new TextComponent("This item does not belong to you.").withStyle(ChatFormatting.RED), ChatType.GAME_INFO, uuid);
         return Optional.empty();
      } else {
         ItemStack stack = new ItemStack(ModItems.VAULT_CRYSTAL);
         CrystalData crystal = CrystalData.read(stack);
         if (crystal.getProperties() instanceof CapacityCrystalProperties properties) {
            properties.setVolume(0);
         }

         crystal.getProperties().setLevel(level);

         for (VaultModifier<?> modifier : ModConfigs.VAULT_MODIFIER_POOLS.getRandom(VaultMod.id("soul_flame_apply"), level, JavaRandom.ofNanoTime())) {
            crystal.getModifiers().add(VaultModifierStack.of(modifier));
         }

         for (int i = 0; i < SoulFlameItem.getStacks(input); i++) {
            for (VaultModifier<?> modifier : ModConfigs.VAULT_MODIFIER_POOLS.getRandom(VaultMod.id("soul_flame_stack"), level, JavaRandom.ofNanoTime())) {
               crystal.getModifiers().add(VaultModifierStack.of(modifier));
            }
         }

         crystal.setObjective(
            CompoundCrystalObjective.flatten(
               new PoolCrystalObjective(VaultMod.id("ascension")),
               new AscensionCrystalObjective(
                  SoulFlameItem.getStacks(input),
                  SoulFlameItem.getOwnerName(input).orElse(null),
                  SoulFlameItem.getOwnerUUID(input).orElse(null),
                  SoulFlameItem.getModifiers(input).orElseGet(DefaultCrystalModifiers::new)
               )
            )
         );
         SoulFlameItem.getModifiers(input).ifPresent(modifiers -> {
            for (VaultModifierStack modifierx : modifiers.getList()) {
               crystal.getModifiers().add(modifierx);
            }
         });
         crystal.getProperties().setUnmodifiable(true);
         crystal.getModifiers().setRandomModifiers(false);
         crystal.write(stack);
         return Optional.of(stack);
      }
   }

   @Override
   protected void reset() {
      this.PULL_SPEED = 1.0F;
      this.PLAYER_RANGE_CHECK = 32.0;
      this.ITEM_RANGE_CHECK = 8.0;
      this.INFUSION_TIME = 5;
      this.GROUP_DISPLAY_TICKS = 20;
   }

   public static class Interface implements ISerializable<CompoundTag, JsonObject> {
      protected ItemPredicate input;
      protected ItemStack output;

      public Interface() {
      }

      public Interface(ItemPredicate input, ItemStack output) {
         this.input = input;
         this.output = output;
      }

      public boolean matchesInput(ItemStack input) {
         return this.input.test(input);
      }

      public ItemStack getOutput() {
         return this.output.copy();
      }

      @Override
      public Optional<JsonObject> writeJson() {
         JsonObject json = new JsonObject();
         Adapters.ITEM_PREDICATE.writeJson(this.input).ifPresent(input -> json.add("input", input));
         Adapters.ITEM_STACK.writeJson(this.output).ifPresent(output -> json.add("output", output));
         return Optional.of(json);
      }

      public void readJson(JsonObject json) {
         this.input = Adapters.ITEM_PREDICATE.readJson(json.get("input")).orElse(ItemPredicate.FALSE);
         this.output = Adapters.ITEM_STACK.readJson(json.get("output")).orElse(new ItemStack(ModItems.ERROR_ITEM));
      }
   }
}
