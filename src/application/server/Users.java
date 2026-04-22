package application.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import application.core.User;


public class Users implements Serializable
{
	private static final long serialVersionUID = 1L;
	private HashSet<User> users;
	private static Users instance;

	private Users()
	{
		users = new HashSet<User>();
	}

	public void add(User usr)
	{
		users.add(usr);
	}


	public HashSet<User> getUsers()
	{
		return users;
	}

	public static Users getInstance()
	{
		if (instance == null)
		{
			// check if file exists
			File f = new File("Users.dat");
			if (f.exists() && !f.isDirectory()) {
				instance = read(f);
				if (instance != null) {
					System.out.println("Users instance read from file.");
				} else {
					System.out.println("Failed to read Users instance from file. Creating new instance.");
					instance = new Users();
				}
			} else {
				instance = new Users();
			}
		}
		return instance;
	}

	public static void close()
	{
		if (instance != null)
		{
			instance.write();
		}
		instance = null;
		System.out.println("Users instance closed.");
	}


	// writeObject and readObject methods for serialization
    public void write() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(String.format("Users.dat")))) {
            oos.writeObject(this);
            System.out.println(String.format("%s written to file.", this));
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public static Users read(File f) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream((f)))) {
            Users u = (Users) ois.readObject();
            //System.out.println(String.format("%s read from file", c));
            return u;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error reading from file: " + e.getMessage());
            return null;
        }
    }


    @Override
    public String toString() {
		StringBuilder sb = new StringBuilder();
		for (User u: users)
		{
			sb.append(u.toString()).append("\n");
		}
		return sb.toString();
    }
}