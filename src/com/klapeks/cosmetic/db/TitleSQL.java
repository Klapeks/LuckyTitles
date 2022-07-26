package com.klapeks.cosmetic.db;

import java.util.ArrayList;
import java.util.List;

import com.klapeks.db.SQL;
import com.klapeks.sql.Where;

public class TitleSQL implements TitleDB {
	
	@Override
	public void init() {
		SQL.sql().createTableIfNotExists(TitlePlayer.class);
	}

	@Override
	public List<String> getTitles(String playername, String category) {
		Where where = SQL.where("`player` = ? AND `category` = ?", playername.toLowerCase(), category);
		return SQL.sql().selectFirst(TitlePlayer.class, where).titles;
	}

	@Override
	public void addTitle(String playername, String category, String title) {
		Where where = SQL.where("`player` = ? AND `category` = ?", playername.toLowerCase(), category);
		TitlePlayer tp = SQL.sql().selectFirst(TitlePlayer.class, where);
		if (tp==null) {
			tp = new TitlePlayer();
			tp.player = playername.toLowerCase();
			tp.category = category;
			tp.titles = new ArrayList<>();
			SQL.sql().insert(tp);
		}
		else if (tp.titles.contains(title)) return;
		tp.titles.add(title);
		SQL.sql().update(tp, where);
	}
	
}
