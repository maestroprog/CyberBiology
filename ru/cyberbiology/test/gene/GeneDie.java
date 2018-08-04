package ru.cyberbiology.test.gene;

import ru.cyberbiology.test.prototype.IBot;
import ru.cyberbiology.test.prototype.gene.ABotGeneController;

public class GeneDie extends ABotGeneController {
	@Override
	public boolean onGene(IBot bot) {
//		bot.die();
		bot.incCommandAddress(1);
		
		return true;
	}
	
	@Override
	public String getDescription(IBot bot, int i) {
		return "Ген умирания";
	}
}
