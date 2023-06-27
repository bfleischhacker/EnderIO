package com.enderio.base.common.item.capacitors;

import com.enderio.api.capability.IMultiCapabilityItem;
import com.enderio.api.capability.MultiCapabilityProvider;
import com.enderio.base.common.capacitor.LootCapacitorData;
import com.enderio.base.common.init.EIOCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

public class LootCapacitorItem extends Item implements IMultiCapabilityItem {

    public LootCapacitorItem(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public MultiCapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt, MultiCapabilityProvider provider) {
        provider.addSerialized(EIOCapabilities.CAPACITOR, LazyOptional.of(LootCapacitorData::new));
        return provider;
    }
}