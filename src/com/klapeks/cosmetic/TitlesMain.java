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
import com.klapeks.cosmetic.db.TitleDB;
import com.klapeks.libs.commands.ComplexMatiaCommand;
import com.klapeks.libs.commands.MatiaCommand;
import com.klapeks.libs.commands.Messaging;
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
		
		new MatiaCommand("m:titles") {
			
			public List<String> tab(CommandSender sender, String[] args){
//				p.sendMessage(Arrays.toString(args) + " - " + args.length);
				List<String> cmds = new ArrayList<>();
				if (args.length==1) {
					if (sender instanceof Player) Titles.categories.forEach(t->{
						if (t.getId().startsWith(args[0])) cmds.add(t.getId());
					});
					if (sender.hasPermission("mytitles.reload")) {
						if ("reload".startsWith(args[0])) cmds.add("reload");
					}
					if ("list".startsWith(args[0])) cmds.add("list");
					if ("add".startsWith(args[0])) cmds.add("add");
				}
				else if ("add".equals(args[0]) || "get".equals(args[0])) {
					if (args.length==2) return players;
					if (args.length==3) {
						Titles.categories.forEach(t->{
							if (t.getId().startsWith(args[2])) cmds.add(t.getId());
						});
					}
					if (args.length==4 && "add".equals(args[0])) {
						if (Titles.getCategory(args[2])==null) return cmds;
						Titles.getCategory(args[2]).titles.keySet().forEach(s -> {
							if (s.startsWith(args[2])) cmds.add(s);
						});
					}
				}
				return cmds;
			}
			public boolean onCmd(CommandSender ccs, String[] args) {
				if (args == null || args.length==0 || args[0].equals("list")) {
					sendList(ccs);
					return true;
				}
				switch (args[0]) {
				case "add": {
					if (args.length <= 3) {
						ccs.sendMessage("§cUsage: /titles add <player> <category_id> <title_id>");
						return true;
					}
					String player = args[1];
					String category_id = args[2];
					String title_id = args[3];
					Titles.db().addTitle(player, category_id, title_id);
					ccs.sendMessage("§aDone");
					return true;
				}
				case "get": {
					if (args.length <= 2) {
						ccs.sendMessage("§cUsage: /titles get <player> <category_id>");
						return true;
					}
					String player = args[1];
					String category_id = args[2];
					ccs.sendMessage("Titles:");
					for (String s : Titles.db().getTitles(player, category_id)) {
						ccs.sendMessage(s);
					};
					return true;
				}

				default:
					break;
				}
				return false;
			}
			
			@Override
			public void onCommand(Player p, String[] args) {
				if (onCmd(p, args)) return;
				if (args[0].equals("reload")) {
					if (!p.hasPermission("mytitles.reload")) {
						p.sendMessage("§9[Titles] §cNo perms");
						return;
					}
					p.sendMessage("§9[Titles] §6Reloading...");
					loadTitles();
					return;
				}
				Titles.openMenu(p, args[0]);
			}
			public void sendList(CommandSender sender) {
				StringBuilder sb = new StringBuilder();
				sb.append("§6Title Categories §7(");
				sb.append(Titles.categories.size());
				sb.append(")§r");
				for (TitleCategory tc : Titles.categories) {
					sb.append("\n");
					sb.append("§r ");
					sb.append(tc.getId());
					sb.append("§7 (");
					sb.append(tc.titles.size());
					sb.append(")§r - ");
					sb.append(Messaging.msg(tc.getName()));
					sb.append("§r");
				}
				sender.sendMessage(sb.toString());
			}
			@Override
			public void onConsole(CommandSender ccs, String[] args) {
				if (onCmd(ccs, args)) return;
				if (args[0].equals("reload")) {
					ccs.sendMessage("§9[Titles] §6Reloading...");
					loadTitles();
					return;
				}
				ccs.sendMessage("§9[Titles] §cUnknown argument");
			};
			@Override
			public List<String> onTab(Player p, String[] args) {
				return this.tab((CommandSender) p, args);
			}
			@Override
			public List<String> onTab(CommandSender p, String[] args) {
				return this.tab(p, args);
			}
		};
		loadTitles();
		Titles.db().init();
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
