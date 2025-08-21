package dev.mariany.mobilityflux.item;

import dev.mariany.mobilityflux.MobilityFlux;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class MFItems {
    public static final Item SHOCKWAVE_GRENADE = register(
            "shockwave_grenade",
            ShockwaveGrenadeItem::new,
            (new Item.Settings()).maxCount(16).useCooldown(1F)
    );

    public static final Item GATECRASH = register(
            "gatecrash",
            GatecrashItem::new,
            (new Item.Settings()).maxDamage(16).useCooldown(1F)
    );

    public static final Item THERMAL_PEARL = register(
            "thermal_pearl",
            ThermalPearlItem::new,
            (new Item.Settings()).maxDamage(32).fireproof().rarity(Rarity.UNCOMMON)
    );

    public static final Item ALLAY = register(
            "allay",
            AllayItem::new,
            (new Item.Settings()).maxCount(1).rarity(Rarity.RARE)
    );

    private static Item register(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
        RegistryKey<Item> itemKey = keyOf(name);
        Item item = factory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    private static RegistryKey<Item> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ITEM, MobilityFlux.id(id));
    }

    public static void bootstrap() {
        MobilityFlux.LOGGER.info("Registering Items for " + MobilityFlux.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.addAfter(Items.WIND_CHARGE, SHOCKWAVE_GRENADE);
            entries.addAfter(SHOCKWAVE_GRENADE, ALLAY);
            entries.addAfter(ALLAY, THERMAL_PEARL);
            entries.addAfter(THERMAL_PEARL, GATECRASH);
        });
    }
}
