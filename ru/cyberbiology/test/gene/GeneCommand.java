package ru.cyberbiology.test.gene;

import ru.cyberbiology.test.prototype.IBot;
import ru.cyberbiology.test.prototype.gene.ABotGeneController;

public class GeneCommand extends ABotGeneController {
	@Override
	public boolean onGene(IBot bot) {
		int param = bot.getParam();
		for (int i = 0; i < 8; i++){
			if (bot.seeBots(i, 0) >= 4) {
				bot.command(i, param);
			}
		}
		bot.incCommandAddress(2);
		
		return false;
	}
	
	@Override
	public String getDescription(IBot bot, int i) {
		return "Выполнить команду";
	}
}
