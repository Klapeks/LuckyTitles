package com.klapeks.cosmetic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.klapeks.cosmetic.Titles.TitleCategory;
import com.klapeks.cosmetic.db.TitleDB;
import com.klapeks.libs.commands.MatiaCommand;
import com.klapeks.libs.commands.Messaging;

public class TitleCommand extends MatiaCommand {

	public TitleCommand() {
		super("m:titles");
	}
	
	@Override
	public void command(CommandSender sender, String[] args) {
		if (args==null||args.length==0||args[0].equals("list")) {
			sendList(sender);
			return;
		}
		if (args[0].equalsIgnoreCase("open")) {
			checkUsage(sender instanceof Player, "This command only for players!");
			checkUsage(args.length > 1, "Usage: /titles open <category_id>");
			TitleCategory tc = Titles.getCategory(args[1]);
			checkUsage(tc != null, "Unknown category: " + args[1]);
			tc.getMenu().openInventory((Player) sender);
			return;
		}
		switch (args[0].toLowerCase()) {
		case "add": {
			checkPerms(sender, "titles.add");
			checkUsage(args.length > 3, "Usage: /titles add <player> <category_id> <title_id>");
			TitleDB.addTitle(args[1], args[2], args[3]);
			sender.sendMessage("§aDone");
			return;
		}
		case "remove": {
			checkPerms(sender, "titles.remove");
			checkUsage(args.length > 3, "Usage: /titles remove <player> <category_id> <title_id>");
			TitleDB.removeTitle(args[1], args[2], args[3]);
			sender.sendMessage("§aDone");
			return;
		}
		case "get": {
			checkPerms(sender, "titles.get");
			checkUsage(args.length > 2, "Usage: /titles get <player> <category_id>");
			sender.sendMessage("Titles:");
			List<String> titles = TitleDB.getTitles(args[1], args[2]);
			for (String s : titles) {
				sender.sendMessage(s);
			};
			return;
		}
		case "reload": {
			checkPerms(sender, "titles.reload");
			sender.sendMessage("§9[Titles] §6Reloading...");
			TitlesMain.loadTitles();
		}
		default: break;
		}
		sender.sendMessage("§cUnknown subcommand!");
	}

	@Override
	public Collection<String> tab(CommandSender sender, String[] args) {
		if (args.length==1) {
			List<String> list = new ArrayList<>();
			if (sender instanceof Player) list.add("open");
			list.add("list");
			if (sender.hasPermission("titles.add")) list.add("add");
			if (sender.hasPermission("titles.remove")) list.add("remove");
			if (sender.hasPermission("titles.get")) list.add("get");
			if (sender.hasPermission("titles.clear")) list.add("clear");
			if (sender.hasPermission("titles.reload")) list.add("reload");
			return list;
		}
		else if (args[0].equalsIgnoreCase("get") && sender.hasPermission("titles.get")) {
			if (args.length == 2) return players;
			if (args.length == 3) return Titles.getCategoriesIds();
		}
		else if (args[0].equalsIgnoreCase("add") && sender.hasPermission("titles.add")) {
			if (args.length == 2) return players;
			if (args.length == 3) return Titles.getCategoriesIds();
			if (args.length == 4) return Titles.getCategoryTitlesIds(args[2]);
		}
		else if (args[0].equalsIgnoreCase("remove") && sender.hasPermission("titles.remove")) {
			if (args.length == 2) return players;
			if (args.length == 3) return Titles.getCategoriesIds();
			if (args.length == 4) return Titles.getCategoryTitlesIds(args[2]);
		}
		else if (args[0].equalsIgnoreCase("clear") && sender.hasPermission("titles.clear")) {
			if (args.length == 2) return players;
			if (args.length == 3) return Titles.getCategoriesIds();
		}
		else if (sender instanceof Player && args[0].equalsIgnoreCase("open")) {
			if (args.length==2) return Titles.getCategoriesIds();
		}
		return null;
	}

	
	private void sendList(CommandSender sender) {
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
	public void onCommand(Player p, String[] args) {
	}

	@Override
	public Collection<String> onTab(Player p, String[] args) {
		return null;
	}

}
