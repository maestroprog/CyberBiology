package ru.cyberbiology.test.prototype;

import ru.cyberbiology.test.Bot;
import ru.cyberbiology.test.util.ProjectProperties;

public interface IWorld
{

	int getWidth();

	int getHeight();

	void setSize(int width, int height);

	void setBot(Bot bot);

	void paint();

	ProjectProperties getProperties();

	Bot[][] getWorldArray();

	void restoreLinks();

}
