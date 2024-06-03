package iskallia.vault.item.crystal.modifiers;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.world.data.ParadoxCrystalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ParadoxCrystalModifiers extends CrystalModifiers {
   private UUID playerUuid;

   public void setPlayerUuid(UUID playerUuid) {
      this.playerUuid = playerUuid;
   }

   @Override
   public List<VaultModifierStack> getList() {
      return (List<VaultModifierStack>)(this.playerUuid == null ? new ArrayList<>() : ParadoxCrystalData.getEntry(this.playerUuid).modifiers);
   }

   @Override
   public boolean hasRandomModifiers() {
      return false;
   }

   @Override
   public boolean hasClarity() {
      return true;
   }

   @Override
   public void setRandomModifiers(boolean randomModifiers) {
   }

   @Override
   public void setClarity(boolean clarity) {
   }

   @Override
   public boolean addByCrafting(CrystalData crystal, List<VaultModifierStack> modifierStackList, boolean simulate) {
      return false;
   }

   @Override
   public boolean addByCrafting(CrystalData crystal, VaultModifierStack modifierStack, boolean preventsRandomModifiers, boolean simulate) {
      return false;
   }

   @Override
   public void add(VaultModifierStack modifierStack) {
      super.add(modifierStack);
      ParadoxCrystalData.getEntry(this.playerUuid).changed = true;
   }

   @Override
   public void addText(List<Component> tooltip, int minIndex, TooltipFlag flag, float time) {
      Style style = Style.EMPTY.withColor(ModConfigs.VAULT_CRYSTAL.MODIFIER_STABILITY.curseColor);
      this.addCatalystModifierInformation(
         stack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isCurse(stack.getModifierId()), new TextComponent("Cursed").withStyle(style), tooltip
      );
      this.addCatalystModifierInformation(
         stack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isGood(stack.getModifierId()),
         new TextComponent("Positive Modifiers").withStyle(ChatFormatting.GREEN),
         tooltip
      );
      this.addCatalystModifierInformation(
         stack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isBad(stack.getModifierId()),
         new TextComponent("Negative Modifiers").withStyle(ChatFormatting.RED),
         tooltip
      );
      this.addNonCatalystModifierInformation(
         stack -> ModConfigs.VAULT_CRYSTAL_CATALYST.isUnlisted(stack.getModifierId()),
         new TextComponent("Other Modifiers").withStyle(ChatFormatting.WHITE),
         tooltip
      );
   }

   @Override
   public void onInventoryTick(Level world, Entity entity, int slot, boolean selected) {
      if (entity instanceof ServerPlayer player) {
         this.playerUuid = player.getGameProfile().getId();
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.UUID.writeNbt(this.playerUuid).ifPresent(uuid -> nbt.put("player_uuid", uuid));
      return Optional.of(nbt);
   }

   public void readNbt(CompoundTag nbt) {
      this.playerUuid = Adapters.UUID.readNbt(nbt.get("player_uuid")).orElse(null);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      Adapters.UUID.writeJson(this.playerUuid).ifPresent(uuid -> json.add("player_uuid", uuid));
      return Optional.of(json);
   }

   public void readJson(JsonObject json) {
      this.playerUuid = Adapters.UUID.readJson(json.get("player_uuid")).orElse(null);
   }
}
