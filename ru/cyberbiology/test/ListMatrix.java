package ru.cyberbiology.test;

public class ListMatrix<T> {
	private T[] bot;
	private int[][] cache;
	private int height;
	
	ListMatrix(T[] bots, int width, int height) {
		bot = bots;
		cache = new int[width][height];
		this.height = height;
		System.out.println("Размеры матрицы: " + width + " " + height);
	}
	
	T get(int x, int y) {
		if (cache[x][y] == 0) {
			cache[x][y] = y * height + x;
		}
		
		return bot[cache[x][y]];
	}
	
	void set(T bot, int x, int y) {
		if (cache[x][y] == 0) {
			cache[x][y] = y * height + x;
		}
		
		this.bot[cache[x][y]] = bot;
	}
	
	public T[] toArray() {
		return bot;
	}
}
