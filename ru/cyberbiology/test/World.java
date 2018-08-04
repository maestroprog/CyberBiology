package ru.cyberbiology.test;

import ru.cyberbiology.test.prototype.IBot;
import ru.cyberbiology.test.prototype.IWindow;
import ru.cyberbiology.test.prototype.IWorld;
import ru.cyberbiology.test.prototype.record.IRecordManager;
import ru.cyberbiology.test.record.v0.PlaybackManager;
import ru.cyberbiology.test.record.v0.RecordManager;
import ru.cyberbiology.test.util.ProjectProperties;
import sun.awt.Mutex;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class World implements IWorld
{
	public World world;
	public IWindow window;
	
	PlaybackManager playback;
	IRecordManager recorder;

	public static final int BOTW = 4;
	public static final int BOTH = 4;

	public int width;
	public int height;

	public Bot[] bots;
	public List<Bot> botAddQueue;
	public List<Bot> botDelQueue;
	public List<Bot> botList;
	public int generation;
	public int population;
	public int organic;

	boolean started;
	Painter thread;
	protected World(IWindow win)
	{
		world = this;
		window = win;
		population = 0;
		generation = 0;
		organic = 0;
		recorder = new RecordManager(this);
	}
	public World(IWindow win, int width, int height)
	{
		this(win);
		this.setSize(width, height);
	}

	@Override
	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.bots = new Bot[width * height];
		this.botAddQueue = new ArrayList<>();
		this.botDelQueue = new ArrayList<>();
		this.botList = new ArrayList<>();
	}
	
	public void addBot(Bot bot)
	{
		this.setBot(bot);
		this.botAddQueue.add(bot);
	}
	
	@Override
	public void setBot(Bot bot)
	{
		this.bots[bot.y * width + bot.x] = bot;
	}

	public void paint()
	{
		window.paint();
	}
	
	void unsetBot(Bot bot)
	{
		this.bots[bot.y * width + bot.x] = null;
	}

	void clearBot(Bot bot)
	{
		this.unsetBot(bot);
		this.botAddQueue.remove(bot);
		this.botDelQueue.add(bot);
	}

	@Override
	public ProjectProperties getProperties()
	{
		return window.getProperties();
	}
	class Painter extends Thread
	{
		private final Worker worker;
		
		Painter(Worker worker) {
			this.worker = worker;
			worker.start();
		}
		
		public void run()
		{
			while (true)
			{
				try {
					sleep(33);
				} catch (InterruptedException e){
					e.printStackTrace();
				}
//				synchronized (worker) {
					try {
						paint();
					}catch (Throwable e){
						e.printStackTrace();
					}
//				}
			}
		}
	}
	class Worker extends Thread
	{
		public void run()
		{
			started = true;// Флаг работы потока, если установить в false поток
							// заканчивает работу
			while (started)
			{

				boolean rec = recorder.isRecording(); // запоминаем флаг
														// "записывать" на
														// полную итерацию кадра
				if (rec)// вызываем обработчика "старт кадра"
				{
					recorder.startFrame();
				}
				
//				synchronized (this) {
					world.botList.addAll(world.botAddQueue);
					world.botList.removeAll(world.botDelQueue);
//				}
				world.botAddQueue.clear();
				world.botDelQueue.clear();
				
				// обновляем матрицу
				for (Bot bot : world.botList) {
					bot.step(); // выполняем шаг бота
					if (rec) {
						// вызываем обработчика записи бота
						recorder.writeBot(bot, bot.x, bot.y);
					}
				}
				if (rec)// вызываем обработчика "конец кадра"
				{
					recorder.stopFrame();
				}
				generation++;
				// sleep(); // пауза между ходами, если надо уменьшить скорость
			}
			paint();// если запаузили рисуем актуальную картинку
			started = false;// Закончили работу
		}
	}

	public void generateAdam()
	{
		// ========== 1 ==============
		// бот номер 1 - это уже реальный бот
		Bot bot = new Bot(this);

		bot.adr = 0;
		bot.x = width / 2; // координаты бота
		bot.y = height / 2;
		bot.health = 990; // энергия
		bot.mineral = 0; // минералы
		bot.alive = 3; // отмечаем, что бот живой
		bot.c_red = 170; // задаем цвет бота
		bot.c_blue = 170;
		bot.c_green = 170;
		bot.direction = 5; // направление
		bot.mprev = null; // бот не входит в многоклеточные цепочки, поэтому
							// ссылки
		bot.mnext = null; // на предыдущего, следующего в многоклеточной цепочке
							// пусты
		for (int i = 0; i < IBot.MIND_SIZE; i++)
		{ // заполняем геном командой 25 - фотосинтез
			bot.mind[i] = 25;
		}

		this.addBot(bot); // даём ссылку на бота в массиве world[]

		return;
	}
	public void restoreLinks()
	{
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				if (hasBot(x, y))
				{
					Bot bot = getBot(x, y);
					if (bot.alive == 3)
					{
						if(bot.mprevX>-1 && bot.mprevY>-1)
						{
							bot.mprev = getBot(bot.mprevX, bot.mprevY);
						}
						if(bot.mnextX>-1 && bot.mnextY>-1)
						{
							bot.mnext = getBot(bot.mnextX, bot.mnextY);
						}
					}
				}
			}
		}
	}
	public boolean started()
	{
		return this.thread != null;
	}

	public void start()
	{
		if (!this.started())
		{
			this.thread = new Painter(new Worker());
			this.thread.start();
		}
	}

	public void stop()
	{
		started = false;
		this.thread = null;
	}

	public boolean isRecording()
	{
		return this.recorder.isRecording();
	}

	public void startRecording()
	{
		this.recorder.startRecording();
	}

	public boolean stopRecording()
	{
		return this.recorder.stopRecording();
	}

	public Bot getBot(int botX, int botY)
	{
		return this.bots[botY * width + botX];
	}
	
	public boolean hasBot(int botX, int botY)
	{
		return this.bots[botY * width + botX] != null;
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	public boolean haveRecord()
	{
		return this.recorder.haveRecord();
	}
	/*
	public void deleteRecord()
	{
		this.recorder.deleteRecord();
	}*/

	public void makeSnapShot()
	{
		this.recorder.makeSnapShot();
	}
	@Override
	public Bot[][] getWorldArray()
	{
		Bot[][] matrix = new Bot[width][height];
		for (Bot bot : bots) {
			matrix[bot.x][bot.y] = bot;
		}
		return matrix;
	}
	public void openFile(File file)
	{
		playback = new PlaybackManager(this, file);
	}
}
