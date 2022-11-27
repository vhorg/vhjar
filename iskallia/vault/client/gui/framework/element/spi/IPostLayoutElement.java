package iskallia.vault.client.gui.framework.element.spi;

public interface IPostLayoutElement<T extends IPostLayoutElement<T>> extends ILayoutElement<T> {
   T postLayout(IPostLayoutStrategy var1);
}
