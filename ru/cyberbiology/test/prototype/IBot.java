package ru.cyberbiology.test.prototype;

public interface IBot {
    
    int MIND_SIZE = 256;
    
    /**
     * получение параметра для команды
     *
     * @return возвращает число из днк, следующее за выполняемой командой
     */
    int getParam();
    
    /**
     * получение направления движения
     *
     * @return возвращает число от 0 до 8 (0 вверх, 5 вниз)
     */
    int getDirection();
    
    /**
     * устанавливает направления движения
     *
     * @param newdrct число от 0 до 8 (0 вверх, 5 вниз)
     */
    void setDirection(int newdrct);
    
    /**
     * увеличение адреса команды
     *
     * @param increment насколько прибавить адрес
     */
    void incCommandAddress(int increment);
    
    /**
     * фотосинтез
     */
    void eatSun();
    
    /**
     * Многоклеточный ли бот?
     *
     * @return int
     */
    int isMulti();
    
    int move(int drct, int i);
    
    void indirectIncCmdAddress(int move);
    
    int eat(int drct, int i);
    
    int seeBots(int drct, int i);
    
    void command(int drct, int command);
    
    int care(int drct, int i);
    
    
    int give(int drct, int i);
    
    int getY();
    
    int getHealth();
    
    int getMineral();
    
    void Double();
    
    void multi();
    
    int fullAroud();
    
    int isHealthGrow();
    
    void mineral2Energy();
    
    void setMind(int ma, int mc);
    
    void genAttack();
    
    IWorld getWorld();
    
    void mutate();
    
    void die();
    
}
