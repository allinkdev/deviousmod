package me.allink.deviousmod.module;

import me.allink.deviousmod.managers.ModuleManager;
import net.minecraft.client.MinecraftClient;

public class ModuleBase {

    public String name;
    public String description;
    public String category;
    public ModuleManager manager;

    public boolean toggled;

    public ModuleBase(String name, String description, String category, ModuleManager manager) {
        this(name, description, category, false, manager);
    }

    public ModuleBase(String name, String description, String category, boolean toggled,
        ModuleManager manager) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.toggled = toggled;
        this.manager = manager;
    }

    public void tick(MinecraftClient client) {

    }

    /**
     * Execute the module's join code
     */
    public void onJoin(final MinecraftClient client) {
    }

    /**
     * Execute the module's leave code
     */
    public void onLeave(final MinecraftClient client) {
    }

    /**
     * Execute the module
     */
    public void execute() {
    }

    /**
     * Check if the module is toggled
     *
     * @return Toggle value
     */
    @Deprecated
    public boolean isToggled() {
        return toggled;
    }

    /**
     * Set the module's toggled value to a boolean
     *
     * @param toggled The value to set it to
     */
    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        if (toggled) {
            onEnabled();
        } else {
            onDisabled();
        }
        manager.updateModuleToggle(this, toggled);
    }

    /**
     * Toggle the module
     *
     * @return The new value
     */
    public boolean toggle() {
        this.toggled = !toggled;
        if (toggled) {
            onEnabled();
        } else {
            onDisabled();
        }
        manager.updateModuleToggle(this, toggled);
        return toggled;
    }

    /**
     * Execute code on enable
     */
    public void onEnabled() {

    }

    /**
     * Execute code on disable
     */
    public void onDisabled() {

    }
}
