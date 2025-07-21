package com.mrpotato.forceunbind;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ForceUnbindMod implements ClientModInitializer {
    private static final File CONFIG_FILE = new File("config/keybind-changer-config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void onInitializeClient() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(this::applyKeyConfig);
    }

    private void applyKeyConfig() {
        Map<String, String> config = loadConfig();

        for (KeyBinding key : MinecraftClient.getInstance().options.allKeys) {
            String action = config.get(key.getTranslationKey());
            if (action != null) {
                if (action.equalsIgnoreCase("unbind")) {
                    key.setBoundKey(InputUtil.UNKNOWN_KEY);
                } else {
                    InputUtil.Key newKey = parseKey(action);
                    if (newKey != null) {
                        key.setBoundKey(newKey);
                    }
                }
            }
        }

        KeyBinding.updateKeysByCode();
        System.out.println("[Keybind Changer] Keybindings updated from config.");
    }

    private Map<String, String> loadConfig() {
        try {
            if (!CONFIG_FILE.exists()) {
                CONFIG_FILE.getParentFile().mkdirs();
                Map<String, String> defaultConfig = new HashMap<>();
                defaultConfig.put("key.sdmshop.shopr", "unbind");
                try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                    GSON.toJson(defaultConfig, writer);
                }
                return defaultConfig;
            } else {
                try (FileReader reader = new FileReader(CONFIG_FILE)) {
                    Type type = new TypeToken<Map<String, String>>() {}.getType();
                    return GSON.fromJson(reader, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private InputUtil.Key parseKey(String keyName) {
        try {
            Field field = GLFW.class.getField("GLFW_KEY_" + keyName.toUpperCase());
            int code = field.getInt(null);
            return InputUtil.fromKeyCode(code, -1);
        } catch (Exception e) {
            System.err.println("[Keybind Changer] Unknown key: " + keyName);
            return null;
        }
    }
}
