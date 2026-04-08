package application.core;


import java.io.Serializable;
import java.util.Random;

public class User implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	
	// player identity logic
	public String username;
	public int r, g, b;
	
	// player state logic
	public double x,y;
	public Double [] velocity;
	public int ttl; //each user has so long to make a interact before they are removed from the server
	public boolean moving;
	
	
	public User(String username){
		
		Random rand = new Random();

		x = rand.nextInt(-300,300);
		y = rand.nextInt(-300,300);
		r = rand.nextInt(0,255);
		g = rand.nextInt(0,255);
		b = rand.nextInt(0,255);
		
		velocity = new Double [] {0.0,0.0};
		
		this.ttl = 1000;
		moving = false;
		
		
		this.username = username;
	}
	public void editUsername(String username)
	{
		this.username = username;
	}


	public String getUsername()
	{
		return this.username;
	}
	
	

	public int getR() {
		return r;
	}
	public void setR(int r) {
		this.r = r;
	}
	public int getG() {
		return g;
	}
	public void setG(int g) {
		this.g = g;
	}
	public int getB() {
		return b;
	}
	public void setB(int b) {
		this.b = b;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public Double[] getVelocity() {
		return velocity;
	}
	public void setVelocity(Double[] velocity) {
		this.velocity = velocity;
	}
	public int getTtl() {
		return ttl;
	}
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
	public boolean isMoving() {
		return moving;
	}
	public void setMoving(boolean moving) {
		this.moving = moving;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Override
	public String toString() {
		// StringBuilder sb = new StringBuilder();

		return String.format("Username: %s\n%d red, %d green, %d blue\nx: %.2f\ny: %.2f\n", username, r, g, b, x, y);
	}

}