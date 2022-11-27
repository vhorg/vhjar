package iskallia.vault.core.event;

public abstract class ForgeEvent<E extends ForgeEvent<E, T>, T extends net.minecraftforge.eventbus.api.Event> extends Event<E, T> {
   protected ForgeEvent() {
      this.register();
   }

   protected ForgeEvent(E parent) {
      super(parent);
   }

   protected abstract void register();
}
