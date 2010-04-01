package zelda.engine;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.HashMap;

/**
 * A GObject is something that gets drawn on the View, checks if it collides and animates itself.
 *
 * @author maartenhus
 */
public abstract class GObject implements DrawAble
{
	protected Game game;
	protected boolean alive = true;

	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected boolean checkcollision = true;

	protected Sprite sprite;
	protected HashMap<String, Rectangle> spriteLoc = new HashMap<String, Rectangle>();
	protected String[] animation;

	protected int animationCounter = 0;
	protected long animationInterval;
	protected long lastAnimation = System.currentTimeMillis();

	public GObject(Game game, int x, int y, int width, int height, String image)
	{
		animationInterval = 90;
		this.game = game;

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		sprite = new Sprite(image);
	}

	public void animate()
	{
		if (System.currentTimeMillis() > lastAnimation + animationInterval) // if it time to reanimate
		{
			preAnimation();

			//System.out.println("Animation " + animationCounter + " == " + animation.length);
			if (animationCounter == animation.length) // if all animations are done.
			{
				//reset the counter.
				animationCounter = 0;
			}

			try
			{
				// Set the next animation image of the GObject.
				sprite.setSprite(spriteLoc.get(animation[animationCounter]));
			}
			catch(Exception e)
			{
				//System.out.println("Animation " + animationCounter + " == " + animation.length);
				animationCounter = 0;
			}
			
			animationCounter += 1;
			lastAnimation = System.currentTimeMillis();

			postAnimation();
		}
	}

    public void setAlive(boolean alive)
    {
        this.alive = alive;
    }

	public void doInLoop(){};
	public void preAnimation(){}
	public void postAnimation(){}

	public void draw(Graphics2D g)
	{
		Image img = sprite.getImage();		
		g.drawImage(img, x, y, sprite.getWidth(), sprite.getHeight(), null);
	}

	private boolean isCollision(int newX, int newY)
	{
		Rectangle rect = new Rectangle(newX, newY, width, height);

		for (Polygon poly : game.getScene().getSolids()) //for each solid object
		{
			final Area area = new Area();
			area.add(new Area(rect));
			area.intersect(new Area(poly)); //check if there is a collision

			if(!area.isEmpty()) // if isEmpty is false there is a collision
			{
                            return true;
			}
		}

		for (GObject obj : game.getScene().getGObjects())
		{
                    final Area area = new Area();
                    area.add(new Area(rect));
                    area.intersect(new Area(obj.getRectangle()));

                    if(!area.isEmpty() && this != obj) // if area is empty, and the obj is not isself. (Self-collision)
                    {
                        collision(obj); //report collision to self, with the object that hit it.
                        return true;
                    }
		}

		return false;
	}

	
	protected void collision(GObject hitObject){}

	public int getX()
	{
		return x;
	}

	public void setX(int newX)
	{
		if (!checkcollision || !isCollision(newX, y))
		{
			x = newX;
		}
	}

	public int getY()
	{
		return y;
	}

	public void setY(int newY)
	{
		if(!checkcollision || !isCollision(x, newY))
		{
			y = newY;
		}
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public int getWidth()
	{
		return width;
	}

	public Game getGame()
	{
		return game;
	}

	public boolean isAlive()
	{
		return alive;
	}

	public Rectangle getRectangle()
	{
		return new Rectangle(x, y, width, height);
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public String[] getAnimation()
	{
		return animation;
	}

	public void setAnimation(String[] animation)
	{
		this.animation = animation;
	}

	public void resetAnimationCounter()
	{
		animationCounter = 0;
	}

	public int getAnimationCounter()
	{
		return animationCounter;
	}

	public long getAnimationInterval()
	{
		return animationInterval;
	}

	public void setAnimationInterval(long animationInterval)
	{
		this.animationInterval = animationInterval;
	}

	public boolean isCheckcollision()
	{
		return checkcollision;
	}

	public void setCheckcollision(boolean checkcollision)
	{
		this.checkcollision = checkcollision;
	}
}
