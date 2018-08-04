package ru.cyberbiology.test.view;

import ru.cyberbiology.test.Bot;
import ru.cyberbiology.test.World;
import ru.cyberbiology.test.prototype.view.IView;

import javax.swing.*;
import java.awt.*;

public class ViewBasic implements IView
{

	public ViewBasic()
	{
		// TODO Auto-generated constructor stub
	}
	@Override
	public String getName()
	{
		// Отображение ...
		return "Базовое";
	}
    public Image paint(World world,JPanel canvas) {
    	int w = canvas.getWidth();
    	int h = canvas.getHeight();
    	//Создаем временный буфер для рисования
    	Image buf = canvas.createImage(w, h);
    	//подеменяем графику на временный буфер
    	Graphics g = buf.getGraphics();
    	
        g.drawRect(0, 0, world.width * World.BOTW + 1, world.height * World.BOTH + 1);
//        g.setColor(Color.WHITE);
//		g.fillRect(0, 0, world.width * World.BOTW + 1, world.height * 4 + 1);

        world.population = 0;
        world.organic = 0;
		for (Bot bot: world.botList.toArray(new Bot[0])) {
            int x = bot.x;
            int y = bot.y;
            if ((bot.alive == 1) || (bot.alive == 2)) {
                g.setColor(new Color(200, 200, 200));
                g.fillRect(x * World.BOTW, y * World.BOTH, World.BOTW, World.BOTH);
                world.organic++;
            } else if (bot.alive == 3) {
                g.setColor(Color.BLACK);
                g.drawRect(x * World.BOTW, y * World.BOTH, World.BOTW, World.BOTH);

//                    g.setColor(new Color(getBot(x, y).c_red, getBot(x, y).c_green, getBot(x, y).c_blue));
                int green = (bot.c_green - ((bot.c_green * bot.health) / 2000));
                if (green < 0) green = 0;
                if (green > 255) green = 255;
                int blue = (int) (bot.c_blue * 0.8 - ((bot.c_blue * bot.mineral) / 2000));
                if (blue < 0)
				{
					blue = 0;
				} else if (blue > 255)
				{
					blue = 255;
				}
				int red = bot.c_red;
                if (red < 0) {
                	red = 0;
				} else if (red > 255) {
                	red = 255;
				}
                g.setColor(new Color(red, green, blue));
//                    g.setColor(new Color(getBot(x, y).c_red, getBot(x, y).c_green, getBot(x, y).c_blue));
                g.fillRect(x * World.BOTW + 1, y * World.BOTH + 1, World.BOTW - 1, World.BOTH - 1);
                world.population++;
            }
        }
        return buf;
    }
}
