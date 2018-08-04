package ru.cyberbiology.test.gene;

import ru.cyberbiology.test.World;
import ru.cyberbiology.test.prototype.IBot;
import ru.cyberbiology.test.prototype.gene.ABotGeneController;
import java.security.SecureRandom;

/**
//*********************************************************************
//................      мутировать   ...................................
// спорная команда, во время её выполнения меняются случайным образом две случайные команды
// читал, что микроорганизмы могут усилить вероятность мутации своего генома в неблагоприятных условиях
            if (command == 48) {
            	byte ma = (byte) (Math.random() * MIND_SIZE);  // 0..63
                byte mc = (byte) (Math.random() * MIND_SIZE);  // 0..63
                mind[ma] = mc;

                ma = (byte) (Math.random() * MIND_SIZE);  // 0..63
                mc = (byte) (Math.random() * MIND_SIZE);  // 0..63
                mind[ma] = mc;
                botIncCommandAddress(this, 1);
                break;     // выходим, так как команда мутировать - завершающая
            }
 * @author Nickolay
 *
 */
public class GeneMutate extends ABotGeneController
{
    private SecureRandom secureRandom = new SecureRandom();

    @Override
    public boolean onGene(IBot bot)
    {
        byte bytes[] = new byte[2];
        secureRandom.nextBytes(bytes);
        byte ma = (byte) (int) ((bytes[0] & 0b01111111) % IBot.MIND_SIZE);
        byte mc = (byte) (int) ((bytes[1] & 0b01111111) % IBot.MIND_SIZE);
//    	byte ma = (byte) (Math.random() * bot.MIND_SIZE);  // 0..63
//      byte mc = (byte) (Math.random() * bot.MIND_SIZE);  // 0..63
        bot.setMind(ma, mc);

        secureRandom.nextBytes(bytes);
        ma = (byte) (int) ((bytes[0] & 0b01111111) % IBot.MIND_SIZE);
        mc = (byte) (int) ((bytes[1] & 0b01111111) % IBot.MIND_SIZE);
//      ma = (byte) (Math.random() * bot.MIND_SIZE);  // 0..63
//      mc = (byte) (Math.random() * bot.MIND_SIZE);  // 0..63
        bot.setMind(ma, mc);
        bot.incCommandAddress(1);
        return true; // выходим, так как команда мутировать - завершающая
	}
	@Override
	public String getDescription(IBot bot, int i)
	{
		return "мутировать";
	}
}
