package iskallia.vault.item;

import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IConditionalDamageable {
   boolean isImmuneToDamage(ItemStack var1, @Nullable Player var2);
}
