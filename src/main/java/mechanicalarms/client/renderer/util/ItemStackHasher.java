package mechanicalarms.client.renderer.util;

import it.unimi.dsi.fastutil.Hash;
import net.minecraft.item.ItemStack;

public class ItemStackHasher implements Hash.Strategy<ItemStack> {
    @Override
    public int hashCode(ItemStack o) {
        int hash = o.getItem().hashCode();
        hash += 31 * o.getMetadata();
        return hash;
    }

    @Override
    public boolean equals(ItemStack a, ItemStack b) {
        if (a == b) {
            return true;
        } if (a == null || b == null) {
            return false;
        }
        return ItemStack.areItemsEqual(a, b);
    }
}
