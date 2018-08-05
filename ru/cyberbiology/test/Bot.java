package ru.cyberbiology.test;


import ru.cyberbiology.test.gene.*;
import ru.cyberbiology.test.prototype.IBot;
import ru.cyberbiology.test.prototype.IWorld;
import ru.cyberbiology.test.prototype.gene.IBotGeneController;

import java.security.SecureRandom;


public class Bot implements IBot, Cloneable {

    public int adr;
    public int x;
    public int y;
    public int health;
    public int mineral;
    public int alive;
    public int c_red;
    public int c_green;
    public int c_blue;
    public int direction;
    public Bot mprev;
    public Bot mnext;

    public static final int MIND_SIZE = IBot.MIND_SIZE; //Объем генома
    static IBotGeneController[] geneController = new IBotGeneController[IBot.MIND_SIZE];
    static
    {
	    geneController[23]	= new GeneChangeDirectionRelative(); //23 сменить направление относительно
	    geneController[24]	= new GeneChangeDirectionAbsolutely(); //24 сменить направление абсолютно
	    geneController[25]	= new GenePhotosynthesis();//25 фотосинтез
	    geneController[26]	= new GeneStepInRelativeDirection();//26 шаг   в относительном направлении
	    geneController[27]	= new GeneStepInAbsolutelyDirection();//27 шаг   в абсолютном направлении
	    geneController[28]	= new GeneEatRelativeDirection();//28 шаг  съесть в относительном направлении
	    geneController[29]	= new GeneEatAbsoluteDirection();//29 шаг  съесть в абсолютном направлении
	    geneController[30]	= new GeneLookRelativeDirection();//30 шаг  посмотреть в относительном направлении
	    
	    geneController[32]	= new GeneCareRelativeDirection();//32 шаг делится   в относительном напралении
	    geneController[42]	= geneController[32];
	    
	    geneController[33]	= new GeneCareAbsolutelyDirection();//33 шаг делится   в абсолютном напралении
	    geneController[50]	= geneController[33];
	    
	    geneController[34]	= new GeneGiveRelativeDirection();//34 шаг отдать   в относительном напралении
	    geneController[51]	= geneController[34];
	    
	    geneController[35]	= new GeneGiveAbsolutelyDirection();//35 шаг отдать   в абсолютном напралении
	    geneController[52]	= geneController[35];
	    
	    geneController[36]	= new GeneFlattenedHorizontally();//36 выравнится по горизонтали
	    geneController[37]	= new GeneMyLevel();//37 высота бота
	    geneController[38]	= new GeneMyHealth();//38 здоровье бота
	    geneController[39]	= new GeneMyMineral();//39 минералы бота
	    geneController[40]	= new GeneCreateCell();//40 создать клетку многоклеточного
	    geneController[41]	= new GeneCreateBot();//40 создать клетку одноклеточного
	    //42 занято
	    geneController[43]	= new GeneFullAroud();//43  окружен ли бот
	    geneController[44]	= new GeneIsHealthGrow();//44  окружен ли бот
	    geneController[45]	= new GeneIsMineralGrow();//45  прибавляются ли минералы
	    geneController[46]	= new GeneIsMultiCell();//46  многоклеточный
	    geneController[47]	= new GeneMineralToEnergy();//47  преобразовать минералы в энерию
	    geneController[48]	= new GeneMutate();//48  мутировать
        geneController[49]  = new GeneCommand();//49 командовать соседями
        geneController[60]  = new GeneDie();//60 превратиться в органику
	    
	    
    }
    
    public int[] mind = new int[MIND_SIZE];                // геном бота содержит 64 команды
    
    //===================          BOT.LIVING                 ======================
    //======= состяние бота, которое отмеченно для каждого бота в массиве bots[] ====================
    /**
     * бот погиб и представляет из себя органику в подвешенном состоянии
     */
    public static int LV_ORGANIC_HOLD = 1;  
    /**
     * ораника начинает тонуть, пока не встретит препятствие, после чего остается в подвешенном состоянии(LV_ORGANIC_HOLD)
     */
    public static int LV_ORGANIC_SINK = 2;
    /**
     * живой бот
     */
    public static int LV_ALIVE = 3;  //
    
    /**
     * Поля нужны для сериализации ботов
     * координаты соседних клеток многоклеточного
     */
	public int mprevX;
	public int mprevY;
	public int mnextX;
	public int mnextY;
    private SecureRandom secureRandom;

	World world;
    public Bot(World world) {
    	this.world	= world;
        direction = 2;
        health = 5;
        alive = LV_ALIVE;
        //Class[] parameterTypes = new Class[] { Bot.class}; 
        //BotCommandController.class.getMethod(name, parameterTypes);
        secureRandom = new SecureRandom();
    }



    // ====================================================================
    // =========== главная функция жизнедеятельности бота  ================
    // =========== в ней выполняется код его мозга-генома  ================
    // ====================================================================
    public void step()
    {
    	/*
    	if(alive == LV_ORGANIC_SINK || alive == LV_ORGANIC_HOLD)
    	{
    		botMove(this, 5, 1);
    	}*/
    	if (alive == LV_ORGANIC_HOLD || alive == LV_ORGANIC_SINK)
		  {
			botMove(5, 1);
              health -= Math.abs(world.sunEnergy) + 1;
			if (health <= -999) {
			    deleteBot();
            }
		    	return;   //Это труп - выходим!
		  }

        IBotGeneController cont;
    
        Integer prevAdr = null;
        int c = 0;
        for (; c < IBot.MIND_SIZE; c++)
        {//15
            if (prevAdr != null && prevAdr == adr) {
                if (c > 64) {
                    System.out.println("Зависон detected, cmd " + mind[adr]);
//                    System.exit(1);
                    this.botIncCommandAddress(1);
                }
            }
            int command = mind[adr];  // текущая команда
            prevAdr=adr;
            
            // Получаем обработчика команды
            cont	= geneController[command];
            if(cont!=null)// если обработчик такой команды назначен
            {
            	if(cont.onGene(this)) // передаем ему управление
            		break; // если обрабочик говорит, что он последний - завершаем цикл?
            }else
            {//если ни с одной команд не совпало значит безусловный переход прибавляем к указателю текущей команды значение команды
            	 this.botIncCommandAddress(command);
            	 break;
            }
        }



//###########################################################################
//.......  выход из функции и передача управления следующему боту   ........
//.......  но перед выходом нужно проверить, входит ли бот в        ........
//.......  многоклеточную цепочку и если да, то нужно распределить  ........
//.......  энергию и минералы с соседями                            ........
//.......  также проверить, количество накопленой энергии, возможно ........
//.......  пришло время подохнуть или породить потомка              ........

        if (alive == LV_ALIVE)
        {
            int a = isMulti();
            // распределяем энергию  минералы по многоклеточному организму
            // возможны три варианта, бот находится внутри цепочки
            // бот имеет предыдущего бота в цепочке и не имеет следующего
            // бот имеет следующего бота в цепочке и не имеет предыдущего
            if (a == 3) {                 // бот находится внутри цепочки
                Bot pb = mprev; // ссылка на предыдущего бота в цепочке
                Bot nb = mnext; // ссылка на следующего бота в цепочке
                // делим минералы .................................................................
                int m = mineral + nb.mineral + pb.mineral; // общая сумма минералов
                //распределяем минералы между всеми тремя ботами
                m = m / 3;
                mineral = m;
                nb.mineral = m;
                pb.mineral = m;
                    // делим энергию ................................................................
                    // проверим, являются ли следующий и предыдущий боты в цепочке крайними .........
                    // если они не являются крайними, то распределяем энергию поровну       .........
                    // связанно это с тем, что в крайних ботах в цепочке должно быть больше энергии ..
                    // что бы они плодили новых ботов и удлиняли цепочку
                int apb = pb.isMulti();
                int anb = nb.isMulti();
                if ((anb == 3) && (apb == 3)) { // если следующий и предыдущий боты не являются крайними
                                                 // то распределяем энергию поровну
                    int h =  health + nb.health + pb.health;
                    h = h / 3;
                    health = h;
                    nb.health = h;
                    pb.health = h;
                }
            }
            // бот является крайним в цепочке и имеет предыдкщего бота
            if (a == 1) {
                Bot pb = mprev; // ссылка на предыдущего бота
                int apb = pb.isMulti();  // проверим, является ли предыдущий бот крайним в цепочке
                if (apb == 3) {   // если нет, то распределяем энергию в пользу текущего бота
                                   // так как он крайний и ему нужна энергия для роста цепочки
                    int h =  health + pb.health;
                    h = h / 4;
                    health = h * 3;
                    pb.health = h;
                }
            }
            // бот является крайним в цепочке и имеет следующего бота
            if (a == 2) {
                Bot nb = mnext; // ссылка на следующего бота
                int anb = nb.isMulti();   // проверим, является ли следующий бот крайним в цепочке
                if (anb == 3) {      // если нет, то распределяем энергию в пользу текущего бота
                                      // так как он крайний и ему нужна энергия для роста цепочки
                    int h =  health + nb.health;
                    h = h / 4;
                    health = h * 3;
                    nb.health = h;
                }
            }
            //******************************************************************************
            //??????????????????????????????????????????????
            //... проверим уровень энергии у бота, возможно пришла пора помереть или родить
            // Вопрос стоит ли так делать, родждение прописано в генных командах
            /*if (health > 499) {    // если энергии больше 499, то плодим нового бота
                if ((a == 1) || (a == 2)) {
                    botMulti(this); // если бот был крайним в цепочке, то его потомок входит в состав цепочки
                } else {
                    botDouble(this); // если бот был свободным или находился внутри цепочки
                    				// то его потомок рождается свободным
                }
            }*/
            health =  health - 3;   // каждый ход отнимает 3 единички здоровья(энегрии)
            if (health < 1) {       // если энергии стало меньше 1
                this.bot2Organic();  // то время умирать, превращаясь в огранику
                return;            // и передаем управление к следующему боту
            }
            // если бот находится на глубине ниже 48 уровня
            // то он автоматом накапливает минералы, но не более 499/4
            if (y > world.height / 2) {
                mineral = mineral + 1;
                if (y > world.height / 6 * 4) { mineral = mineral + 1; }
                if (y > world.height / 6 * 5) { mineral = mineral + 1; }
                if (mineral > 499/4) { mineral = 499/4; }
            }
        }
    }
	@Override
	public IWorld getWorld()
	{
		return this.world;
	}



    //жжжжжжжжжжжжжжжжжжжхжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжж
    // -- получение Х-координаты рядом        ---------
    //  с био по относительному направлению  ----------
    // in - номер бота, направление       --------------
    // out - X -  координата             --------------
    /**
     * получение Х-координаты рядом с био по относительному направлению
     * @param n направление
     * @return X -  координата 
     */
    int xFromVektorR(int n) {
        int xt = x;
        n = n + direction;
        if (n >= 8) {
            n = n - 8;
        }
        if (n == 0 || n == 6 || n == 7) {
            xt = xt - 1;
            if (xt == -1) {
                xt = world.width - 1;
            }
        } else if (n == 2 || n == 3 || n == 4) {
            xt = xt + 1;
            if (xt == world.width) {
                xt = 0;
            }
        }
        return xt;
    }

    //жжжжжжжжжжжжжжжжжжжхжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжж
    // -- получение Х-координаты рядом        ---------
    //  с био по абсолютному направлению     ----------
    // in - номер био, направление       --------------
    // out - X -  координата             --------------
    /**
     * получение Х-координаты рядом с био по абсолютному направлению
     * @param n
     * @return X -  координата
     */
    int xFromVektorA(int n) {
        int xt = x;
        if (n == 0 || n == 6 || n == 7) {
            xt = xt - 1;
            if (xt == -1) {
                xt = world.width - 1;
            }
        } else if (n == 2 || n == 3 || n == 4) {
            xt = xt + 1;
            if (xt == world.width) {
                xt = 0;
            }
        }
        return xt;
    }

    //жжжжжжжжжжжжхжжжжжхжжжжжжхжхжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжж
    // ------ получение Y-координаты рядом              ---------
    // ---- Y координата по относительному направлению  ----------
    // ---  in - номер бота, направление              ------------
    // ---  out - Y -  координата                    -------------
    /**
     * получение Y-координаты рядом
     * @param n направление
     * @return Y координата по относительному направлению
     */
    int yFromVektorR(int n) {
        int yt = y;
        n = n + direction;
        if (n >= 8) {
            n = n - 8;
        }
        if (n == 0 || n == 1 || n == 2) {
            yt = yt - 1;
        } else if (n == 4 || n == 5 || n == 6) {
            yt = yt + 1;
        }
        return yt;
    }

    //жжжжжжжжжжжжхжжжжжхжжжжжжхжхжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжж
    // ------ получение Y-координаты рядом              ---------
    // ---- Y координата по абсолютному направлению     ----------
    // ---  in - номер бота, направление              ------------
    // ---  out - Y -  координата                    -------------
    /**
     * получение Y-координаты рядом 
     * @param n направление
     * @return Y координата по абсолютному направлению
     */
    int yFromVektorA(int n) {
        int yt = y;
        if (n == 0 || n == 1 || n == 2) {
            yt = yt - 1;
        } else if (n == 4 || n == 5 || n == 6) {
            yt = yt + 1;
        }
        return yt;
    }

    //жжжжжжжжжжжжжжжжжжжхжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжж
    //===========   окружен ли бот          ==========
    // ---  in - бот                 ------------
    //===== out  1-окружен  2-нет           ===
    /**
     * окружен ли бот 
     * @return 1-окружен  2-нет
     */
    @Override
    public int fullAroud() {
        for (int i = 0; i < 8; i++) {
            int xt = xFromVektorR(i);
            int yt = yFromVektorR(i);
            if ((yt >= 0) && (yt < world.height)) {
                if (!world.hasBot(xt, yt)) {
                    return 2;
                }
            }
        }
        return 1;
    }


    //жжжжжжжжжжжжжжжжжжжхжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжж
    //==== ищет свободные ячейки вокруг бота ============
    //==== начинает спереди и дальше по      ============
    //==== кругу через низ    ( world )      ============
    //==== in  - бот                  ============
    //==== out - номер направление или       ============
    //====  или 8 , если свободных нет       ============
    /**
     * ищет свободные ячейки вокруг бота кругу через низ    ( world )
     * @return номер направление или 8 , если свободных нет
     */
    int findEmptyDirection() {
        for (int i = 0; i < 8; i++) {
            int xt = xFromVektorR(i);
            int yt = yFromVektorR(i);
            if ((yt >= 0) && (yt < world.height)) {
                if (!world.hasBot(xt, yt)) {
                    return i;
                }
            }
        }
        //........no empty..........
        return 8;
    }

    //жжжжжжжжжжжжжжжжжжжхжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжж
    // -- получение параметра для команды   --------------
    //  in - bot
    // out - возвращает число из днк, следующее за выполняемой командой
    /**
     * получение параметра для команды
     * 
     * @return возвращает число из днк, следующее за выполняемой командой
     */
    int botGetParam() {
        int paramadr = adr + 1;
        if (paramadr >= MIND_SIZE) {
            paramadr = paramadr - MIND_SIZE;
        }
        return mind[paramadr]; // возвращает число, следующее за выполняемой командой
    }

    //жжжжжжжжжжжжжжжжжжжхжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжж
    // -- увеличение адреса команды   --------------
    //  in - bot, насколько прибавить адрес --
    /**
     * увеличение адреса команды
     * @param a насколько прибавить адрес
     */
    void botIncCommandAddress(int a) {
        int paramadr = adr + a;
        if (paramadr >= MIND_SIZE) {
            paramadr = paramadr - MIND_SIZE;
        }
        adr = paramadr;
    }

    //жжжжжжжжжжжжжжжжжжжхжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжж
    //---- косвенное увеличение адреса команды   --------------
    //---- in - номер bot, смещение до команды,  --------------
    //---- которая станет смещением              --------------
    /**
     * косвенное увеличение адреса команды
     *
     * @param a смещение до команды, которая станет смещением
     */
    void botIndirectIncCmdAddress(int a) {
        int paramadr = adr + a;
        if (paramadr >= MIND_SIZE) {
            paramadr = paramadr - MIND_SIZE;
        }
        int bias = mind[paramadr];
        botIncCommandAddress(bias);
    }


    //жжжжжжжжжжжжжжжжжжжхжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжж
    //=====  превращение бота в органику    ===========
    //=====  in - номер бота                ===========
    /**
     * превращение бота в органику
     */
    void bot2Organic() {
        alive = LV_ORGANIC_SINK;       // отметим в массиве bots[], что бот органика
        Bot pbot = mprev;
        Bot nbot = mnext;
        if (pbot != null){ pbot.mnext = null; } // удаление бота из многоклеточной цепочки
        if (nbot != null){ nbot.mprev = null; }
        mprev = null;
        mnext = null;
    }

    //жжжжжжжжжжжжжжжжжжжхжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжж
    //========   нахожусь ли я в многоклеточной цепочке  =====
    //========   in - номер бота                         =====
    //========   out- 0 - нет, 1 - есть MPREV, 2 - есть MNEXT, 3 есть MPREV и MNEXT
    /**
     * нахожусь ли я в многоклеточной цепочке
     * 
     * @return 0 - нет, 1 - есть MPREV, 2 - есть MNEXT, 3 есть MPREV и MNEXT
     */
    @Override
    public int isMulti() {
        int a = 0;
        if (mprev != null) {
            a = 1;
        }
        if (mnext != null) {
            a = a + 2;
        }
        return a;
    }

    //жжжжжжжжжжжжжжжжжжжхжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжж
    //===== перемещает бота в нужную точку  ==============
    //===== без проверок                    ==============
    //===== in - номер бота и новые координаты ===========
    /**
     * перемещает бота в нужную точку без проверок 
     *  @param xt новые координаты x
     * @param yt новые координаты y
     */
    void moveBot(int xt, int yt) {
        world.unsetBot(this);
        x = xt;
        y = yt;
        world.setBot(this);
    }

    //жжжжжжжжжжжжжжжжжжжхжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжж
    //=====   удаление бота        =============
    //=====  in - бот       =============
    /**
     * удаление бота
     */
    private void deleteBot() {
        Bot pbot = mprev;
        Bot nbot = mnext;
        if (pbot != null){ pbot.mnext = null; } // удаление бота из многоклеточной цепочки
        if (nbot != null){ nbot.mprev = null; }
        mprev = null;
        mnext = null;
        world.clearBot(this); // удаление бота с карты
    }


    //=========================================================================================
    //============================       КОД КОМАНД   =========================================
    //=========================================================================================
    // ...  фотосинтез, этой командой забит геном первого бота     ...............
    // ...  бот получает энергию солнца в зависимости от глубины   ...............
    // ...  и количества минералов, накопленных ботом              ...............
    /**
     * фотосинтез, этой командой забит геном первого бота 
     * бот получает энергию солнца в зависимости от глубины
     * и количества минералов, накопленных ботом
     *
     */
    public void botEatSun() {
        int t;
        if (mineral < 100) {
            t = 0;
        } else if (mineral < 400) {
            t = 1;
        } else {
            t = 2;
        }
        int hlt = (int)(world.sunEnergy * ((double)world.height / (y + 1))) + t;
        if (health + hlt > 499) {
            hlt -= (health + hlt) - 499;
        }
        // формула вычисления энергии ============================= SEZON!!!!!!!!!!
//        System.out.println(world.generation + ": " + bot.health + " + " + hlt);
        if (hlt > 0) {
            health = health + hlt;   // прибавляем полученную энергия к энергии бота
            goGreen(hlt);                                     // бот от этого зеленеет
        }
    }


    // ...  преобразование минералов в энергию  ...............
    /**
     *  преобразование минералов в энергию
     */
    public void botMineral2Energy() {
        //TODO стабилизировать минералы
        if (mineral > 100) {   // максимальное количество минералов, которые можно преобразовать в энергию = 100
            mineral = mineral - 100;
            health = health + 400; // 1 минерал = 4 энергии
            goBlue(100);  // бот от этого синеет
        } else {  // если минералов меньше 100, то все минералы переходят в энергию
            goBlue(mineral);
            health = health + 4 * mineral;
            mineral = 0;
        }
    }

    //===========================  перемещение бота   ========================================
    /**
     * перемещение бота
     * @param direction направлелие
     * @param ra флажок(относительное или абсолютное направление)
     * @return
     */
    public int botMove(int direction, int ra) { // ссылка на бота, направлелие и флажок(относительное или абсолютное направление)
        // на выходе   2-пусто  3-стена  4-органика 5-бот 6-родня
        int xt;
        int yt;
        if (ra == 0) {          // вычисляем координату клетки, куда перемещается бот (относительное направление)
            xt = xFromVektorR(direction);
            yt = yFromVektorR(direction);
        } else {                // вычисляем координату клетки, куда перемещается бот (абсолютное направление)
            xt = xFromVektorA(direction);
            yt = yFromVektorA(direction);
        }
        if ((yt < 0) || (yt >= world.height)) {  // если там ... стена
            return 3;                       // то возвращаем 3
        }
        if (!world.hasBot(xt, yt)) {  // если клетка была пустая,
            moveBot(xt, yt);    // то перемещаем бота
            return 2;                       // и функция возвращает 2
        }
        // осталось 2 варианта: ограника или бот
        Bot bot1 = world.getBot(xt, yt);
        if (bot1.alive <= LV_ORGANIC_SINK) { // если на клетке находится органика
            return 4;                       // то возвращаем 4
        }
        if (isRelative(this, bot1) == 1) {  // если на клетке родня
            return 6;                      // то возвращаем 6
        }
        return 5;                         // остался только один вариант - на клетке какой-то бот возвращаем 5
    }

    //============================    скушать другого бота или органику  ==========================================
    /**
     * скушать другого бота или органику
     * @param direction направлелие
     * @param ra флажок(относительное или абсолютное направление)
     * @return пусто - 2  стена - 3  органик - 4  бот - 5
     */
    int botEat(int direction, int ra) { // на входе ссылка на бота, направлелие и флажок(относительное или абсолютное направление)
        // на выходе пусто - 2  стена - 3  органик - 4  бот - 5
        health = health - 4; // бот теряет на этом 4 энергии в независимости от результата
        int xt;
        int yt;
        if (ra == 0) {  // вычисляем координату клетки, с которой хочет скушать бот (относительное направление)
            xt = xFromVektorR(direction);
            yt = yFromVektorR(direction);
        } else {        // вычисляем координату клетки, с которой хочет скушать бот (абсолютное направление)
            xt = xFromVektorA(direction);
            yt = yFromVektorA(direction);
        }
        if ((yt < 0) || (yt >= world.height)) {  // если там стена возвращаем 3
            return 3;
        }
        Bot bot1 = world.getBot(xt, yt);
        if (bot1 == null) {  // если клетка пустая возвращаем 2
            return 2;
        }
        // осталось 2 варианта: ограника или бот
        else if (bot1.alive <= LV_ORGANIC_SINK) {   // если там оказалась органика
            bot1.deleteBot();                           // то удаляем её из списков
            health = health + bot1.health; //здоровье увеличилось на 100
            goRed(bot1.health);                                     // бот покраснел
            return 4;                                               // возвращаем 4
        }
        //--------- дошли до сюда, значит впереди живой бот -------------------
        int min0 = mineral;  // определим количество минералов у бота
        int min1 = bot1.mineral;  // определим количество минералов у потенциального обеда
        int hl = bot1.health;  // определим энергию у потенциального обеда
        // если у бота минералов больше
        if (min0 >= min1) {
            mineral = min0 - min1; // количество минералов у бота уменьшается на количество минералов у жертвы
            // типа, стесал свои зубы о панцирь жертвы
            bot1.deleteBot();          // удаляем жертву из списков
            int cl = 100 + (hl / 2);           // количество энергии у бота прибавляется на 100+(половина от энергии жертвы)
            health = health + cl;
            goRed(cl);                    // бот краснеет
            return 5;                              // возвращаем 5
        }
        //если у жертвы минералов больше ----------------------
        mineral = 0; // то бот израсходовал все свои минералы на преодоление защиты
        min1 = min1 - min0;       // у жертвы количество минералов тоже уменьшилось
        bot1.mineral = min1 - min0;       // перезаписали минералы жертве =========================ЗАПЛАТКА!!!!!!!!!!!!
        //------ если здоровья в 2 раза больше, чем минералов у жертвы  ------
        //------ то здоровьем проламываем минералы ---------------------------
        if (health >= 2 * min1) {
            bot1.deleteBot();         // удаляем жертву из списков
            int cl = 100 + (hl / 2) - 2 * min1; // вычисляем, сколько энергии смог получить бот
            health = health + cl;
            if (cl < 0) { cl = 0; } //========================================================================================ЗАПЛАТКА!!!!!!!!!!! - энергия не должна быть отрицательной

            goRed(cl);                   // бот краснеет
            return 5;                             // возвращаем 5
        }
        //--- если здоровья меньше, чем (минералов у жертвы)*2, то бот погибает от жертвы
        bot1.mineral = min1 - (health / 2);  // у жертвы минералы истраченны
        health = 0;  // здоровье уходит в ноль
        return 5;                       // возвращаем 5
    }

    //.======================  посмотреть ==================================================
    /**
     * посмотреть 
     * @param direction направлелие
     * @param ra флажок(относительное или абсолютное направление)
     * @return пусто - 2  стена - 3  органик - 4  бот - 5  родня - 6
     */
    int botSeeBots(int direction, int ra) { // на входе ссылка на бота, направлелие и флажок(относительное или абсолютное направление)
        // на выходе  пусто - 2  стена - 3  органик - 4  бот - 5  родня - 6
        int xt;
        int yt;
        if (ra == 0) {  // выясняем, есть ли что в этом  направлении (относительном)
            xt = xFromVektorR(direction);
            yt = yFromVektorR(direction);
        } else {       // выясняем, есть ли что в этом  направлении (абсолютном)
            xt = xFromVektorA(direction);
            yt = yFromVektorA(direction);
        }
        if (yt < 0 || yt >= world.height) {  // если там стена возвращаем 3
            return 3;
        } else if (!world.hasBot(xt, yt)) {  // если клетка пустая возвращаем 2
            return 2;
        } else {
            Bot bot1 = world.getBot(xt, yt);
            if (bot1.alive <= LV_ORGANIC_SINK) { // если органика возвращаем 4
                return 4;
            } else if (isRelative(this, bot1) == 1) {  // если родня, то возвращаем 6
                return 6;
            } else { // если какой-то бот, то возвращаем 5
                return 5;
            }
        }
    }


    //======== атака на геном соседа, меняем случайны ген случайным образом  ===============
    /**
     * атака на геном соседа, меняем случайны ген случайным образом
     */
    void botGenAttack() {   // вычисляем кто у нас перед ботом (используется только относительное направление вперед)
        int xt = xFromVektorR(0);
        int yt = yFromVektorR(0);
        if ((yt >= 0) && (yt < world.height) && (world.hasBot(xt, yt))) {
            Bot bot1 = world.getBot(xt, yt);
            if (bot1.alive == LV_ALIVE) { // если там живой бот
                health = health - 10; // то атакуюий бот теряет на атаку 10 энергии
                if (health > 0) {                    // если он при этом не умер
                    mutate();
                }
            }
        }
    }


    //==========               поделится          ====================================================
    // =========  если у бота больше энергии или минералов, чем у соседа в заданном направлении  =====
    //==========  то бот делится излишками                                                       =====
    /**
     * поделится
     * если у бота больше энергии или минералов, чем у соседа в заданном направлении
     * то бот делится излишками
     * 
     * @param direction направлелие
     * @param ra флажок(относительное или абсолютное направление)
     * @return
     */
    private int botCare(int direction, int ra) { // на входе ссылка на бота, направлелие и флажок(относительное или абсолютное направление)
        // на выходе стена - 2 пусто - 3 органика - 4 удачно - 5
        int xt;
        int yt;
        if (ra == 0) {  // определяем координаты для относительного направления
            xt = xFromVektorR(direction);
            yt = yFromVektorR(direction);
        } else {        // определяем координаты для абсолютного направления
            xt = xFromVektorA(direction);
            yt = yFromVektorA(direction);
        }
        
        Bot bot1;
        if (yt < 0 || yt >= world.height) {  // если там стена возвращаем 3
            return 3;
        } else {
            bot1= world.getBot(xt, yt);
            if (bot1 == null) {  // если клетка пустая возвращаем 2
                return 2;
            } else if (bot1.alive <= LV_ORGANIC_SINK) { // если органика возвращаем 4
                return 4;
            }
        }
        //------- если мы здесь, то в данном направлении живой ----------
        int hlt0 = health;         // определим количество энергии и минералов
        int hlt1 = bot1.health;  // у бота и его соседа
        int min0 = mineral;
        int min1 = bot1.mineral;
        if (hlt0 > hlt1) {              // если у бота больше энергии, чем у соседа
            int hlt = (hlt0 - hlt1) / 2;   // то распределяем энергию поровну
            health = health - hlt;
            bot1.health = bot1.health + hlt;
        }
        if (min0 > min1) {              // если у бота больше минералов, чем у соседа
            int min = (min0 - min1) / 2;   // то распределяем их поровну
            mineral = mineral - min;
            bot1.mineral = bot1.mineral + min;
        }
        return 5;
    }


    //=================  отдать безвозместно, то есть даром    ==========
    /**
     * отдать безвозместно, то есть даром
     * @param direction направлелие
     * @param ra флажок(относительное или абсолютное направление)
     * @return стена - 2 пусто - 3 органика - 4 удачно - 5
     */
    int botGive(int direction, int ra) // на входе ссылка на бота, направлелие и флажок(относительное или абсолютное направление)
    {                         // на выходе стена - 2 пусто - 3 органика - 4 удачно - 5
        int xt;
        int yt;
        if (ra == 0) {  // определяем координаты для относительного направления
            xt = xFromVektorR(direction);
            yt = yFromVektorR(direction);
        } else {        // определяем координаты для абсолютного направления
            xt = xFromVektorA(direction);
            yt = yFromVektorA(direction);
        }
        Bot bot1 ;
        if (yt < 0 || yt >= world.height) {  // если там стена возвращаем 3
            return 3;
        } else {
            bot1 =  world.getBot(xt, yt);
            if (bot1 == null) {  // если клетка пустая возвращаем 2
                return 2;
            } else if (bot1.alive <= LV_ORGANIC_SINK) { // если органика возвращаем 4
                return 4;
            }
        }
        //------- если мы здесь, то в данном направлении живой ----------
        int hlt0 = health;  // бот отдает четверть своей энергии
        int hlt = hlt0 / 4;
        health = hlt0 - hlt;
        bot1.health = bot1.health + hlt;

        int min0 = mineral;  // бот отдает четверть своих минералов
        if (min0 > 3) {                 // только если их у него не меньше 4
            int min = min0 / 4;
            mineral = min0 - min;
            bot1.mineral = bot1.mineral + min;
            if (bot1.mineral > 499/4) {
                bot1.mineral = 499/4;
            }
        }
        return 5;
    }


    //....................................................................
    // рождение нового бота делением
    /**
     * рождение нового бота делением
     */
    private void botDouble() {
        health = health - 150;      // бот затрачивает 150 единиц энергии на создание копии
        if (health <= 0) return; // если у него было меньше 150, то пора помирать

        int n = findEmptyDirection();    // проверим, окружен ли бот
        if (n == 8) {                      // если бот окружен, то он в муках погибает
            health = 0;
            return;
        }

        Bot newbot = new Bot(this.world);

        int xt = xFromVektorR(n);   // координаты X и Y
        int yt = yFromVektorR(n);

        System.arraycopy(mind, 0, newbot.mind, 0, MIND_SIZE);
		if (Math.random() < 0.25) {     // в одном случае из четырех случайным образом меняем один случайный байт в геноме
			newbot.mutate();
		}
    
        newbot.adr = 0;                         // указатель текущей команды в новорожденном устанавливается в 0
        newbot.x = xt;
        newbot.y = yt;

        newbot.health = health / 2;   // забирается половина здоровья у предка
        health = health / 2;
        newbot.mineral = mineral / 2; // забирается половина минералов у предка
        mineral = mineral / 2;

        newbot.alive = LV_ALIVE;             // отмечаем, что бот живой

        newbot.c_red = c_red;   // цвет такой же, как у предка
        newbot.c_green = c_green;   // цвет такой же, как у предка
        newbot.c_blue = c_blue;   // цвет такой же, как у предка

        newbot.direction = (int) (Math.random() * 8);   // направление, куда повернут новорожденный, генерируется случайно

        world.addBot(newbot);
    }
    
    @Override
	public void mutate() {
        int ma = secureRandom.nextInt(IBot.MIND_SIZE-1)+1;
        int mc = secureRandom.nextInt(IBot.MIND_SIZE-1)+1;
//            byte ma = (byte) (Math.random() * MIND_SIZE);  // 0..63
//            byte mc = (byte) (Math.random() * MIND_SIZE);  // 0..63
		mind[ma] = mc;
    }
    
    @Override
    public void die() {
        this.health=0;
    }
    
    Bot clone(int x, int y) throws CloneNotSupportedException {
        Bot cloned = (Bot) super.clone();

        cloned.x = x;
        cloned.y = y;
        cloned.mind[(int) (Math.random() * IBot.MIND_SIZE)] = (int) (Math.random() * IBot.MIND_SIZE);

        return cloned;
    }

    // ======       рождение новой клетки многоклеточного    ==========================================
    /**
     * рождение новой клетки многоклеточного
     */
    private void botMulti() {
        Bot pbot = mprev;    // ссылки на предыдущего и следущего в многоклеточной цепочке
        Bot nbot = mnext;
        // если обе ссылки больше 0, то бот уже внутри цепочки
        if ((pbot != null) && (nbot != null)) return; // поэтому выходим без создания нового бота

        health = health - 150; // бот затрачивает 150 единиц энергии на создание копии
        if (health <= 0) return; // если у него было меньше 150, то пора помирать
        int n = findEmptyDirection(); // проверим, окружен ли бот

        if (n == 8) {  // если бот окружен, то он в муках погибает
            health = 0;
            return;
        }
        Bot newbot = new Bot(this.world);

        int xt = xFromVektorR(n);   // координаты X и Y
        int yt = yFromVektorR(n);

        System.arraycopy(mind, 0, newbot.mind, 0, MIND_SIZE);    // копируем геном в нового бота
		if (Math.random() < 0.25) {     // в одном случае из четырех случайным образом меняем один случайный байт в геноме
			newbot.mutate();
		}
        newbot.adr = 0;                         // указатель текущей команды в новорожденном устанавливается в 0
        newbot.x = xt;
        newbot.y = yt;

        newbot.health = health / 2;   // забирается половина здоровья у предка
        health = health / 2;
        newbot.mineral = mineral / 2; // забирается половина минералов у предка
        mineral = mineral / 2;

        newbot.alive = LV_ALIVE;             // отмечаем, что бот живой

        newbot.c_red = c_red;   // цвет такой же, как у предка
        newbot.c_green = c_green;   // цвет такой же, как у предка
        newbot.c_blue = c_blue;   // цвет такой же, как у предка

        newbot.direction = (int) (Math.random() * 8);   // направление, куда повернут новорожденный, генерируется случайно

        world.addBot(newbot);

        if (nbot == null) {                      // если у бота-предка ссылка на следующего бота в многоклеточной цепочке пуста
            mnext = newbot; // то вставляем туда новорожденного бота
            newbot.mprev = this;    // у новорожденного ссылка на предыдущего указывает на бота-предка
            newbot.mnext = null;       // ссылка на следующего пуста, новорожденный бот является крайним в цепочке
        } else {                              // если у бота-предка ссылка на предыдущего бота в многоклеточной цепочке пуста
            mprev = newbot; // то вставляем туда новорожденного бота
            newbot.mnext = this;    // у новорожденного ссылка на следующего указывает на бота-предка
            newbot.mprev = null;       // ссылка на предыдущего пуста, новорожденный бот является крайним в цепочке
        }
    }


    //жжжжжжжжжжжжжжжжжжжхжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжж
    //========   копится ли энергия            =====
    //========   in - номер бота                =====
    //========   out- 1 - да, 2 - нет           =====
    /**
     * копится ли энергия 
     * @return 1 - да, 2 - нет
     */
    @Override
    public int isHealthGrow() {
        int t;
        if (mineral < 100) {
            t = 0;
        } else {
            if (mineral < 400) {
                t = 1;
            } else {
                t = 2;
            }
        }
        int hlt = 10 - (15 * y / world.height) + t; // ====================================================== SEZON!!!!!!!!!!!!!!!!!!
        if (hlt >= 3) {
            return 1;
        } else {
            return 2;
        }
    }

    //жжжжжжжжжжжжжжжжжжжхжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжж
    //========   родственники ли боты?              =====
    //========   in - номер 1 бота , номер 2 бота   =====
    //========   out- 0 - нет, 1 - да               =====
    /**
     * родственники ли боты?
     * @param bot0
     * @param bot1
     * @return 0 - нет, 1 - да
     */
    int isRelative(Bot bot0, Bot bot1) {
        if (bot1.alive != LV_ALIVE) {
            return 0;
        }
        int dif = 0;    // счетчик несовпадений в геноме
        for (int i = 0; i < MIND_SIZE; i++) {
            if (bot0.mind[i] != bot1.mind[i]) {
                dif = dif + 1;
                if (dif == 5) {
                    return 0;
                } // если несовпадений в генеме больше 1
            }                               // то боты не родственики
        }
        return 1;
    }

    //жжжжжжжжжжжжжжжжжжжхжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжж
    //=== делаем бота более зеленым на экране         ======
    //=== in - номер бота, на сколько озеленить       ======
    /**
     *  делаем бота более зеленым на экране
     * @param num номер бота, на сколько озеленить
     */
    void goGreen(int num) {  // добавляем зелени
        c_green = c_green + num;
        if (c_green + num > 255) {
            c_green = 255;
        }
        int nm = num / 2;
        // убавляем красноту
        c_red = c_red - nm;
        if (c_red < 0) {
            c_blue = c_blue +  c_red;
        }
        // убавляем синеву
        c_blue = c_blue - nm;
        if (c_blue < 0 ) {
            c_red = c_red + c_blue;
        }
        if (c_red < 0) {
            c_red = 0;
        }
        if (c_blue < 0) {
            c_blue = 0;
        }
    }

    //жжжжжжжжжжжжжжжжжжжхжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжжж
    //=== делаем бота более синим на экране         ======
    //=== in - номер бота, на сколько осинить       ======
    /**
     *  делаем бота более синим на экране
     * @param num номер бота, на сколько осинить
     */
    void goBlue(int num) {  // добавляем синевы
        c_blue = c_blue + num;
        if (c_blue > 255) {
            c_blue = 255;
        }
        int nm = num / 2;
        // убавляем зелень
        c_green = c_green - nm;
        if (c_green < 0 ) {
            c_red = c_red + c_green;
        }
        // убавляем красноту
        c_red = c_red - nm;
        if (c_red < 0) {
            c_green = c_green +  c_red;
        }
        if (c_red < 0) {
            c_red = 0;
        }
        if (c_green < 0) {
            c_green = 0;
        }
    }

    /**
     *  делаем бота более красным на экране
     * @param num номер бота, на сколько окраснить
     */
    void goRed(int num) {  // добавляем красноты
        c_red = c_red + num;
        if (c_red > 255) {
            c_red = 255;
        }
        int nm = num / 2;
        // убавляем зелень
        c_green = c_green - nm;
        if (c_green < 0 ) {
            c_blue = c_blue + c_green;
        }
        // убавляем синеву
        c_blue = c_blue - nm;
        if (c_blue < 0) {
            c_green = c_green +  c_blue;
        }
        if (c_blue < 0) {
            c_blue = 0;
        }
        if (c_green < 0) {
            c_green = 0;
        }
    }



	@Override
	public int getParam()
	{
		return this.botGetParam();
	}
	@Override
	public int getDirection()
	{
		return this.direction;
	}
	@Override
	public void setDirection(int newdrct)
	{
        if (newdrct >= 8)
            newdrct = newdrct - 8; // результат должен быть в пределах от 0 до 8
		this.direction	= newdrct;
	}
	@Override
	public void incCommandAddress(int i)
	{
		this.botIncCommandAddress(i);
	}
	@Override
	public void eatSun()
	{
		botEatSun();
	}
	@Override
	public void indirectIncCmdAddress(int a)
	{
		botIndirectIncCmdAddress(a);
	}
	@Override
	public int move(int drct, int i)
	{
		return this.botMove(drct, i);
	}
	@Override
	public int eat(int drct, int i)
	{
		return botEat(drct, i);
	}
	@Override
	public int seeBots(int drct, int i)
	{
		return botSeeBots(drct, i);
	}
    
    @Override
    public void command(int drct, int command) {
        int xt = this.xFromVektorA(drct);
        int yt = this.yFromVektorA(drct);
        world.getBot(xt, yt).adr = command;
    }
    
    @Override
	public int care(int drct, int i)
	{
		return botCare(drct, i);
	}
	@Override
	public int give(int drct, int i)
	{
		return botGive(drct, i);
	}
	public int getY()
	{
		return y;
	}
	public int getHealth()
	{
		return health;
	}
	public int getMineral()
	{
		return mineral;
	}
	@Override
	public void Double()
	{
		this.botDouble();
	}
	@Override
	public void multi()
	{
		this.botMulti();
	}
	@Override
	public void mineral2Energy()
	{
		botMineral2Energy();
	}
	@Override
	public void setMind(int ma, int mc)
	{
		this.mind[ma]=mc;
	}
	@Override
	public void genAttack()
	{
		this.botGenAttack();
		
	}
}