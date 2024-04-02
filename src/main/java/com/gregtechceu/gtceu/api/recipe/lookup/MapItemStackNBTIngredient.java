package com.gregtechceu.gtceu.api.recipe.lookup;

import com.gregtechceu.gtceu.api.recipe.ingredient.IntCircuitIngredient;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.NBTIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MapItemStackNBTIngredient extends MapItemStackIngredient {

    protected NBTIngredient nbtIngredient;

    public MapItemStackNBTIngredient(ItemStack s, NBTIngredient nbtIngredient) {
        super(s);
        this.nbtIngredient = nbtIngredient;
    }

    @NotNull
    public static List<AbstractMapIngredient> from(@NotNull NBTIngredient r) {
        ObjectArrayList<AbstractMapIngredient> list = new ObjectArrayList<>();
        for (ItemStack s : r.getItems()) {
            list.add(new MapItemStackNBTIngredient(s, r));
        }
        return list;
    }

    @NotNull
    public static List<AbstractMapIngredient> from(@NotNull IntCircuitIngredient r) {
        NBTIngredient nbtIngredient = NBTIngredient.of(true, IntCircuitBehaviour.stack(r.getConfiguration()));
        ObjectArrayList<AbstractMapIngredient> list = new ObjectArrayList<>();
        for (ItemStack s : r.getItems()) {
            list.add(new MapItemStackNBTIngredient(s, nbtIngredient));
        }
        return list;
    }

    @Override
    protected int hash() {
        return stack.getItem().hashCode() * 31;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MapItemStackNBTIngredient other) {
            if (this.stack.getItem() != other.stack.getItem()) {
                return false;
            }
            if (this.nbtIngredient != null) {
                if (other.nbtIngredient != null) {
                    return ItemStack.isSameItemSameTags(nbtIngredient.getItems()[0], other.nbtIngredient.getItems()[0]);
                }
            } else if (other.nbtIngredient != null) {
                return other.nbtIngredient.test(this.stack);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "MapItemStackNBTIngredient{" + "item=" + BuiltInRegistries.ITEM.getKey(stack.getItem()) + "}";
    }

    @Override
    public boolean isSpecialIngredient() {
        return true;
    }
}