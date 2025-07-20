package com.mrpotato.forceunbind;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class ForceUnbindMod implements ClientModInitializer {
    private boolean unbound = false;
    private int ticksWaited = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!unbound) {
                ticksWaited++;
                if (ticksWaited < 40) return;

                for (KeyBinding key : MinecraftClient.getInstance().options.allKeys) {
                    System.out.println("[ForceUnbindMod] Found key: " + key.getTranslationKey());
                    if ("key.sdmshop.shopr".equals(key.getTranslationKey())) {
                        key.setBoundKey(InputUtil.UNKNOWN_KEY);
                        System.out.println("[ForceUnbindMod] Successfully unbound key.sdmshop.shopr");
                        break;
                    }
                }

                unbound = true;
            }
        });
    }
}
