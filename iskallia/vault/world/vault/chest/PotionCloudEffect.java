package iskallia.vault.world.vault.chest;

import com.google.gson.annotations.Expose;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.player.VaultPlayer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;
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
         .map(s -> (Potion)Registry.field_212621_j.func_241873_b(new ResourceLocation(s)).orElse(null))
         .filter(Objects::nonNull)
         .collect(Collectors.toList());
   }

   @Override
   public void apply(VaultRaid vault, VaultPlayer player, ServerWorld world) {
      player.runIfPresent(world.func_73046_m(), playerEntity -> {
         PotionEntity entity = new PotionEntity(world, playerEntity);
         ItemStack stack = new ItemStack(Items.field_185156_bI);
         this.getPotions().forEach(potion -> PotionUtils.func_185188_a(stack, potion));
         entity.func_213884_b(stack);
         entity.func_234612_a_(playerEntity, playerEntity.field_70125_A, playerEntity.field_70177_z, -20.0F, 0.5F, 1.0F);
         world.func_217376_c(entity);
      });
   }
}
