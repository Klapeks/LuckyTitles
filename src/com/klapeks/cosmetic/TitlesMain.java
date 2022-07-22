package com.klapeks.cosmetic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.klapeks.cosmetic.Titles.TitleCategory;
import com.klapeks.libs.commands.ComplexMatiaCommand;
import com.klapeks.libs.commands.MatiaCommand;
import com.klapeks.libs.nms.NMS;

public class TitlesMain extends JavaPlugin {
	
	@Override
	public void onEnable() {
		ComplexMatiaCommand cmd = new ComplexMatiaCommand("pa:fakeopen");
		cmd.on(p -> {
			p.sendMessage("open/close");
			
		});
		cmd.on("open", p -> NMS.server.broadcastChestAnimation(p.getTargetBlock(null, 10).getLocation(), true));
		cmd.on("close", p -> NMS.server.broadcastChestAnimation(p.getTargetBlock(null, 10).getLocation(), false));
		
		new MatiaCommand("m:title") {
			@Override
			public List<String> onTab(Player p, String[] args) {
				return this.tab((CommandSender) p, args);
			}
			@Override
			public List<String> onTab(ConsoleCommandSender p, String[] args) {
				return this.tab((CommandSender) p, args);
			}
			public List<String> tab(CommandSender sender, String[] args){
//				p.sendMessage(Arrays.toString(args) + " - " + args.length);
				if (args.length!=1) return null;
				List<String> cmds = new ArrayList<>();
				Titles.categories.forEach(t->cmds.add(t.getId()));
				if (sender.hasPermission("mytitles.reload")) cmds.add("reload");
				cmds.add("list");
				return cmds.stream().filter(s->s.startsWith(args[0])).toList();
			}
			@Override
			public void onCommand(Player p, String[] args) {
				if (args == null || args.length==0) {
					p.sendMessage("a b");
					return;
				}
				if (args[0].equals("reload")) {
					if (!p.hasPermission("mytitles.reload")) {
						p.sendMessage("§9[Titles] §cNo perms");
						return;
					}
					p.sendMessage("§9[Titles] §6Reloading...");
					loadTitles();
					return;
				}
				if (args[0].equals("list")) {
					sendList(p);
					return;
				}
				Titles.openMenu(p, args[0]);
			}
			@Override
			public void onConsole(ConsoleCommandSender ccs, String[] args) {
				if (args[0].equals("list")) {
					sendList(ccs);
					return;
				}
				if (args[0].equals("reload")) {
					ccs.sendMessage("§9[Titles] §6Reloading...");
					loadTitles();
					return;
				}
				ccs.sendMessage("§9[Titles] §cUnknown argument");
			};
			public void sendList(CommandSender sender) {
				StringBuilder sb = new StringBuilder();
				sb.append("§6Title Categories §7(");
				sb.append(Titles.categories.size());
				sb.append(")§r");
				for (TitleCategory tc : Titles.categories) {
					sb.append("\n");
					sb.append("§r- ");
					sb.append(tc.getId());
					sb.append("§7 (");
					sb.append(tc.titles.size());
					sb.append(")§r - ");
					sb.append(tc.getName());
				}
				sender.sendMessage(sb.toString());
			}
		};
		loadTitles();
	}
	
	public static void loadTitles() {
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
