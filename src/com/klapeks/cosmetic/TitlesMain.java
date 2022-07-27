package com.klapeks.cosmetic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.klapeks.cosmetic.Titles.TitleCategory;
import com.klapeks.cosmetic.db.TitleDB;
import com.klapeks.libs.commands.ComplexMatiaCommand;
import com.klapeks.libs.commands.MatiaCommand;
import com.klapeks.libs.commands.Messaging;
import com.klapeks.libs.nms.NMS;

public class TitlesMain extends JavaPlugin {
	
	@Override
	public void onEnable() {
//		ComplexMatiaCommand cmd = new ComplexMatiaCommand("pa:fakeopen");
//		cmd.on(p -> {
//			p.sendMessage("open/close");
//			
//		});
//		cmd.on("open", p -> NMS.server.broadcastChestAnimation(p.getTargetBlock(null, 10).getLocation(), true));
//		cmd.on("close", p -> NMS.server.broadcastChestAnimation(p.getTargetBlock(null, 10).getLocation(), false));
		new TitleCommand();
		loadTitles();
	}
	
	public static void loadTitles() {
		TitleDB.init();
		File file = new File("plugins/Titles/titles.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		Titles.resetTitles();
		for (String categoryID : cfg.getKeys(false)) {
			if (!cfg.isConfigurationSection(categoryID)) continue;
			ConfigurationSection tt = cfg.getConfigurationSection(categoryID);
			String name = tt.contains("_name") ? tt.getString("_name") : categoryID;
			TitleCategory tc = Titles.createCategory(categoryID, name);
			for (String titleID: tt.getKeys(false)) {
				if (titleID.startsWith("_")) continue;
				tc.addTitle(titleID, tt.getString(titleID));
			}
		}
		String msg = "§9[Titles] §aPlugin configuration was successfully reloaded";
		Bukkit.getLogger().info(msg);
		Bukkit.getOnlinePlayers().forEach(p -> {
			if (!p.hasPermission("mytitles.reload")) return;
			p.sendMessage(msg);
		});
	}
	
}
