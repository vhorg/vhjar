package iskallia.vault.world.vault.chest;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.world.storage.VirtualWorld;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class PotionCloudEffect extends VaultChestEffect {
   @Expose
   public List<String> potions;

   public PotionCloudEffect(String name, Potion... potions) {
      super(name);
      this.potions = Arrays.stream(potions)
         .<ResourceLocation>map(ForgeRegistryEntry::getRegistryName)
         .filter(Objects::nonNull)
         .<String>map(ResourceLocation::toString)
         .collect(Collectors.toList());
   }

   public List<Potion> getPotions() {
      return this.potions
         .stream()
         .map(s -> (Potion)Registry.POTION.getOptional(new ResourceLocation(s)).orElse(null))
         .filter(Objects::nonNull)
         .collect(Collectors.toList());
   }

   @Override
   public void apply(VirtualWorld world, Vault vault, ServerPlayer player) {
      ThrownPotion entity = new ThrownPotion(world, player);
      ItemStack stack = new ItemStack(Items.LINGERING_POTION);
      this.getPotions().forEach(potion -> PotionUtils.setPotion(stack, potion));
      entity.setItem(stack);
      entity.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.0F);
      world.addFreshEntity(entity);
   }
}
