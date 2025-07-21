package com.mrpotato.forceunbind;

import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;

public class GLFWKeyMap {
public static int getCodeForKeyName(String name) throws Exception {
Field field = GLFW.class.getField("GLFW_KEY_" + name);
return field.getInt(null);
}
}