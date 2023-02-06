package iskallia.vault.core.event.common;

import iskallia.vault.core.event.ForgeEvent;
import java.util.Collection;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

public class EntityDropsEvent extends ForgeEvent<EntityDropsEvent, LivingDropsEvent> {
   public EntityDropsEvent() {
   }

   protected EntityDropsEvent(EntityDropsEvent parent) {
      super(parent);
   }

   @Override
   protected void register() {
      MinecraftForge.EVENT_BUS.addListener(event -> this.invoke(event));
   }

   public EntityDropsEvent createChild() {
      return new EntityDropsEvent(this);
   }

   public EntityDropsEvent containing(Item item) {
      return this.filter(event -> {
         Collection<ItemEntity> drops = event.getDrops();
         return drops.stream().anyMatch(itemEntity -> itemEntity.getItem().getItem() == item);
      });
   }
}
